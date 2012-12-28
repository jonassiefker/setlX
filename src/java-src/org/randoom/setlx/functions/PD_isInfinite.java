package org.randoom.setlx.functions;

import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// isInfinite(value)       : test if value-type is infinity

public class PD_isInfinite extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isInfinite();

    private PD_isInfinite() {
        super("isInfinite");
        addParameter("value");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) {
        return args.get(0).isInfinity();
    }
}
