package br.com.agendasus.auth.v1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AccessRecoveryControllerTest {

    @Autowired
    private AccessRecoveryController accessRecoveryController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() throws Exception {
        assertThat(accessRecoveryController).isNotNull();
    }

    @Test
    public void forgot_password_should_return_bad_request_because_body_is_not_informed() throws Exception {
        this.mockMvc.perform(post("/forgot-password")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void forgot_password_should_return_ok_because_was_informed_a_body_content() throws Exception {
        this.mockMvc.perform(post("/forgot-password")
                .content("someemail@email.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void forgot_password_should_return_ok_because_login_exists_and_link_to_renew_password_was_send() throws Exception {
        this.mockMvc.perform(post("/forgot-password")
                .content("joao@gmail.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void password_recovery_should_return_bad_request_because_body_is_not_informed() throws Exception {
        this.mockMvc.perform(post("/password-recovery")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void password_recovery_should_return_bad_request_because_hash_informed_is_invalid() throws Exception {
       this.mockMvc.perform(post("/password-recovery")
                .content("{\n" +
                        "    \"hash\": \"some_invalid_hash\",\n" +
                        "    \"password\": \"123456\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Ocorreu um erro, por favor entre em contato com o suporte do AgendaSUS")));
    }

}
