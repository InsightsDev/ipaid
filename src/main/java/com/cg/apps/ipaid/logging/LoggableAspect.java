
package com.cg.apps.ipaid.logging;

import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggableAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggableAspect.class);

	// =================================================
	// Class Methods
	// =================================================
	/**
	 * Log.
	 * 
	 * @param proceedingJoinPoint the proceeding join point
	 * @param loggable the loggable
	 * @return the object
	 * @throws Throwable the throwable
	 */
	@Around("@annotation(loggable)")
	public final Object log(final ProceedingJoinPoint proceedingJoinPoint, final Loggable loggable) throws Throwable {
		final StopWatch stopWatch = new StopWatch();
		Object proceedingObject = null;
		stopWatch.start();
		final Signature signature = proceedingJoinPoint.getSignature();
		final String qualifiedMethodName = signature.getName() + "()";
		final String qualifiedClassName = proceedingJoinPoint.getTarget().getClass().getSimpleName();
		LOGGER.info("ENTERED_METHOD = {}.{}", qualifiedClassName, qualifiedMethodName);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("METHOD_PARAMS = {}", proceedingJoinPoint.getArgs());
		}
		try {
			proceedingObject = proceedingJoinPoint.proceed();
		} catch (Exception exception) {
			stopWatch.stop();
			LOGGER.info("EXITED_METHOD = {}.{}  TIME = {} EXCEPTION_MESSAGE = {}", qualifiedClassName, qualifiedMethodName, stopWatch.getTime(), exception.getMessage());
			throw exception;
		}
		stopWatch.stop();
		LOGGER.info("EXITED_METHOD = {}.{} TIME = {}", qualifiedClassName, qualifiedMethodName, stopWatch.getTime());

		return proceedingObject;

	}
}
