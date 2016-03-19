package com.cg.apps.ipaid.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.cg.apps.ipaid.job.EmailReader;

@Aspect
@Component
public class LogAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailReader.class);

	@Around("execution(* com.cg.apps..*.* (..))")
	public Object logAll(ProceedingJoinPoint joinPoint) throws Throwable {
		Object result = null;
		Throwable throwable = null;
		LOGGER.info("Executing {} with {}", joinPoint.getSignature(), joinPoint.getArgs());
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			result = joinPoint.proceed();
		} catch (Throwable t) {
			throwable = t;
		}
		stopWatch.stop();

		if (throwable == null) {
			LOGGER.info("Executed {} in {} ms!", joinPoint.getSignature(), stopWatch.getTotalTimeMillis());
		} else {
			LOGGER.info("Executed {} in {} ms! Threw an exception: ", joinPoint.getSignature(),
					stopWatch.getTotalTimeMillis(), throwable);
			throw throwable;
		}
		return result;
	}
}
