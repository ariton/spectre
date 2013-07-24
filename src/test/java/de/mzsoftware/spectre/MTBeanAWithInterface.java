package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.MapClass;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 24.07.13
 * Time: 13:47
 */
@MapClass(value = MTInterface.class, implementationClass = MTBeanBWithInterface.class)
public class MTBeanAWithInterface implements MTInterface {

    private String testString;

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }
}
