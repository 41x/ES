package sample;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class MyLIM implements Ilim {
    private WMemory memory;
    private KB kb;

    @Override
    /**
     * Infers a variable
     */
    public void infer(Variable var) {
        // if type is ask
        if (var.getType().equals(VarType.ASK)){
            memory.addFact(var.getName(),Main.getShell().ask(var).getValue());
            return;
        }

        // getting list of rules that have our variable in conclusion
        ArrayList<Rule> rules = getSortedTargetRules(kb.getRules(),var);
        if (rules.size()==0) {
            System.out.println(String.format("no rules found for the Var %s",var.getName()));
            return;
        }
        // iterate rules until our variable gets value
        iterateRules(rules);

        // if type is infer-ask and still no value then ask
        if (var.getType().equals(VarType.INFER_ASK) && memory.getFact(var.getName()).equalsIgnoreCase("")){
            memory.addFact(var.getName(),Main.getShell().ask(var).getValue());
        }
    }

    private void iterateRules(ArrayList<Rule> rules) {
        int i=0;
        Variable targetVar=rules.get(0).getConclusion().getVarval().getVariable();
        String varname=targetVar.getName();

        while (i<rules.size() && memory.getFact(varname).equalsIgnoreCase("")){
            test(rules.get(i));
            i++;
        }
    }


    private void test(Rule rule) {
        Premises premises = rule.getPremises();
        for(VarVal p:premises){
            // if variable was found in another rule
            if (memory.getFact(p.varname()).equalsIgnoreCase(p.varvalue())) continue;
            // if it has wrong value then this rule is broken
            if (!memory.getFact(p.varname()).equalsIgnoreCase("")) return;
            // the variable is not initialized
            infer(p.getVariable());
            // if it has wrong value then this rule is broken
            if (!memory.getFact(p.varname()).equalsIgnoreCase(p.varvalue())) return;
        }
        VarVal varval =rule.getConclusion().getVarval();
        memory.addFact(varval.varname(),varval.varvalue());
    }

    private ArrayList<Rule> getSortedTargetRules(Rules rules, Variable var){
        ArrayList<Rule> list = new ArrayList<>();
        for (Rule r:rules){
            if(r.getConclusion().getVarval().getVariable().getName().equalsIgnoreCase(var.getName())){
                list.add(r);
            }
        }
        Collections.sort(list);
        return list;
    }

    public void setMemory(WMemory memory) {
        this.memory = memory;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }
}
