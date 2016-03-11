package sample;

/**
 * Created by ваа on 02.03.2016.
 */
public class KBFactory {

    public KB make(){
        KB kb=new KB();
        kb.getDomains().setKb(kb);
        kb.getRules().setKb(kb);
        kb.getVariables().setKb(kb);
        return kb;
    }
}
