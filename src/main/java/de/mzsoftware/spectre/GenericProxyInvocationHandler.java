package de.mzsoftware.spectre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 25.07.13
 * Time: 03:09
 */
public class GenericProxyInvocationHandler implements InvocationHandler{

    private Logger log = LoggerFactory.getLogger(GenericProxyInvocationHandler.class);

    private Map<String, MappingContainer> mappedData = new ConcurrentHashMap<String, MappingContainer>();


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.debug("Method {}", method.getName());
        String memberName = "";
        if(method.getName().startsWith(Constants.GETTER_PREFIX)){
            memberName = method.getName().substring(Constants.GETTER_PREFIX.length());
            return invokeGetter(memberName);
        }else if(method.getName().startsWith(Constants.BOOLEAN_GETTER_PREFIX)){
            memberName = method.getName().substring(Constants.BOOLEAN_GETTER_PREFIX.length());
            return invokeGetter(memberName);
        } else{
            memberName = method.getName().substring(Constants.SETTER_PREFIX.length());
            invokeSetter(memberName, args);
            return Void.TYPE;
        }
    }

    private<T>  T invokeGetter(String memberName){
        MappingContainer mappingContainer = mappedData.get(memberName);
        //TODO: check if generics used the right way , (looks creepy)
        T type = (T)mappingContainer.getType();
        return (T) mappingContainer.getValue();
    }

    private void invokeSetter(String memberName, Object[] args){
        for (Object arg : args){
            mappedData.put(memberName, new MappingContainer(arg.getClass(), arg));
        }
    }



}
