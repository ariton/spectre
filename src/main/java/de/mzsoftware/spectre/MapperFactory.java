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
public class MapperFactory {

    private static Logger log = LoggerFactory.getLogger(MapperFactory.class);

    public static <S, I> Mapper getMapper(S source){
        log.debug("source Instance of Proxy? {}", source instanceof Proxy);
        if(source instanceof Proxy){
            log.info("Proxy instance found, returning AnnotationMapper");
            return new AnnotationMapper();
        }
        if(isProxyUsed(source)){

            return new ProxyMapper();

        }
        log.info("No @TargetInterface annotation found, looking for @MapClass ...");
        return new AnnotationMapper();
    }


    private static <S> boolean isProxyUsed(S source){
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


}
