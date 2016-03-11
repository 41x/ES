package sample;

/**
 * Created by ваа on 02.03.2016.
 */
public class si {
    private static si ourInstance = new si();

    public static si getInstance() {
        return ourInstance;
    }

    private si() {
    }
}
