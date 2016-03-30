package sample;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RunnableFuture;
import java.util.stream.Collectors;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class MyLIM implements Runnable {
    private WMemory memory;
    private KB kb;
    private Variable var;

    public MyLIM(WMemory memory, KB kb, Variable var) {
        this.memory = memory;
        this.kb = kb;
        this.var = var;
    }

    public void run(){
        try {
            infer(var,"");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String res=memory.getFact(var.getName());
        Main.getController().getTargetTextArea().appendText(res.equals("")
                ?String.format("Given the information, value of the variable \'%s\' is unknown :(",var.getName())
                :"Result: "+res);

        Platform.runLater(() -> Main.getController().resetCons());
    }

    /**
     * Infers a variable
     */
    public void infer(Variable var, String indent) throws InterruptedException {
        // if type is ask
        log(String.format("%sTarget variable: \'%s\'\n",indent,var.getName()));
        if (var.getType().equals(VarType.ASK)){
            log(String.format("%s%s TYPE is 'ASK', asking...\n",indent,var.getName()));

            String answer=Main.getController().askVar(var);
            if(answer==null || answer.equals("?")) {
                log(indent+"Consultation was stopped!\n");
                return;
            }

            memory.addFact(var.getName(),answer);
            log(String.format("%s%s=\'%s\'\n",indent,var.getName(),answer));
            return;
        }

        // getting list of rules that have our variable in conclusion
//        log(String.format("\n%sTrying to get list of rules for Var %s",indent,var.getName()));
        ArrayList<Rule> rules = getSortedTargetRules(kb.getRules(),var);
        if (rules.size()==0) {
            log(String.format("%sNo rules found for the Var \'%s\'\n",indent,var.getName()));
            if(var.getType().equals(VarType.INFER))
                return;
        }
        // iterate rules until our variable gets value

//        String mess=String.format("\n%sFound rules for Var %s:\n%s",indent,var.getName(),indent);
        if (rules.size()!=0){
            String mess=String.format("%sRules:\n",indent);
            mess+=indent+rules.stream().map(Rule::getName).collect(Collectors.joining(String.format("\n%s ",indent)));
            log(mess+"\n");
            iterateRules(rules,indent);
        }

        // if type is infer-ask and still no value then ask
        if (var.getType().equals(VarType.INFER_ASK) && memory.getFact(var.getName()).equalsIgnoreCase("")){
            log(String.format("%sCould not infer variable \'%s\', asking...\n",indent,var.getName()));
            String answer=Main.getController().askVar(var);
            if(answer==null || answer.equals("?")) {
                log(indent+"Consultation is stopped!\n");
                return;
            }
            memory.addFact(var.getName(),answer);
            log(String.format("%s%s=\'%s\'\n",indent,var.getName(),answer));        }
    }

    private void iterateRules(ArrayList<Rule> rules, String indent) throws InterruptedException {
        if(rules.size()==0) return;
        int i=0;
        Variable targetVar=rules.get(0).getConclusion().getVarval().getVariable();
        String varname=targetVar.getName();

        while (i<rules.size() && memory.getFact(varname).equalsIgnoreCase("")){

            test(rules.get(i),indent);
            i++;
        }
    }


    private void test(Rule rule, String indent) throws InterruptedException {
        log(String.format("%sTrying rule \'%s\':\n%s%s\n",indent,rule.getName(),indent,rule.getRuleView(indent)));
        Premises premises = rule.getPremises();
        for(VarVal p:premises){
            // if variable was found in another rule
            if (memory.getFact(p.varname()).equalsIgnoreCase(p.varvalue())) {
                log(String.format("%s Variable \'%s\' was assigned previously\n",indent,p.varname()));
                continue;
            }
            // if it has wrong value then this rule is broken
            if (!memory.getFact(p.varname()).equalsIgnoreCase("")) {
                String val=memory.getFact(p.varname()).equals("")?"?":memory.getFact(p.varname());
                log(String.format("%s Variable %s=\'%s\', expected:\'%s\' the rule is broken\n",
                        indent,p.varname(),val,p.varvalue()));
                return;
            }
            // the variable is not initialized
            infer(p.getVariable(), indent+"\t");
            // if it has wrong value then this rule is broken
            if (!memory.getFact(p.varname()).equalsIgnoreCase(p.varvalue())) {
                String val=memory.getFact(p.varname()).equals("")?"?":memory.getFact(p.varname());
                log(String.format("%s Variable %s=\'%s\', expected:\'%s\' the rule is broken\n",
                        indent,p.varname(),val,p.varvalue()));
                return;
            }
        }
        VarVal varval =rule.getConclusion().getVarval();
        memory.addFact(varval.varname(),varval.varvalue());
        log(String.format("%s Rule \'%s\' fired\n",indent,rule.getName()));
    }

    private ArrayList<Rule> getSortedTargetRules(Rules rules, Variable var){
        List<Rule> list=rules.getList().stream()
                .filter(x->x.getConclusion().getVarval().varname().equals(var.getName()))
                .sorted((x1,x2)->x1.getPremises().getList().size()-x2.getPremises().getList().size())
                .collect(Collectors.toList());
        return new ArrayList<>(list);
    }

    public void setMemory(WMemory memory) {
        this.memory = memory;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }


    private void log(String m){
        Platform.runLater(() -> {
            Main.getController().getConsTabLogTextArea().appendText(m);
        });
    }

}
