package sample;

import java.io.Serializable;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class DomainValue implements Serializable{
    private String value;

    public DomainValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
