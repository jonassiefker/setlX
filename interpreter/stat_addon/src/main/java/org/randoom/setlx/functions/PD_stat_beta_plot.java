package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.plot.types.Canvas;
import org.randoom.setlx.plot.utilities.ConnectJFreeChart;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.Defaults;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * stat_beta_plot(alpha, beta, canvas):
 *                  Plots the probability density function for beta distributions with given parameters 'alpha' and 'beta' on a given canvas.
 */
public class PD_stat_beta_plot extends PreDefinedProcedure {

    private final static ParameterDefinition ALPHA       = createParameter("alpha");
    private final static ParameterDefinition BETA        = createParameter("beta");
    private final static ParameterDefinition CANVAS      = createParameter("canvas");
    private final static ParameterDefinition LOWER_BOUND = createOptionalParameter("lowerBound", Defaults.createSetlDoubleValue(0));
    private final static ParameterDefinition INTERVAL    = createOptionalParameter("interval", Defaults.getDefaultPlotInterval());
    private final static ParameterDefinition UPPER_BOUND = createOptionalParameter("upperBound", Defaults.createSetlDoubleValue(1));

    /** Definition of the PreDefinedProcedure 'stat_beta_plot' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_beta_plot();

    private PD_stat_beta_plot() {
        super();
        addParameter(ALPHA);
        addParameter(BETA);
        addParameter(CANVAS);
        addParameter(LOWER_BOUND);
        addParameter(INTERVAL);
        addParameter(UPPER_BOUND);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value alpha      = args.get(ALPHA);
        final Value beta       = args.get(BETA);
        final Value canvas     = args.get(CANVAS);
        final Value lowerBound = args.get(LOWER_BOUND);
        final Value interval   = args.get(INTERVAL);
        final Value upperBound = args.get(UPPER_BOUND);


        Checker.checkIfNumberAndGreaterZero(state, alpha, beta);
        Checker.checkIfNumber(state, lowerBound, upperBound);
        Checker.checkIfUpperBoundGreaterThanLowerBound(state, lowerBound, upperBound);
        Checker.checkIfNumberAndGreaterZero(state, interval);
        Checker.checkIfCanvas(state, canvas);

        double minLowerBound = lowerBound.toJDoubleValue(state);
        double maxUpperBound = upperBound.toJDoubleValue(state);

        // The function is only defined for Interval = [0,1]. Therefore the bounds will be set to [0,1], if the bounds are x1 < 0 or x2 > 1.
        if (minLowerBound < 0 || minLowerBound > 1) {
            minLowerBound = 0;
            // Since the distribution is not defined for x == 0 and x == 1 if one of the parameters is less than 1, the lower bound is set to a value > 0 here.
            if (alpha.toJDoubleValue(state) < 1 || beta.toJDoubleValue(state) < 1) {
                minLowerBound = interval.toJDoubleValue(state);
            }
        }
        if (maxUpperBound > 1 || maxUpperBound < 0) {
            maxUpperBound = 1;
        }

        state.outWrite("This function is only defined for the interval [0,1]. The bounds have been set accordingly.\n\n");

        BetaDistribution bd = new BetaDistribution(alpha.toJDoubleValue(state), beta.toJDoubleValue(state));
;
        /** The valueList is the list of every pair of coordinates [x,y] that the graph consists of.
         *  It is filled by iteratively increasing the variable 'counter' (x), and calculating the density for every new value of 'counter' (y).
         */
        List<List<Double>> valueList = new ArrayList<>();
        for (double counter = minLowerBound; counter < maxUpperBound; counter += interval.toJDoubleValue(state)) {
            valueList.add(new ArrayList<Double>(Arrays.asList(counter, bd.density(counter))));
        }

        return ConnectJFreeChart.getInstance().addListGraph((Canvas) canvas, valueList, "Probability Density Function (alpha: " + alpha.toString() +" and beta: " + beta.toString() + ")", Defaults.DEFAULT_COLOR_SCHEME, false);
    }
}
