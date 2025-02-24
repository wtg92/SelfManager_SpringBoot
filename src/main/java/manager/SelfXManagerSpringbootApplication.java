package manager;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

/*!!! 测试时打开该注解*/
@SpringBootApplication
@ServletComponentScan
@EnableScheduling
public class SelfXManagerSpringbootApplication {
	
	
	private static final Logger log = LoggerFactory.getLogger(SelfXManagerSpringbootApplication.class);
	
	public static void main(String[] args) throws UnknownHostException {
		Environment env = SpringApplication.run(SelfXManagerSpringbootApplication.class, args).getEnvironment();
		
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}\n\t" +
                "External: \t{}://{}:{}\n\t" +
                "Profile(s): \t{}\n\t" +
                "Context-Path: \t{}\n"+
                "----------------------------------------------------------",

        env.getProperty("spring.application.name"),
        protocol,
        env.getProperty("server.port"),
        protocol,
        InetAddress.getLocalHost().getHostAddress(),
        env.getProperty("server.port"),
        env.getActiveProfiles(),env.getProperty("server.servlet.context-path"));
	}

}
