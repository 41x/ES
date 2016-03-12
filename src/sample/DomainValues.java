package sample;

import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class DomainValues implements Serializable{
    private ObservableList<DomainValue> list;
    private Domain domain;

    public DomainValues(ObservableList<DomainValue> list) {
        this.list = list;
    }

    private boolean add(String dv){
        if(contains(dv)) {
            System.out.println("Values in one domain should be unique");
            return false;
        }
        if (domain.getKb().getVariables().useDomain(domain)){
            System.out.println("This DomainValues are already used, it should not be edited");
            return false;
        }
        return list.add(new DomainValue(dv));
    }

    public boolean contains(String dv){
        int i=0;
        while (i<list.size() && !list.get(i).getValue().equalsIgnoreCase(dv)) i++;
        return i<list.size();
    }

    private boolean remove(DomainValue dv){
        if (domain.getKb().getVariables().useDomain(domain)){
            System.out.println("This DomainValue Domain is already in use, you can't remove it");
            return false;
        }
        return list.remove(dv);
    }

    public DomainValue find(String val){
        int i=0;
        while (i<list.size() && !list.get(i).getValue().equalsIgnoreCase(val)) i++;
        if(i==list.size()) return null;
        return list.get(i);
    }

    public ObservableList<DomainValue> getList() {
        return list;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
}
