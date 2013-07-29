package de.mzsoftware.spectre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 25.07.13
 * Time: 03:09
 */
public class GenericProxyInvocationHandler implements InvocationHandler{

    private Logger log = LoggerFactory.getLogger(GenericProxyInvocationHandler.class);

    private Object object;

    public GenericProxyInvocationHandler(Object object){
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.debug("Method {}", method.getName());
        log.debug("Proxy {}", proxy.getClass().getName());
        log.debug("Object {}", object.getClass().getName());
        return method.invoke(object, args);
    }

}
