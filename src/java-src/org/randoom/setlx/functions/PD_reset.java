package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.ResetException;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

// reset()                       : DEBUG: stop execution and return to interactive prompt

public class PD_reset extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_reset();

    private PD_reset() {
        super("reset");
    }

    public Value execute(final State state, List<Value> args, List<Value> writeBackVars) throws ResetException {
        throw new ResetException("reset");
    }
}
