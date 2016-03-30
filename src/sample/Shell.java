package sample;

import javafx.scene.control.TextArea;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by alxAsus on 28.02.2016.
 */
public class Shell {
    private WMemory memory;
//    private MyLIM lim;
    private KB knowledgeBase;

    public Shell() {
        memory=new WMemory();
        setKB((new KBFactory()).make());
    }

    private KB deser(String path){
        KB kb = null;
        try
        {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            kb = (KB) in.readObject();
            kb.toWorkingState();
            in.close();
            fileIn.close();
            System.out.println(String.format("Deserialized %s",path));
            return kb;
        }catch(Exception i) {
            System.out.println(i.getMessage());
            i.printStackTrace();
            return null;
        }
    }

    public boolean ser(String path){
        if(!path.matches(".*\\.kb$"))
            path+=".kb";

        try
        {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(knowledgeBase.toSerializableState());
            out.close();
            fileOut.close();
            System.out.println(String.format("Serialized data in %s",path));
        }catch(Exception i) {
            System.out.println(String.format("Could not serialize %s",path));
//            i.printStackTrace();
            System.out.println(i.getMessage());
            return false;
        }
        return true;
    }

    public boolean setKnowledgeBase(String path) {
        KB kb=deser(path);
        if (kb==null){
            System.out.println(String.format("Could not deserialize %s",path));
            return false;
        }
        return setKB(kb);
    }

    public boolean setKB(KB kb) {
        this.knowledgeBase = kb;
        Main.getController().getDomainTableView().setItems(getKnowledgeBase().getDomains().getList());
        Main.getController().getVarTableView().setItems(getKnowledgeBase().getVariables().getList());
        Main.getController().getRuleTableView().setItems(getKnowledgeBase().getRules().getList());
        return true;
    }



    public KB getKnowledgeBase() {
        return knowledgeBase;
    }

//    public String ask(Variable var) throws InterruptedException {
//        return Main.ask(var);
//    }

    public WMemory getMemory() {
        return memory;
    }

    public void startCons(Variable v) throws InterruptedException {
        Runnable lim=new MyLIM(memory.clear(),knowledgeBase,v);
        new Thread(lim).start();
    }

}
