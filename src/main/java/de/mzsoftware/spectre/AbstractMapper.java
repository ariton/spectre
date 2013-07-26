package de.mzsoftware.spectre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class AbstractMapper<I, T> implements Mapper{
    public static final String SETTER_PREFIX = "set";
    public static final String GETTER_PREFIX = "get";
    public static final String BOOLEAN_GETTER_PREFIX = "is";
    private Logger log = LoggerFactory.getLogger(AbstractMapper.class);

    protected I targetInterface;
    protected T targetImplementation;

    protected List<Method> sourceGetterMethods = new ArrayList<Method>();
    protected HashMap<String, Method> targetSetterMethods = new HashMap<String, Method>();



    protected <S, T> void doMapping(S source, T targetImplementation) throws IllegalAccessException, InvocationTargetException {
        for (Method method : sourceGetterMethods) {
            Object retVal = method.invoke(source);
            Class returnType = method.getReturnType();
            log.debug("getter returns \"{}\" of Type \"{}\"", retVal, returnType);

            String strippedMethodName = null;
            if (method.getName().startsWith(GETTER_PREFIX)) {
                strippedMethodName = method.getName().substring(GETTER_PREFIX.length());
                log.debug("StrippedMethodName: {} ", strippedMethodName);
            } else if (method.getName().startsWith(BOOLEAN_GETTER_PREFIX)) {
                strippedMethodName = method.getName().substring(BOOLEAN_GETTER_PREFIX.length());
                log.debug("StrippedMethodName: {} ", strippedMethodName);
            }

            Method targetSetter = targetSetterMethods.get(SETTER_PREFIX + strippedMethodName);
            log.debug("targetSetter: {}", targetSetter);
            targetSetter.invoke(targetImplementation, retVal);
        }
    }

    protected  <S, T> void loadSourceGetterMethods(S source) throws IllegalAccessException, InstantiationException {
        log.debug("Loading Source - getters");
        T superclass = (T) source.getClass().getSuperclass().newInstance();
        log.debug("Superclass {} of {} detected.", superclass, source.getClass());

        if (superclass.getClass().getName().equals("java.lang.Object")) {
            log.debug("Superclass of Type {} detected - skipping...", superclass.getClass().getName());
        } else {
            log.debug("Superclass of Type {} detected, scanning...", superclass.getClass());
            loadSourceGetterMethods(superclass);
        }

        for (Method method : source.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith(GETTER_PREFIX) || method.getName().startsWith(BOOLEAN_GETTER_PREFIX)) {
                sourceGetterMethods.add(method);
                log.debug("Adding Method {} to Source-Getters", method);
            } else {
                log.debug("Method {} not recognized as getter - skipping", method.getName());
            }
        }
    }
}
