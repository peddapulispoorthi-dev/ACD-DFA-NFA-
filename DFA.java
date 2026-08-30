import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DFAVisualization {

    public static void main(String[] args) throws IOException {

        File folder = new File("diagrams");
        folder.mkdirs();

        FileWriter file = new FileWriter("diagrams/dfa.dot");

        file.write("digraph DFA {\n");
        file.write("    rankdir=LR;\n");
        file.write("    node [shape=circle];\n\n");

        // Start state
        file.write("    start [shape=point];\n");
        file.write("    start -> q0;\n\n");

        // Final state
        file.write("    q2 [shape=doublecircle];\n\n");

        // Transitions
        file.write("    q0 -> q1 [label=\"0\"];\n");
        file.write("    q0 -> q0 [label=\"1\"];\n");

        file.write("    q1 -> q1 [label=\"0\"];\n");
        file.write("    q1 -> q2 [label=\"1\"];\n");

        file.write("    q2 -> q1 [label=\"0\"];\n");
        file.write("    q2 -> q0 [label=\"1\"];\n");

        file.write("}\n");

        file.close();

        // Generate PNG using Graphviz
        ProcessBuilder pb = new ProcessBuilder(
                "dot",
                "-Tpng",
                "diagrams/dfa.dot",
                "-o",
                "diagrams/dfa.png"
        );

        pb.inheritIO();
        Process process = pb.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("DFA transition diagram created!");
        System.out.println("Saved at: diagrams/dfa.png");
    }
}
