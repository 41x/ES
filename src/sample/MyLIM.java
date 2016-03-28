package sample;

import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public void infer(Variable var, TextArea logW,String indent) throws InterruptedException {
        // if type is ask
        logW.setText(String.format("\n%sTarget:%s",indent,var.getName()));
        if (var.getType().equals(VarType.ASK)){
            logW.appendText(String.format("\n%s%s TYPE is 'ASK'",indent,var.getName()));

            String answer=Main.getShell().ask(var);
            if(answer.equals("")) {
                System.out.println(indent+"Consultation is stopped");
                return;
            }

            memory.addFact(var.getName(),answer);
            logW.appendText(String.format("\n%s%s=%s",indent,var.getName(),answer));
            return;
        }

        // getting list of rules that have our variable in conclusion
        logW.appendText(String.format("\n%sTrying to get list of rules for Var %s",indent,var.getName()));
        ArrayList<Rule> rules = getSortedTargetRules(kb.getRules(),var);
        if (rules.size()==0) {
            logW.appendText(String.format("\n%sNo rules found for the Var %s",indent,var.getName()));
            return;
        }
        // iterate rules until our variable gets value

        String mess=String.format("\n%sFound rules for Var %s:\n",indent,var.getName());
        mess+=rules.stream().map(Rule::getName).collect(Collectors.joining(String.format("\n%s ",indent)));
        logW.appendText(mess);

        iterateRules(rules,logW,indent);

        // if type is infer-ask and still no value then ask
        if (var.getType().equals(VarType.INFER_ASK) && memory.getFact(var.getName()).equalsIgnoreCase("")){
            logW.appendText(String.format("\n%sCould not infer variable %s, asking...",indent,var.getName()));
            String answer=Main.getShell().ask(var);
            if(answer.equals("")) {
                System.out.println(indent+"Consultation is stopped");
                return;
            }
            memory.addFact(var.getName(),answer);
            logW.appendText(String.format("\n%s%s=%s",indent,var.getName(),answer));        }
    }

    private void iterateRules(ArrayList<Rule> rules, TextArea logW, String indent) throws InterruptedException {
        int i=0;
        Variable targetVar=rules.get(0).getConclusion().getVarval().getVariable();
        String varname=targetVar.getName();

        while (i<rules.size() && memory.getFact(varname).equalsIgnoreCase("")){

            test(rules.get(i),logW,indent);
            i++;
        }
    }


    private void test(Rule rule, TextArea logW, String indent) throws InterruptedException {
        logW.appendText(String.format("\n%sTrying rule %s: %s",indent,rule.getName(),rule.getRuleView()));
        Premises premises = rule.getPremises();
        for(VarVal p:premises){
            // if variable was found in another rule
            if (memory.getFact(p.varname()).equalsIgnoreCase(p.varvalue())) {
                logW.appendText(String.format("\n%s Variable %s was assigned previously",indent,p.varname()));
                continue;
            }
            // if it has wrong value then this rule is broken
            if (!memory.getFact(p.varname()).equalsIgnoreCase("")) {
                logW.appendText(String.format("\n%s Variable %s=%s!=%s the rule is broken",
                        indent,p.varname(),memory.getFact(p.varname()),p.varvalue()));
                return;
            }
            // the variable is not initialized
            infer(p.getVariable(), logW,indent+"\t");
            // if it has wrong value then this rule is broken
            if (!memory.getFact(p.varname()).equalsIgnoreCase(p.varvalue())) {
                logW.appendText(String.format("\n%s Variable %s=%s!=%s the rule is broken",
                        indent,p.varname(),memory.getFact(p.varname()),p.varvalue()));
                return;
            }
        }
        VarVal varval =rule.getConclusion().getVarval();
        memory.addFact(varval.varname(),varval.varvalue());
        logW.appendText(String.format("\n%s Rule %s fired",indent,rule.getName()));
    }

    private ArrayList<Rule> getSortedTargetRules(Rules rules, Variable var){
        List<Rule> list=rules.getList().stream()
                .filter(x->x.getConclusion().getVarval().varname().equals(var.getName()))
                .sorted((x1,x2)->x1.getPremises().getList().size()-x2.getPremises().getList().size())
                .collect(Collectors.toList());

//        ArrayList<Rule> list = new ArrayList<>();
//        for (Rule r:rules){
//            if(r.getConclusion().getVarval().getVariable().getName().equalsIgnoreCase(var.getName())){
//                list.add(r);
//            }
//        }
//        Collections.sort(list);
        return new ArrayList<>(list);
    }

    public void setMemory(WMemory memory) {
        this.memory = memory;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }



}
