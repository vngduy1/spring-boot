package dvn.local.dvnjs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DvnjsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DvnjsApplication.class, args);
	}

}
