package sample;

import javafx.scene.control.TextArea;

/**
 * Created by alxAsus on 28.02.2016.
 */
public interface Ilim {
    public void infer(Variable var, TextArea logW,String indent) throws InterruptedException;
}
