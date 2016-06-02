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

    public boolean add(Variable var) {
        return list.add(var);
    }
    public void add(int ind,Variable var) {
        list.add(ind,var);
    }

    public boolean remove(Variable v){
        if(kb.getRules().use(v)){
            Main.perror("The variable is used in rules");
            return false;
        }
        return getList().remove(v);



    }

    public String useDomain(Domain d) {
        return list.stream().filter(v -> v.usesDomain(d)).map(Variable::getName).collect(Collectors.joining("\n"));
//        return list != null && list.stream().filter(v -> v.usesDomain(d)).count() > 0;
    }
    public List<Variable> usesDomain(Domain d) {
        return list.stream().filter(v -> v.usesDomain(d)).collect(Collectors.toList());
    }

    public boolean containsName(String varname){
        int i=0;
        while (i<list.size() && !list.get(i).getName().equalsIgnoreCase(varname)) i++;
        return i < list.size();
    }

    public boolean contains(Variable var){
        return list.stream().filter(x->x.equals(var)).count()>0;
    }

    public Variable find(String varname){
        List<Variable> l=getList().stream().filter(v->v.getName().equalsIgnoreCase(varname))
                .collect(Collectors.toList());
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
