package eu.outerheaven.certmanager.agent

import eu.outerheaven.certmanager.agent.entity.User
import eu.outerheaven.certmanager.agent.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

import javax.annotation.PostConstruct
import java.util.stream.Collectors
import java.util.stream.Stream

@SpringBootApplication
class AgentApplication {

	@Autowired
	private UserRepository repository;

	@PostConstruct
	void initUsers() {
		List<User> users = Stream.of(
				new User(101, "javatechie", "password", "javatechie@gmail.com"),
				new User(102, "user1", "pwd1", "user1@gmail.com"),
				new User(103, "user2", "pwd2", "user2@gmail.com"),
				new User(104, "user3", "pwd3", "user3@gmail.com")
		).collect(Collectors.toList());
		repository.saveAll(users);
	}

	static void main(String[] args) {
		SpringApplication.run(AgentApplication, args)
	}

}
