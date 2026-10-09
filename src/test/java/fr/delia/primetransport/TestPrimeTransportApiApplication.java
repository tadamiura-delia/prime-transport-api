package fr.delia.primetransport;

import org.springframework.boot.SpringApplication;

public class TestPrimeTransportApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(PrimeTransportApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
