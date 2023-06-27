package de.uni_passau.fim.prog2.lambda_nfa;

/**
 * Class modelling a transition between states inside an automaton.
 */
public class Transition implements Comparable<Transition> {
    private final State source;
    private final State target;
    private final char symbol;

    /**
     * General purpose constructor for the transition class, setting all fields.
     *
     * @param source The state from which the transition should start.
     * @param target The state to which the transition should go.
     * @param symbol The symbol in the alphabet that needs to be supplied to
     *               use the transition, or the symbol that lambda is
     *               identified by.
     */
    public Transition(State source, State target,
                      char symbol) {
        this.source = source;
        this.target = target;
        this.symbol = symbol;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required
     * that {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking,
     * any class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    @Override
    public int compareTo(Transition o) {
        if (this.target.getNumber() < o.target.getNumber()) {
            return -1;
        } else if (this.target.getNumber() > o.target.getNumber()) {
            return 1;
        } else {
            int self = State.getSymbolIndex(this.symbol);
            int other = State.getSymbolIndex(o.symbol);
            return Integer.signum(self - other);
        }
    }

    /**
     * Default getter for the target field.
     *
     * @return The state which the transition goes to.
     */
    public State getTarget() {
        return this.target;
    }

    /**
     * Default getter for the symbol field.
     *
     * @return The character used to take the transition or the symbol that
     * identifies lambda.
     */
    public char getSymbol() {
        return this.symbol;
    }

    /**
     * Generates string representation of the transition.
     *
     * @return String containing source and target state as well as the
     * symbol used in the transition.
     */
    @Override
    public String toString() {
        return "(" + this.source.getNumber() + ", " + this.target.getNumber()
                + ") " + symbol + System.lineSeparator();
    }
}
