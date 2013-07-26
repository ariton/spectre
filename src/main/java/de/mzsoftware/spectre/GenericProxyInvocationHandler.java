package de.mzsoftware.spectre;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 25.07.13
 * Time: 03:09
 */
public class GenericProxyInvocationHandler<T> implements InvocationHandler{

    private T type;

    public GenericProxyInvocationHandler(T type){
        this.type = type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(type, args);
    }

}
