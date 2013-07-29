package de.mzsoftware.spectre;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 29.07.13
 * Time: 14:52
 */
public class MappingContainer {

    private Class type;
    private Object value;

    public MappingContainer(Class<?> type, Object value){
        this.type = type;
        this.value = value;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
