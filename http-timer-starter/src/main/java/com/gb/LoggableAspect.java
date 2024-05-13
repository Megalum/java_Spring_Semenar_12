package com.gb;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class LoggableAspect {

    @Pointcut("within(@com.gb.Loggable *)")
    public void beansMethod(){}

    @Pointcut("@annotation(com.gb.Loggable)")
    public void beansWithAnnotation(){}

    @Around("beansMethod() || beansWithAnnotation()")
    public Object loggable(ProceedingJoinPoint proceedingJoinPoint){
        long start = System.currentTimeMillis();
        Level level = extractLevel(proceedingJoinPoint);
        long stamp = extractTimer(proceedingJoinPoint);

        log.atLevel(level).log("target: " + proceedingJoinPoint.getTarget());
        log.atLevel(level).log("args: " + Arrays.toString(proceedingJoinPoint.getArgs()));
        log.atLevel(level).log("method: " + proceedingJoinPoint.getSignature());

        try {
            Object result = proceedingJoinPoint.proceed();
            log.atLevel(level).log(timer(proceedingJoinPoint, start, stamp));
            return result;
        } catch (Throwable e){
            log.atLevel(level).log(e.getMessage());
            log.atLevel(level).log(timer(proceedingJoinPoint, start, stamp));
        }
        return null;

    }

    private Level extractLevel(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Loggable annotation = signature.getMethod().getAnnotation(Loggable.class);
        if (annotation != null) {
            return annotation.level();
        }

        return joinPoint.getTarget().getClass().getAnnotation(Loggable.class).level();
    }

    private long extractTimer(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Loggable annotation = signature.getMethod().getAnnotation(Loggable.class);
        if (annotation != null) {
            return annotation.stamp();
        }

        return joinPoint.getTarget().getClass().getAnnotation(Loggable.class).stamp();
    }

    private String timer(ProceedingJoinPoint proceedingJoinPoint, long start, long timestamp){
        try {
            Thread.sleep(timestamp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long finish = System.currentTimeMillis();
        long elapsed = finish - start;
        String clas = proceedingJoinPoint.getTarget().toString()
                .replaceAll("ru.gb.", "");
        clas = clas.substring(0, clas.indexOf('@'));
        String method = proceedingJoinPoint.getSignature().toString();

        return clas + " - " + method.replaceAll("ru.gb.", "") + ": " + elapsed + "мс.";
    }
}
