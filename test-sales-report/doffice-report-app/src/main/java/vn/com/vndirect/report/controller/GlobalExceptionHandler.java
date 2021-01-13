package vn.com.vndirect.report.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
	@RequestMapping(method = RequestMethod.GET, value = {"403"})
	public ModelAndView directorReport() {
		return new ModelAndView("403");
	}
}
