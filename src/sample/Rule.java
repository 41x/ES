package sample;

import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by ваа on 02.03.2016.
 */
public class Rule implements Serializable, Comparable<Rule> {
    private String realName;
    private String name;
    private String reasoning;
    private Conclusion conclusion;
    private Premises premises;
    private KB kb;

    static Rule create(String realName, String reasoning, ObservableList<VarVal> premisesList, Conclusion conclusion, KB kb){
        Rule r=new Rule(realName,reasoning, premisesList, conclusion, kb);
        r.setName(r.getRuleView2());
        return r;
    }

    private Rule(String realName, String reasoning, ObservableList<VarVal> premisesList, Conclusion conclusion, KB kb) {
        this.realName=realName;
        this.kb=kb;
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
        updateName();
        return name;
    }

    public void updateName(){
        this.setName(this.getRuleView2());
    }

    String getReasoning() {
        return reasoning;
    }

    Conclusion getConclusion() {
        return conclusion;
    }

    Premises getPremises() {
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

    String getRuleView(String indent){
        String res="IF\n "+indent;
        String premises=getPremises().getList().stream().map(x->x.varname()+"="+x.varvalue()).collect(Collectors.joining(",\n "+indent));

        String concl="\n"+indent+"THEN\n "+indent+getConclusion().getVarval().varname()+"="+getConclusion().getVarval().varvalue();
        return res+premises+concl;
    }

    String getRuleView2(){
        String res="IF ";
        String premises=getPremises().getList().stream().map(x->x.varname()+" = "+x.varvalue()).collect(Collectors.joining(" and "));

        String concl=" THEN "+getConclusion().getVarval().varname()+" = "+getConclusion().getVarval().varvalue();
        return res+premises+concl;
    }

    void setName(String name) {
        this.name = name;
    }

    void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

//    public boolean equals(Rule obj) {
//        boolean res= conclusion.equals(obj.getConclusion()) && premises.equals(obj.getPremises());
//        return res;
//    }
    public boolean equals(Rule obj) {
        return realName.equalsIgnoreCase(obj.getRealName());
    }

    public boolean uses(Variable var){
        return getPremises().getList().stream().filter(x->x.getVariable().equals(var)).count()>0
                || getConclusion().getVarval().getVariable().equals(var);
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setPremises(Premises premises) {
        this.premises = premises;
    }
}
