package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class VariableController {
    public TextField nameTextField;
    public RadioButton radioInfer;
    public RadioButton radioRequest;
    public RadioButton radioInfReq;
    public TextArea requestTextField;
    public ComboBox<Domain> domainCombo;
    public TableView<DomainValue> addVarDomainValTableView;

    public void updateCombo(){
        Main.getController().getVariableController().getDomainCombo().setItems(Main.getShell().getKnowledgeBase().getDomains().getList());
    }

    public void onAdd(ActionEvent actionEvent) {
        Main.getController().onAddDomain();
    }

    public void onOK(ActionEvent actionEvent) {
        if(!validate()) return;
        Domains doms=Main.getShell().getKnowledgeBase().getDomains();
        Domain d=getDomainCombo().getSelectionModel().getSelectedItem();

        Variable var=new Variable(
                getNameTextField().getText(),
                requestTextField.getText(),
                radioInfer.isSelected()?VarType.INFER:(radioRequest.isSelected()?VarType.ASK:VarType.INFER_ASK),
                getDomainCombo().getSelectionModel().getSelectedItem());
        Variables variables=Main.getShell().getKnowledgeBase().getVariables();

        if(Main.getController().getVariableOperation().equals("add")){
            if(variables.contains(var)){
                Main.perror("duplicate variable");
                return;
            }
            variables.add(var);
            getNameTextField().clear();
            getRequestTextField().clear();
            getDomainCombo().getSelectionModel().clearSelection();
            getNameTextField().requestFocus();
        }else if(Main.getController().getVariableOperation().equals("edit")){
            Variable selectedVar=Main.getController().getVarTableView().getSelectionModel().getSelectedItem();
            if(variables.contains(var) && !var.equals(selectedVar) ){
                Main.perror("duplicate variable");
                return;
            }

            selectedVar.setName(var.getName());
            selectedVar.setDomain(var.getDomain());
            selectedVar.setQuestion(var.getQuestion());
            selectedVar.setType(var.getType());

            //upd shit
            Main.getController().getVarTabDomValTableView()
                    .setItems(selectedVar.getDomain()==null?null
                            :selectedVar.getDomain().getValues().getList());
            Main.getController().getReqTextArea().setText(var.getQuestion());

            Main.getController().getVarTableView().getColumns().get(0).setVisible(false);
            Main.getController().getVarTableView().getColumns().get(0).setVisible(true);

            //upd rule table
            if(Main.getController().getRuleTableView().getItems().size()>0){
                Main.getController().getRuleTableView().getSelectionModel().clearSelection();
                Main.getController().getRuleTableView().getSelectionModel().select(0);
            }
            Main.getController().getRuleTableView().getColumns().get(0).setVisible(false);
            Main.getController().getRuleTableView().getColumns().get(0).setVisible(true);

            ((Stage)getNameTextField().getScene().getWindow()).close();
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        ((Stage)getDomainCombo().getScene().getWindow()).close();
    }


    public void onEdit(ActionEvent actionEvent) {
        if(getDomainCombo().getSelectionModel().isEmpty()) return;
        Main.getController().getDomainTableView().getSelectionModel().select(
                getDomainCombo().getSelectionModel().getSelectedItem());
        Main.getController().onEditDomain();
    }

    public TableView<DomainValue> getAddVarDomainValTableView() {
        return addVarDomainValTableView;
    }

    private boolean validate() {
        if (!getNameTextField().getText().matches("[a-zA-Zа-яА-Я0-9/\\.\\-,]+(\\s[a-zA-Zа-яА-Я0-9/\\.\\-,]+)*")
                || getNameTextField().getText().equalsIgnoreCase("Введите название переменной")) {
            getNameTextField().requestFocus();
            return false;
        }
        if(getDomainCombo().getSelectionModel().isEmpty()) {
            getDomainCombo().show();
            return false;
        }
        if(getDomainCombo().getSelectionModel().getSelectedItem().getValues().getList().size()==0){
            Main.perror(String.format("Домен %s пуст",getDomainCombo().getSelectionModel().getSelectedItem().getName()));
            return false;
        }
        return true;
    }


    public ComboBox<Domain> getDomainCombo() {
        return domainCombo;
    }

    public RadioButton getRadioInfer() {
        return radioInfer;
    }

    public RadioButton getRadioRequest() {
        return radioRequest;
    }

    public RadioButton getRadioInfReq() {
        return radioInfReq;
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

    public void setRadioInfer(RadioButton radioInfer) {
        this.radioInfer = radioInfer;
    }

    public void setRadioRequest(RadioButton radioRequest) {
        this.radioRequest = radioRequest;
    }

    public void setRadioInfReq(RadioButton radioInfReq) {
        this.radioInfReq = radioInfReq;
    }

    public void setRequestTextField(TextArea requestTextField) {
        this.requestTextField = requestTextField;
    }

    public void setDomainCombo(ComboBox<Domain> domainCombo) {
        this.domainCombo = domainCombo;
    }

    public void onDomPlus(ActionEvent actionEvent) {
        Main.getController().onAddDomain();
    }
}
