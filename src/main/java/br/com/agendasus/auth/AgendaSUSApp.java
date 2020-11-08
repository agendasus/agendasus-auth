package br.com.agendasus.auth;


import br.com.agendasus.auth.v1.infrastructure.system.SystemProperties;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyVetoException;

@Configuration
@EnableFeignClients
@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class AgendaSUSApp {

	@Autowired
	private SystemProperties systemProperties;

	public static String systemVersion;
	public static final String ADMIN_LOGIN = "admin";
	public static final String ADMIN_SENHA = "adminagendasus";

	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(AgendaSUSApp.class, "");
	}

	@Bean
	public ComboPooledDataSource dataSource() throws PropertyVetoException {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("org.postgresql.Driver");
        dataSource.setJdbcUrl("jdbc:postgresql://"+systemProperties.getDatabaseServer()+":"+systemProperties.getDatabasePort()+"/" + systemProperties.getDatabaseName());
		dataSource.setUser(systemProperties.getDatabaseUser());
		dataSource.setPassword(systemProperties.getDatabasePassword());
		dataSource.setMinPoolSize(5);
		dataSource.setMaxPoolSize(80);
		dataSource.setMaxIdleTime(120);
		dataSource.setMaxStatementsPerConnection(100);
		dataSource.setAcquireIncrement(2);
		dataSource.setInitialPoolSize(5);
		return dataSource;
	}

}
