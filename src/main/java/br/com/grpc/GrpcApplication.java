package br.com.grpc;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import br.com.grpc.service.UserService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.grpc")
@EnableJpaRepositories(basePackages = "br.com.grpc.dao") 
public class GrpcApplication {

	private static final Logger logger = Logger.getLogger(GrpcApplication.class.getName());

public static void main(String[] args) throws IOException, InterruptedException {
		ConfigurableApplicationContext context = SpringApplication.run(GrpcApplication.class, args);

		UserService userService = context.getBean(UserService.class);

		Server server = ServerBuilder.forPort(8081).
		addService(userService)
		.build();

		server.start();

		logger.info("Server started on " + server.getPort());

		server.awaitTermination();
	}
}
