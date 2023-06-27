package de.uni_passau.fim.prog2.lambda_nfa;

import java.util.List;
import java.util.Scanner;

/**
 * Command line interface used to communicate with a NFA. Enforces additional
 * conventions inside the automaton.
 */
final class Shell {
    private static final String PROMPT = "nfa> ";

    private static LambdaNFA nfa = null;
    private static boolean running = true;

    private Shell() throws InstantiationException {
        throw new InstantiationException("Shell may not be instantiated.");
    }

    /**
     * Entrypoint for the program.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (running) {
            System.out.print(PROMPT);
            String userInput = in.nextLine();
            if (userInput == null || userInput.length() == 0) {
                printErr("Invalid! Try again.");
                continue;
            }
            String[] slices = userInput.trim().split("\s+");
            if (slices.length == 0) {
                printErr("Invalid! Try again.");
                continue;
            }
            String command = slices[0];
            switch (command.toUpperCase()) {
                case "INIT" -> tryInit(slices);
                case "ADD" -> tryAdd(slices);
                case "CHECK" -> checkCommand(userInput);
                case "PREFIX" -> prefixCommand(userInput);
                case "DISPLAY" -> displayCommand();
                case "GENERATE" -> generateCommand();
                case "HELP" -> helpCommand();
                case "QUIT" -> quitCommand();
                default -> printErr("Not a valid command.");
            }
        }
    }

    private static void tryInit(String[] slices) {
        if (slices.length < 2) {
            printErr("Not enough arguments supplied.");
            return;
        }
        try {
            int size = Integer.parseInt(slices[1]);
            if (size > 0) {
                initCommand(size);
            } else {
                printErr("Size has to be greater than 0.");
            }
        } catch (NumberFormatException e) {
            printErr("First argument has to be an integer");
        }
    }

    private static void initCommand(Integer size) {
        nfa = new LambdaNFA(size, 1, List.of(size));
    }

    private static void tryAdd(String[] slices) {
        if (slices.length < 4) {
            printErr("Not enough arguments supplied.");
            return;
        }
        if (slices[3].length() > 1) {
            printErr("Third argument needs to be a character.");
            return;
        }
        try {
            int source = Integer.parseInt(slices[1]);
            int target = Integer.parseInt(slices[2]);
            char symbol = slices[3].charAt(0);
            addCommand(source, target, symbol);
        } catch (NumberFormatException e) {
            printErr("First and second argument have to be an integer");
        }
    }

    private static void addCommand(Integer source, Integer target,
                                   char symbol) {
        if (nfa == null) {
            printErr("NFA has not been initialized.");
            return;
        }
        if (nfa.isValidTransition(source, target, symbol)) {
            nfa.addTransition(source, target, symbol);
        } else {
            printErr("Transition provided is not valid.");
        }
    }

    private static void checkCommand(String userInput) {
        String word = extractWord(userInput);
        if (word == null) return;
        boolean isElement = nfa.isElement(word);
        System.out.println(isElement ? "In language." : "Not in language.");
    }

    private static void prefixCommand(String userInput) {
        String word = extractWord(userInput);
        if (word == null) return;
        String prefix = nfa.longestPrefix(word);
        if (prefix == null) {
            System.out.println("No prefix in language.");
        } else {
            System.out.println("\"" + prefix + "\"");
        }
    }

    private static void displayCommand() {
        if (nfa == null) {
            printErr("NFA has not been initialized.");
        } else {
            System.out.print(nfa);
        }
    }

    private static void generateCommand() {
        nfa = new LambdaNFA(5, 1, List.of(5));
        nfa.addTransition(1, 2, '~');
        nfa.addTransition(2, 2, '~');
        nfa.addTransition(2, 3, 'a');
        nfa.addTransition(3, 4, 'b');
        nfa.addTransition(3, 4, '~');
        nfa.addTransition(4, 5, 'a');
        nfa.addTransition(2, 4, '~');
        nfa.addTransition(4, 1, '~');
    }

    private static void helpCommand() {
        System.out.println("""
                Lambda NFA:
                Available Commands:
                                
                                
                INIT <n>:           Generates new automaton with n states.
                                
                ADD <i> <j> <c>:    Adds a new transition from state i to state
                                    j with symbol c.
                                    
                CHECK "s":          Checks if a given word s is in the language
                                    of L(A); A being the current automaton.
                                    
                PREFIX "s":         Prints the longest prefix of s that is part
                                    of the language L(A); A being the current
                                    automaton.
                                    
                DISPLAY:            Prints all transitions that make up the
                                    automaton in a sorted list.
                                 
                GENERATE:           Loads a predefined automaton.
                                
                HELP:               Prints a help dialog, showing all available
                                    commands and their use.
                                    
                QUIT:               Exits the program.
                """);
    }

    private static void quitCommand() {
        running = false;
    }

    private static String extractWord(String userInput) {
        if (nfa == null) {
            printErr("NFA has not been initialized.");
            return null;
        }
        if (!userInput.matches(".*\".*\".*")) {
            printErr("Word has to be wrapped in double quotes (\"w\") ");
            return null;
        }
        return userInput.substring(userInput.indexOf("\"") + 1,
                userInput.lastIndexOf("\""));
    }

    private static void printErr(String msg) {
        System.out.println("Error! " + msg);
    }

}
