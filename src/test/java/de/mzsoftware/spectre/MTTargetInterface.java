package de.mzsoftware.spectre;

import de.mzsoftware.spectre.annotations.MapClass;
import de.mzsoftware.spectre.annotations.TargetInterface;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 25.07.13
 * Time: 01:05
 */
@TargetInterface
@MapClass(value = MTInterface.class, implementationClass = MTBeanImplementingTargetInterface.class)
public interface MTTargetInterface {
    String getTestString();

    void setTestString(String testString);

}
