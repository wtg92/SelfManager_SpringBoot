package manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SelfManagerSpringbootConfig implements WebMvcConfigurer{

	@Value("${file.external.root}")
	private String externalDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/sm_files/**").addResourceLocations("file:"+externalDir+"/");
	}
	
	
}
