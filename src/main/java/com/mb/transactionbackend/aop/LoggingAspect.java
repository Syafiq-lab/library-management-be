package com.mb.transactionbackend.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Before("execution(* com.mb.transactionbackend.controller.*.*(..))")
	public void logBefore(JoinPoint joinPoint) {
		log.info("Entering method: {} with arguments: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
	}

	@Around("execution(* com.mb.transactionbackend.controller.*.*(..))")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object result = joinPoint.proceed();
			log.info("Method: {} executed successfully with result: {}", joinPoint.getSignature().getName(), result);
			return result;
		} catch (Throwable throwable) {
			log.error("Exception in method: {} with cause: {}", joinPoint.getSignature().getName(), throwable.getCause() != null ? throwable.getCause() : "NULL");
			throw throwable;
		}
	}

	@AfterThrowing(pointcut = "execution(* com.mb.transactionbackend.controller.*.*(..))", throwing = "error")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
		log.error("Exception in method: {} with cause: {}", joinPoint.getSignature().getName(), error.getCause() != null ? error.getCause() : "NULL");
	}
}
