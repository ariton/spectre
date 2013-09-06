package de.mzsoftware.spectre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Ariton
 * Date: 06.08.13
 * Time: 12:39
 */
public class AnnotationIntrospector {

    Logger log = LoggerFactory.getLogger(AnnotationIntrospector.class);

    private Object object;
    Map<Class<?>, Annotation> annotationMap = new ConcurrentHashMap<Class<?>, Annotation>();

    public AnnotationIntrospector(Object object){
        log.debug("Creating AnnotationIntrospector for Object {}", object.getClass().getName());
        this.object = object;
        introspect();
    }

    private void introspect(){

        Class<?> clazz = object.getClass();
        log.debug("Introspect Class {}", clazz.getName());
        if(!Proxy.isProxyClass(clazz)){
            log.debug("No Proxy detected, processing Annotations in Class hierarchy.");
            saveAnnotations(clazz.getAnnotations());
            Class<?> superclazz;
            do {
                superclazz = clazz.getSuperclass();
                saveAnnotations(superclazz.getAnnotations());
            } while (!superclazz.getName().equals(Object.class.getName()));
            log.debug("Found {} Annotations in Class hierarchy.", annotationMap.size());
        }

        log.debug("Processing Annotations on Interfaces.");
        for (Class iFaceClazz : clazz.getInterfaces()){
            saveAnnotations(iFaceClazz.getAnnotations());
        }
        log.debug("Found {} Annotations on Class {}.", annotationMap.size(), clazz.getName());

    }

    private void saveAnnotations(Annotation[] annotations){

        for(Annotation annotation : annotations){
            log.debug("Saving Annotation with Key {} and Value {}.",annotation.getClass(), annotation);
            annotationMap.put(annotation.getClass(), annotation);
        }

    }

    public <T> Annotation findAnnotation(Class<?> annotationClass){

        if(annotationMap.containsKey(annotationClass)){
            log.debug("Annotation {} found in annotationList...", annotationClass);
            return annotationMap.get(annotationClass);
        }
        log.debug("Annotation {} not found ...", annotationClass);
        return null;
    }

}
