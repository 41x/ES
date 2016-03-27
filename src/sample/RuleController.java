package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class RuleController {
    public TextField nameTextField;
    public TextArea requestTextField;
    public TableView<VarVal> addRulePremisesTableView;
    public ComboBox<Variable> addRulePremisVarCombo;
    public ComboBox<DomainValue> addRulePremisDomValCombo;
    public ComboBox<Variable> addRuleConcVarCombo;
    public ComboBox<DomainValue> addRuleConcDomValCombo;
    public TextArea ruleDescription;

    public void onAddPremis(ActionEvent actionEvent) {
        if(getAddRulePremisVarCombo().getSelectionModel().isEmpty() ||
                getAddRulePremisDomValCombo().getSelectionModel().isEmpty()) return;

        VarVal keyVal=new VarVal(getAddRulePremisVarCombo().getSelectionModel().getSelectedItem(),
                getAddRulePremisDomValCombo().getSelectionModel().getSelectedItem());
        getAddRulePremisesTableView().getItems().add(keyVal);

        getRuleDescription().setText(getRuleView());
    }

    public void onOK(ActionEvent actionEvent) {
//        todo
        if(!validate()) return;
        Conclusion conclusion=new Conclusion(new VarVal(getAddRuleConcVarCombo().getSelectionModel().getSelectedItem(),
                getAddRuleConcDomValCombo().getSelectionModel().getSelectedItem()));
        if(Main.getController().getRuleOperation().equals("add")){
            Rule r=new Rule(nameTextField.getText(),
                    requestTextField.getText(),getAddRulePremisesTableView().getItems(),conclusion,
                    Main.getShell().getKnowledgeBase());
            Main.getShell().getKnowledgeBase().getRules().add(r);

            getNameTextField().clear();
            getRequestTextField().clear();
            getAddRulePremisVarCombo().getSelectionModel().clearSelection();
            getAddRuleConcVarCombo().getSelectionModel().clearSelection();
            getAddRulePremisDomValCombo().getSelectionModel().clearSelection();
            getAddRuleConcDomValCombo().getSelectionModel().clearSelection();
            ObservableList<VarVal> list=FXCollections.observableArrayList();
            getAddRulePremisesTableView().setItems(list);
            getRuleDescription().clear();
            getNameTextField().requestFocus();
        }else if(Main.getController().getRuleOperation().equals("edit")){
            Rule selRule=Main.getController().getRuleTableView().getSelectionModel().getSelectedItem();
            selRule.setName(getNameTextField().getText());
            selRule.setReasoning(getRequestTextField().getText());
            selRule.getPremises().setList(getAddRulePremisesTableView().getItems());

            selRule.getConclusion().setVarval(new VarVal(getAddRuleConcVarCombo().getSelectionModel()
                    .getSelectedItem(),getAddRuleConcDomValCombo().getSelectionModel().getSelectedItem()));

            Main.getController().getRuleContent().setText(selRule.getRuleView());
            Main.getController().getReasoningTextArea().setText(selRule.getReasoning());

            Main.getController().getRuleTableView().getColumns().get(0).setVisible(false);
            Main.getController().getRuleTableView().getColumns().get(0).setVisible(true);

            ((Stage)getNameTextField().getScene().getWindow()).close();
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        ((Stage)getNameTextField().getScene().getWindow()).close();
    }


    private boolean validate() {
        if (!getNameTextField().getText().matches("[a-zA-Zа-яА-Я0-9]+(\\s[a-zA-Zа-яА-Я0-9]+)*")) {
            getNameTextField().requestFocus();
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



    public TextField getNameTextField() {
        return nameTextField;
    }

    public TextArea getRequestTextField() {
        return requestTextField;
    }

    public void setNameTextField(TextField nameTextField) {
        this.nameTextField = nameTextField;
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
