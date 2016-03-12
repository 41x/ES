package sample;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;

public class Controller {
    final FileChooser fileChooser = new FileChooser();
    public TableView<Domain> domainTableView;
    public TableView<DomainValue> domainValuesTableView;

    public void OpenKB(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(Main.getStage());
        if (file == null) return;
        if(!Main.getShell().setKnowledgeBase(file.getPath())) return;
        Main.getStage().setTitle(Main.getShell().getKnowledgeBase().getName()+" "+ file.getPath());

        try{
            PrintWriter writer = new PrintWriter("lastKB", "UTF-8");
            writer.print(file.getPath());
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void SaveKBas(ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(Main.getStage());
        if (file != null) {
            Main.getShell().ser(file.getPath());
        }

    }

    public TableView<DomainValue> getDomainValuesTableView() {
        return domainValuesTableView;
    }

    public TableView<Domain> getDomainTableView() {
        return domainTableView;
    }

    public void Close(ActionEvent actionEvent) {
        Main.getStage().close();
    }

    public void onAddDomain(ActionEvent actionEvent) {
        AddDomainWindowFactory();
    }

    public void onEditDomain(ActionEvent actionEvent) {
//        todo
        Stage stage=new Stage();
        Parent root;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(getClass().getResource("addDomain.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        addDomainController c = loader.getController();

        TableColumn<DomainValue,String> numberCol = new TableColumn<>("#");
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(
                Integer.toString(c.getTableView().getItems().indexOf(p.getValue()) + 1)));
        numberCol.setSortable(false);

        TableColumn<DomainValue, String> value= new TableColumn<>("Value");
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        c.tableView.getColumns().addAll(numberCol,value);

        final ObservableList<DomainValue> data = FXCollections.observableArrayList();
        c.setList(data);
        c.tableView.setItems(data);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();


    }

    public void onDeleteDomain(ActionEvent actionEvent) {
//        todo
    }

    private void AddDomainWindowFactory(){
        Stage stage=new Stage();
        Parent root;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(getClass().getResource("addDomain.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        addDomainController c = loader.getController();

        TableColumn<DomainValue,String> numberCol = new TableColumn<>("#");
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(
                Integer.toString(c.getTableView().getItems().indexOf(p.getValue()) + 1)));
        numberCol.setSortable(false);

        TableColumn<DomainValue, String> value= new TableColumn<>("Value");
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        c.tableView.getColumns().addAll(numberCol,value);

        final ObservableList<DomainValue> data = FXCollections.observableArrayList();
        c.setList(data);
        c.tableView.setItems(data);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
