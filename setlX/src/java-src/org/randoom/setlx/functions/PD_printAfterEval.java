package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// printAfterEval(toggle)    : configures output of results after each evaluation of an statement containing an expression

public class PD_printAfterEval extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_printAfterEval();

    private PD_printAfterEval() {
        super("printAfterEval");
        addParameter("toggle");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException {
        Value   toggle  = args.get(0);
        if ( ! (toggle instanceof SetlBoolean)) {
            throw new IncompatibleTypeException(
                "toggle-argument '" + toggle + "' is not a Boolean value."
            );
        }

        if (toggle == SetlBoolean.TRUE) {
            Environment.setPrintAfterEval(true);
        } else /* if (toggle == SetlBoolean.FALSE) */ {
            Environment.setPrintAfterEval(false);
        }

        // everything seems fine
        return toggle;
    }
}
