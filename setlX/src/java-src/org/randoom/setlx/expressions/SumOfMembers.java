package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

/*
grammar rule:
prefixOperation
    : '+/' factor
    | [...]
    ;

implemented here as:
           ======
           mExpr
*/

public class SumOfMembers extends Expr {
    // functional character used in terms (MUST be class name starting with lower case letter!)
    private final static String FUNCTIONAL_CHARACTER = "^sumOfMembers";
    // precedence level in SetlX-grammar
    private final static int    PRECEDENCE           = 1900;

    private final Expr mExpr;

    public SumOfMembers(final Expr expr) {
        mExpr = expr;
    }

    protected Value evaluate() throws SetlException {
        return mExpr.eval().sumOfMembers(Om.OM);
    }

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    protected void collectVariables (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    ) {
        mExpr.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
    }

    /* string operations */

    public void appendString(final StringBuilder sb, final int tabs) {
        sb.append("+/");
        mExpr.appendString(sb, tabs);
    }

    /* term operations */

    public Term toTerm() {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 1);
        result.addMember(mExpr.toTerm());
        return result;
    }

    public static SumOfMembers termToExpr(final Term term) throws TermConversionException {
        if (term.size() != 1) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final Expr expr = TermConverter.valueToExpr(PRECEDENCE, false, term.firstMember());
            return new SumOfMembers(expr);
        }
    }

    // precedence level in SetlX-grammar
    public int precedence() {
        return PRECEDENCE;
    }
}

