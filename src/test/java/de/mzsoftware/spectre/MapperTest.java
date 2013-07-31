package de.mzsoftware.spectre;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 24.07.13
 * Time: 12:29
 */
public class MapperTest {


    @Rule
    public MethodRule watchman = new TestWatchman() {
        public void starting(FrameworkMethod method) {
            logger.info("{} being run...", method.getName());
        }
    };

    private final Logger logger =  LoggerFactory.getLogger(MapperTest.class);

    @Test
    public void testAnnotationDetection() {
        String testvalue = "this is a Test";
        MTBeanAWithInterface source = new MTBeanAWithInterface();
        Mapper mapper = MapperFactory.getMapper(source);
        source.setTestString(testvalue);
        MTInterface result = mapper.map(source);
        logger.debug(result.getTestString());
        assertEquals(testvalue, result.getTestString());
    }

    @Test
    public void testSimpleMappingWithoutImplementationClass(){
        String testvalue = "this is a Test";
        MTBeanAWithoutInterface source = new MTBeanAWithoutInterface();
        Mapper mapper = MapperFactory.getMapper(source);
        source.setTestString(testvalue);
        MTBeanBWithoutInterface result = mapper.map(source);
        logger.debug(result.getTestString());
        assertEquals(testvalue, result.getTestString());
    }

    @Test
    public void testSimpleMappingForClassesExtendingABaseClass(){
        String testvalue = "this is a Test";
        String baseBeanTestValue = "this is another Test";
        MTBeanAExtending source = new MTBeanAExtending();
        Mapper mapper = MapperFactory.getMapper(source);
        source.setTestString(testvalue);
        source.setBaseBeanTestField(baseBeanTestValue);
        MTBeanBExtending result = mapper.map(source);
        logger.debug(result.getTestString());
        logger.debug(result.getBaseBeanTestField());
        assertEquals(testvalue, result.getTestString());
        assertEquals(baseBeanTestValue, result.getBaseBeanTestField());
    }

    @Test
    public void testSimpleMappingForClassesExtendingABaseClassWithInterface(){
        String testvalue = "this is a Test";
        String baseBeanTestValue = "this is another Test";
        MTBeanAExtendingWithInterface source = new MTBeanAExtendingWithInterface();
        Mapper mapper = MapperFactory.getMapper(source);
        source.setTestString(testvalue);
        source.setBaseBeanTestField(baseBeanTestValue);
        MTInterface result = mapper.map(source);
        logger.debug(result.getTestString());
        //logger.debug(result.getBaseBeanTestField());
        assertEquals(testvalue, result.getTestString());
        //assertEquals(baseBeanTestValue, result.getBaseBeanTestField());
    }

    @Test
    public void testMappingUsingProxyClass(){
        String testvalue = "this is a Test";
        MTBeanImplementingTargetInterface source = new MTBeanImplementingTargetInterface();
        Mapper mapper = MapperFactory.getMapper(source);
        source.setTestString(testvalue);
        MTTargetInterface result = mapper.map(source);
        logger.debug(result.getTestString());
        assertEquals(testvalue, result.getTestString());
    }

    @Test
    public void testMappingFromProxyToRealImplementation() {
        String testvalue = "this is a Test";
        MTBeanImplementingTargetInterface source = new MTBeanImplementingTargetInterface();
        Mapper mapper = MapperFactory.getMapper(source);
        source.setTestString(testvalue);
        MTTargetInterface result = mapper.map(source);
        logger.debug(result.getTestString());
        assertEquals(testvalue, result.getTestString());
        //and the way back
        Mapper mapper1 = MapperFactory.getMapper(result);
        MTTargetInterface newResult = mapper1.map(result);
        logger.debug(newResult.getTestString());
        assertEquals(testvalue, newResult.getTestString());
        assertEquals(source.getClass(), newResult.getClass());

    }

    //TODO: write more tests
}
