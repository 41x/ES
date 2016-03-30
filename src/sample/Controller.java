package sample;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class Controller {
    final FileChooser fileChooser = new FileChooser();
    public TableView<Domain> domainTableView;
    public TableView<DomainValue> domainValuesTableView;
    public TableView<Variable> varTableView;
    public TableView<DomainValue> varTabDomValTableView;
    public TextArea reqTextArea;
    public TextArea ruleContent;
    public TableView<Rule> ruleTableView;
    public TextArea reasoningTextArea;
    public ComboBox<Variable> consVarCombo;
    public VBox answerList;
    public TextArea consTabLogTextArea;
    public TextArea consTabQuestionTextArea;
    public Button confirmButton;
    public Button stopConsButton;
    public Button startConsButton;
    public TextArea targetTextArea;

    private DomainController domainController;
    private String domainOperation;

    private VariableController variableController;
    private String variableOperation;

    private RuleController ruleController;
    private String RuleOperation;

    private Semaphore sem=null;
    private String answer="?";

    public void NewKB(ActionEvent actionEvent) {
        Main.setShell(new Shell());
        Main.getStage().setTitle(Main.getShell().getKnowledgeBase().getName());

    }

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
        if (file == null || !Main.getShell().ser(file.getPath())) return;

        String filename=file.getPath();
        if(!filename.matches(".*\\.kb$")) filename+=".kb";
        try{
            PrintWriter writer = new PrintWriter("lastKB", "UTF-8");
            writer.print(filename);
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
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
        if (Main.getShell().getKnowledgeBase().getVariables().useDomain(selectedDomain)){
            System.out.println("The domain is used in variables");
            return;
        }

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
        if(Main.getShell().getKnowledgeBase().getRules().use(selectedVar)){
            System.out.println("The variable is used in rules");
            return;
        }

        getVariableController().getNameTextField().setText(selectedVar.getName());
        getVariableController().getRadioInfer().setSelected(selectedVar.getType()==VarType.INFER);
        getVariableController().getRadioRequest().setSelected(selectedVar.getType()==VarType.ASK);
        getVariableController().getRadioInfReq().setSelected(selectedVar.getType()==VarType.INFER_ASK);
        getVariableController().getRequestTextField().setText(selectedVar.getQuestion());
        getVariableController().getDomainCombo().getSelectionModel().select(selectedVar.getDomain());

        if (varStage != null) varStage.show();
    }

    public void onAddRule(ActionEvent actionEvent) {
        setRuleOperation("add");
        Stage varStage = ruleStageFactory();

        ObservableList<VarVal> list=FXCollections.observableArrayList();
        getRuleController().getAddRulePremisesTableView().setItems(list);

        if (varStage != null)
            varStage.show();
    }

    public void onEditRule(ActionEvent actionEvent) {
        if(getRuleTableView().getSelectionModel().isEmpty()) return;
        Rule selRule=getRuleTableView().getSelectionModel().getSelectedItem();
        setRuleOperation("edit");
        Stage varStage = ruleStageFactory();

        List<VarVal> list=selRule.getPremises().getList().stream().map(VarVal::clone).collect(Collectors.toList());
        getRuleController().getAddRulePremisesTableView().setItems(FXCollections.observableArrayList(list));

        getRuleController().getNameTextField().setText(selRule.getName());
        getRuleController().getRequestTextField().setText(selRule.getReasoning());

        getRuleController().getAddRuleConcVarCombo().getSelectionModel().select(
                selRule.getConclusion().getVarval().getVariable());
        getRuleController().getAddRuleConcDomValCombo().getSelectionModel().select(
                selRule.getConclusion().getVarval().getDomainValue());

        if (varStage != null)
            varStage.show();

    }

    private Stage ruleStageFactory() {
        Stage stage=new Stage();
        Parent root;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(getClass().getResource("Rule.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        setRuleController(loader.getController());
//        premises table
//        getRuleController().getAddRulePremisesTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<VarVal,String> numberCol = new TableColumn<>("#");
        numberCol.setPrefWidth(50);
        numberCol.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(Integer.toString(getRuleController()
                .getAddRulePremisesTableView().getItems().indexOf(p.getValue()) + 1)));
        numberCol.setSortable(false);

        TableColumn<VarVal, String> varname= new TableColumn<>("Variable");
        varname.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(p.getValue().getVariable().getName()));

        TableColumn<VarVal,String> eq = new TableColumn<>("");
        eq.setPrefWidth(30);
        eq.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>("="));

        TableColumn<VarVal, String> value= new TableColumn<>("Value");
        value.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(p.getValue().getDomainValue().getValue()));
        getRuleController().getAddRulePremisesTableView().getColumns().addAll(numberCol,varname,eq, value);

        //configuring combos
        getRuleController().getAddRulePremisVarCombo().setCellFactory(new Callback<ListView<Variable>,
                ListCell<Variable>>(){
            @Override
            public ListCell<Variable> call(ListView<Variable> p) {
                return new ListCell<Variable>(){
                    @Override
                    protected void updateItem(Variable t, boolean bln) {
                        super.updateItem(t, bln);

                        if(t != null){
                            setText(t.getName());
                        }else{
                            setText(null);
                        }
                    }
                };
            }
        });
        getRuleController().getAddRuleConcVarCombo().setCellFactory(new Callback<ListView<Variable>,ListCell<Variable>>(){
            @Override
            public ListCell<Variable> call(ListView<Variable> p) {
                return new ListCell<Variable>(){
                    @Override
                    protected void updateItem(Variable t, boolean bln) {
                        super.updateItem(t, bln);

                        if(t != null){
                            setText(t.getName());
                        }else{
                            setText(null);
                        }
                    }
                };
            }
        });
        getRuleController().getAddRulePremisDomValCombo().setCellFactory(
                new Callback<ListView<DomainValue>,ListCell<DomainValue>>(){
            @Override
            public ListCell<DomainValue> call(ListView<DomainValue> p) {
                return new ListCell<DomainValue>(){
                    @Override
                    protected void updateItem(DomainValue t, boolean bln) {
                        super.updateItem(t, bln);

                        if(t != null){
                            setText(t.getValue());
                        }else{
                            setText(null);
                        }
                    }
                };
            }
        });
        getRuleController().getAddRuleConcDomValCombo().setCellFactory(
                new Callback<ListView<DomainValue>,ListCell<DomainValue>>(){
            @Override
            public ListCell<DomainValue> call(ListView<DomainValue> p) {
                return new ListCell<DomainValue>(){
                    @Override
                    protected void updateItem(DomainValue t, boolean bln) {
                        super.updateItem(t, bln);

                        if(t != null){
                            setText(t.getValue());
                        }else{
                            setText(null);
                        }
                    }
                };
            }
        });

        getRuleController().getAddRulePremisVarCombo().setItems(
                Main.getShell().getKnowledgeBase().getVariables().getList());
        // adding listener to combo
        getRuleController().getAddRulePremisVarCombo().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue!=null)
                        getRuleController().getAddRulePremisDomValCombo().setItems(
                                newValue.getDomain().getValues().getList());
                });

        List<Variable> filteredList=Main.getShell().getKnowledgeBase().getVariables().getList()
                .stream().filter(x->x.getType()!=VarType.ASK).collect(Collectors.toList());
        getRuleController().getAddRuleConcVarCombo().setItems(FXCollections.observableArrayList(filteredList));

        // adding listener to combo
        getRuleController().getAddRuleConcVarCombo().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue!=null)
                        getRuleController().getAddRuleConcDomValCombo().setItems(
                                newValue.getDomain().getValues().getList());
                });

        // adding listener to combo
        getRuleController().getAddRuleConcDomValCombo().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue!=null)
                        getRuleController().getRuleDescription().setText(getRuleController().getRuleView());
                });



        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
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

        getVariableController().getDomainCombo().setCellFactory(new Callback<ListView<Domain>
                ,ListCell<Domain>>(){

            @Override
            public ListCell<Domain> call(ListView<Domain> p) {

                return new ListCell<Domain>(){
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
            }
        });

        getVariableController().getDomainCombo().setItems(getKB().getDomains().getList());
        // adding listener to combo
        getVariableController().getDomainCombo().getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue!=null){
                        getVariableController().getAddVarDomainValTableView().setItems(
                                newValue.getValues().getList());
                    }
                });


        getVariableController().getRadioInfer().fire();

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

    public String getRuleOperation() {
        return RuleOperation;
    }

    public void setRuleOperation(String ruleOperation) {
        RuleOperation = ruleOperation;
    }

    public RuleController getRuleController() {
        return ruleController;
    }

    public void setRuleController(RuleController ruleController) {
        this.ruleController = ruleController;
    }

    public TextArea getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(TextArea ruleContent) {
        this.ruleContent = ruleContent;
    }

    public TableView<Rule> getRuleTableView() {
        return ruleTableView;
    }

    public void setRuleTableView(TableView ruleTableView) {
        this.ruleTableView = ruleTableView;
    }

    public TextArea getReasoningTextArea() {
        return reasoningTextArea;
    }

    public void onRuleDelete(ActionEvent actionEvent) {
        Main.getShell().getKnowledgeBase().getRules()
                .remove(getRuleTableView().getSelectionModel().getSelectedItem().getName());
    }

    public ComboBox<Variable> getConsVarCombo() {
        return consVarCombo;
    }

    public VBox getAnswerList() {
        return answerList;
    }

    public void onConsultTab(Event event) {
        getConfirmButton().setDisable(true);
        getStopConsButton().setDisable(true);

        List<Variable> l=Main.getShell().getKnowledgeBase().getVariables().getList()
                .stream().filter(x->x.getType()!=VarType.ASK).collect(Collectors.toList());
        getConsVarCombo().setItems(FXCollections.observableArrayList(l));
    }

    public void onStopCons(ActionEvent actionEvent) {
        setAnswer("?");
        getSem().release();
        getStartConsButton().setDisable(false);
        getStopConsButton().setDisable(true);
        getConfirmButton().setDisable(true);
    }

    public void onConfirm(ActionEvent actionEvent) {
        getConfirmButton().setDisable(true);
        int i=0;
        List<Node> l=getAnswerList().getChildren();
        while (i<l.size() && !((RadioButton)l.get(i)).isSelected()) i++;
        setAnswer(i==l.size()?"":((RadioButton)l.get(i)).getText());
        getSem().release();
    }

    public void onStartCons(ActionEvent actionEvent) throws InterruptedException {
        getConsTabLogTextArea().clear();
        if(getConsVarCombo().getSelectionModel().isEmpty()) return;
        getStopConsButton().setDisable(false);
        getStartConsButton().setDisable(true);
        Variable selVar=getConsVarCombo().getSelectionModel().getSelectedItem();
        setSem(new Semaphore(0, true));

        getAnswerList().requestFocus();
        getTargetTextArea().setText(String.format("Consultation target: \'%s\'\n",selVar.getName()));
        Main.getShell().startCons(selVar);
    }

    public String askVar(Variable var) throws InterruptedException {

        Platform.runLater(() -> {
            getConsTabQuestionTextArea().setText(var.getQuestion());
            getAnswerList().getChildren().clear();
            ToggleGroup group = new ToggleGroup();
            var.getDomain().getValues().getList().forEach(x->{
                RadioButton rb=new RadioButton(x.getValue());
                rb.setToggleGroup(group);
                rb.setPrefHeight(25);
                rb.setPrefWidth(1000);
                rb.setBackground(new Background(new BackgroundFill(Color.WHEAT,new CornerRadii(10), Insets.EMPTY)));
                getAnswerList().getChildren().add(rb);
//                rb.setSelected(true);
            });
            getConfirmButton().setDisable(false);
            if(getAnswerList().getChildren().size()>0){
                getAnswerList().getChildren().get(0).requestFocus();
                ((RadioButton)getAnswerList().getChildren().get(0)).setSelected(true);
            }

        });


        getSem().acquire();
        String res=getAnswer();
//        System.out.println(String.format("im thread answer is %s, go",res));
        return res;

    }

    public void resetCons(){
        getStartConsButton().setDisable(false);
        getStopConsButton().setDisable(true);
        getConfirmButton().setDisable(true);
        getAnswerList().getChildren().clear();
        getConsTabQuestionTextArea().clear();

    }

    public TextArea getConsTabLogTextArea() {
        return consTabLogTextArea;
    }

    public void setConsTabLogTextArea(TextArea consTabLogTextArea) {
        this.consTabLogTextArea = consTabLogTextArea;
    }

    public TextArea getConsTabQuestionTextArea() {
        return consTabQuestionTextArea;
    }

    public void setConsTabQuestionTextArea(TextArea consTabQuestionTextArea) {
        this.consTabQuestionTextArea = consTabQuestionTextArea;
    }

    public Semaphore getSem() {
        return sem;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Button getConfirmButton() {
        return confirmButton;
    }

    public Button getStopConsButton() {
        return stopConsButton;
    }

    public void setSem(Semaphore sem) {
        this.sem = sem;
    }

    public Button getStartConsButton() {
        return startConsButton;
    }

    public void CloseCons(ActionEvent actionEvent) {
        if (getSem()!=null)
            getSem().release();
        Main.getStage().close();
    }

    public TextArea getTargetTextArea() {
        return targetTextArea;
    }

    public void onConsKeyUp(Event event) {
//        if(((KeyEvent)event).getCode()== KeyCode.ENTER){
//            if(!getConfirmButton().isDisabled()){
//                getConfirmButton().fire();
//                System.out.println("shit");
//
//            }
//
//        }
    }
}
