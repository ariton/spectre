package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.MapClass;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 24.07.13
 * Time: 19:11
 */
@MapClass(MTBeanAExtending.class)
public class MTBeanBExtending extends MTBaseBean {
    private String testString;

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

}
