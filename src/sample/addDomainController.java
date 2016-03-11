package sample;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;

public class addDomainController {
    public TextField nameTextField;
    public TableView tableView;
    public TextField valueTextField;
    private ObservableList<DomainValue> list;

    public void onAdd(ActionEvent actionEvent) {
        if (!validate()) return;
        list.add(new DomainValue(valueTextField.getText()));



//        todo
    }

    public void onEdit(ActionEvent actionEvent) {
//        todo
    }

    public void onDelete(ActionEvent actionEvent) {
//        todo
    }

    public void onOK(ActionEvent actionEvent) {
//        todo
    }

    public void onCancel(ActionEvent actionEvent) {
//        todo
    }

    private boolean validate(){
        if(!nameTextField.getText().matches("[a-zA-Z0-9]+(\\s[a-zA-Z0-9]+)*")){
            nameTextField.requestFocus();
            return false;
        }
        if(!valueTextField.getText().matches("[a-zA-Z0-9]+(\\s[a-zA-Z0-9]+)*")){
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
}
