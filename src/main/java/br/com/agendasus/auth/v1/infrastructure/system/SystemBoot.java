package br.com.agendasus.auth.v1.infrastructure.system;

import br.com.agendasus.auth.AgendaSUSApp;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Configuration
public class SystemBoot {

    private Logger logger = LoggerFactory.getLogger(SystemBoot.class);

    @Autowired
    private SystemProperties properties;

    @PostConstruct
    public void inicialization() throws IOException, XmlPullParserException {
        Locale.setDefault(new Locale("pt", "BR"));
        getVersionFromPomXML();
        showSystemInfo();
    }

    private void getVersionFromPomXML() throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        AgendaSUSApp.systemVersion = model.getVersion();
    }

    private void showSystemInfo() {
        StringBuilder logo = new StringBuilder()
        .append("\n#--------------- INITIALIZING SYSTEM ---------------#")
        .append("\n     ___       _______  _______ .__   __.  _______       ___           _______. __    __       _______.")
        .append("\n    /   \\     /  _____||   ____||  \\ |  | |       \\     /   \\         /       ||  |  |  |     /       |")
        .append("\n   /  ^  \\   |  |  __  |  |__   |   \\|  | |  .--.  |   /  ^  \\       |   (----`|  |  |  |    |   (----`")
        .append("\n  /  /_\\  \\  |  | |_ | |   __|  |  . `  | |  |  |  |  /  /_\\  \\       \\   \\    |  |  |  |     \\   \\ ")
        .append("\n /  _____  \\ |  |__| | |  |____ |  |\\   | |  '--'  | /  _____  \\  .----)   |   |  `--'  | .----)   |")
        .append("\n/__/     \\__\\ \\______| |_______||__| \\__| |_______/ /__/     \\__\\ |_______/     \\______/  |_______/ ")
        .append("\n ")
        .append("\n#--------------- SYSTEM STARTED ---------------#")
        .append("\nCurrent Date: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()))
        .append("\nService: AgendaSUS-Auth")
        .append("\nRunning on port: ").append(properties.getAppPort())
        .append("\nVersion: ").append(AgendaSUSApp.systemVersion)
        .append("\nDatabase Server: ").append(properties.getDatabaseServer()).append(":").append(properties.getDatabasePort())
        .append("\nDatabase Name: ").append(properties.getDatabaseName())
        .append("\nRedis Server: ").append(properties.getRedisHost()).append(":").append(properties.getRedisPort())
        .append("\nToken timeout (minutes): ").append(properties.getTokenTimeoutMinutes());
        logger.info(logo.toString());
    }

}
