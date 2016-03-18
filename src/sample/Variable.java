package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Variable implements Serializable {
    private String name;
    private String question;
    private VarType type;
    private Domain domain;

    public Variable(String name, String question, VarType type, Domain domain) {
        setName(name);
        this.question = question;
        this.type = type;
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean usesDomain(Domain d){
        return domain.equals(d);
    }

    public Domain getDomain() {
        return domain;
    }

    public VarType getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

//    public void toSerializable() {
//    }


    public void setQuestion(String question) {
        this.question = question;
    }

    public void setType(VarType type) {
        this.type = type;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }


}
