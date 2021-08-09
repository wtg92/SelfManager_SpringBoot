package manager.servlet;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import manager.exception.SMException;

@RestController
public class ServletExceptionHandler {
	
	public ServletExceptionHandler() {
		System.out.println("ServletExceptionHandler created..");
	}
	
	@ExceptionHandler(SMException.class)
	@ResponseBody
	public String handleError(Exception e) {
		System.out.println("catch!!!");
		System.out.println("unnun");
		System.out.println("I" + e.getMessage());
		return "really?";
	}
	
}
