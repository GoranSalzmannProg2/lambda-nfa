package de.uni_passau.fim.prog2.lambda_nfa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * The class representing a single state inside an automaton. Transitions are
 * organized inside an adjacency list, and a nextSet may be precomputed and
 * saved for optimizing performance.
 */
public class State {
    private final Integer number;
    private final List<Collection<Transition>> charAdj;
    private Collection<State> nextSet = new HashSet<>();

    /**
     * Constructor creating dummy states by setting all fields to null.
     */
    public State() {
        this.number = null;
        this.charAdj = null;
        this.nextSet = null;
    }

    /**
     * Default constructor for the State class. Immediately creates the
     * adjacency list data structure.
     *
     * @param number The number which the state is identified by.
     */
    public State(int number) {
        this.number = number;
        int capacity = LambdaNFA.ALPHABET_LENGTH + 1;
        this.charAdj = new ArrayList<>(capacity);
        for (int i = 0; i <= LambdaNFA.ALPHABET_LENGTH; i++) {
            this.charAdj.add(null);
        }
    }

    /**
     * Method for calculating an index in the adjacency list from a symbol.
     *
     * @param symbol Symbol in the automaton's alphabet or lambda.
     * @return Index of the list inside the adjacency list.
     */
    public static int getSymbolIndex(char symbol) {
        if (symbol == LambdaNFA.LAMBDA_SYMBOL) {
            return 0;
        } else {
            return symbol - LambdaNFA.FIRST_SYMBOL + 1;
        }
    }

    /**
     * Calculates a set of states which can be reached from the current state
     * only using lambda transitions. Immediately saves list inside state.
     */
    public void precomputeNextSet() {
        this.nextSet = new LinkedList<>();
        Map<State, Boolean> visited = new HashMap<>();
        Queue<State> bfsQueue = new LinkedList<>();
        bfsQueue.offer(this);
        visited.put(this, true);
        while (!bfsQueue.isEmpty()) {
            State state = bfsQueue.poll();
            if (state != this) {
                this.nextSet.add(state);
            }
            Collection<State> lambdaTargets =
                    state.getTargets(LambdaNFA.LAMBDA_SYMBOL);
            for (State lambdaTarget : lambdaTargets) {
                if (!visited.getOrDefault(lambdaTarget, false)) {
                    bfsQueue.offer(lambdaTarget);
                    visited.put(lambdaTarget, true);
                }
            }
        }
    }

    /**
     * Calculates a set of states that can be reached by taking one
     * transition over a given symbol.
     *
     * @param symbol The symbol identifying the transitions to be used.
     * @return The set of all reachable states over a single transition with
     * a given symbol.
     */
    public Collection<State> getTargets(char symbol) {
        Collection<Transition> transitions =
                this.charAdj.get(State.getSymbolIndex(symbol));
        Collection<State> targets = new HashSet<>();
        if (transitions == null || transitions.isEmpty()) {
            return targets;
        }
        transitions.forEach(transition -> targets.add(transition.getTarget()));
        return targets;
    }

    /**
     * Inserts a transition object into the adjacency list.
     *
     * @param transition The transition object that should be inserted.
     */
    public void addTransition(Transition transition) {
        int index = State.getSymbolIndex(transition.getSymbol());
        Collection<Transition> transitions = this.charAdj.get(index);
        if (transitions == null) {
            transitions = new LinkedList<>();
            this.charAdj.set(index, transitions);
        }
        transitions.add(transition);
    }

    /**
     * Generates string representation of the state.
     *
     * @return Sorted string containing all transitions that go out of this
     * state.
     */
    @Override
    public String toString() {
        List<Transition> transitions = getOrderedTransitions();
        StringBuilder sb = new StringBuilder();
        transitions.forEach(sb::append);
        return sb.toString();
    }

    /**
     * Default getter for the nextSet field
     *
     * @return A set of states that can be reached from this state by only
     * using lambda transitions.
     */
    public Collection<State> getNextSet() {
        return this.nextSet;
    }

    /**
     * Default getter for the number field.
     *
     * @return The number this state is identified by.
     */
    public int getNumber() {
        return this.number;
    }

    private List<Transition> getTransitions() {
        List<Transition> transitions = new ArrayList<>();
        for (Collection<Transition> collection : this.charAdj) {
            if (collection != null) {
                transitions.addAll(collection);
            }
        }
        return transitions;
    }

    private List<Transition> getOrderedTransitions() {
        List<Transition> transitions = getTransitions();
        Collections.sort(transitions);
        return transitions;
    }
}
