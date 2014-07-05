package net.sf.markov4jmeter.behaviormodelextractor.extraction.transformation;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import net.sf.markov4jmeter.behavior.AbstractBehaviorModelGraph;
import net.sf.markov4jmeter.behavior.BehaviorModelRelative;
import net.sf.markov4jmeter.behavior.Transition;
import net.sf.markov4jmeter.behavior.UseCase;
import net.sf.markov4jmeter.behavior.Vertex;
import net.sf.markov4jmeter.behaviormodelextractor.extraction.MarkovMatrixHandler;

/**
 * This class provides methods for transforming "relative" Behavior Models to
 * Markov Matrices.
 *
 * <p>Relative Behavior Models are equipped with probabilities and think times.
 * They correspond to Behavior Models as defined for Markov4JMeter; the prefix
 * "Relative" just emphasizes their difference to "absolute" Behavior Models,
 * from which they originate.
 *
 * @author Eike Schulz (esc@informatik.uni-kiel.de)
 *
 * @version 1.0
 */
public class RBMToMarkovMatrixTransformer {


    /* *************************  global variables  ************************* */


    /** Instance for creating and modifying Markov matrices. */
    private final MarkovMatrixHandler markovMatrixHandler;


    /* ***************************  constructors  *************************** */


    /**
     * Constructor for an RBM-to-(Markov-)matrix Transformer.
     *
     * @param markovMatrixHandler
     *     instance for creating and modifying Markov matrices.
     */
    public RBMToMarkovMatrixTransformer (
            final MarkovMatrixHandler markovMatrixHandler) {

        this.markovMatrixHandler = markovMatrixHandler;
    }


    /* **************************  public methods  ************************** */


    /**
     * Transforms a given "relative" Behavior Model to a Markov matrix.
     *
     * @param behaviorModelRelative
     *     the Behavior Model to be transformed.
     *
     * @return
     *     the resulting Markov matrix.
     */
    public String[][] transform (
            final BehaviorModelRelative behaviorModelRelative) {

        final List<Vertex> vertices = behaviorModelRelative.getVertices();

        final LinkedList<String> states = new LinkedList<String>();

        for (final Vertex vertex : vertices) {

            states.add(this.getVertexName(vertex));
        }

        final String[][] matrix = this.markovMatrixHandler.
                createEmptyMarkovMatrixForStates(states);

        return this.storeModelInMatrix(behaviorModelRelative, matrix);
    }

    /**
     * Returns the associated instance for creating and modifying Markov
     * matrices.
     *
     * @return  the associated instance of {@link MarkovMatrixHandler}.
     */
    public MarkovMatrixHandler getMarkovMatrixHandler () {

        return markovMatrixHandler;
    }


    /* **************************  private methods  ************************* */


    /**
     * Fills a given Matrix matrix with the values, which are indicated by the
     * vertices of a given Behavior Model graph.
     *
     * @param abstractBehaviorModelGraph
     *     Behavior Model graph to be stored in a given Markov matrix.
     * @param matrix
     *     the Markov matrix to be filled with values.
     *
     * @return
     *     the filled Markov matrix.
     */
    private String[][] storeModelInMatrix (
            final AbstractBehaviorModelGraph abstractBehaviorModelGraph,
            final String[][] matrix) {

        final List<Vertex> vertices = abstractBehaviorModelGraph.getVertices();

        final String finalStateName =
                this.markovMatrixHandler.getFinalStateName();

        this.markovMatrixHandler.resetMatrix(matrix);

        for (final Vertex srcVertex : vertices) {

            double valueSum = 0;

            final String srcVertexName = this.getVertexName(srcVertex);

            if ( !finalStateName.equals(srcVertexName) ) {

                final List<Transition> transitions =
                        srcVertex.getOutgoingTransitions();

                for (final Transition transition : transitions) {

                    final double probability   = transition.getValue();
                    final Vertex dstVertex     = transition.getTargetVertex();
                    final String dstVertexName = this.getVertexName(dstVertex);

                    if ( !finalStateName.equals(dstVertexName) ) {

                        valueSum += probability;
                    }

                    final List<BigDecimal> times = transition.getTimes();

                    final double mean;
                    final double deviation;

                    if (times.size() == 2) {

                        mean      = times.get(0).doubleValue();
                        deviation = times.get(1).doubleValue();

                    } else {

                        mean      = 0.0d;
                        deviation = 0.0d;
                    }

                    this.markovMatrixHandler.setValueAtCell(
                            probability + "; n(" + (long)mean + " " +
                                                   (long)deviation + ")",
                            srcVertexName,
                            dstVertexName,
                            matrix);
                }

                this.markovMatrixHandler.setValueAtCell(
                        (1.0 - valueSum) + "; " +
                            this.markovMatrixHandler.getDefaultThinkTimeValue(),
                        srcVertexName,
                        finalStateName,
                        matrix);
            }
        }

        return matrix;
    }

    /**
     * Returns the name of the use case which is assigned to a given vertex.
     *
     * @param vertex
     *     the vertex whose use case's name shall be returned.
     *
     * @return
     *     the name of the vertex use case; if no use case is associated with
     *     the vertex, the name of the final state will be returned.
     */
    private String getVertexName (final Vertex vertex) {

        final UseCase useCase = vertex.getUseCase();

        if (useCase == null) {  // final vertex has no use case;

            return this.markovMatrixHandler.getFinalStateName();
        }

        return useCase.getName();
    }
}
