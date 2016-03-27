package sample;

import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by ваа on 02.03.2016.
 */
public class Rule implements Serializable, Comparable<Rule> {
    private String name;
    private String reasoning;
    private Conclusion conclusion;
    private Premises premises;
    private KB kb;

    public Rule(String name, String reasoning, ObservableList<VarVal> premisesList,Conclusion conclusion,KB kb) {
        this.kb=kb;
        this.name = name;
        this.reasoning = reasoning;
        this.conclusion = conclusion;
        this.premises = new Premises(premisesList);
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
        getPremises().toSerializable();
    }

    public void toWorkingState(){
        getPremises().toWorkingState();
    }

    public String getRuleView(){
        String res="IF\n ";
        String premises=getPremises().getList().stream().map(x->x.varname()+"="+x.varvalue()).collect(Collectors.joining(",\n "));

        String concl="\nTHEN\n "+getConclusion().getVarval().varname()+"="+getConclusion().getVarval().varvalue();
        return res+premises+concl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}
