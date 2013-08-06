package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.TargetInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 26.07.13
 * Time: 15:00
 */
class MapperFactory {

    private static final Logger log = LoggerFactory.getLogger(MapperFactory.class);

    /**
     *
     * @param source
     * @param <S>
     * @return
     */
    public static <S> Mapper getMapper(S source){
        log.debug("source Instance of Proxy? {}", source instanceof Proxy);
        if(source instanceof Proxy){
            log.info("Proxy instance found, returning AnnotationMapper");
            return new AnnotationMapper();
        }
        if(mustReturnProxyInstance(source)){

            return new ProxyMapper();

        }
        if(isAnnotationBased(source)){
            log.info("AnnotationBased Mapper required.");
            return new AnnotationMapper();
        }
        log.info("No Marker Annotations Found, don't know what to do!");
        throw new MappingException();
    }


    private static <S> boolean mustReturnProxyInstance(S source){
        Class[] interfaces = source.getClass().getInterfaces();
        for (Class iface : interfaces) {
            log.debug("Detecting @TargetInterface annotation for proxy generation...");
            if (iface.isAnnotationPresent(TargetInterface.class)) {
                log.debug("@TargetInterface annotation found at Interface {} ", iface);
                return true;
            }
        }
        return false;
    }

    private static <S> boolean isAnnotationBased(S source){

        return true;
    }


}
