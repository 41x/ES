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
//        todo
        if(!validate()) return;

        if(Main.getController().getVariableOperation().equals("add")){
            Main.getShell().getKnowledgeBase().getVariables().add(
                    getNameTextField().getText(),
                    requestTextField.getText(),
                    radioInfer.isSelected()?VarType.INFER:(radioRequest.isSelected()?VarType.ASK:VarType.INFER_ASK),
                    getDomainCombo().getSelectionModel().getSelectedItem()
            );
        }else if(Main.getController().getVariableOperation().equals("edit")){
            if(!validate()) return;
            Variable selectedVar=Main.getController().getVarTableView().getSelectionModel().getSelectedItem();
            selectedVar.setName(getNameTextField().getText());
            selectedVar.setQuestion(getRequestTextField().getText());
            selectedVar.setType(getRadioInfer().isSelected()?
                    VarType.INFER:(getRadioRequest().isSelected()?VarType.ASK:VarType.INFER_ASK));
            selectedVar.setDomain(getDomainCombo().getSelectionModel().getSelectedItem());


            Main.getController().getVarTabDomValTableView()
                    .setItems(selectedVar.getDomain()==null?null
                            :selectedVar.getDomain().getValues().getList());
            Main.getController().getReqTextArea()
                    .setText(selectedVar.getQuestion());

            Main.getController().getVarTableView().getColumns().get(0).setVisible(false);
            Main.getController().getVarTableView().getColumns().get(0).setVisible(true);

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
        if (!getNameTextField().getText().matches("[a-zA-Zа-яА-Я0-9]+(\\s[a-zA-Zа-яА-Я0-9]+)*")) {
            getNameTextField().requestFocus();
            return false;
        }
        if (getRequestTextField().getText().trim().equals("")) {
            getRequestTextField().requestFocus();
            return false;
        }

        if(getDomainCombo().getSelectionModel().isEmpty()) {
            getDomainCombo().show();
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
}
