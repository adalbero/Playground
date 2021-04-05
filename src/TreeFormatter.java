import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TreeFormatter {
	protected List<String> input = new ArrayList<>();

	public void printTree(PrintStream out, MyTree tree) {
		printHeader(out);
		printTree(out, "", tree.toString());
		printFooter(out);
	}

	protected void printHeader(PrintStream out) {
		out.println();
	}

	protected void printFooter(PrintStream out) {
		out.println();
	}

	protected void printTree(PrintStream out, String parent, String aTree) {
		String parts[] = MyTree.split(aTree);
		String left = parts[0];
		String right = parts[1];

		printPart(out, parent + "0", left);
		printPart(out, parent + "1", right);
	}

	protected void printPart(PrintStream out, String node, String part) {
		if (MyTree.isLeaf(part)) {
			printLeaf(out, node, part);
		} else {
			printNode(out, node, part);
		}
	}

	protected void printLeaf(PrintStream out, String node, String part) {
		out.println(String.format("%s %s", node, part));
	}

	protected void printNode(PrintStream out, String node, String part) {
		out.println(String.format("%s", node));
		printTree(out, node, part);
	}

	public String parseTree(List<String> input) {
		this.input = input;
		return parseTree("");
	}

	protected String parseTree(String parent) {
		String left = getPart(parent + "0");
		String right = getPart(parent + "1");

		return String.format("(%s,%s)", left, right);
	}

	protected String getPart(String parent) {
		boolean exists = false;

		for (String line : input) {
			String[] vet = line.trim().split("\\s", 2);
			String node = vet[0];
			String value = (vet.length > 1 ? vet[1] : null);
			if (node.equals(parent) && value != null) {
				return value;
			}
			if (node.startsWith(parent)) {
				exists = true;
			}
		}

		if (exists) {
			return parseTree(parent);
		} else {
			throw new RuntimeException("Node not found: " + parent);
		}

	}
}
