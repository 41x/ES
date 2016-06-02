package sample;

import java.io.Serializable;
import java.io.StringReader;

/**
 * Created by ваа on 02.03.2016.
 */
public class VarVal implements Serializable {
    private Variable variable;
    private DomainValue domainValue;

    public VarVal(Variable variable, DomainValue domainValue) {
        this.variable = variable;
        this.domainValue = domainValue;
    }

    public String varname(){
        return variable.getName();
    }
    public String varvalue(){
        return domainValue.getValue();
    }

    public Variable getVariable() {
        return variable;
    }

    public DomainValue getDomainValue() {
        return domainValue;
    }

    @Override
    public VarVal clone(){
        return new VarVal(getVariable(),getDomainValue());
    }

    public boolean equals(VarVal obj) {
        boolean res= variable.equals(obj.getVariable()) && domainValue.equals(obj.getDomainValue());
        return res;
    }
}