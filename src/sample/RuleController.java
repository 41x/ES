package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.*;

public class RuleController {
    public TextField nameTextField;
    public TextArea requestTextField;
    public TableView<VarVal> addRulePremisesTableView;
    public ComboBox<Variable> addRulePremisVarCombo;
    public ComboBox<DomainValue> addRulePremisDomValCombo;
    public ComboBox<Variable> addRuleConcVarCombo;
    public ComboBox<DomainValue> addRuleConcDomValCombo;
    public TextArea ruleDescription;


    public void onAdd(ActionEvent actionEvent) {
        Main.getController().onAddDomain();
    }

    public void onOK(ActionEvent actionEvent) {
//        todo
//        if(!validate()) return;
//
//        if(Main.getController().getVariableOperation().equals("add")){
//            Main.getShell().getKnowledgeBase().getVariables().add(
//                    getNameTextField().getText(),
//                    requestTextField.getText(),
//                    radioInfer.isSelected()?VarType.INFER:(radioRequest.isSelected()?VarType.ASK:VarType.INFER_ASK),
//                    getDomainCombo().getSelectionModel().getSelectedItem()
//            );
//        }else if(Main.getController().getVariableOperation().equals("edit")){
//            if(!validate()) return;
//            Variable selectedVar=Main.getController().getVarTableView().getSelectionModel().getSelectedItem();
//            selectedVar.setName(getNameTextField().getText());
//            selectedVar.setQuestion(getRequestTextField().getText());
//            selectedVar.setType(getRadioInfer().isSelected()?
//                    VarType.INFER:(getRadioRequest().isSelected()?VarType.ASK:VarType.INFER_ASK));
//            selectedVar.setDomain(getDomainCombo().getSelectionModel().getSelectedItem());
//
//            Main.getController().getVarTabDomValTableView()
//                    .setItems(selectedVar.getDomain().getValues().getList());
//            Main.getController().getReqTextArea()
//                    .setText(selectedVar.getQuestion());
//            Main.getController().getVarTableView().getColumns().get(0).setVisible(false);
//            Main.getController().getVarTableView().getColumns().get(0).setVisible(true);
//
//            ((Stage)getNameTextField().getScene().getWindow()).close();
//        }
    }

    public void onCancel(ActionEvent actionEvent) {
//        ((Stage)getDomainCombo().getScene().getWindow()).close();
    }


    public void onEdit(ActionEvent actionEvent) {
//        if(getDomainCombo().getSelectionModel().isEmpty()) return;
//        Main.getController().getDomainTableView().getSelectionModel().select(
//                getDomainCombo().getSelectionModel().getSelectedItem());
//        Main.getController().onEditDomain();
    }

    private boolean validate() {
//        if (!getNameTextField().getText().matches("[a-zA-Zа-яА-Я0-9]+(\\s[a-zA-Zа-яА-Я0-9]+)*")) {
//            getNameTextField().requestFocus();
//            return false;
//        }
//        if (getRequestTextField().getText().trim().equals("")) {
//            getRequestTextField().requestFocus();
//            return false;
//        }
//        if(getDomainCombo().getSelectionModel().isEmpty()) {
//            getDomainCombo().show();
//            return false;
//        }
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
}
