package sample;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class DomainValue implements Serializable, Cloneable {
    private transient SimpleStringProperty value = new SimpleStringProperty();
    private String serValue;

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

    @Override
    protected DomainValue clone() throws CloneNotSupportedException {
        return new DomainValue(getValue());
    }

    public void toSerializable() {
        setSerValue(getValue());
    }

    public void toWorkingState() {
        value = new SimpleStringProperty(getSerValue());
        setSerValue("");
    }

    public void setSerValue(String serValue) {
        this.serValue = serValue;
    }

    public String getSerValue() {
        return serValue;
    }

    @Override
    public String toString() {
        return getValue();
    }

    public boolean equals(DomainValue obj) {
        return getValue().equalsIgnoreCase(obj.getValue());
    }
}
