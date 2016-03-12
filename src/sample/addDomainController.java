package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class addDomainController {
    public TextField nameTextField;
    public TableView<DomainValue> tableView;
    public TextField valueTextField;
    private ObservableList<DomainValue> list;


    public void onAdd(ActionEvent actionEvent) {
        if (!validate()) return;
        valueTextField.requestFocus();
        // dvs are unique
        if(list.stream().filter(dv->dv.getValue().equals(valueTextField.getText())).count()>0) {
            return;
        }
        list.add(new DomainValue(valueTextField.getText()));
    }

    public void onReplace(ActionEvent actionEvent) {
        valueTextField.requestFocus();
        if (tableView.getSelectionModel().isEmpty()) return;
        DomainValue selitem=tableView.getSelectionModel().getSelectedItem();
        if (valueTextField.getText().equalsIgnoreCase("")
                || valueTextField.getText().equalsIgnoreCase(selitem.getValue())
                || list.stream().filter(
                dv->dv.getValue().equalsIgnoreCase(valueTextField.getText())).count()>0)
            return;
        selitem.setValue(valueTextField.getText());
    }

    public void onDelete(ActionEvent actionEvent) {
        if (tableView.getSelectionModel().isEmpty()) return;
        list.remove(tableView.getSelectionModel().getSelectedItem());
    }

    public void onOK(ActionEvent actionEvent) {
        if (nameTextField.getText().equalsIgnoreCase("")
                || tableView.getItems().size()==0) return;
        Main.getShell().getKnowledgeBase().getDomains().add(nameTextField.getText(),new DomainValues(list));
        Domains ds=Main.getShell().getKnowledgeBase().getDomains();
        Domain d=ds.getList().get(0);

        nameTextField.clear();
        valueTextField.clear();
        list = FXCollections.observableArrayList();
        tableView.setItems(list);
    }

    public void onCancel(ActionEvent actionEvent) {
        ((Stage)valueTextField.getScene().getWindow()).close();
    }

    private boolean validate(){
        if(!nameTextField.getText().matches("[a-zA-Zа-яА-Я0-9]+(\\s[a-zA-Zа-яА-Я0-9]+)*")){
            nameTextField.requestFocus();
            return false;
        }
        if(!valueTextField.getText().matches("[a-zA-Zа-яА-Я0-9]+(\\s[a-zA-Zа-яА-Я0-9]+)*")){
            valueTextField.requestFocus();
            return false;
        }
        return true;
    }

    public TableView getTableView() {
        return tableView;
    }

    public void setList(ObservableList<DomainValue> list) {
        this.list = list;
    }


    public void onClick(Event event) {
        if(tableView.getSelectionModel().isEmpty()) return;
        valueTextField.setText(tableView.getSelectionModel().getSelectedItem().getValue());
        valueTextField.requestFocus();
    }
}
