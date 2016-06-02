package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.omg.PortableInterceptor.ServerRequestInfo;

import java.io.*;
import java.util.ArrayList;

public class Main extends Application {

    private static Shell shell;
    private static Stage stage;
    private static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setProperty("glass.accessible.force", "false");

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

        buildDomainsTable();
        buildVariablesTable();
        buildRulesTable();
        buildExplTable();

        //
        TableView<Rule> tableView=getController().getRuleTableView();
        tableView.setRowFactory(tv -> {
            TableRow<Rule> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (! row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(Integer.toString(index));
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    if (row.getIndex() != Integer.parseInt(db.getString())) {
                        event.acceptTransferModes(TransferMode.MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    int draggedIndex = Integer.parseInt(db.getString());
                    Rule draggedPerson = tableView.getItems().remove(draggedIndex);

                    int dropIndex ;
                    if (row.isEmpty())
                        dropIndex = tableView.getItems().size() ;
                    else
                        dropIndex = row.getIndex();
                    tableView.getItems().add(dropIndex, draggedPerson);

                    event.setDropCompleted(true);
                    tableView.getSelectionModel().select(dropIndex);
                    event.consume();

//                    ObservableList<Rule> rules=getShell().getKnowledgeBase().getRules().getList();
//                    for(int i=0;i<rules.size();i++){
//                        System.out.println(rules.get(i).getName());
//                    }
//                    System.out.println();
                }
            });

            return row ;
        });


        shell=new Shell();
        boolean kbisset=false;
        if (kbPath!=null)
            kbisset=shell.setKnowledgeBase(kbPath);

        stage.setTitle(shell.getKnowledgeBase().getName()+" "+ (kbisset?kbPath:""));

        stage.setScene(new Scene(root));
        stage.show();


    }

    private void buildExplTable() {
        TableColumn<VarVal, String> varname= new TableColumn<>("Переменная");
        varname.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(p.getValue().getVariable().getName()));

        TableColumn<VarVal, String> value= new TableColumn<>("Значение");
        value.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(p.getValue().getDomainValue().getValue()));
        getController().getExplTabTableView().getColumns().addAll(varname, value);

        ObservableList<VarVal> l=FXCollections.observableArrayList();
        getController().getExplTabTableView().setItems(l);
    }

    private void buildRulesTable(){
        getController().getRuleTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Rule,Integer> numberCol = new TableColumn<>("#");
        numberCol.setMaxWidth(200);
        numberCol.setMinWidth(50);
        numberCol.setPrefWidth(75);
        numberCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getRuleTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol.setSortable(false);

        TableColumn<Rule, String> realName= new TableColumn<>("Название");
        realName.setCellValueFactory(new PropertyValueFactory<>("realName"));

        TableColumn<Rule, String> value= new TableColumn<>("Описание");
        value.setCellValueFactory(new PropertyValueFactory<>("name"));

        getController().getRuleTableView().getColumns().addAll(numberCol,realName,value);

        getController().getRuleTableView().getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        Rule selRule=getController().getRuleTableView().getSelectionModel().getSelectedItem();
                        getController().getRuleContent().setText(selRule.getRuleView(""));
                        getController().getReasoningTextArea().setText(selRule.getReasoning());
                        selRule.updateName();
                    }
                });
    }

    private void buildVariablesTable(){
        getController().getVarTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Variable,Integer> numberCol = new TableColumn<>("#");
        numberCol.setMaxWidth(500);
        numberCol.setMinWidth(50);
        numberCol.setPrefWidth(75);
        numberCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getVarTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol.setSortable(false);

        TableColumn<Variable, String> value= new TableColumn<>("Название");
        value.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Variable, VarType> vartype= new TableColumn<>("Тип");
        vartype.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Variable, String> value2= new TableColumn<>("Домен");
        value2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                p.getValue().getDomain()==null?null:p.getValue().getDomain().getName()));

        controller.getVarTableView().getColumns().addAll(numberCol,value,vartype,value2);
//
        getController().getVarTabDomValTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<DomainValue,Integer> numberCol2 = new TableColumn<>("#");
        numberCol2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getVarTabDomValTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol2.setSortable(false);
        numberCol2.setMaxWidth(200);
        numberCol2.setMinWidth(50);
        numberCol2.setPrefWidth(75);

        TableColumn<DomainValue, String> domval= new TableColumn<>("Значение");
        domval.setCellValueFactory(new PropertyValueFactory<>("value"));

        getController().getVarTabDomValTableView().getColumns().addAll(numberCol2,domval);

        getController().getVarTableView().getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        getController().getVarTabDomValTableView().setItems(newSelection.getDomain()==null?null
                                :newSelection.getDomain().getValues().getList());
                        getController().getReqTextArea().setText(newSelection.getQuestion());
                    }
                });

    }

    private void buildDomainsTable(){
        getController().getDomainTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getController().getDomainValuesTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Domain,Integer> numberCol = new TableColumn<>("#");
        numberCol.setMaxWidth(200);
        numberCol.setMinWidth(50);
        numberCol.setPrefWidth(75);
        numberCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getDomainTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol.setSortable(false);

        TableColumn<Domain, String> value= new TableColumn<>("Название");
        value.setCellValueFactory(new PropertyValueFactory<>("name"));
        getController().getDomainTableView().getColumns().addAll(numberCol,value);

        TableColumn<DomainValue,Integer> numberCol2 = new TableColumn<>("#");
        numberCol2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getDomainValuesTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol2.setSortable(false);
        numberCol2.setMaxWidth(200);
        numberCol2.setMinWidth(50);
        numberCol2.setPrefWidth(75);

        TableColumn<DomainValue, String> value2= new TableColumn<>("Значение");
        value2.setCellValueFactory(new PropertyValueFactory<>("value"));

        getController().getDomainValuesTableView().getColumns().addAll(numberCol2,value2);

        getController().getDomainTableView().getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                getController().getDomainValuesTableView().setItems(newSelection.getValues().getList());
            }
        });

        if(getController().getDomainTableView().getItems().size()>0)
            getController().getDomainTableView().getSelectionModel().select(0);

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

    public static Controller getController() {
        return controller;
    }

    public static void setShell(Shell shell) {
        Main.shell = shell;
    }

    public static void perror(String m){
        Text t = new Text(m);
        t.setFill(Color.RED);
        getController().getErrorVbox().getChildren().clear();
        getController().getErrorVbox().getChildren().add(t);

        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {
                Thread.sleep(10000);
                Platform.runLater(() -> getController().getErrorVbox().getChildren().clear());
                return null;
            }
        };
        Thread th = new Thread(task);
        th.start();
    }


}
