package de.mzsoftware.spectre;

/**
 * Interface for Mappers to be Used
 *
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 26.07.13
 * Time: 15:03
 */
public interface Mapper {

    public <S, I> I map(S source);

}
