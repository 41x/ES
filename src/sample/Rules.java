package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Rules implements Serializable, Iterable<Rule>{
    private ArrayList<Rule> list;
    private KB kb;
    private int count = 0;

    public Rules() {
        this.list = new ArrayList<>();
    }

    public boolean add(Rule r){
        if(find(r.getName())!=null) {
            System.out.println(String.format("Rule name %s should be unique",r.getName()));
            return false;
        }
        r.setKb(kb);
        return list.add(r);
    }

    public boolean remove(String rname) {
        Rule r = find(rname);
        return r != null && list.remove(r);
    }

    public Rule find(String name){
        int i=0;
        while (i<list.size() && !list.get(i).getName().equalsIgnoreCase(name)) i++;
        if(i==list.size()) return null;
        return list.get(i);
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }

    public ArrayList<Rule> getList() {
        return list;
    }

    @Override
    public Iterator<Rule> iterator() {
        return list.iterator();
    }

    public void toSerializable() {
//        todo
    }

    public void toWorkingState() {
//        todo
    }

//    @Override
//    public boolean hasNext() {
//        return count < list.size();
//    }
//
//    @Override
//    public Rule next() {
//        if (count == list.size()) throw new NoSuchElementException();
//        count++;
//        return list.get(count - 1);
//    }
}
