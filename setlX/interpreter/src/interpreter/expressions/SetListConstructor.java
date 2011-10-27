package interpreter.expressions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.exceptions.UndefinedOperationException;
import interpreter.types.SetlList;
import interpreter.types.SetlOm;
import interpreter.types.SetlSet;
import interpreter.types.Value;
import interpreter.utilities.Constructor;

import java.util.List;

public class SetListConstructor extends Expr {
    public final static int LIST  = 23;
    public final static int SET   = 42;

    private int         mType;
    private Constructor mConstructor;

    public SetListConstructor(int type, Constructor constructor) {
        mType        = type;
        mConstructor = constructor;
    }

    public Value evaluate() throws SetlException {
        Value result = null;
        if (mType == SET) {
            SetlSet set = new SetlSet();
            if (mConstructor != null) {
                mConstructor.fillCollection(set);
            }
            result = set;
        } else if (mType == LIST) {
            SetlList list = new SetlList();
            if (mConstructor != null) {
                mConstructor.fillCollection(list);
            }
            list.compress();
            result = list;
        } else {
            result = SetlOm.OM;
        }
        return result;
    }

    // sets this expression to the given value
    public void assign(Value v) throws SetlException {
        if (v instanceof SetlList) {
            if (mType == LIST) {
                mConstructor.setIds((SetlList) v);
            } else {
                throw new UndefinedOperationException("Error in '" + this + "':\n"
                                                +     "Only explicit lists of variables can be used as targets for list assignments.");
            }
        } else {
            throw new IncompatibleTypeException("The value `" + v + "´ is unusable for assignment to '" + this + "'.");
        }
    }

    public String toString(int tabs) {
        String r;
        if (mType == SET) {
            r = "{";
        } else if (mType == LIST) {
            r = "[";
        } else {
            r = "";
        }
        if (mConstructor != null) {
            r += mConstructor.toString(tabs);
        }
        if (mType == SET) {
            r += "}";
        } else if (mType == LIST) {
            r += "]";
        }
        return r;
    }
}
