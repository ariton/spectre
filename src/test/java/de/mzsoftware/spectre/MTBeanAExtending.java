package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.MapClass;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 24.07.13
 * Time: 19:10
 */
@MapClass(MTBeanBExtending.class)
public class MTBeanAExtending extends MTBaseBean {

    private String testString;

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

}
