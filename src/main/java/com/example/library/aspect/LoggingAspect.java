package com.example.library.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.util.Arrays;

/**
 * Aspect for logging execution of service and controller components.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

	/**
	 * Pointcut that matches all repositories, services, and Web REST endpoints.
	 */
	@Pointcut("within(@org.springframework.stereotype.Service *) || within(@org.springframework.web.bind.annotation.RestController *)")
	public void applicationLayer() {}

	/**
	 * Advice that logs methods entering and exiting.
	 *
	 * @param joinPoint join point for advice
	 * @return result of method execution
	 * @throws Throwable if any error occurs
	 */
	@Around("applicationLayer()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();

		String className = signature.getDeclaringTypeName();
		String methodName = signature.getName();

		log.info("Entering method [{}.{}] with arguments: {}", className, methodName, Arrays.toString(joinPoint.getArgs()));

		try {
			Object result = joinPoint.proceed();
			log.info("Exiting method [{}.{}] with result: {}", className, methodName, result);
			return result;
		} catch (Throwable e) {
			log.error("Exception in {}.{}() with cause = {}", className, methodName, e.getCause() != null ? e.getCause() : "NULL");
			throw e;
		}
	}
}
