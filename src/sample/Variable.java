package sample;

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
        this.name = name;
        this.question = question;
        this.type = type;
        this.domain = domain;
    }

    public boolean usesDomain(Domain d){
        return domain.equals(d);
    }

    public String getName() {
        return name;
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

    public void toSerializable() {
//        todo
    }
}
