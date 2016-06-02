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
import java.util.StringJoiner;
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
    public TextArea consTabQuestionTextArea;
    public Button confirmButton;
    public Button stopConsButton;
    public Button startConsButton;
    public TextArea targetTextArea;
    public Tab domsTab;
    public Tab varsTab;
    public Tab rulesTab;
    public Tab consTab;
    public Tab reasTab;
    public TreeView<String> treeView;
    public TableView explTabTableView;
    public Button expandButton;
    public VBox errorVbox;

    private DomainController domainController;
    private String domainOperation;

    private VariableController variableController;
    private String variableOperation;

    private RuleController ruleController;
    private String RuleOperation;

    private Semaphore sem=null;
    private String answer="?";

    DialogBoxController dialogBoxController;

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
        domainController.getNameTextField().setText("Введите название домена");
        final ObservableList<DomainValue> data = FXCollections.observableArrayList();
        getDomainController().setList(data);
        getDomainController().getTableView().setItems(data);
        if (domainStage != null)
            domainStage.show();
    }

    public void onEditDomain() {
        if (getDomainTableView().getSelectionModel().isEmpty()) return;
        Domain selectedDomain = getDomainTableView().getSelectionModel().getSelectedItem();
        String vars=Main.getShell().getKnowledgeBase().getVariables().useDomain(selectedDomain);

        if (!vars.trim().equals("")){
            if (!Main.getController().alert("Внимание!\nДанный домен используется переменными:\n"
                    +vars+"\nВы хотите продолжить?")) return;
        }

        setDomainOperation("edit");
        Stage domainStage = domainStageFactory();

        getDomainController().nameTextField.setText(selectedDomain.getName());

        ObservableList<DomainValue> data = selectedDomain.getValues().getListCopy();
        getDomainController().setList(data);
        getDomainController().getTableView().setItems(data);
        if (domainStage != null)
            domainStage.show();
    }


    private boolean rUsesVars(Rule r,List<Variable> vars){
        return vars.stream().filter(x->r.uses(x)).count()>0;
    }
    public void onDeleteDomain(ActionEvent actionEvent) {

        if (getDomainTableView().getSelectionModel().isEmpty()) return;
        Domain selectedDomain = getDomainTableView().getSelectionModel().getSelectedItem();
        List<Variable> vars=Main.getShell().getKnowledgeBase().getVariables().usesDomain(selectedDomain);
        Variables variables=Main.getShell().getKnowledgeBase().getVariables();
        Rules rules=Main.getShell().getKnowledgeBase().getRules();
        List<Rule> rulesUseDomain=rules.getList().stream().filter(x->rUsesVars(x,vars)).collect(Collectors.toList());
        if (vars.size()>0){
            if (!Main.getController().alert(String.format("Внимание!\nДанный домен используется\nправилами:\n%s\n" +
                    "переменными:\n%s\nУдаление будет каскадным!\nВы хотите продолжить?",
                    rulesUseDomain.stream().map(x->x.getRealName()).collect(Collectors.joining("\n")),
                    vars.stream().map(Variable::getName).collect(Collectors.joining("\n")))))
                return;
        }
        rulesUseDomain.forEach(x-> rules.getList().remove(x));
        vars.forEach(x-> variables.getList().remove(x));
        getKB().getDomains().remove(selectedDomain);
    }


    public void onAddVariable(ActionEvent actionEvent) {
        setVariableOperation("add");
        Stage varStage = variableStageFactory();
        getVariableController().getNameTextField().setText("Введите название переменной");
        if (varStage != null)
            varStage.show();
    }



    public void onEditVariable(ActionEvent actionEvent) {
        if (getVarTableView().getSelectionModel().isEmpty()) return;
        Variable selectedVar=getVarTableView().getSelectionModel().getSelectedItem();
        Rules rules=Main.getShell().getKnowledgeBase().getRules();
        List<Rule> rulesThatUseVar=rules.uses(selectedVar);
        if(rulesThatUseVar.size()>0){
            if(!Main.getController().alert(String.format("Внимание!\nДанная переменная используется в правилах:" +
                    "\n%s\nВы хотите продолжить?",rulesThatUseVar.stream().map(x->x.getRealName())
                    .collect(Collectors.joining("\n"))))){
                return;
            }
        }
        setVariableOperation("edit");
        Stage varStage = variableStageFactory();

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
        getRuleController().realNameTextFiled.setText("Введите название правила");

        ObservableList<VarVal> list=FXCollections.observableArrayList();
        getRuleController().getAddRulePremisesTableView().setItems(list);

        if (varStage != null)
            varStage.show();
    }


    public void onVarDelete(ActionEvent actionEvent) {
        if(getVarTableView().getSelectionModel().isEmpty()) return;
        Variable selVar=getVarTableView().getSelectionModel().getSelectedItem();
        Rules rules=Main.getShell().getKnowledgeBase().getRules();
        List<Rule> rulesThatUseVar=rules.uses(selVar);

        if(rulesThatUseVar.size()>0){
            if(!Main.getController().alert(String.format("Внимание!\nДанная переменная используется в правилах:" +
                    "\n%s\nУдаление будет каскадным!\nВы хотите продолжить?",rulesThatUseVar.stream().map(x->x.getRealName())
                    .collect(Collectors.joining("\n"))))){
                return;
            }
        }
        rulesThatUseVar.forEach(x->{
            rules.getList().remove(x);
        });
        getKB().getVariables().remove(selVar);
    }

    public void onEditRule(ActionEvent actionEvent) {
        if(getRuleTableView().getSelectionModel().isEmpty()) return;
        Rule selRule=getRuleTableView().getSelectionModel().getSelectedItem();
        setRuleOperation("edit");
        Stage varStage = ruleStageFactory();

        List<VarVal> list=selRule.getPremises().getList().stream().map(VarVal::clone).collect(Collectors.toList());
        getRuleController().getAddRulePremisesTableView().setItems(FXCollections.observableArrayList(list));

        getRuleController().realNameTextFiled.setText(selRule.getRealName());
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
        TableColumn<VarVal,String> numberCol = new TableColumn<>("#");
        numberCol.setMaxWidth(200);
        numberCol.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(Integer.toString(getRuleController()
                .getAddRulePremisesTableView().getItems().indexOf(p.getValue()) + 1)));
        numberCol.setSortable(false);

        TableColumn<VarVal, String> varname= new TableColumn<>("Переменная");
        varname.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(p.getValue().getVariable().getName()));

        TableColumn<VarVal,String> eq = new TableColumn<>("");
        eq.setMaxWidth(200);
        eq.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>("="));

        TableColumn<VarVal, String> value= new TableColumn<>("Значение");
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
        numberCol.setMaxWidth(200);
        numberCol.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(Integer.toString(getVariableController()
                .getAddVarDomainValTableView().getItems().indexOf(p.getValue()) + 1)));
        numberCol.setSortable(false);

        TableColumn<DomainValue, String> value= new TableColumn<>("Значение");
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


        getVariableController().getRadioRequest().fire();

        if(Main.getShell().getKnowledgeBase().getDomains().getList().size()>0)
            variableController.getDomainCombo().getSelectionModel().select(0);

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
        numberCol.setMaxWidth(200);
        numberCol.setCellValueFactory(p ->new ReadOnlyObjectWrapper<>(
                Integer.toString(getDomainController().getTableView().getItems().indexOf(p.getValue()) + 1)));
        numberCol.setSortable(false);

        TableColumn<DomainValue, String> value= new TableColumn<>("Значение");
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        getDomainController().getTableView().getColumns().addAll(numberCol, value);

        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
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
        getSem().release(100);
        getStartConsButton().setDisable(false);
        getStopConsButton().setDisable(true);
        getConfirmButton().setDisable(true);
        unblockTabs();
    }

    public void onConfirm(ActionEvent actionEvent) {
        getConfirmButton().setDisable(true);
        int i=0;
        List<Node> l=getAnswerList().getChildren();
        while (i<l.size() && !((RadioButton)l.get(i)).isSelected()) i++;
        setAnswer(i==l.size()?"":((RadioButton)l.get(i)).getText());
        getConsTabQuestionTextArea().appendText(String.format("ответ: %s\n\n",getAnswer()));
        getSem().release();
    }

    public void onStartCons(ActionEvent actionEvent) throws InterruptedException {
        Main.perror("Cons started");
        blockTabs();
        getExplTabTableView().getItems().clear();
        getConsTabQuestionTextArea().clear();
        getAnswerList().setSpacing(10);


        getTreeView().setShowRoot(false);

        getTreeView().setRoot(null);
        if(getConsVarCombo().getSelectionModel().isEmpty()) return;
        getStopConsButton().setDisable(false);
        getStartConsButton().setDisable(true);
        Variable selVar=getConsVarCombo().getSelectionModel().getSelectedItem();
        setSem(new Semaphore(0, true));

        getAnswerList().requestFocus();
        getTargetTextArea().setText(String.format("Цель Консультации: \'%s\'\n",selVar.getName()));
        Main.getShell().startCons(selVar);

//        getConsTabLogTreeView().scrollTo(getConsTabLogTreeView().getExpandedItemCount()-1);
    }

    public String askVar(Variable var) throws InterruptedException {

        Platform.runLater(() -> {
            String question=var.getQuestion().trim().equals("")?var.getName()+"?":var.getQuestion();
            getConsTabQuestionTextArea().appendText("вопрос: "+question+"\n");
            getAnswerList().getChildren().clear();
            ToggleGroup group = new ToggleGroup();
            var.getDomain().getValues().getList().forEach(x->{
                RadioButton rb=new RadioButton(x.getValue());
                rb.setToggleGroup(group);
                rb.setPrefWidth(1000);
                rb.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE,new CornerRadii(10), Insets.EMPTY)));
                getAnswerList().getChildren().add(rb);
                rb.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if(rb.isSelected()){
                        rb.setBackground(new Background(new BackgroundFill(Color.GRAY,new CornerRadii(10), Insets.EMPTY)));
                    }else {
                        rb.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE,new CornerRadii(10), Insets.EMPTY)));
                    }
                });

            });
            getConfirmButton().setDisable(false);
            if(getAnswerList().getChildren().size()>0){
                getAnswerList().getChildren().get(0).requestFocus();
                ((RadioButton)getAnswerList().getChildren().get(0)).setSelected(true);
            }

        });


        getSem().acquire();
        String res=getAnswer();
        return res;

    }

    public void resetCons(){
        getStartConsButton().setDisable(false);
        getStopConsButton().setDisable(true);
        getConfirmButton().setDisable(true);
        getAnswerList().getChildren().clear();
        unblockTabs();

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
        if (getSem()!=null) {
//            System.out.println(getSem().availablePermits());
            getSem().release(100);
//            System.out.println(getSem().availablePermits());
        }
        Main.getStage().close();
    }

    public TextArea getTargetTextArea() {
        return targetTextArea;
    }

    public Tab getDomsTab() {
        return domsTab;
    }

    public Tab getVarsTab() {
        return varsTab;
    }

    public Tab getRulesTab() {
        return rulesTab;
    }

    public Tab getConsTab() {
        return consTab;
    }

    public Tab getReasTab() {
        return reasTab;
    }

    public TreeView<String> getTreeView() {
        return treeView;
    }

    public TableView getExplTabTableView() {
        return explTabTableView;
    }

    public Button getExpandButton() {
        return expandButton;
    }

    private void blockTabs(){
        getRulesTab().setDisable(true);
        getVarsTab().setDisable(true);
        getDomsTab().setDisable(true);
        getReasTab().setDisable(true);
    }
    private void unblockTabs(){
        getRulesTab().setDisable(false);
        getVarsTab().setDisable(false);
        getDomsTab().setDisable(false);
        getReasTab().setDisable(false);
    }

    public void appendExplVarTableView(Variable var,DomainValue val){
        getExplTabTableView().getItems().add(new VarVal(var,val));
    }

    public void expandCollapse(ActionEvent actionEvent) {
        TreeItem r=getTreeView().getRoot();
        if(treeView==null || r==null) return;
        if (getExpandButton().getText().equals("Развернуть")) {
            collapse(getTreeView().getRoot());
            getExpandButton().setText("Свернуть");
        }
        else{
            getTreeView().getRoot().getChildren().forEach(x->{
                x.setExpanded(false);
            });
            getExpandButton().setText("Развернуть");
        }
    }

    private void collapse(TreeItem<String> root){
        if (root==null) return;
        root.setExpanded(true);
        if(root.getChildren().size()!=0)
            root.getChildren().forEach(this::collapse);
    }

    public VBox getErrorVbox() {
        return errorVbox;
    }

    public Stage alertStageBuilder(String message){
        Stage stage=new Stage();
        Parent root;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(getClass().getResource("DialogBox.fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        dialogBoxController=loader.getController();
        dialogBoxController.getTArea().setText(message);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    public boolean alert(String m){
        Stage stage=alertStageBuilder(m);
        stage.showAndWait();
        return dialogBoxController.getRes();
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
        Rule selRule=getRuleTableView().getSelectionModel().getSelectedItem();
        boolean sure=alert(String.format("Удалить правило \"%s\"?",selRule.getRealName()));
        if(sure)
            Main.getShell().getKnowledgeBase().getRules().remove(selRule.getName());
    }

    public ComboBox<Variable> getConsVarCombo() {
        return consVarCombo;
    }

    public VBox getAnswerList() {
        return answerList;
    }
}
