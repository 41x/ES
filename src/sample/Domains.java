package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Domains implements Serializable{
    private transient ObservableList<Domain> list;
    private KB kb;
    private ArrayList<Domain> serList;

    public Domains() {
        this.list = FXCollections.observableArrayList();
    }

    public boolean add(Domain dom){
        dom.setKb(kb);
        return list.add(dom);
    }

    public boolean remove(Domain d){

        return list.remove(d);
    }

    public boolean contains(Domain dom){
        return list.stream().filter(x->x.equals(dom)).count()>0;
    }

    public ObservableList<Domain> getList() {
        return list;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }

    public void toSerializable() {
        getList().forEach(Domain::toSerializable);
        serList = new ArrayList<>(list);
    }

    public void toWorkingState() {
        setList(FXCollections.observableArrayList(getSerList()));
        getList().forEach(Domain::toWorkingState);
//        setSerList(null);
    }

    public void setList(ObservableList<Domain> list) {
        this.list = list;
    }

    public ArrayList<Domain> getSerList() {
        return serList;
    }

    public void setSerList(ArrayList<Domain> serList) {
        this.serList = serList;
    }
}
