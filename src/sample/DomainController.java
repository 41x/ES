package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DomainController {
    public TextField nameTextField;
    public TableView<DomainValue> tableView;
    public TextField valueTextField;
    private ObservableList<DomainValue> list;


    public void onAdd(ActionEvent actionEvent) {
        if (!validate()) return;
        valueTextField.requestFocus();
        // dvs are unique
        if (list.stream().filter(dv -> dv.getValue().equals(valueTextField.getText())).count() > 0) {
            return;
        }
        list.add(new DomainValue(valueTextField.getText()));
    }

    public void onReplace(ActionEvent actionEvent) {
        valueTextField.requestFocus();
        if (tableView.getSelectionModel().isEmpty()) return;
        DomainValue selitem = tableView.getSelectionModel().getSelectedItem();
        if (valueTextField.getText().equalsIgnoreCase("")
                || valueTextField.getText().equalsIgnoreCase(selitem.getValue())
                || list.stream().filter(
                dv -> dv.getValue().equalsIgnoreCase(valueTextField.getText())).count() > 0)
            return;
        selitem.setValue(valueTextField.getText());
    }

    public void onDelete(ActionEvent actionEvent) {
        if (getTableView().getSelectionModel().isEmpty() || getTableView().getItems().size() == 1) return;
        list.remove(tableView.getSelectionModel().getSelectedItem());
    }

    public void onOK(ActionEvent actionEvent) {
        if (Main.getController().getDomainOperation().equals("add")) {
            if (!validateOK()) return;
            if (nameTextField.getText().equalsIgnoreCase("")
                    || tableView.getItems().size() == 0) return;

            Main.getShell().getKnowledgeBase().getDomains().add(nameTextField.getText(), new DomainValues(list));
            nameTextField.clear();
            valueTextField.clear();
            list = FXCollections.observableArrayList();
            tableView.setItems(list);

        } else if (Main.getController().getDomainOperation().equals("edit")) {
            if (!validateOK()) return;
            Domain selectedDomain = Main.getController().getDomainTableView().getSelectionModel().getSelectedItem();
            Domains ds = Main.getShell().getKnowledgeBase().getDomains();

            if (nameTextField.getText().equalsIgnoreCase("")
                    || (!nameTextField.getText().equalsIgnoreCase(selectedDomain.getName())
                    && ds.getList().stream().filter(d -> d.getName()
                    .equalsIgnoreCase(nameTextField.getText())).count() > 0)) return;

            selectedDomain.setName(nameTextField.getText());
            selectedDomain.getValues().setList(list);

            Main.getController().getDomainValuesTableView().setItems(selectedDomain.getValues().getList());

            // update varTabEditDomValTV and combo if its called from editVar window
            VariableController varcontr=Main.getController().getVariableController();
            if (varcontr!=null){
                Domain seld=varcontr.getDomainCombo().getSelectionModel().getSelectedItem();
                ObservableList<Domain> l=Main.getShell().getKnowledgeBase().getDomains().getList();
                varcontr.getDomainCombo().setItems(null);
                varcontr.getDomainCombo().setItems(l);
                SelectionModel<Domain> sm = varcontr.getDomainCombo().getSelectionModel();
                sm.clearSelection();
                sm.select(seld);
            }
            // upd varTabDomValTV
            Domain selDomain=Main.getController().getVarTableView().getSelectionModel().getSelectedItem().getDomain();
            Main.getController().getVarTabDomValTableView().setItems(selDomain==null?null
                    :selDomain.getValues().getList());
            // upd varTabTV
            Main.getController().getVarTableView().getColumns().get(0).setVisible(false);
            Main.getController().getVarTableView().getColumns().get(0).setVisible(true);

            ((Stage) valueTextField.getScene().getWindow()).close();
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        ((Stage) valueTextField.getScene().getWindow()).close();
    }

    private boolean validate() {
        if (!nameTextField.getText().matches("[a-zA-Zа-яА-Я0-9]+(\\s[a-zA-Zа-яА-Я0-9]+)*")) {
            nameTextField.requestFocus();
            return false;
        }
        if (!valueTextField.getText().matches("[a-zA-Zа-яА-Я0-9]+(\\s[a-zA-Zа-яА-Я0-9]+)*")) {
            valueTextField.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateOK() {
        if (!nameTextField.getText().matches("[a-zA-Zа-яА-Я0-9]+(\\s[a-zA-Zа-яА-Я0-9]+)*")) {
            nameTextField.requestFocus();
            return false;
        }
        if (getTableView().getItems().size()==0) {
            valueTextField.requestFocus();
            return false;
        }
        return true;
    }

    public TableView<DomainValue> getTableView() {
        return tableView;
    }

    public void setList(ObservableList<DomainValue> list) {
        this.list = list;
    }


    public void onClick(Event event) {
        if (tableView.getSelectionModel().isEmpty()) return;
        valueTextField.setText(tableView.getSelectionModel().getSelectedItem().getValue());
        valueTextField.requestFocus();
    }
}
