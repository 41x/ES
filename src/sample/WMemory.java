package sample;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class WMemory {
    private Facts facts;

    public WMemory() {
        this.facts = new Facts();
    }

    public String getFact(String key){
        return facts.getFact(key);
    }

    public void addFact(String var,String val){
        facts.addFact(var,val);
    }

}
