package de.mzsoftware.spectre;

import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 25.07.13
 * Time: 13:31
 *
 * found at: http://www.ibm.com/developerworks/java/library/j-jtp08305/index.html
 */
public class GenericProxyFactory {

    public static <T> T getProxy(Class<T> iface, final ClassLoader classLoader) {
        return (T)
                Proxy.newProxyInstance(classLoader, new Class[]{iface}, new GenericProxyInvocationHandler<T>((T)iface));
    }
}
