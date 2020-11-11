package br.com.agendasus.auth.v1;

import br.com.agendasus.auth.v1.domain.usecase.AccessRecovery;
import br.com.agendasus.auth.v1.infrastructure.response.ResponseUtils;
import br.com.agendasus.auth.v1.infrastructure.system.SystemProperties;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"br.com.agendasus.auth.v1"})
public class WebAppContext {

    @Bean
    public AccessRecovery accessRecovery() {
        return Mockito.mock(AccessRecovery.class);
    }

    @Bean
    public ResponseUtils responseUtils() {
        return Mockito.mock(ResponseUtils.class);
    }

    @Bean
    public SystemProperties systemProperties() {
        return new SystemProperties();
    }

}
