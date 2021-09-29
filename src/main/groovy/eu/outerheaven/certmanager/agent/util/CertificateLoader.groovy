package eu.outerheaven.certmanager.agent.util

import com.ibm.security.cmskeystore.CMSProvider
import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.repository.KeystoreRepository
import io.jsonwebtoken.security.Keys
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import eu.outerheaven.certmanager.agent.entity.Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.security.cert.CertificateEncodingException
import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.security.*
import java.security.cert.*


/**
 * Class that contains method to load certificates in {@link java.security.cert.X509Certificate}.
 */
class CertificateLoader {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateLoader.class);

    /**
     * Method for getting certificate file.
     *
     * @param uri certificate web url
     * @return loaded certificate
     * @throws CertificateException error
     * @throws IOException          error
     */
    static X509Certificate loadFileCertificate(String uri) throws CertificateException, IOException {
        if (uri.toLowerCase().endsWith("pem")) {
            InputStream res = Files.newInputStream(Paths.get(uri))
            Reader fRd = new BufferedReader(new InputStreamReader(res))
            PEMParser pemParser = new PEMParser(fRd)
            return (X509Certificate) pemParser.readObject()
        }
        return (X509Certificate) CertificateFactory
                .getInstance("X509")
                .generateCertificate(
                        new ByteArrayInputStream(Files.readAllBytes(Paths.get(uri)))
                )
    }

    /**
     * Method for getting certificate and its trust chain.
     *
     * @param uri certificate web url
     * @return list of certificates
     * @throws KeyManagementException       error
     * @throws NoSuchAlgorithmException     error
     * @throws IOException                  error
     * @throws CertificateException         error
     * @throws CertificateEncodingException error
     */
    //UPDATED
    static List<Certificate> loadWebCertificates(String uri) throws IOException, NoSuchAlgorithmException,
            CertificateException, CertificateEncodingException, KeyManagementException, URISyntaxException {
        if (!uri.startsWith("https")) {
            int index = uri.indexOf("//")
            if (index != -1)
                uri = uri.substring(index + 2)
            uri = "https://" + uri
        }
        URI certURI = new URI(uri)
        return loadCertificatesFromHost(certURI.getHost(), certURI.getPort() != -1 ? certURI.getPort() : 443)
    }

    /**
     * Method for loading certificate chain from some host and port.
     *
     * @param host host
     * @param port port
     * @return list of certificates
     * @throws KeyManagementException       error
     * @throws NoSuchAlgorithmException     error
     * @throws IOException                  error
     * @throws CertificateException         error
     * @throws CertificateEncodingException error
     */
    //UPDATED
    static List<Certificate> loadCertificatesFromHost(String host, int port) throws KeyManagementException,
            NoSuchAlgorithmException, IOException, CertificateException, CertificateEncodingException {
        SSLSocket socket = null
        try {
            if (port == -1) {
                throw new RuntimeException("Port could not be found from URI")
            }
            List<Certificate> certificates = new ArrayList<>()
            SSLSocketFactory factory = SSLContext.getInstance("TLS") as SSLSocketFactory
            socket = (SSLSocket) factory.createSocket(host, port)
            socket.startHandshake()
            SSLSession session = socket.getSession()

            for (javax.security.cert.X509Certificate certificate : session.getPeerCertificateChain()) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509")
                ByteArrayInputStream bais = new ByteArrayInputStream(certificate.getEncoded())
                Certificate custom_certificate = new Certificate()
                custom_certificate.setX509Certificate(cf.generateCertificate(bais) as X509Certificate)
                custom_certificate.setManaged(false)
                certificates.add(custom_certificate)
                //certificates.add((X509Certificate) cf.generateCertificate(bais))
            }
            return certificates
        } finally {
            if (socket != null) {
                socket.close()
            }
        }
    }


    /**
     * Method for loading certificates from keystore,
     *
     * @param uri uri to keystore
     * @return list of certificates
     * @throws KeyStoreException        error
     * @throws IOException              error
     * @throws CertificateException     error
     * @throws NoSuchAlgorithmException error
     */
    //UPDATED
    static List<Certificate> loadCertificatesFromKeystore(String uri, String password, Keystore tKeystore) throws KeyStoreException,
            IOException, CertificateException, NoSuchAlgorithmException {
        String[] types = new String[]{"JKS", "JCEKS", "PKCS12", "IBMCMSKS",/*BC types*/ "BKS", "PKCS12", "UBER"}
        boolean read = false
        //List<X509Certificate> certificates = new ArrayList<>()
        List<Certificate> certificates = new ArrayList<>()
        Security.addProvider(new BouncyCastleProvider())
        Security.addProvider(new CMSProvider())
        for (int i = 0; i < types.length; ++i) {
            try {
                KeyStore keystore
                if (i >= 4) { //BC
                    keystore = KeyStore.getInstance(types[i], "BC")
                } else { //SUN AND IBM
                    keystore = KeyStore.getInstance(types[i])
                }

                keystore.load(new FileInputStream(uri), password.toCharArray())
                LOG.debug("Reading aliases from keystore")
                Enumeration<String> aliases = keystore.aliases()
                while (aliases.hasMoreElements()) {
                    Certificate certificate = new Certificate()
                    String alias = aliases.nextElement()
                    certificate.setAlias(alias)
                    certificate.setX509Certificate(keystore.getCertificate(alias) as X509Certificate)
                    certificate.setManaged(false)
                    certificates.add(certificate)
                    certificate.setKeystoreId(tKeystore.getId())
                    //certificates.add((X509Certificate) keystore.getCertificate(alias))
                    LOG.info("Read certificate with alias: " + alias)
                }
                read = true
                break
            } catch (Exception e) {
                LOG.error("Reading keystore with type " + types[i] + " : " + e.toString())
            }
        }
        //needed?
        // Security.removeProvider("BC")
        if (!read) {
            throw new RuntimeException("Could not read keystore: " + uri)
        }
        return certificates
    }


    //UPDATED (NOTE: DOES NOT APPLY ALIAS)
    static List<Certificate> loadCertificatesFromCacerts(String uri, String password) throws KeyStoreException,
            IOException, CertificateException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
       //List<X509Certificate> certificates = new ArrayList<>()
        List<Certificate> certificates = new ArrayList<>()
        // Load the JDK's cacerts keystore file
        FileInputStream is = new FileInputStream(uri)
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType())
        keystore.load(is, password.toCharArray())
        // This class retrieves the most-trusted CAs from the keystore
        PKIXParameters params = new PKIXParameters(keystore)

        // Get the set of trust anchors, which contain the most-trusted CA certificates
        for (TrustAnchor ta : params.getTrustAnchors()) {
            // Get certificate
            X509Certificate cert = ta.getTrustedCert()
            Certificate certificate = new Certificate()
            certificate.setX509Certificate(cert)
            certificate.setManaged(false)
            certificates.add(certificate)
        }

        return certificates
    }

    //TODO PEM
    static List<X509Certificate> getPublicCertFromPEM(String path) throws IOException, CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509")
        // TODO problem ako se u fileu nađe private key ...
        //Ovo čita samo certove
        try(FileInputStream is = new FileInputStream (path)){
            final Collection<X509Certificate> certs = (Collection<X509Certificate>) fact.generateCertificates(is)
            return new ArrayList<>(certs)
        }
    }

}
