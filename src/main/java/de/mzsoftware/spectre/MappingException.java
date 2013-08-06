package de.mzsoftware.spectre;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 24.07.13
 * Time: 17:17
 */
class MappingException extends RuntimeException {

    public MappingException(Throwable t){
        super(t);
    }

    public MappingException(){
        super();
    }
}
