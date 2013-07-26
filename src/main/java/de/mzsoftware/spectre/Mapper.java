package de.mzsoftware.spectre;

/**
 * Created with IntelliJ IDEA.
 * User: ariton
 * Date: 26.07.13
 * Time: 15:03
 */
public interface Mapper {

    public <S, T, I> I map(S source);

}
