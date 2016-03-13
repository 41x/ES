package sample;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Variables implements Serializable{
    private ArrayList<Variable> list;
    private KB kb;

    public Variables() {
        this.list = new ArrayList<>();
    }

    public boolean add(String name, String question, VarType type, Domain domain) {
        if(this.contains(name)){
            System.out.println("variable should be unique");
            return false;
        }

        list.add(new Variable(name, question, type, domain));
        return true;
    }

    public boolean remove(String varname){
        Variable v=find(varname);
        return v != null && list.remove(v);
    }

    public boolean useDomain(Domain d){
        int i=0;
        while (i<list.size() && !list.get(i).usesDomain(d)) i++;
        return i<list.size();
    }



    public boolean contains(String varname){
        int i=0;
        while (i<list.size() && !list.get(i).getName().equals(varname)) i++;
        return i < list.size();
    }

    public Variable find(String varname){
        int i=0;
        while (i<list.size() && !list.get(i).getName().equalsIgnoreCase(varname)) i++;
        if(i == list.size()) return null;
        return list.get(i);
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }

    public ArrayList<Variable> getList() {
        return list;
    }

    public void toSerializable() {
//        todo
    }

    public void toWorkingState() {
//        todo
    }
}
