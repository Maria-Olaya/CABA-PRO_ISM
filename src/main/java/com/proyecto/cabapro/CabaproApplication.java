package com.proyecto.cabapro; 
// Define el paquete donde está esta clase. 
// Sirve para organizar el código y que Spring pueda escanearlo correctamente.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class CabaproApplication {
// Clase principal de la aplicación. Contiene el método main que arranca todo Spring Boot.

	public static void main(String[] args) {
		// Método principal que Java ejecuta al iniciar el programa.
		
		SpringApplication.run(CabaproApplication.class, args);
	}

}
