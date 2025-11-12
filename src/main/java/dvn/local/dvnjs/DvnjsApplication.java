package dvn.local.dvnjs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "dvn.local.dvnjs.modules")
public class DvnjsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DvnjsApplication.class, args);
	}

}
