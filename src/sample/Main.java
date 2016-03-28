package sample;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;

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

        buildDomainsTable();
        buildVariablesTable();
        buildRulesTable();

        shell=new Shell(new MyLIM());
        boolean kbisset=false;
        if (kbPath!=null)
            kbisset=shell.setKnowledgeBase(kbPath);

        stage.setTitle(shell.getKnowledgeBase().getName()+" "+ (kbisset?kbPath:""));

        stage.setScene(new Scene(root));
        stage.show();


    }

    private void buildRulesTable(){
        getController().getRuleTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Rule,Integer> numberCol = new TableColumn<>("#");
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getRuleTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol.setSortable(false);

        TableColumn<Rule, String> value= new TableColumn<>("Name");
        value.setCellValueFactory(new PropertyValueFactory<>("name"));

        getController().getRuleTableView().getColumns().addAll(numberCol,value);

        getController().getRuleTableView().getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        Rule selRule=getController().getRuleTableView().getSelectionModel().getSelectedItem();
                        getController().getRuleContent().setText(selRule.getRuleView());
                        getController().getReasoningTextArea().setText(selRule.getReasoning());
                    }
                });
    }


    private void buildVariablesTable(){
        getController().getVarTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Variable,Integer> numberCol = new TableColumn<>("#");
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getVarTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol.setSortable(false);

        TableColumn<Variable, String> value= new TableColumn<>("Name");
        value.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Variable, VarType> vartype= new TableColumn<>("Type");
        vartype.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Variable, String> value2= new TableColumn<>("Domain");
        value2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                p.getValue().getDomain()==null?null:p.getValue().getDomain().getName()));

        controller.getVarTableView().getColumns().addAll(numberCol,value,vartype,value2);
//
        TableColumn<DomainValue,Integer> numberCol2 = new TableColumn<>("#");
        numberCol2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getVarTabDomValTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol2.setSortable(false);
        numberCol2.setPrefWidth(50);

        TableColumn<DomainValue, String> domval= new TableColumn<>("Value");
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
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getDomainTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol.setSortable(false);

        TableColumn<Domain, String> value= new TableColumn<>("Name");
        value.setCellValueFactory(new PropertyValueFactory<>("name"));
        getController().getDomainTableView().getColumns().addAll(numberCol,value);

        TableColumn<DomainValue,Integer> numberCol2 = new TableColumn<>("#");
        numberCol2.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(
                getController().getDomainValuesTableView().getItems().indexOf(p.getValue()) + 1));
        numberCol2.setSortable(false);
        numberCol2.setPrefWidth(50);

        TableColumn<DomainValue, String> value2= new TableColumn<>("Value");
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

    public static DomainValue ask(Variable var) {
//        getController().


//        todo
        return null;
    }

    public static Controller getController() {
        return controller;
    }

    public static void setShell(Shell shell) {
        Main.shell = shell;
    }
}
