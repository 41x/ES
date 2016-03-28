package sample;

import java.util.HashMap;
import java.util.StringJoiner;

/**
 * Created by ваа on 02.03.2016.
 */
public class Facts {
    private HashMap<String,String> facts;

    public Facts() {
        this.facts = new HashMap<>();
    }

    public String getFact(String variable){
        return facts.getOrDefault(variable,"");
    }

    public void addFact(String var, String val){
        facts.put(var,val);
    }




}
