package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Rules implements Serializable, Iterable<Rule>{
    private transient ObservableList<Rule> list;
    private KB kb;
    private int count = 0;
    private List<Rule> serList;


    public Rules() {
        this.list = FXCollections.observableArrayList();
    }

    public boolean add(Rule r){
        if(find(r.getName())!=null) {
            System.out.println(String.format("Rule name %s should be unique",r.getName()));
            return false;
        }
        r.setKb(kb);
        return list.add(r);
    }

    public void add(int indx,Rule r){
        if(find(r.getName())!=null) {
            System.out.println(String.format("Rule name %s should be unique",r.getName()));
            return;
        }
        r.setKb(kb);
        list.add(indx,r);
    }

    public boolean remove(String rname) {
        Rule r = find(rname);
        return r != null && list.remove(r);
    }

    public Rule find(String name){
        List<Rule> l=getList().stream().filter(x->x.getName().equals(name)).collect(Collectors.toList());
        return l.size()>0?l.get(0):null;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }

    public ObservableList<Rule> getList() {
        return list;
    }

    @Override
    public Iterator<Rule> iterator() {
        return list.iterator();
    }

    public void toSerializable() {
        getList().forEach(Rule::toSerializable);
        setSerList(getList().stream().map(x->x).collect(Collectors.toList()));
    }

    public void toWorkingState() {
        getSerList().forEach(Rule::toWorkingState);
        setList(FXCollections.observableArrayList(getSerList()));
    }

    public List<Rule> getSerList() {
        return serList;
    }

    public void setSerList(List<Rule> serList) {
        this.serList = serList;
    }

    public void setList(ObservableList<Rule> list) {
        this.list = list;
    }

    public boolean use(Variable v) {
        return getList().stream().filter(x->
                x.getPremises().getList().stream().filter(y->
                        y.getVariable().equals(v)).findFirst().isPresent()
                        || x.getConclusion().getVarval().getVariable().equals(v)).findFirst().isPresent();
    }
    public List<Rule> uses(Variable v) {
        return getList().stream().filter(x->
                x.getPremises().getList().stream().filter(y->
                        y.getVariable().equals(v)).findFirst().isPresent()
                        || x.getConclusion().getVarval().getVariable().equals(v))
                .collect(Collectors.toList());
    }

    public boolean contains(Rule r){
        return list.stream().filter(x->x.equals(r)).count()>0;
    }
}
