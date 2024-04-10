package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		
		Hello hello = new Hello();
		
		hello.setHello("hello");
		System.out.println("JpashopApplication.java ======== " + hello.getHello());
		
		SpringApplication.run(JpashopApplication.class, args);
		
	}

	@Bean
	Hibernate5JakartaModule hibernate5JakartaModule() {
		
//		Hibernate5JakartaModule hibernate5JakartModule = new Hibernate5JakartaModule();
//		
//		hibernate5JakartModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);
//		
//		return hibernate5JakartModule;
		return new Hibernate5JakartaModule();
		
	}
	
}
