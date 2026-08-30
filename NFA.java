import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NFAVisualization {

    public static void main(String[] args) throws IOException {

        File folder = new File("diagrams");
        folder.mkdirs();

        FileWriter file = new FileWriter("diagrams/nfa.dot");

        file.write("digraph NFA {\n");
        file.write("    rankdir=LR;\n");
        file.write("    node [shape=circle];\n\n");

        // Start state
        file.write("    start [shape=point];\n");
        file.write("    start -> q0;\n\n");

        // Final state
        file.write("    q2 [shape=doublecircle];\n\n");

        /*
         * NFA transitions
         *
         * q0 has TWO transitions on input 0.
         * This demonstrates non-determinism.
         */

        file.write("    q0 -> q0 [label=\"0, 1\"];\n");
        file.write("    q0 -> q1 [label=\"0\"];\n");

        file.write("    q1 -> q2 [label=\"1\"];\n");

        file.write("}\n");

        file.close();

        // Generate PNG
        ProcessBuilder pb = new ProcessBuilder(
                "dot",
                "-Tpng",
                "diagrams/nfa.dot",
                "-o",
                "diagrams/nfa.png"
        );

        pb.inheritIO();
        Process process = pb.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("NFA transition diagram created!");
        System.out.println("Saved at: diagrams/nfa.png");
    }
}
