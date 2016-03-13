package sample;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class Main extends Application {

    private static Shell shell;
    private static Stage stage;
    private static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(getClass().getResource("sample.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        controller = loader.getController();
        stage=primaryStage;

        BufferedReader br = null;
        String kbPath = null;

        File f= new File("lastKB");
        if (f.exists() && !f.isDirectory()){
            try {
                br = new BufferedReader(new FileReader(f.getPath()));
                kbPath = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        shell=new Shell(new MyLIM());
        boolean kbisset=false;
        if (kbPath!=null)
            kbisset=shell.setKnowledgeBase(kbPath);


        stage.setTitle(shell.getKnowledgeBase().getName()+" "+ (kbisset?kbPath:""));
        stage.setScene(new Scene(root));
        stage.show();

        fillDomainsTab();

    }


    private void fillDomainsTab(){

        TableColumn<Domain,Integer> numberCol = new TableColumn<>("#");
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                controller.getDomainTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol.setSortable(false);

        TableColumn<Domain, String> value= new TableColumn<>("Name");
        value.setCellValueFactory(new PropertyValueFactory<>("name"));
        controller.getDomainTableView().getColumns().addAll(numberCol,value);

        controller.getDomainTableView().setItems(getShell().getKnowledgeBase().getDomains().getList());

        TableColumn<DomainValue,Integer> numberCol2 = new TableColumn<>("#");
        numberCol2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                controller.getDomainValuesTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol2.setSortable(false);
        numberCol2.setPrefWidth(50);

        TableColumn<DomainValue, String> value2= new TableColumn<>("Value");
        value2.setCellValueFactory(new PropertyValueFactory<>("value"));

        controller.getDomainValuesTableView().getColumns().addAll(numberCol2,value2);

        controller.getDomainTableView().getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                controller.getDomainValuesTableView().setItems(newSelection.getValues().getList());
            }
        });

        if(controller.domainTableView.getItems().size()>0)
            controller.domainTableView.getSelectionModel().select(0);

    }

    public static Shell getShell() {
        return shell;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String ask() {
//        todo
        return null;
    }

    public static Controller getController() {
        return controller;
    }
}
