package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.MapClass;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 24.07.13
 * Time: 21:04
 */
@MapClass(value = MTInterface.class, implementationClass = MTBeanAExtendingWithInterface.class)
public class MTBeanBExtendingWithInterface extends MTBaseBean implements MTInterface{
    private String testString;

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

}
