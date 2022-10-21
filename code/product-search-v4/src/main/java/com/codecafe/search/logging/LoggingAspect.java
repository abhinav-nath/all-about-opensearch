package com.codecafe.search.logging;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Configuration
public class LoggingAspect {

  /**
   * Pointcut that matches all the REST controllers
   */
  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerPointcut() {
    // Method is empty as this is just a Pointcut, the implementations are in the advices.
  }

  /**
   * Pointcut that matches all repositories, services and Web REST endpoints.
   */
  @Pointcut("within(@org.springframework.stereotype.Repository *)"
    + " || within(@org.springframework.stereotype.Service *)"
    + " || within(@org.springframework.web.bind.annotation.RestController *)")
  public void springBeanPointcut() {
    // Method is empty as this is just a Pointcut, the implementations are in the advices.
  }

  /**
   * Pointcut that matches all Spring beans in the application's main packages.
   */
  @Pointcut("within(com.codecafe.search.controller..*)" + " || within(com.codecafe.search.service..*)")
  public void applicationPackagePointcut() {
    // Method is empty as this is just a Pointcut, the implementations are in the advices.
  }

  @Before("controllerPointcut()")
  public void beforeHttpLogging(JoinPoint joinPoint) {
    Logger logger = this.logger(joinPoint);
    HttpServletRequest request = extractRequest();
    StringBuilder sb = new StringBuilder().append(request.getMethod())
                                          .append(" ")
                                          .append(request.getRequestURI());
    logger.info("Incoming Request : {}", sb);
  }

  /**
   * Advice that logs methods throwing exceptions.
   *
   * @param joinPoint join point for advice.
   * @param e         exception.
   */
  @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
    logger(joinPoint).error("Exception in {}() with cause = '{}' and exception = '{}'",
      joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL", e.getMessage(), e);
  }

  /**
   * Advice that does the performance tracing of methods.
   *
   * @param joinPoint join point for advice.
   * @return result.
   * @throws Throwable throws {@link IllegalArgumentException}.
   */
  @Around("applicationPackagePointcut() && springBeanPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    Logger log = logger(joinPoint);
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

    //Get intercepted method details
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();

    final StopWatch stopWatch = new StopWatch();

    //Measure method execution time
    stopWatch.start();
    Object result = joinPoint.proceed();
    stopWatch.stop();

    //Log method execution time
    log.info("Execution time of {}.{} :: {} ms", className, methodName, stopWatch.getTotalTimeMillis());

    return result;
  }

  /**
   * Retrieves the {@link Logger} associated to the given {@link JoinPoint}.
   *
   * @param joinPoint join point we want the logger for.
   * @return {@link Logger} associated to the given {@link JoinPoint}.
   */
  private Logger logger(JoinPoint joinPoint) {
    return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
  }

  private HttpServletRequest extractRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
  }

}