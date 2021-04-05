import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MyTree {
	private String tree;

	private final static boolean DEBUG = false;
	private final static boolean INCLUDE_REMINDING = true;

	public MyTree(String tree) {
		this.tree = tree.trim();
	}

	/**
	 * Get a list of values, based on the path.
	 * 
	 * @param path - binary representation of the path. 0 means left and 1 means
	 *             right.
	 * @return a list of found values.
	 */
	public List<String> getValues(String path) {
		List<String> list = new ArrayList<>();
		String value = null;
		int level = 0;

		for (int idx = 0; idx < path.length(); idx++) {
			char ch = path.charAt(idx);

			if (value == null) {
				value = tree;
				level = 0;
			}

			debug("Level=%d, Tree=%s", level, value);

			String vet[] = split(value);
			String left = vet[0];
			String right = vet[1];

			debug("  Left=%s, Right=%s", left, right);

			debug("  Path=%s", path);
			debug("  Idx: %s^", repeat(idx, " "));

			if (ch == '0') {
				value = left;
				debug("  get Left: %s", value);
			} else {
				value = right;
				debug("  get Right: %s", value);
			}

			level++;

			if (isLeaf(value)) {
				debug("  Leaf=%s", value);
				list.add(value);
				value = null;
			}

			debug("-------------");
		}

		if (value != null && INCLUDE_REMINDING) {
			list.add(value);
		}

		return list;
	}

	private void debug(String format, Object... args) {
		if (DEBUG) {
			System.out.println(String.format(format, args));
		}
	}

	/**
	 * Split a tree string on left of the comma and right of the comma.
	 * 
	 * @param value - a tree representation. Like ((1,2),3)
	 * @return a String array of 2 elements: [0] = left and [1] = right
	 */
	public static String[] split(String value) {
		String left = "";
		String right = "";

		if (!isLeaf(value)) {
			int idx = indexOfComma(value);
			if (idx >= 0) {
				left = value.substring(1, idx);
				right = value.substring(idx + 1, value.length() - 1);
				return new String[] { left.trim(), right.trim() };
			}
		}

		throw new RuntimeException("Not a valid bi-tree");
	}

	/**
	 * Returns the index of the comma on the first level.
	 * 
	 * Ex: 012345678<br>
	 * ((a,b),c)<br>
	 * 
	 * <ul>
	 * <li>has a comma at position 3, but on level 2.
	 * <li>the one we are looking for is the comma at position 6, on level 1.
	 * </ul>
	 * 
	 * @param value - a tree representation. Like ((1,2),3)
	 * @return index of level 1 comman, or -1 if not found.
	 */
	public static int indexOfComma(String value) {
		int level = 0;

		for (int idx = 0; idx < value.length(); idx++) {
			char ch = value.charAt(idx);

			if (ch == '(') {
				level++;
			} else if (ch == ')') {
				level--;
			} else if (ch == ',' && level <= 1) {
				return idx;
			}
		}

		return -1;
	}

	/**
	 * Verify if the String is a leaf or a tree representation.
	 * 
	 * @param value - the value to test.
	 * @return true if it is a leaf, and false if it is a tree representation.
	 */
	public static boolean isLeaf(String value) {
		return !value.startsWith("(");
	}

	@Override
	public String toString() {
		return tree;
	}

	private String repeat(int n, String ch) {
		return new String(new char[n]).replace("\0", ch);
	}

	public static void main(String[] args) {
		TreeFormatter formatter = new DotTreeFormatter();

		MyTree t1 = new MyTree("((1,2),3)");
		MyTree t2 = new MyTree("((((1,2),(3,4)),((5,6),(7,8))),17)");
		MyTree t3 = new MyTree("((((1,2),(3,4)),((5,6),((7,8),9))),17)");
		MyTree t4 = new MyTree(formatter.parseTree(loadFile("tree.trxt")));

		MyTree t = t3;

		find(t, "001001100");

		formatter.printTree(System.out, t);
	}

	public static void find(MyTree t, String path) {
		List<String> values = t.getValues(path);
		System.out.println();
		System.out.println("tree : " + t);
		System.out.println("path : " + path);
		System.out.println("value: " + values);
	}

	public static List<String> loadFile(String filename) {
		Path path = Path.of("/Users/adalbero/eclipse-workspace/Playground/src", filename);

		try {
			String content = new String(Files.readAllBytes(path), Charset.defaultCharset());
			return List.of(content.split("\n"));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
