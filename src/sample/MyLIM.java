package sample;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private TreeItem<String> root;

    public MyLIM(WMemory memory, KB kb, Variable var) {
        this.memory = memory;
        this.kb = kb;
        this.var = var;
    }

    public void run(){
        root=new TreeItem<>("root");
        root.setExpanded(true);
        Platform.runLater(() -> Main.getController().getTreeView().setRoot(getRoot()));
        try {
            infer(var,getRoot());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String res=memory.getFact(var.getName());

        Platform.runLater(() -> {
            Main.getController().getTargetTextArea().appendText(res.equals("")
                    ?String.format("Не удалось вывести значение переменной: \'%s\'",var.getName())
                    :"Значение: "+res);
            Main.getController().resetCons();
        });
    }

    /**
     * Infers a variable
     */
    public void infer(Variable var, TreeItem<String> root) throws InterruptedException {
        root.setExpanded(false);
        // if type is ask
        root.getChildren().add(treeItemMake(String.format("Цель: \'%s\'",var.getName())));

        if (var.getType().equals(VarType.ASK)){
            root.getChildren().add(treeItemMake(String.format("%s - запрашиваемая, спрашиваю...",var.getName())));

            String answer=Main.getController().askVar(var);
            if(answer==null || answer.equals("?")) {
                root.getChildren().add(treeItemMake("Консультация отменена!"));
                return;
            }

            memory.addFact(var.getName(),answer);
            root.getChildren().add(treeItemMake(String.format("%s = \'%s\'",var.getName(),answer)));
            Main.getController().appendExplVarTableView(var,new DomainValue(answer));
            return;
        }

        // getting list of rules that have our variable in conclusion
        ArrayList<Rule> rules = getSortedTargetRules(kb.getRules(),var);
        if (rules.size()==0) {
            root.getChildren().add(treeItemMake(String.format("Для \'%s\' правила не найдены",var.getName())));
            if(var.getType().equals(VarType.INFER))
                return;
        }
        // iterate rules until our variable gets value

//        String mess=String.format("\n%sFound rules for Var %s:\n%s",indent,var.getName(),indent);
        if (rules.size()!=0){
            String mess="Правила:\n";
            mess+=rules.stream().map(Rule::getName).collect(Collectors.joining("\n"));
//            root.getChildren().add(treeItemMake(mess));
            iterateRules(rules,root);
        }

        // if type is infer-ask and still no value then ask
        if (var.getType().equals(VarType.INFER_ASK) && memory.getFact(var.getName()).equalsIgnoreCase("")){
            root.getChildren().add(treeItemMake(
                    String.format("Не удалось вывести \'%s\', спрашиваю...",var.getName())));
            String answer=Main.getController().askVar(var);
            if(answer==null || answer.equals("?")) {
                root.getChildren().add(treeItemMake("Консультация отменена!"));
                return;
            }
            memory.addFact(var.getName(),answer);
//            root.getChildren().add(treeItemMake(String.format("%s = \'%s\'",var.getName(),answer)));
            Main.getController().appendExplVarTableView(var,new DomainValue(answer));
        }
    }

    private void iterateRules(ArrayList<Rule> rules, TreeItem<String> root) throws InterruptedException {
        if(rules.size()==0) return;
        int i=0;
        Variable targetVar=rules.get(0).getConclusion().getVarval().getVariable();
        String varname=targetVar.getName();

        while (i<rules.size() && memory.getFact(varname).equalsIgnoreCase("")){

            test(rules.get(i),root);
            i++;
        }
    }


    private void test(Rule rule, TreeItem<String> root) throws InterruptedException {
        TreeItem<String> newroot=treeItemMake(String.format("Правило \'%s\'",rule.getName()));
        root.getChildren().add(newroot);
        Premises premises = rule.getPremises();
        for(VarVal p:premises){
            // if variable was found in another rule
            if (memory.getFact(p.varname()).equalsIgnoreCase(p.varvalue())) {
                root.getChildren().add(treeItemMake(
                        String.format("Переменная \'%s\' выведена ранее",p.varname())));
                continue;
            }
            // if it has wrong value then this rule is broken
            if (!memory.getFact(p.varname()).equalsIgnoreCase("")) {
                String val=memory.getFact(p.varname()).equals("")?"?":memory.getFact(p.varname());
                root.getChildren().add(treeItemMake(
                        String.format("%s = \'%s\' != \'%s\' правило НЕ сработало",
                                p.varname(),val,p.varvalue())));
//                root.getChildren().add(treeItemMake(
//                        String.format("Пояснение: %s",rule.getReasoning())));

                return;
            }
            // the variable is not initialized
            newroot.setExpanded(true);
            infer(p.getVariable(), newroot);
            // if it has wrong value then this rule is broken
            if (!memory.getFact(p.varname()).equalsIgnoreCase(p.varvalue())) {
                String val=memory.getFact(p.varname()).equals("")?"?":memory.getFact(p.varname());
//                root.getChildren().add(treeItemMake(
//                        String.format("%s = \'%s\' != \'%s\' правило НЕ сработало",
//                        p.varname(),val,p.varvalue())));
//                root.getChildren().add(treeItemMake(
//                        String.format("Пояснение: %s",rule.getReasoning())));


                root.getChildren().remove(newroot);
                return;
            }
        }
        VarVal varval =rule.getConclusion().getVarval();
        memory.addFact(varval.varname(),varval.varvalue());
        Main.getController().appendExplVarTableView(varval.getVariable(),varval.getDomainValue());

        root.getChildren().add(treeItemMake(
                String.format("Правило сработало")));
//        root.getChildren().add(treeItemMake(String.format("%s = \'%s\'",varval.varname(),varval.varvalue())));

//        root.getChildren().add(treeItemMake(
//                String.format("Пояснение: %s",rule.getReasoning())));
    }

    private ArrayList<Rule> getSortedTargetRules(Rules rules, Variable var){
        List<Rule> list=rules.getList().stream()
                .filter(x->x.getConclusion().getVarval().varname().equals(var.getName()))
//                .sorted((x1,x2)->x1.getPremises().getList().size()-x2.getPremises().getList().size())
                .collect(Collectors.toList());
        return new ArrayList<>(list);
    }

    public void setMemory(WMemory memory) {
        this.memory = memory;
    }

    public void setKb(KB kb) {
        this.kb = kb;
    }


    public TreeItem<String> getRoot() {
        return root;
    }

    private TreeItem<String> treeItemMake(String msg){
        return new TreeItem<>(msg);
    }
}
