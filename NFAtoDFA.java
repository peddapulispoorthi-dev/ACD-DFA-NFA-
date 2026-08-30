import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class NFAToDFA {

    static Map<String, Map<String, Set<String>>> nfa = new HashMap<>();

    static Set<String> alphabet = new LinkedHashSet<>();

    static List<Set<String>> dfaStates = new ArrayList<>();

    static Map<String, Map<String, String>> dfaTransitions = new LinkedHashMap<>();

    static Set<String> finalStates = new HashSet<>();


    public static void main(String[] args) throws Exception {

        File folder = new File("diagrams");
        folder.mkdirs();

        // Alphabet
        alphabet.add("0");
        alphabet.add("1");

        // -------------------------
        // Define NFA
        // -------------------------

        addTransition("q0", "0", "q0");
        addTransition("q0", "0", "q1");
        addTransition("q0", "1", "q0");

        addTransition("q1", "1", "q2");

        // NFA final state
        finalStates.add("q2");

        // -------------------------
        // Convert NFA to DFA
        // -------------------------

        convertNFAtoDFA();

        // -------------------------
        // Display conversion
        // -------------------------

        System.out.println("\n===== NFA TO DFA CONVERSION =====");

        System.out.println("\nDFA STATES:");

        for (Set<String> state : dfaStates) {
            System.out.println(formatState(state));
        }

        System.out.println("\nDFA TRANSITIONS:");

        for (String from : dfaTransitions.keySet()) {

            for (String symbol : alphabet) {

                if (dfaTransitions.get(from).containsKey(symbol)) {

                    String to = dfaTransitions.get(from).get(symbol);

                    System.out.println(
                            from + " --" + symbol + "--> " + to
                    );
                }
            }
        }

        // -------------------------
        // Generate diagram
        // -------------------------

        generateDiagram();

        System.out.println("\nDFA diagram created!");
        System.out.println("Saved at: diagrams/nfa_to_dfa.png");
    }


    // Add NFA transition
    static void addTransition(
            String from,
            String symbol,
            String to) {

        nfa.putIfAbsent(from, new HashMap<>());

        nfa.get(from).putIfAbsent(
                symbol,
                new LinkedHashSet<>()
        );

        nfa.get(from)
                .get(symbol)
                .add(to);
    }


    // NFA → DFA conversion
    static void convertNFAtoDFA() {

        Set<String> startState = new LinkedHashSet<>();

        startState.add("q0");

        Queue<Set<String>> queue = new LinkedList<>();

        queue.add(startState);

        dfaStates.add(startState);

        while (!queue.isEmpty()) {

            Set<String> current = queue.poll();

            String currentName = formatState(current);

            dfaTransitions.putIfAbsent(
                    currentName,
                    new LinkedHashMap<>()
            );

            for (String symbol : alphabet) {

                Set<String> next = new LinkedHashSet<>();

                // Find all possible NFA destinations
                for (String state : current) {

                    if (nfa.containsKey(state)
                            && nfa.get(state).containsKey(symbol)) {

                        next.addAll(
                                nfa.get(state).get(symbol)
                        );
                    }
                }

                if (next.isEmpty()) {
                    continue;
                }

                String nextName = formatState(next);

                dfaTransitions
                        .get(currentName)
                        .put(symbol, nextName);

                // Add new DFA state
                if (!containsState(next)) {

                    dfaStates.add(next);

                    queue.add(next);
                }
            }
        }
    }


    // Check whether DFA state already exists
    static boolean containsState(Set<String> state) {

        for (Set<String> s : dfaStates) {

            if (s.equals(state)) {
                return true;
            }
        }

        return false;
    }


    // Format DFA state
    static String formatState(Set<String> state) {

        List<String> list =
                new ArrayList<>(state);

        Collections.sort(list);

        return "{" + String.join(",", list) + "}";
    }


    // Generate Graphviz diagram
    static void generateDiagram() throws IOException {

        FileWriter file =
                new FileWriter(
                        "diagrams/nfa_to_dfa.dot"
                );

        file.write("digraph DFA {\n");

        file.write("    rankdir=LR;\n");

        file.write(
                "    node [shape=circle];\n\n"
        );

        // Start arrow
        file.write(
                "    start [shape=point];\n"
        );

        file.write(
                "    start -> \"{q0}\";\n\n"
        );

        // DFA states
        for (Set<String> state : dfaStates) {

            String name = formatState(state);

            boolean isFinal = false;

            for (String nfaState : state) {

                if (finalStates.contains(nfaState)) {
                    isFinal = true;
                    break;
                }
            }

            if (isFinal) {

                file.write(
                        "    \"" + name +
                        "\" [shape=doublecircle];\n"
                );

            } else {

                file.write(
                        "    \"" + name +
                        "\" [shape=circle];\n"
                );
            }
        }

        file.write("\n");

        // Transitions
        for (String from : dfaTransitions.keySet()) {

            for (String symbol : alphabet) {

                if (dfaTransitions
                        .get(from)
                        .containsKey(symbol)) {

                    String to =
                            dfaTransitions
                                    .get(from)
                                    .get(symbol);

                    file.write(
                            "    \"" + from +
                            "\" -> \"" + to +
                            "\" [label=\"" +
                            symbol + "\"];\n"
                    );
                }
            }
        }

        file.write("}\n");

        file.close();

        // Run Graphviz
        ProcessBuilder pb =
                new ProcessBuilder(
                        "dot",
                        "-Tpng",
                        "diagrams/nfa_to_dfa.dot",
                        "-o",
                        "diagrams/nfa_to_dfa.png"
                );

        pb.inheritIO();

        Process process = pb.start();

        process.waitFor();
    }
}
