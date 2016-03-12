package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Domains implements Serializable{
    private ObservableList<Domain> list;
    private KB kb;

    public Domains() {
        this.list = FXCollections.observableArrayList();
    }

    public boolean add(String name, DomainValues values){
        if (this.contains(name)){
            System.out.println("Domain name should be unique");
            return false;
        }

        Domain d=new Domain(name,values);
        d.setKb(kb);
        values.setDomain(d);
        return list.add(d);
    }

    public boolean remove(Domain d){
        if(kb.getVariables().useDomain(d))
            return list.remove(d);
        System.out.println("This domain is used in variables");
        return false;
    }

    public boolean contains(String domname){
        int i=0;
        while (i<list.size() && !list.get(i).getName().equals(domname)) i++;
        return i < list.size();
    }

    public ObservableList<Domain> getList() {
        return list;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }
}
