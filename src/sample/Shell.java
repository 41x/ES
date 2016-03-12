package sample;

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
        memory=new WMemory();
        this.knowledgeBase = (new KBFactory()).make();
        ((MyLIM)lim).setMemory(memory);
        ((MyLIM)lim).setKb(knowledgeBase);
        this.lim = lim;
    }

    private KB deser(String path){
        KB kb = null;
        try
        {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            kb = (KB) in.readObject();
            in.close();
            fileIn.close();
            System.out.println(String.format("Deserialized %s",path));
            return kb;
        }catch(Exception i) {
            System.out.println(i.getMessage());
//            i.printStackTrace();
            return null;
        }
    }

    public void ser(String path){
        if(!path.matches(".*\\.kb$"))
            path+=".kb";

        try
        {
            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(knowledgeBase);
            out.close();
            fileOut.close();
            System.out.println(String.format("Serialized data in %s",path));
        }catch(Exception i) {
            System.out.println(String.format("Could not serialize %s",path));
//            i.printStackTrace();
            System.out.println(i.getMessage());
        }
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
        this.knowledgeBase = kb;
        return true;
    }

    public KB getKnowledgeBase() {
        return knowledgeBase;
    }

    public String ask(String question) {
        return Main.ask();
    }
}
