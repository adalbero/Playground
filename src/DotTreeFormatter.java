import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DotTreeFormatter extends TreeFormatter {
	private List<String> leafs = new ArrayList<>();

	public void printTree(PrintStream out, MyTree tree) {
		printHeader(out);
		printTree(out, "", tree.toString());
		printFooter(out);
	}

	protected void printHeader(PrintStream out) {
		out.println();
		out.println("strict graph {");
		out.println("root -- {0 1}");
	}

	protected void printFooter(PrintStream out) {
		out.println("{rank=same " + leafs.stream().collect(Collectors.joining(" ")) + "}");
		out.println("}");
	}

	protected void printLeaf(PrintStream out, String node, String part) {
		part = "n" + part;
		leafs.add(part);
		out.println(String.format("%s -- %s", node, part));
	}

	protected void printNode(PrintStream out, String node, String part) {
		out.println(String.format("%s -- {%s0  %s1}", node, node, node));
		printTree(out, node, part);
	}

}
