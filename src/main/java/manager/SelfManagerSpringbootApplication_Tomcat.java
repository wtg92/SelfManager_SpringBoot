package manager;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication
//@ServletComponentScan
//@EnableScheduling
public class SelfManagerSpringbootApplication_Tomcat  extends SpringBootServletInitializer {
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SelfManagerSpringbootApplication_Tomcat.class);
    }
    
	private static final Logger log = LoggerFactory.getLogger(SelfManagerSpringbootApplication_Tomcat.class);
	
	public static void main(String[] args) throws UnknownHostException {
		Environment env = SpringApplication.run(SelfManagerSpringbootApplication_Tomcat.class, args).getEnvironment();
		
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
