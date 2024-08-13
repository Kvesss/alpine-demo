package com.example.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.accounts.dto.AccountsContactInfoDto;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@EnableConfigurationProperties(value = { AccountsContactInfoDto.class})
@EnableJpaAuditing(auditorAwareRef = "AuditAwareImpl")
@OpenAPIDefinition(info = @Info(title = "Accounts microservice REST API documentation",
								description = "Rest API documentation for microservices demo project",
								version = "v1",
								contact = @Contact(
									name = "David Kvesic",
									email = "someemail@email.com",
									url = "https://www.github.com"),
								license = @License(name = "Apache 2.0",
												   url = "https://www.github.com/license")
),
				   externalDocs = @ExternalDocumentation(
				   description = "More and More documentation",
				   url = "https://www.github.com/external/docs"
				   )

)
public class AccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

}
