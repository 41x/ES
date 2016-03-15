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
    private Background bg;


    public void onAdd(ActionEvent actionEvent) {
        Main.getController().onAddDomain();
    }

    public void onOK(ActionEvent actionEvent) {
//        todo
        if(!validate()) return;

        if(Main.getController().getVariableOperation().equals("add")){

        }else if(Main.getController().getVariableOperation().equals("edit")){

            ((Stage)getDomainCombo().getScene().getWindow()).close();
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

    public void onDomainSelected(Event event) {
        if(getDomainCombo().getSelectionModel().isEmpty()) return;
        Main.getController().getVariableController().getAddVarDomainValTableView().setItems(
                getDomainCombo().getSelectionModel().getSelectedItem().getValues().getList());
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

//        todo
//        if(getDomainCombo().getSelectionModel().isEmpty()) {
//            bg=getDomainCombo().getBackground();
//            getDomainCombo().setBackground(
//                    new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
//            return false;
//        }
//
//        getDomainCombo().onMouseClickedProperty().addListener((a,b,c)->{
//            getDomainCombo().setBackground(bg);
//        });

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

    public void removeBackground(ActionEvent actionEvent) {
        getDomainCombo().setBackground(Background.EMPTY);
    }
}
