package de.mzsoftware.spectre;

import org.junit.BeforeClass;
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

    final Logger logger =  LoggerFactory.getLogger(MapperTest.class);

    @BeforeClass
    public static void setup() {
//        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//        StatusPrinter.print(lc);
    }

    @Test
    public void testAnnotationDetection() {
        String testvalue = "this is a Test";
        Mapper mapper = new Mapper();
        MTBeanAWithInterface MTBeanAWithInterface = new MTBeanAWithInterface();
        MTBeanAWithInterface.setTestString(testvalue);
        MTInterface result = mapper.map(MTBeanAWithInterface);
        logger.debug(result.getTestString());
        assertEquals(testvalue, result.getTestString());
    }

    @Test
    public void testSimpleMappingWithoutImplementationClass(){
        String testvalue = "this is a Test";
        Mapper mapper = new Mapper();
        MTBeanAWithoutInterface MTBeanAWithoutInterface = new MTBeanAWithoutInterface();
        MTBeanAWithoutInterface.setTestString(testvalue);
        MTBeanBWithoutInterface result = mapper.map(MTBeanAWithoutInterface);
        logger.debug(result.getTestString());
        assertEquals(testvalue, result.getTestString());
    }

    @Test
    public void testSimpleMappingForClassesExtendingABaseClass(){
        String testvalue = "this is a Test";
        String baseBeanTestValue = "this is another Test";
        Mapper mapper = new Mapper();
        MTBeanAExtending mtBeanAExtending = new MTBeanAExtending();
        mtBeanAExtending.setTestString(testvalue);
        mtBeanAExtending.setBaseBeanTestField(baseBeanTestValue);
        MTBeanBExtending result = mapper.map(mtBeanAExtending);
        logger.debug(result.getTestString());
        logger.debug(result.getBaseBeanTestField());
        assertEquals(testvalue, result.getTestString());
        assertEquals(baseBeanTestValue, result.getBaseBeanTestField());
    }

    @Test
    public void testSimpleMappingForClassesExtendingABaseClassWithInterface(){
        String testvalue = "this is a Test";
        String baseBeanTestValue = "this is another Test";
        Mapper mapper = new Mapper();
        MTBeanAExtendingWithInterface mtBeanAExtendingWithInterface = new MTBeanAExtendingWithInterface();
        mtBeanAExtendingWithInterface.setTestString(testvalue);
        mtBeanAExtendingWithInterface.setBaseBeanTestField(baseBeanTestValue);
        MTInterface result = mapper.map(mtBeanAExtendingWithInterface);
        logger.debug(result.getTestString());
        //logger.debug(result.getBaseBeanTestField());
        assertEquals(testvalue, result.getTestString());
        //assertEquals(baseBeanTestValue, result.getBaseBeanTestField());
    }

    @Test
    public void testMappingUsingProxyClass(){
        String testvalue = "this is a Test";
        Mapper mapper = new Mapper();
        MTBeanImplementingTargetInterface mtBeanImplementingTargetInterface = new MTBeanImplementingTargetInterface();
        mtBeanImplementingTargetInterface.setTestString(testvalue);
        MTTargetInterface result = mapper.map(mtBeanImplementingTargetInterface);
        logger.debug(result.getTestString());
        assertEquals(testvalue, result.getTestString());
    }
}
