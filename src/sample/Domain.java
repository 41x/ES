package sample;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Domain implements Serializable{
    private transient SimpleStringProperty name = new SimpleStringProperty();
    private DomainValues values;
    private KB kb;
    private String serName;


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

    public void toSerializable() {
        setSerName(getName());
        values.toSerializable();

    }

    public void toWorkingState() {
        name = new SimpleStringProperty(getSerName());
        setSerName("");
        values.toWorkingState();
    }

    public String getSerName() {
        return serName;
    }

    public void setSerName(String serName) {
        this.serName = serName;
    }
}
