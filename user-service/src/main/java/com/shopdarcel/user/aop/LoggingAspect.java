package com.shopdarcel.user.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Logs entry, exit, and timing for every service-layer method, per
 * ARCHITECTURE.md's "AOP LoggingAspect (service layer timing + Kafka
 * events)" requirement.
 * <p>
 * Deliberately does not log exceptions here — {@link com.shopdarcel.user.exception.GlobalExceptionHandler}
 * is the single source of truth for failure logging, since it's the one
 * place that sees every failure path (including validation and malformed
 * JSON, which never reach the service layer at all). Logging exceptions
 * here too would duplicate that.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.shopdarcel.user.service..*(..))")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature()
                .toShortString();
        long start = System.currentTimeMillis();

        log.info("Entering {}", methodName);
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("Exiting {} ({} ms)", methodName, duration);
        return result;
    }
}