package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by ваа on 02.03.2016.
 */
public class Premises implements Serializable,Iterable<VarVal> {
    private transient ObservableList<VarVal> list;
    private Variables variables;
    private List<VarVal> serList;

    public Premises(ObservableList<VarVal> l) {
        this.list = l;
    }

    public boolean add(String variable, String value) {
        Variable v = variables.find(variable);
        DomainValue dv = v.getDomain().getValues().find(value);
        VarVal vv=new VarVal(v, dv);
        if(list.stream().filter(x->x.equals(vv)).count()>0){
            Main.perror("such pair of var and value already exists in the premise ");
            return false;
        }

        return v != null && dv != null && list.add(vv);
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

    public void toSerializable(){
        setSerList(getList().stream().map(x->x).collect(Collectors.toList()));
    }

    public void toWorkingState(){
        setList(FXCollections.observableArrayList(getSerList()));
    }

    public List<VarVal> getSerList() {
        return serList;
    }

    public void setSerList(List<VarVal> serList) {
        this.serList = serList;
    }

    public ObservableList<VarVal> getList() {
        return list;
    }

    public void setList(ObservableList<VarVal> list) {
        this.list = list;
    }

    private boolean contains(VarVal vv){
        return list.stream().filter(x->x.equals(vv)).count()>0;
    }

    public boolean equals(Premises obj) {
        boolean res=list.size()==obj.getList().size() && list.stream().filter(x->obj.contains(x)).count()==list.size();
        return res;
    }
}
