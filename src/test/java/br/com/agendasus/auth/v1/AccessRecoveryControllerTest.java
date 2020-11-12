package br.com.agendasus.auth.v1;

import br.com.agendasus.auth.v1.domain.usecase.AccessRecovery;
import br.com.agendasus.auth.v1.infrastructure.security.JwtAuthenticationFilter;
import br.com.agendasus.auth.v1.infrastructure.system.EntityManagerConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppContext.class})
@WebAppConfiguration
@TestPropertySource("classpath:application.properties")
public class AccessRecoveryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AccessRecovery accessRecovery;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        Mockito.reset(this.accessRecovery);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(new JwtAuthenticationFilter()).build();
    }

    @Ignore
    @Test
    public void forgotPassword() {

    }

}
