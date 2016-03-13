package sample;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by ваа on 02.03.2016.
 */
public class Rule implements Serializable, Comparable<Rule> {
    private String name;
    private String reasoning;
    private Conclusion conclusion;
    private Premises premises;
    private KB kb;

    public Rule(String name, String reasoning) {
        this.name = name;
        this.reasoning = reasoning;
        this.conclusion = conclusion;
        this.premises = new Premises();
        premises.setVariables(kb.getVariables());

    }

    public void setConclusion(Conclusion conclusion) {
        this.conclusion = conclusion;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }

    public KB getKb() {
        return kb;
    }


    public String getName() {
        return name;
    }

    public String getReasoning() {
        return reasoning;
    }

    public Conclusion getConclusion() {
        return conclusion;
    }

    public Premises getPremises() {
        return premises;
    }

    @Override
    public int compareTo(Rule o) {
        return premises.count() - o.getPremises().count();
    }

    public void toSerializable() {
//        todo
    }

//    @Override
//    public int compare(Rule o1, Rule o2) {
//        return o1.getPremises().count() - o2.getPremises().count();
//    }
}
