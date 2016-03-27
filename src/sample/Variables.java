package sample;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Variables implements Serializable{
    private transient ObservableList<Variable> list;
    private KB kb;
    private ArrayList<Variable> serList;

    public Variables() {
        this.list = FXCollections.observableArrayList();
    }

    public boolean add(String name, String question, VarType type, Domain domain) {
        if(this.contains(name)){
            System.out.println("variable should be unique");
            return false;
        }

        list.add(new Variable(name, question, type, domain));
        return true;
    }

    public boolean remove(Variable v){
        if(kb.getRules().use(v)){
            System.out.println("The variable is used in rules");
            return false;
        }
        return getList().remove(v);



    }

    public boolean useDomain(Domain d) {
        return list != null && list.stream().filter(v -> v.usesDomain(d)).count() > 0;
    }



    public boolean contains(String varname){
        int i=0;
        while (i<list.size() && !list.get(i).getName().equals(varname)) i++;
        return i < list.size();
    }

    public Variable find(String varname){
        List<Variable> l=getList().stream().filter(v->v.getName()==varname).collect(Collectors.toList());
        return l.size()>0?l.get(0):null;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }

    public ObservableList<Variable> getList() {
        return list;
    }

    public void toSerializable() {
        setSerList(new ArrayList<>(list));
    }

    public void toWorkingState() {
        setList(FXCollections.observableArrayList(getSerList()));
        getList().addListener(new ListChangeListener<Variable>() {
            @Override
            public void onChanged(Change<? extends Variable> c) {
                System.out.println("list was changed");
            }
        });

    }

    public ArrayList<Variable> getSerList() {
        return serList;
    }

    public void setSerList(ArrayList<Variable> serList) {
        this.serList = serList;
    }

    public void setList(ObservableList<Variable> list) {
        this.list = list;
    }


}
