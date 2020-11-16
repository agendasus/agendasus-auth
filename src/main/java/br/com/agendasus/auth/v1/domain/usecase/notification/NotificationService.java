package br.com.agendasus.auth.v1.domain.usecase.notification;

import br.com.agendasus.auth.v1.domain.model.UserLogin;
import br.com.agendasus.auth.v1.domain.usecase.notification.dto.AccessRecoveryEmailDTO;
import br.com.agendasus.auth.v1.domain.usecase.notification.dto.ReceiverEmailDTO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationClient notificationClient;

    private static final String AUTHORIZATION_TOKEN = "DM2kDSAn34Akjn2Hkj3K2j4nXkn2oTfn23AonDfoCkAn32";


    public void fallbackSendEmailToPasswordRecovery(UserLogin userLogin, String requestIP, String linkPasswordRecovery, String linkInvalidateRecovery) {
        logger.warn("Error to send dto to password recovery: {}", userLogin.getLogin());
    }

    @HystrixCommand(fallbackMethod = "fallbackSendEmailToPasswordRecovery")
    public void sendEmailToPasswordRecovery(UserLogin userLogin, String requestIP, String linkPasswordRecovery, String linkInvalidateRecovery){
        AccessRecoveryEmailDTO accessRecovery = new AccessRecoveryEmailDTO();
        accessRecovery.setSubject("AgendaSUS - Esqueceu sua senha?");
        accessRecovery.setRequestIP(requestIP);
        accessRecovery.setLinkPasswordRecovery(linkPasswordRecovery);
        accessRecovery.setLinkInvalidateRecovery(linkInvalidateRecovery);
        accessRecovery.getReceivers().add(new ReceiverEmailDTO(userLogin.getName(), userLogin.getLogin()));
        notificationClient.sendAccessRecoveryEmail(AUTHORIZATION_TOKEN, accessRecovery);
    }

}
