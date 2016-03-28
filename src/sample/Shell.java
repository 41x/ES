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
    private Ilim lim;
    private KB knowledgeBase;

    public Shell(Ilim lim) {
        this.lim = lim;
        memory=new WMemory();
        ((MyLIM)lim).setMemory(memory);
        setKB((new KBFactory()).make());
//        ((MyLIM)lim).setKb(knowledgeBase);
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



    public void setLim(Ilim lim) {
        this.lim = lim;
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
        ((MyLIM)getLim()).setKb(kb);
        Main.getController().getDomainTableView().setItems(getKnowledgeBase().getDomains().getList());
        Main.getController().getVarTableView().setItems(getKnowledgeBase().getVariables().getList());
        Main.getController().getRuleTableView().setItems(getKnowledgeBase().getRules().getList());
        return true;
    }



    public KB getKnowledgeBase() {
        return knowledgeBase;
    }

    public String ask(Variable var) throws InterruptedException {
        return Main.ask(var);
    }

    public Ilim getLim() {
        return lim;
    }

    public WMemory getMemory() {
        return memory;
    }

    public String startCons(Variable v, TextArea logW) throws InterruptedException {
        getLim().infer(v,logW,"");
        String res=getMemory().getFact(v.getName());
        return res.equals("")?String.format("Given the information, value of the variable %s is unknown :(",v.getName())
                :res;
    }
}
