package eu.outerheaven.certmanager.agent.util

import com.ibm.security.cmskeystore.CMSProvider
import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.entity.KeystoreCertificate
import eu.outerheaven.certmanager.agent.repository.CertificateRepository
import eu.outerheaven.certmanager.agent.repository.KeystoreCertificateRepository
import eu.outerheaven.certmanager.agent.repository.KeystoreRepository
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import eu.outerheaven.certmanager.agent.entity.Certificate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.security.cert.CertificateEncodingException
import java.nio.file.Files
import java.nio.file.Paths
import java.security.*
import java.security.cert.*


/**
 * Class that contains method to load certificates in {@link java.security.cert.X509Certificate}.
 */
@Service
class CertificateLoader {

    @Autowired
    private final KeystoreRepository repository

    @Autowired
    private final CertificateRepository certificateRepository

    @Autowired
    private final KeystoreCertificateRepository keystoreCertificateRepository

    private static final Logger LOG = LoggerFactory.getLogger(CertificateLoader.class);

    /**
     * Method for getting certificate file.
     *
     * @param uri certificate web url
     * @return loaded certificate
     * @throws CertificateException error
     * @throws IOException          error
     */
    //Unchanged
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
    ///Unchanged
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
    //Unchanged
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
                certificates.add(custom_certificate)
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
    //Refactored
    List<KeystoreCertificate> loadCertificatesFromKeystore(String uri, String password, Keystore tKeystore) throws KeyStoreException,
            IOException, CertificateException, NoSuchAlgorithmException {
        LOG.info("Start of load from keystore")
        String[] types = new String[]{"JKS", "JCEKS", "PKCS12", "IBMCMSKS",/*BC types*/ "BKS", "PKCS12", "UBER"}
        boolean read = false
        //List<X509Certificate> certificates = new ArrayList<>()
        List<KeystoreCertificate> certificates = new ArrayList<>()
        List<KeystoreCertificate> currentcertificates = tKeystore.keystoreCertificates
        List<KeystoreCertificate> unchangedCertificates = new ArrayList<>()
        List<KeystoreCertificate> modifiedCertificates = new ArrayList<>()
        List<KeystoreCertificate> addedCertificates = new ArrayList<>()
        List<KeystoreCertificate> removedCertificates = new ArrayList<>()
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
                    Certificate x509certificate = new Certificate()
                    KeystoreCertificate certificate = new KeystoreCertificate()
                    String alias = aliases.nextElement()
                    certificate.setAlias(alias)
                    if(keystore.getKey(alias,password.toCharArray()) != null){
                        //NOTE this was modified to cast as private key
                        x509certificate.setKey(keystore.getKey(alias,password.toCharArray()) as PrivateKey)
                        //certificate.setKey(keystore.getKey(alias,password.toCharArray()))
                        LOG.info("Certificate with alias {} has a private key attached to it!",alias)
                    }
                    x509certificate.setX509Certificate(keystore.getCertificate(alias) as X509Certificate)
                    certificate.setCertificate(x509certificate)
                    certificate.setKeystoreId(tKeystore.getId())

                    if(currentcertificates != null){
                        KeystoreCertificate tmpCertificate = currentcertificates.stream()
                                .filter(tmp -> certificate.getAlias().equals(tmp.getAlias()))
                                .findAny()
                                .orElse(null);
                        if(tmpCertificate == null){
                            LOG.info("Found new certificate")
                            addedCertificates.add(certificate)
                        }else {
                            if (tmpCertificate.getCertificate().x509Certificate == certificate.getCertificate().x509Certificate && tmpCertificate.getCertificate().key == certificate.getCertificate().key) {
                                LOG.info("Found unmodified certificate")
                                unchangedCertificates.add(tmpCertificate)
                                currentcertificates.remove(tmpCertificate)
                            }else{
                                LOG.info("Found modified certificate")
                                certificate.setId(tmpCertificate.getId())
                                modifiedCertificates.add(certificate)
                                currentcertificates.remove(tmpCertificate)
                            }
                        }
                    }
                    else {certificates.add(certificate)}
                    //certificates.add((X509Certificate) keystore.getCertificate(alias))
                    LOG.info("Read certificate with alias: " + alias)
                }
                if(currentcertificates != null) removedCertificates.addAll(currentcertificates)
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
        certificates.addAll(unchangedCertificates)
        certificates.addAll(modifiedCertificates)
        certificates.addAll(addedCertificates)
        removedCertificates.forEach(r->{
            keystoreCertificateRepository.deleteById(r.getId())
        })
        return certificates
    }
    //Unchanged
    void addCertificatesToKeystore(String uri, String password, List<KeystoreCertificate> certificates) throws KeyStoreException,
            IOException, CertificateException, NoSuchAlgorithmException {
        String[] types = new String[]{"JKS", "JCEKS", "PKCS12", "IBMCMSKS",/*BC types*/ "BKS", "PKCS12", "UBER"}
        boolean read = false
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
                LOG.debug("Adding certificate(s) to keystore")
                for(int n=0;n<certificates.size();n++){
                    if(keystore.containsAlias(certificates.get(n).getAlias())){
                        LOG.warn("Added certificate with alias {} in keystore {} already exist, it will be overwritten", certificates.get(n).getAlias(),uri)
                    }
                    if(certificates.get(n).getCertificate().key != null){
                        keystore.setKeyEntry(certificates.get(n).getAlias(),certificates.get(n).getCertificate().getKey(),password.toCharArray(),certificates.get(n).getCertificate().getX509Certificate())
                    }else{
                        keystore.setCertificateEntry(certificates.get(n).getAlias(),certificates.get(n).getCertificate().getX509Certificate())
                    }

                }

                FileOutputStream fos = new FileOutputStream(uri);
                keystore.store(fos, password.toCharArray());



                read=true
                break
            } catch (Exception e) {
                LOG.error("[ADD]Reading keystore with type " + types[i] + " : " + e.toString())
            }
        }
        //needed?
        // Security.removeProvider("BC")
        if (!read) {
            throw new RuntimeException("Could not read keystore: " + uri)
        }
    }

    //Unchanged
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
    //Unchanged
    static List<X509Certificate> getPublicCertFromPEM(String path) throws IOException, CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509")
        // TODO problem ako se u fileu nađe private key ...
        //Ovo čita samo certove
        try(FileInputStream is = new FileInputStream (path)){
            final Collection<X509Certificate> certs = (Collection<X509Certificate>) fact.generateCertificates(is)
            return new ArrayList<>(certs)
        }
    }
    //Unchanged
    void removeCertFromKeystore(String uri, String password, String certalias) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException{
        String[] types = new String[]{"JKS", "JCEKS", "PKCS12", "IBMCMSKS",/*BC types*/ "BKS", "PKCS12", "UBER"}
        boolean read = false
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
                LOG.debug("Removing certificate from keystore")
                keystore.deleteEntry(certalias)
                FileOutputStream fos = new FileOutputStream(uri);
                keystore.store(fos, password.toCharArray());



                read=true
                break
            } catch (Exception e) {
                LOG.error("[ADD]Reading keystore with type " + types[i] + " : " + e.toString())
            }
        }
        //needed?
        // Security.removeProvider("BC")
        if (!read) {
            throw new RuntimeException("Could not read keystore: " + uri)
        }

    }
    //Unchanged
    String encodeX509(X509Certificate x509Certificate){
        try{
            ByteArrayOutputStream binaryOutput = new ByteArrayOutputStream()
            ObjectOutputStream objectStream = new ObjectOutputStream(binaryOutput)
            objectStream.writeObject(x509Certificate)
            objectStream.close()
            binaryOutput.close()
            return Base64.getUrlEncoder().encodeToString(binaryOutput.toByteArray())
        }catch (Exception exception){
            LOG.error("Could not encode X509Certificate to base64 with error: " + exception)
        }
    }
    //Unchanged
    X509Certificate decodeX509(String input){
        try{
            byte [] data = Base64.getUrlDecoder().decode(input)
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))
            X509Certificate x509Certificate = objectInputStream.readObject() as X509Certificate
            objectInputStream.close()
            return x509Certificate
        }catch(Exception exception){
            LOG.error("Could not decode  base64 to X509Certificate with error: " + exception)
        }
    }
    //Unchanged
    String encodeKey(PrivateKey key){
        try{
            ByteArrayOutputStream binaryOutput = new ByteArrayOutputStream()
            ObjectOutputStream objectStream = new ObjectOutputStream(binaryOutput)
            objectStream.writeObject(key)
            objectStream.close()
            binaryOutput.close()
            return Base64.getUrlEncoder().encodeToString(binaryOutput.toByteArray())
        }catch (Exception exception){
            LOG.error("Could not encode Key to base64 with error: " + exception)
        }
    }
    //Unchanged
    PrivateKey decodeKey(String input){
        try{
            byte [] data = Base64.getUrlDecoder().decode(input)
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))
            PrivateKey key = objectInputStream.readObject() as PrivateKey
            objectInputStream.close()
            return key
        }catch(Exception exception){
            LOG.error("Could not decode base64 to key with error: " + exception)
        }
    }
    //Unchanged
    String generateRandomAlphanumeric(){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 20
        Random random = new Random()

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();


        return generatedString
    }

}
