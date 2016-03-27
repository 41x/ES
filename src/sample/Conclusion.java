package sample;

import java.io.Serializable;

/**
 * Created by ваа on 02.03.2016.
 */
public class Conclusion implements Serializable {
    private VarVal varval;

    public Conclusion(VarVal varval) {
        this.varval = varval;
    }

    public VarVal getVarval() {
        return varval;
    }

    public void setVarval(VarVal varval) {
        this.varval = varval;
    }
}
