package sample;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class DomainValue implements Serializable{
    private SimpleStringProperty value=new SimpleStringProperty();

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getValue() {
        return value.get();
    }

    public DomainValue(String value) {
        this.value.set(value);
    }

}
