package sample;

import java.io.Serializable;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Domain implements Serializable{
    private String name;
    private DomainValues values;
    private KB kb;

    public Domain(String name, DomainValues values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public DomainValues getValues() {
        return values;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }

    public KB getKb() {
        return kb;
    }
}
