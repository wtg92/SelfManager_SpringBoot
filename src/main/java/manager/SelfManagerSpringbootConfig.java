package manager;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import manager.system.SM;

@Configuration
public class SelfManagerSpringbootConfig implements WebMvcConfigurer{
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/sm_files/**").addResourceLocations("file:"+SM.SM_EXTERNAL_FILES_DIRECTORY.getAbsolutePath()+"/");
	}
	
	
}
