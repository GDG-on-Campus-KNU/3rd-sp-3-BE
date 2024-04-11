package gdsc.comunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CommunityApplication {
	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}
}
