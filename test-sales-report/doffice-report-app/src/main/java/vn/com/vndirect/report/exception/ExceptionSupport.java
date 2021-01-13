package vn.com.vndirect.report.exception;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionSupport {
	
	@Value("classpath:error-popup.html")
	private Resource resource;
	
	private final static Logger logger = LoggerFactory.getLogger(ExceptionSupport.class);
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleException(Exception ex, HttpServletResponse response) {
		logger.error(ex.getMessage(), ex);
		return ResponseEntity.badRequest().body(showErrorPopup());
	}

	public String showErrorPopup() {		
		try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
	}
	
	public static boolean checkBrokenPipeEx(Exception ex) {
		return (ex instanceof IOException && "Broken pipe".equals(ex.getMessage()));
	}
}
