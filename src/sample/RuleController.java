package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class RuleController {
    public TextArea requestTextField;
    public TableView<VarVal> addRulePremisesTableView;
    public ComboBox<Variable> addRulePremisVarCombo;
    public ComboBox<DomainValue> addRulePremisDomValCombo;
    public ComboBox<Variable> addRuleConcVarCombo;
    public ComboBox<DomainValue> addRuleConcDomValCombo;
    public TextArea ruleDescription;
    public TextField realNameTextFiled;

    public void onAddPremis(ActionEvent actionEvent) {
        if(getAddRulePremisVarCombo().getSelectionModel().isEmpty()
                || getAddRulePremisDomValCombo().getSelectionModel().isEmpty()) return;

        VarVal keyVal=new VarVal(getAddRulePremisVarCombo().getSelectionModel().getSelectedItem(),
                getAddRulePremisDomValCombo().getSelectionModel().getSelectedItem());
        if(getAddRulePremisesTableView().getItems().stream().filter(x->x.equals(keyVal)).count()>0) return;
        getAddRulePremisesTableView().getItems().add(keyVal);
        getRuleDescription().setText(getRuleView());
    }

    public void onOK(ActionEvent actionEvent) {
        if(!validate()) return;
        Conclusion conclusion=new Conclusion(new VarVal(getAddRuleConcVarCombo().getSelectionModel().getSelectedItem(),
                getAddRuleConcDomValCombo().getSelectionModel().getSelectedItem()));
        Rule r=Rule.create(realNameTextFiled.getText(),
                requestTextField.getText(),getAddRulePremisesTableView().getItems(),conclusion,
                Main.getShell().getKnowledgeBase());
        Rules rules=Main.getShell().getKnowledgeBase().getRules();

        if(Main.getController().getRuleOperation().equals("add")){
            if (rules.contains(r)){
                Main.perror("such rule already exists in KB");
                return;
            }
            r.setRealName(realNameTextFiled.getText());

            int index=Main.getController().getRuleTableView().getSelectionModel().isEmpty()
                    ?rules.getList().size()
                    :Main.getController().getRuleTableView().getSelectionModel().getSelectedIndex();
            Main.getShell().getKnowledgeBase().getRules().add(index,r);

            getRequestTextField().clear();
            getAddRulePremisVarCombo().getSelectionModel().clearSelection();
            getAddRuleConcVarCombo().getSelectionModel().clearSelection();
            getAddRulePremisDomValCombo().getSelectionModel().clearSelection();
            getAddRuleConcDomValCombo().getSelectionModel().clearSelection();
            ObservableList<VarVal> list=FXCollections.observableArrayList();
            getAddRulePremisesTableView().setItems(list);
            getRuleDescription().clear();
            getRequestTextField().requestFocus();
        }else if(Main.getController().getRuleOperation().equals("edit")){
            Rule selRule=Main.getController().getRuleTableView().getSelectionModel().getSelectedItem();

            if (rules.contains(r) && !r.equals(selRule)){
                Main.perror("such rule already exists in KB");
                return;
            }

            selRule.setName(r.getName());
            selRule.setRealName(r.getRealName());
            selRule.setReasoning(r.getReasoning());
            selRule.setConclusion(r.getConclusion());
            selRule.setPremises(r.getPremises());
            selRule.setKb(r.getKb());

            //upd
            Main.getController().getRuleContent().setText(r.getRuleView(""));
            Main.getController().getReasoningTextArea().setText(r.getReasoning());

            Main.getController().getRuleTableView().getColumns().get(0).setVisible(false);
            Main.getController().getRuleTableView().getColumns().get(0).setVisible(true);

            ((Stage)getAddRuleConcDomValCombo().getScene().getWindow()).close();
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        ((Stage)getAddRuleConcDomValCombo().getScene().getWindow()).close();
    }


    private boolean validate() {
        if (realNameTextFiled.getText().trim().equals("")
                || realNameTextFiled.getText().equalsIgnoreCase("Введите название правила")) {
            realNameTextFiled.requestFocus();
            return false;
        }
        if (getRequestTextField().getText().trim().equals("")) {
            getRequestTextField().requestFocus();
            return false;
        }
        if(getAddRulePremisesTableView().getItems().size()==0) {
            if(getAddRulePremisVarCombo().getSelectionModel().isEmpty())
                getAddRulePremisVarCombo().show();
            else
                getAddRulePremisDomValCombo().show();
            return false;
        }
        if(getAddRuleConcVarCombo().getSelectionModel().isEmpty()) {
            getAddRuleConcVarCombo().show();
            return false;
        }
        if(getAddRuleConcDomValCombo().getSelectionModel().isEmpty()) {
            getAddRuleConcDomValCombo().show();
            return false;
        }


        return true;
    }




    public TextArea getRequestTextField() {
        return requestTextField;
    }



    public void setRequestTextField(TextArea requestTextField) {
        this.requestTextField = requestTextField;
    }

    public TableView<VarVal> getAddRulePremisesTableView() {
        return addRulePremisesTableView;
    }

    public void setAddRulePremisesTableView(TableView addRulePremisesTableView) {
        this.addRulePremisesTableView = addRulePremisesTableView;
    }

    public ComboBox<Variable> getAddRulePremisVarCombo() {
        return addRulePremisVarCombo;
    }

    public ComboBox<DomainValue> getAddRulePremisDomValCombo() {
        return addRulePremisDomValCombo;
    }

    public ComboBox<Variable> getAddRuleConcVarCombo() {
        return addRuleConcVarCombo;
    }

    public ComboBox<DomainValue> getAddRuleConcDomValCombo() {
        return addRuleConcDomValCombo;
    }

    public TextArea getRuleDescription() {
        return ruleDescription;
    }


    public void onDelPremis(ActionEvent actionEvent) {
        if(getAddRulePremisesTableView().getSelectionModel().isEmpty()) return;
        getAddRulePremisesTableView().getItems().remove(
                getAddRulePremisesTableView().getSelectionModel().getSelectedItem());

        getRuleDescription().setText(getRuleView());
    }

    public String getRuleView(){
        String res="IF\n ";
        String preises=getAddRulePremisesTableView().getItems().stream().map(x->x.varname()+"="+x.varvalue()).collect(Collectors.joining(",\n "));

        String concl=getAddRuleConcVarCombo().getSelectionModel().isEmpty()
                ?""
                :"\nTHEN\n "+getAddRuleConcVarCombo().getSelectionModel().getSelectedItem().getName()+"="+
                getAddRuleConcDomValCombo().getSelectionModel().getSelectedItem().getValue();

        return res+preises+concl;
    }
}
