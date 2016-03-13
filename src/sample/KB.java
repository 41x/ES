package sample;

import java.io.Serializable;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class KB implements Serializable {
    private String name;
    private Domains domains;
    private Variables variables;
    private Rules rules;

// Не использовать!!! KB создается только через KBFactory
    public KB() {
        this.name="default";
        this.domains = new Domains();
        this.variables = new Variables();
        this.rules = new Rules();
    }

    // this is all because observablelist is not serializable
    public KB toSerializableState() {
        domains.toSerializable();
        variables.toSerializable();
        rules.toSerializable();
        return this;
    }

    public KB toWorkingState() {
        domains.toWorkingState();
        variables.toWorkingState();
        rules.toWorkingState();
        return this;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Domains getDomains() {
        return domains;
    }

    public Variables getVariables() {
        return variables;
    }

    public Rules getRules() {
        return rules;
    }
}
