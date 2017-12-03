import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MainClass {
    static PriorityQueue<BinaryTree> forests;
    static char[] charArray;
    static ArrayList<Character> sortedChars = new ArrayList<>();
    static Map<Character, Integer> frequencies = new HashMap<>();
    static Map<Character, String> huffCodes = new HashMap<>();
    static int totalLength = 0;
    
    public static void readInput() throws IOException {
        System.out.println("Please input the file path where input.dat exists. If you want to load from the default path, input nothing here");
        Scanner sc = new Scanner(System.in);
        String path = sc.nextLine();
        String fileName = "infile.dat";
        if (path.length() == 0) {
            fileName = path + fileName;
        }
        BufferedReader d = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String data = d.lines().collect(Collectors.joining());
        data = data.replaceAll("[^A-Za-z0-9]+", "");
        charArray = data.toCharArray();
        //Count the frequency of each word
        for (char ch : charArray) {
            if (!frequencies.containsKey(ch)) {
                frequencies.put(ch, 1);
                sortedChars.add(ch);
            } else {
                frequencies.put(ch, frequencies.get(ch) + 1);
            }
        }
        d.close();
    }
    
    public static void writeOutput() throws IOException {
        System.out.println("Please input the file path you want to write outfile.dat. If you want to save to default path, input nothing here");
        Scanner sc = new Scanner(System.in);
        String path = sc.nextLine();
        String fileName = "outfile.dat";
        if (path.length() == 0) {
            fileName = path + fileName;
        }
        DataOutputStream output = new DataOutputStream(new FileOutputStream(fileName));
        Writer osw = new OutputStreamWriter(output, "UTF-8");
        osw.write("Symbol\tFrequency\n");
        for (char ch : sortedChars) {
            float percentage = (float)frequencies.get(ch) * 100 / (float) charArray.length;
            osw.write("" + ch + ",\t\t" + percentage + "%\n");
        }
        osw.write("\nSymbol\tHuffman Codes\n");
        for (char ch : sortedChars) {
            String code = huffCodes.get(ch);
            int frequency = frequencies.get(ch);
            totalLength += code.length() * frequency;
            osw.write("" + ch + ",\t\t" + code + "\n");
        }
        osw.write("\nTotal Bits: " + totalLength);
        osw.close();
    }
    
    public static BinaryTree mergeAndCreate(BinaryTree bst1, BinaryTree bst2) {
        Node newRoot = new Node(bst1.root.val + bst2.root.val);
        newRoot.left = bst1.root;
        newRoot.right = bst2.root;
        return new BinaryTree(newRoot);
    }
    
    
    public static void dfsHelper(String code, Node root) {
        if (root.left == null && root.right == null && root.lable != '-') {
            huffCodes.put(root.lable, code);
            return;
        }
        if (root.left != null) {
            dfsHelper(code + "0", root.left);
        }
        if (root.right != null) {
            dfsHelper(code + "1", root.right);
        }
    }

    public static void main(String[] args) throws IOException {
        readInput();
        forests = new PriorityQueue<>(frequencies.keySet().size(), new Comparator<BinaryTree>() {
            @Override
            public int compare(BinaryTree o1, BinaryTree o2) {
                return o1.root.val - o2.root.val;
            }
        });
        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            Node rt = new Node(entry.getValue());
            rt.lable = entry.getKey();
            BinaryTree currT = new BinaryTree(rt);
            forests.offer(currT);
        }
        while (forests.size() > 1) {
            BinaryTree bst1 = forests.poll();
            BinaryTree bst2 = forests.poll();
            forests.offer(mergeAndCreate(bst1,bst2));
        }
        BinaryTree lastTree = forests.poll();
        dfsHelper("", lastTree.root);
        //Lambda expression to sort descendingly
        Collections.sort(sortedChars, (Character c1, Character c2) -> (frequencies.get(c2).compareTo(frequencies.get(c1))));
        writeOutput();
    }
    
}
