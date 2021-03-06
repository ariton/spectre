package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.TargetInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 26.07.13
 * Time: 15:13
 */
public class ProxyMapper extends AbstractMapper {

    private final Logger log = LoggerFactory.getLogger(ProxyMapper.class);


    @Override
    public <S, I> I map(S source) {
        findTargetInterface(source);
        log.debug("TargetInterface: {}", targetInterface);
        log.debug("Interfaces to construct the Proxy: {}",targetInterface.getClass());

        ClassLoader proxyClassLoader = ((Class<I>) targetInterface).getClassLoader();
        targetImplementation =  GenericProxyFactory.getProxy((Class<I>) targetInterface, proxyClassLoader);

        try {
            loadSourceGetterMethods(source);
            loadTargetSetterMethods(targetImplementation);
            doMapping(source, targetImplementation);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        } catch (InstantiationException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e);
        }

        return (I)targetImplementation;
    }

    private <S> void findTargetInterface(S source){
        for(Class clazz : source.getClass().getInterfaces()){
            if(clazz.isAnnotationPresent(TargetInterface.class)){
                targetInterface = clazz;
            }
        }
    }

    void loadTargetSetterMethods(Object target) {
            log.debug("Target is ProxyClass? {}", Proxy.isProxyClass(target.getClass()));
            log.debug("Target {}", target.getClass());
            log.debug("Interface {}",target.getClass().getInterfaces()[0]);
        log.debug("Methods {}", new Object[]{target.getClass().getInterfaces()[0].getDeclaredMethods()});

            for (Method method : target.getClass().getInterfaces()[0].getDeclaredMethods()) {
                if (method.getName().startsWith(Constants.SETTER_PREFIX)) {
                    targetSetterMethods.put(method.getName(), method);
                    log.debug("Adding Method {} to Target-Setters", method);
                }
            }
    }
}
