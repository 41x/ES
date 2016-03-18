package sample;

import com.sun.java.browser.plugin2.DOM;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class Controller {
    final FileChooser fileChooser = new FileChooser();
    public TableView<Domain> domainTableView;
    public TableView<DomainValue> domainValuesTableView;
    public TableView<Variable> varTableView;
    public TableView<DomainValue> varTabDomValTableView;
    public TextArea reqTextArea;

    private DomainController domainController;
    private String domainOperation;

    private VariableController variableController;
    private String variableOperation;

    public void OpenKB(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(Main.getStage());
        if (file == null) return;
        if(!Main.getShell().setKnowledgeBase(file.getPath())) return;
        Main.getStage().setTitle(getKB().getName()+" "+ file.getPath());

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


    public void Close(ActionEvent actionEvent) {
        Main.getStage().close();
    }

    public void onAddDomain() {
        setDomainOperation("add");
        Stage domainStage = domainStageFactory();
        final ObservableList<DomainValue> data = FXCollections.observableArrayList();
        getDomainController().setList(data);
        getDomainController().getTableView().setItems(data);
        if (domainStage != null)
            domainStage.show();
    }

    public void onEditDomain() {
        if (getDomainTableView().getSelectionModel().isEmpty()) return;
        setDomainOperation("edit");
        Stage domainStage = domainStageFactory();
        Domain selectedDomain = getDomainTableView().getSelectionModel().getSelectedItem();
        getDomainController().nameTextField.setText(selectedDomain.getName());

        ObservableList<DomainValue> data = selectedDomain.getValues().getListCopy();
        getDomainController().setList(data);
        getDomainController().getTableView().setItems(data);
        if (domainStage != null)
            domainStage.show();
    }


    public void onDeleteDomain(ActionEvent actionEvent) {
        if (getDomainTableView().getSelectionModel().isEmpty()) return;
        Domain selDomain = getDomainTableView().getSelectionModel().getSelectedItem();
        getKB().getDomains().remove(selDomain);
    }


    public void onAddVariable(ActionEvent actionEvent) {
        setVariableOperation("add");
        Stage varStage = variableStageFactory();

        if (varStage != null)
            varStage.show();
    }



    public void onEditVariable(ActionEvent actionEvent) {
        if (getVarTableView().getSelectionModel().isEmpty()) return;
        setVariableOperation("edit");
        Stage varStage = variableStageFactory();

        Variable selectedVar=getVarTableView().getSelectionModel().getSelectedItem();
        getVariableController().getNameTextField().setText(selectedVar.getName());
        getVariableController().getRadioInfer().setSelected(selectedVar.getType()==VarType.INFER);
        getVariableController().getRadioRequest().setSelected(selectedVar.getType()==VarType.ASK);
        getVariableController().getRadioInfReq().setSelected(selectedVar.getType()==VarType.INFER_ASK);
        getVariableController().getRequestTextField().setText(selectedVar.getQuestion());
        getVariableController().getDomainCombo().getSelectionModel().select(selectedVar.getDomain());

        if (varStage != null) varStage.show();
    }


    private Stage variableStageFactory() {
        Stage stage=new Stage();
        Parent root;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(getClass().getResource("Variable.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        setVariableController(loader.getController());

        TableColumn<DomainValue,String> numberCol = new TableColumn<>("#");
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(Integer.toString(getVariableController()
                .getAddVarDomainValTableView().getItems().indexOf(p.getValue()) + 1)));
        numberCol.setSortable(false);

        TableColumn<DomainValue, String> value= new TableColumn<>("Value");
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        getVariableController().getAddVarDomainValTableView().getColumns().addAll(numberCol, value);

        getVariableController().getDomainCombo().setCellFactory(new Callback<ListView<Domain>,ListCell<Domain>>(){

            @Override
            public ListCell<Domain> call(ListView<Domain> p) {

                final ListCell<Domain> cell = new ListCell<Domain>(){

                    @Override
                    protected void updateItem(Domain t, boolean bln) {
                        super.updateItem(t, bln);

                        if(t != null){
                            setText(t.getName());
                        }else{
                            setText(null);
                        }
                    }
                };

                return cell;
            }
        });

        getVariableController().getDomainCombo().setItems(getKB().getDomains().getList());
        // adding listener to combo
        getVariableController().getDomainCombo().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue!=null)
                        getVariableController().getAddVarDomainValTableView().setItems(
                                newValue.getValues().getList());
                });

        getVariableController().getRadioInfer().fire();
        final ToggleGroup group = getVariableController().getRadioInfer().getToggleGroup();

        group.selectedToggleProperty()
                .addListener((ov, old_toggle, new_toggle) -> {
                    if (group.getSelectedToggle() != null) {
                        if (new_toggle==getVariableController().getRadioRequest()){
                            getVariableController().getDomainCombo().getSelectionModel().clearSelection();
                            getVariableController().getAddVarDomainValTableView().setItems(null);
                        }
                    }
                });


        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    private Stage domainStageFactory() {
        Stage stage=new Stage();
        Parent root;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(getClass().getResource("Domain.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        setDomainController(loader.getController());

        TableColumn<DomainValue,String> numberCol = new TableColumn<>("#");
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(
                Integer.toString(getDomainController().getTableView().getItems().indexOf(p.getValue()) + 1)));
        numberCol.setSortable(false);

        TableColumn<DomainValue, String> value= new TableColumn<>("Value");
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        getDomainController().getTableView().getColumns().addAll(numberCol, value);


        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }


    public DomainController getDomainController() {
        return domainController;
    }

    public String getDomainOperation() {
        return domainOperation;
    }

    public void setDomainOperation(String domainOperation) {
        this.domainOperation = domainOperation;
    }

    public TableView<DomainValue> getDomainValuesTableView() {
        return domainValuesTableView;
    }

    public TableView<Domain> getDomainTableView() {
        return domainTableView;
    }

    public void setDomainController(DomainController domainController) {
        this.domainController = domainController;
    }

    public TableView<Variable> getVarTableView() {
        return varTableView;
    }

    public String getVariableOperation() {
        return variableOperation;
    }

    public void setVariableOperation(String variableOperation) {
        this.variableOperation = variableOperation;
    }

    public VariableController getVariableController() {
        return variableController;
    }

    public void setVariableController(VariableController variableController) {
        this.variableController = variableController;
    }

    public TableView<DomainValue> getVarTabDomValTableView() {
        return varTabDomValTableView;
    }

    public TextArea getReqTextArea() {
        return reqTextArea;
    }

    public void onVarDelete(ActionEvent actionEvent) {
        getKB().getVariables().remove(getVarTableView().getSelectionModel().getSelectedItem());
    }

    private KB getKB(){
        return Main.getShell().getKnowledgeBase();
    }
}
