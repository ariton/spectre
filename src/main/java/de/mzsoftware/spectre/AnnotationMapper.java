package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.MapClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 26.07.13
 * Time: 15:14
 */
public class AnnotationMapper extends AbstractMapper{

    private final Logger log = LoggerFactory.getLogger(AnnotationMapper.class);


    @Override
    public <S, T, I> I map(S source) {

        Annotation annotation = findMapClassAnnotation(source);
        log.debug("Annotation {}", annotation);
        try {
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
                    loadSourceGetterMethods(source);
                    loadTargetSetterMethods(targetImplementation);
                    doMapping(source, targetImplementation);
                }
            }
        } catch (InstantiationException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e);
        }


        return (I) targetImplementation;
    }



    <S, T> void loadTargetSetterMethods(T target) throws IllegalAccessException, InstantiationException {
        log.debug("Not ProxyMode, detecting superclass...");
        S superclass = (S) target.getClass().getSuperclass().newInstance();
        log.debug("Superclass {} of {} detected.", superclass, target.getClass());

        if (superclass.getClass().getName().equals("java.lang.Object")) {
            log.debug("Superclass of Type {} detected - skipping...", superclass.getClass().getName());
        } else {
            log.debug("Superclass of Type {} detected, scanning...", superclass.getClass());
            this.loadTargetSetterMethods(superclass);
        }

        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith(Constants.SETTER_PREFIX)) {
                targetSetterMethods.put(method.getName(), method);
                log.debug("Adding Method {} to Target-Setters", method);
            }
        }

    }

    private Annotation findMapClassAnnotation(Object source){
         if(source.getClass().isAnnotationPresent(MapClass.class)){
             return source.getClass().getAnnotation(MapClass.class);
         }
        Annotation annotation = findMapClassOnSuperclass(source.getClass().getSuperclass());
        if (null == annotation){
            for(Class<?> clazz : source.getClass().getInterfaces()){
                if (clazz.isAnnotationPresent(MapClass.class)){
                    return clazz.getAnnotation(MapClass.class);
                }
            }
            log.error("@MapClass annotation not found!");
            return null;
        }
        return annotation;
    }

    private Annotation findMapClassOnSuperclass(Class<?> clazz){
        if(null==clazz) {
            return null;
        }
        if(clazz.isAnnotationPresent(MapClass.class)){
            return clazz.getAnnotation(MapClass.class);
        }
        return findMapClassOnSuperclass(clazz.getSuperclass());
    }
}
