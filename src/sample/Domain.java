package sample;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Domain implements Serializable{
    private SimpleStringProperty name=new SimpleStringProperty();
    private DomainValues values;
    private KB kb;

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public Domain(String name, DomainValues values) {
        this.name.set(name);
        this.values = values;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
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
