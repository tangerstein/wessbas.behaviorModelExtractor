package net.sf.markov4jmeter.behaviormodelextractor.test;

import net.sf.markov4jmeter.behavior.AbstractBehaviorModelGraph;
import net.sf.markov4jmeter.behavior.Transition;
import net.sf.markov4jmeter.behavior.UseCase;
import net.sf.markov4jmeter.behavior.Vertex;

import org.eclipse.emf.common.util.EList;

/**
 * This class provides some "pretty printing" methods for testing purposes only.
 *
 * @author Eike Schulz (esc@informatik.uni-kiel.de)
 *
 * @version 1.0
 */
public class PrettyPrinter {


    /* *****************************  constants  **************************** */


    /** Template for use case output. */
    private final static String USE_CASE_TEMPLATE = "\"%s\" (\"%s\")";


    /* **************************  public methods  ************************** */


    /**
     * Prints a Behavior Model graph on standard output stream.
     *
     * @param abstractBehaviorModelGraph  Behavior Model graph to be printed.
     */
    public static void print (
            final AbstractBehaviorModelGraph abstractBehaviorModelGraph) {

        final EList<Vertex> vertices = abstractBehaviorModelGraph.getVertices();

        for (final Vertex vertex : vertices) {

            PrettyPrinter.print(vertex);

            final EList<Transition> transitions = vertex.getOutgoingTransitions();

            for (final Transition transition : transitions) {

                PrettyPrinter.print(transition);
            }
        }
    }


    /* **************************  private methods  ************************* */


    /**
     * Prints a vertex on standard output stream.
     *
     * @param vertex  vertex to be printed.
     */
    private static void print (final Vertex vertex) {

        final UseCase useCase = vertex.getUseCase();

        if (useCase != null) {  // final vertex has no use case;

            final String output = String.format(
                    PrettyPrinter.USE_CASE_TEMPLATE,
                    useCase.getName(),
                    useCase.getId());

            System.out.println(output);

        } else {

            System.out.println("\"$\"");
        }
    }

    /**
     * Prints a transition on standard output stream.
     *
     * @param transition  transition to be printed.
     */
    private static void print (final Transition transition) {

        final double value = transition.getValue();
        final Vertex targetVertex = transition.getTargetVertex();

        System.out.print(" [" + value + "] ");
        PrettyPrinter.print(targetVertex);
    }
}
