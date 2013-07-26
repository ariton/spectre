package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.MapClass;
import de.mzsoftware.spectre.annotations.TargetInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
    private boolean isProxyMode = false;
    private Logger log = LoggerFactory.getLogger(Mapper.class);

    public <S, T, I> I map(S source) {
        log.debug("Mapper.map()");

        T targetImplementation = null;
        I targetInterface = null;

        Class[] interfaces = source.getClass().getInterfaces();
        for (Class iface : interfaces) {
            log.debug("Detecting @TargetInterface annotation for proxy generation...");
            if (iface.isAnnotationPresent(TargetInterface.class)) {
                log.debug("@TargetInterface annotation found at Interface {} ", iface);
                targetInterface = (I) iface;
                log.debug("TargetInterface: {}", targetInterface);
                log.debug("Interfaces to construct the Proxy: {}",targetInterface.getClass());

                ClassLoader proxyClassLoader = ((Class<I>) targetInterface).getClassLoader();
                targetImplementation = (T) GenericProxyFactory.getProxy((Class<I>) targetInterface, proxyClassLoader);
                log.debug("Proxy Instance {} created.", targetImplementation);
                this.isProxyMode = true;
                break;
            }
        }

        try {
            if (!isProxyMode) {
                log.info("No @TargetInterface annotation found, looking for @MapClass ...");
                Annotation annotation = source.getClass().getAnnotation(MapClass.class);
                log.debug("Annotation {}", annotation);
                if (null != annotation) {
                    if (annotation instanceof MapClass) {
                        MapClass mapClass = (MapClass) annotation;
                        Object implementationClass = mapClass.implementationClass().newInstance();
                        if (!(implementationClass instanceof NullMarker)) {

                            targetImplementation = (T) mapClass.implementationClass().newInstance();
                            targetInterface = (I) mapClass.value();
                            log.debug("ImplementationClass given, returning \"{}\" as Type and using \"{}\" as implementation.",
                                    targetInterface,
                                    targetImplementation.getClass().getName());

                        } else {

                            targetImplementation = (T) mapClass.value().newInstance();
                            targetInterface = (I) targetImplementation;
                            log.debug("No ImplementationClass given, returning \"{}\" as Type.", targetInterface.getClass().getName());

                        }
                    }
                }
            }

            List<Method> sourceGetterMethods = new ArrayList<Method>();
            HashMap<String, Method> targetSetterMethods = new HashMap<String, Method>();

            loadSourceGetterMethods(source, sourceGetterMethods);

            loadTargetSetterMethods(targetImplementation, targetSetterMethods);

            doMapping(source, targetImplementation, sourceGetterMethods, targetSetterMethods);
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
        return (I) targetImplementation;
    }

    private <S, T> void doMapping(S source, T targetImplementation, List<Method> sourceGetterMethods, HashMap<String, Method> targetSetterMethods) throws IllegalAccessException, InvocationTargetException {
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

    private <T, S> void loadTargetSetterMethods(T target, HashMap<String, Method> targetSetterMethods) throws IllegalAccessException, InstantiationException {
        log.debug("Loading Target - setters...");
        if (!isProxyMode) {
            log.debug("Not ProxyMode, detecting superclass...");
            S superclass = (S) target.getClass().getSuperclass().newInstance();
            log.debug("Superclass {} of {} detected.", superclass, target.getClass());

            if (superclass.getClass().getName().equals("java.lang.Object")) {
                log.debug("Superclass of Type {} detected - skipping...", superclass.getClass().getName());
            } else {
                log.debug("Superclass of Type {} detected, scanning...", superclass.getClass());
                loadTargetSetterMethods(superclass, targetSetterMethods);
            }
        }
        log.debug("...");
        if(isProxyMode){

            log.debug("Target is ProxyClass? {}", Proxy.isProxyClass(target.getClass()));
            log.debug("Target {}", target.getClass());
            log.debug("Interface {}",target.getClass().getInterfaces()[0]);
            log.debug("Methods {}",target.getClass().getInterfaces()[0].getDeclaredMethods());

            for (Method method : target.getClass().getInterfaces()[0].getDeclaredMethods()) {
                if (method.getName().startsWith(SETTER_PREFIX)) {
                    targetSetterMethods.put(method.getName(), method);
                    log.debug("Adding Method {} to Target-Setters", method);
                }
            }
        } else {
            for (Method method : target.getClass().getInterfaces()[0].getDeclaredMethods()) {
                if (method.getName().startsWith(SETTER_PREFIX)) {
                    targetSetterMethods.put(method.getName(), method);
                    log.debug("Adding Method {} to Target-Setters", method);
                }
            }
        }
    }

    private <S, T> void loadSourceGetterMethods(S source, List<Method> sourceGetterMethods) throws IllegalAccessException, InstantiationException {
        log.debug("Loading Source - getters");
        T superclass = (T) source.getClass().getSuperclass().newInstance();
        log.debug("Superclass {} of {} detected.", superclass, source.getClass());

        if (superclass.getClass().getName().equals("java.lang.Object")) {
            log.debug("Superclass of Type {} detected - skipping...", superclass.getClass().getName());
        } else {
            log.debug("Superclass of Type {} detected, scanning...", superclass.getClass());
            loadSourceGetterMethods(superclass, sourceGetterMethods);
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
