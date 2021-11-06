package eu.outerheaven.certmanager.agent


import eu.outerheaven.certmanager.agent.entity.User
import eu.outerheaven.certmanager.agent.entity.UserRole
import eu.outerheaven.certmanager.agent.repository.UserRepository
import eu.outerheaven.certmanager.agent.storage.StorageProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import javax.annotation.PostConstruct
import java.util.stream.Collectors
import java.util.stream.Stream

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties(StorageProperties)
class AgentApplication {

	private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder()

	@Autowired
	private UserRepository repository;

	@PostConstruct
	void initUsers() {
		List<User> users = Stream.of(
				new User(10, "admin", passwordEncoder.encode("kuracnamotociklu"), "", UserRole.ADMIN),
		).collect(Collectors.toList());
		repository.saveAll(users);
	}

	static void main(String[] args) {
		SpringApplication.run(AgentApplication, args)
	}


}
