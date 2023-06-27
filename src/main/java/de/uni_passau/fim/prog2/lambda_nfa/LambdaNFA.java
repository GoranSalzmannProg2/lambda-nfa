package de.uni_passau.fim.prog2.lambda_nfa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * A nondeterministic implementation for an automaton including lambda
 * transitions.
 */
public class LambdaNFA implements Automaton {
    /**
     * The symbol that represents the lambda transition symbol. May not be
     * included in the automaton's alphabet.
     */
    public static final char LAMBDA_SYMBOL = '~';
    /**
     * The length of the automaton's alphabet. Calculated by finding the
     * interval length between the first and last symbol in the alphabet.
     */
    public static final int ALPHABET_LENGTH = LAST_SYMBOL - FIRST_SYMBOL + 1;

    private final State[] states;
    private final State startingState;
    private final List<State> endStates;

    /**
     * General purpose constructor for the lambda automaton. Will initialize
     * the automaton with some number of states, initializing all of them
     * immediately.
     *
     * @param stateCount    Number of states to automaton should consist of.
     * @param startingState The number of the state where the automaton starts.
     * @param endStates     A list of numbers describing which states should be
     *                      the final states of the automaton.
     */
    public LambdaNFA(int stateCount, int startingState,
                     List<Integer> endStates) {
        this.states = new State[stateCount + 1];
        this.states[0] = null;
        for (int i = 1; i <= stateCount; i++) {
            this.states[i] = new State(i);
        }
        this.startingState = this.states[startingState];
        this.endStates = new ArrayList<>();
        for (Integer i : endStates) {
            this.endStates.add(this.states[i]);
        }
    }

    /**
     * Checks a transition on validity, i.e., if it can be part of the
     * automaton.
     *
     * @param source The id of the source state.
     * @param target The id of the target state.
     * @param symbol The symbol to be read.
     * @return {@code true} if and only if the parameters represent a valid
     * transition of this automation.
     */
    @Override
    public boolean isValidTransition(int source, int target, char symbol) {
        if (source >= this.states.length || target >= this.states.length
                || source <= 0 || target <= 0) {
            return false;
        }
        return (symbol <= LAST_SYMBOL && symbol >= FIRST_SYMBOL)
                || symbol == LAMBDA_SYMBOL;
    }

    /**
     * Adds a transition to the automaton. Multitransitions, i.e. transitions
     * with equal {@code source}s, {@code target}s and {@code symbol}s, and
     * self loops, i.e., with {@code source} equal to {@code target} are
     * explicitly allowed. Needs O(1) time.
     *
     * @param source The source state.
     * @param target The target state.
     * @param symbol The symbol to read.
     */
    @Override
    public void addTransition(int source, int target, char symbol) {
        Transition transition = new Transition(this.states[source],
                this.states[target], symbol);
        this.states[source].addTransition(transition);
        for (int i = 1; i < this.states.length; i++) {
            this.states[i].precomputeNextSet();
        }
    }

    /**
     * Decides the element problem for the regular language defined by this
     * automaton.
     *
     * @param word The word to check.
     * @return {@code true} if and only if {@code word} is in the language.
     */
    @Override
    public boolean isElement(String word) {
        return Objects.equals(longestPrefix(word), word);
    }

    /**
     * Computes the longest prefix of {@code word} which is an element of the
     * language. Can be the {@code word} itself, if and only if
     * {@link Automaton#isElement(String)} called on it returns {@code true}.
     *
     * @param word The word whose prefixes will be checked.
     * @return The longest prefix, or {@code null} if none exists.
     */
    @Override
    public String longestPrefix(String word) {
        final State separator = new State();
        Queue<State> queue = new LinkedList<>();
        queue.offer(separator);
        queue.offer(this.startingState);
        for (State s : this.startingState.getNextSet()) {
            queue.offer(s);
        }
        int cursor = -1;
        char symbol = 0;
        String prefix = null;
        while (!queue.isEmpty()) {
            State state = queue.poll();
            if (this.endStates.contains(state)) {
                prefix = word.substring(0, cursor);
            }
            if (state == separator) {
                ++cursor;
                if (cursor < word.length()) {
                    queue.offer(separator);
                    symbol = word.charAt(cursor);
                }
            } else if (this.endStates.contains(state)
                    && cursor == word.length()) {
                prefix = word;
            } else if (cursor < word.length()) {
                Collection<State> set = new HashSet<>();
                for (State s : state.getTargets(symbol)) {
                    set.add(s);
                    set.addAll(s.getNextSet());
                }
                for (State s : set) {
                    queue.offer(s);
                }
            }

        }
        return prefix;
    }

    /**
     * Generates a string representation of the automaton.
     *
     * @return A string including all transitions in the automaton.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < this.states.length; i++) {
            sb.append(this.states[i]);
        }
        return sb.toString();
    }
}
