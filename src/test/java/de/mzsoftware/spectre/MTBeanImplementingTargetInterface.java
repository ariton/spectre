package de.mzsoftware.spectre;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 25.07.13
 * Time: 01:06
 */
public class MTBeanImplementingTargetInterface implements MTTargetInterface {

    protected String testString;

    @Override
    public String getTestString() {
        return testString;
    }

    @Override
    public void setTestString(String testString) {
        this.testString = testString;
    }
}
