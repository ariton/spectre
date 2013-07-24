package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.MapClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 24.07.13
 * Time: 12:26
 */
public class Mapper {
    public static final String SETTER_PREFIX = "set";
    public static final String GETTER_PREFIX = "get";
    public static final String BOOLEAN_GETTER_PREFIX = "is";
    private Logger log = LoggerFactory.getLogger(Mapper.class);

    public <S, T, I> I map(S source) {
        log.debug("Mapper.map()");

        T targetImplementation = null;
        I targetInterface = null;
        try {
            targetImplementation = null;
            Annotation annotation = source.getClass().getAnnotation(MapClass.class);
            log.debug("Annotation {}", annotation);
            if(null != annotation){
                if(annotation instanceof MapClass){
                    MapClass mapClass = (MapClass) annotation;
                    Object implementationClass = mapClass.implementationClass().newInstance();
                    if(!(implementationClass instanceof NullMarker)){

                        targetImplementation = (T) mapClass.implementationClass().newInstance();
                        targetInterface = (I) mapClass.value();
                        log.debug("ImplementationClass given, returning \"{}\" as Type and using \"{}\" as implementation.",
                                targetInterface,
                                targetImplementation.getClass().getName());

                    } else {

                        targetImplementation = (T) mapClass.value().newInstance();
                        targetInterface = (I)targetImplementation;
                        log.debug("No ImplementationClass given, returning \"{}\" as Type.", targetInterface.getClass().getName());

                    }
                }

                List<Method> sourceGetterMethods = new ArrayList<Method>();
                HashMap<String,Method> targetSetterMethods = new HashMap<String, Method>();

                loadSourceGetterMethods(source, sourceGetterMethods);

                loadTargetSetterMethods(targetImplementation, targetSetterMethods);

                doMapping(source, targetImplementation, sourceGetterMethods, targetSetterMethods);
            }
        } catch (InstantiationException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        } catch (IllegalArgumentException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e);
        }

        log.debug("mapping sucessful!");
        return (I)targetImplementation;
    }

    private <S, T> void doMapping(S source, T targetImplementation, List<Method> sourceGetterMethods, HashMap<String, Method> targetSetterMethods) throws IllegalAccessException, InvocationTargetException {
        for (Method method : sourceGetterMethods){
            Object retVal = method.invoke(source);
            Class returnType = method.getReturnType();
            log.debug("getter returns \"{}\" of Type \"{}\"", retVal, returnType);

            String strippedMethodName = null;
            if(method.getName().startsWith(GETTER_PREFIX)){
                strippedMethodName = method.getName().substring(GETTER_PREFIX.length());
                log.debug("StrippedMethodName: {} ", strippedMethodName);
            } else if (method.getName().startsWith(BOOLEAN_GETTER_PREFIX)){
                strippedMethodName = method.getName().substring(BOOLEAN_GETTER_PREFIX.length());
                log.debug("StrippedMethodName: {} ", strippedMethodName);
            }

            Method targetSetter = targetSetterMethods.get(SETTER_PREFIX+strippedMethodName);
            log.debug("targetSetter: {}", targetSetter);
            targetSetter.invoke(targetImplementation,retVal);
        }
    }

    private <T, S> void loadTargetSetterMethods(T target, HashMap<String, Method> targetSetterMethods) throws IllegalAccessException, InstantiationException {

        S superclass = (S)target.getClass().getSuperclass().newInstance();
        log.debug("Superclass {} of {} detected.",superclass, target.getClass());

        if(superclass.getClass().getName().equals("java.lang.Object")){
            log.debug("Superclass of Type {} detected - skipping...", superclass.getClass().getName());
        } else {
            log.debug("Superclass of Type {} detected, scanning...", superclass.getClass());
            loadTargetSetterMethods(superclass, targetSetterMethods);
        }

        for(Method method : target.getClass().getDeclaredMethods()){
            if (method.getName().startsWith(SETTER_PREFIX)){
                targetSetterMethods.put(method.getName(), method);
                log.debug("Adding Method {} to Target-Setters", method);
            }
        }
    }

    private <S,T> void loadSourceGetterMethods(S source, List<Method> sourceGetterMethods) throws IllegalAccessException, InstantiationException {

        T superclass = (T)source.getClass().getSuperclass().newInstance();
        log.debug("Superclass {} of {} detected.",superclass, source.getClass());

        if(superclass.getClass().getName().equals("java.lang.Object")){
            log.debug("Superclass of Type {} detected - skipping...", superclass.getClass().getName());
        } else {
            log.debug("Superclass of Type {} detected, scanning...", superclass.getClass());
            loadSourceGetterMethods(superclass, sourceGetterMethods);
        }

        for(Method method : source.getClass().getDeclaredMethods()){
            if(method.getName().startsWith(GETTER_PREFIX) || method.getName().startsWith(BOOLEAN_GETTER_PREFIX)){
                sourceGetterMethods.add(method);
                log.debug("Adding Method {} to Source-Getters", method);
            } else {
                log.debug("Method {} not recognized as getter - skipping", method.getName());
            }
        }
    }
}
