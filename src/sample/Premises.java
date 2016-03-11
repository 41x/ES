package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;

/**
 * Created by ваа on 02.03.2016.
 */
public class Premises implements Serializable,Iterable<VarVal> {
    private ArrayList<VarVal> list;
    private Variables variables;

    public Premises() {
        this.list = new ArrayList<>();
    }

    public boolean add(String variable, String value) {
        Variable v = variables.find(variable);
        DomainValue dv = v.getDomain().getValues().find(value);
        return !(v == null || dv == null) && list.add(new VarVal(v, dv));
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }

    public VarVal get(int i){
        return list.get(i);
    }

    public int count(){
        return list.size();
    }

    @Override
    public Iterator<VarVal> iterator() {
        return list.iterator();
    }
}
