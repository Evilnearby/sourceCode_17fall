import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

//Definition of the node of a trie tree
class Node {
    char label;
    List<Node> descendents;
    String primaryName;
    public Node(char label) {
        this.label = label;
        descendents = new ArrayList<Node>();
        primaryName = null;
    }
}

public class Main {
    static Map<String, List<String>> nameLists = new HashMap<>();
    static Map<String, Integer> hitCounts = new HashMap<>();
    static List<String> keys = new ArrayList<>();

    static int totalWords = 0;
    static int totalHits = 0;

    static ArrayList<String> ignorances = new ArrayList<String>(Arrays.asList(new String[]{"a", "an", "the", "and", "or", "but"}));

    static Node root = new Node('0');

    //Read the .dat into the hashmap
    public static void readCompanies() throws IOException {
        BufferedReader bd = new BufferedReader(new InputStreamReader(new FileInputStream("company.dat")));
        String line;
        while ((line = bd.readLine()) != null) {
            String[] list = line.split("\t");
            keys.add(list[0]);
            nameLists.put(list[0], new ArrayList<String>());
            for (int i = 0; i < list.length; i++) {
                nameLists.get(list[0]).add(list[i]);
            }
        }
        //Initialize the hitCounts
        for (String key : nameLists.keySet()) {
            hitCounts.putIfAbsent(key, 0);
        }
    }

    //Build the trie tree
    public static void buildTrie() {
        for (String key : nameLists.keySet()) {
            for (String name : nameLists.get(key)) {
                insertName(key, name);
            }
        }
    }

    //Insert a single name into the trie tree
    private static void insertName(String key, String name) {
        Node currRoot = root;
        char[] chArr = name.toCharArray();
        for (int i = 0; i < chArr.length; i++) {
            if (!Character.isDigit(chArr[i]) && !Character.isLetter(chArr[i]) && chArr[i] != ' ') continue;
            boolean newBranch = true;
            for (Node n : currRoot.descendents) {
                if (n.label == chArr[i]) {
                    currRoot = n;
                    newBranch = false;
                    break;
                }
            }
            if (newBranch) {
                Node nextNode = new Node(chArr[i]);
                currRoot.descendents.add(nextNode);
                currRoot = nextNode;
            }
        }
        currRoot.primaryName = key;
    }

    //Search a word in the tree
    private static Node searchWord(Node start, String word) {
        boolean found = false;
        char[] wordArray = word.toCharArray();
        int index = 0;
        while (index < wordArray.length) {
            //If punctuation symbols, pass it.
            while (index < wordArray.length && !Character.isDigit(wordArray[index]) && !Character.isLetter(wordArray[index]) && wordArray[index] != ' ') {
                index++;
            }
            if (index >= wordArray.length) break;
            int record = index;
            for (Node n : start.descendents) {
                if (n.label == wordArray[index]) {
                    found = true;
                    start = n;
                    index++;
                    break;
                }
            }
            if (record == index) break;
        }

        if (!found || index < wordArray.length) return null;
        return start;
    }

    //Read Article
    public static void readArticle() throws IOException {
        System.out.println("Please input the article, the last line should be a dot");
        Scanner sc = new Scanner(System.in);
        String line;
        //Node curr = root;
        while (true) {
            line = sc.nextLine();
            if (line.matches("[.]+")) break;
            Set<Integer> ignors = new HashSet<>();
            String[] words = line.split(" ");
            totalWords += words.length;
            int next = 0;
            while (next < words.length) {
                next = helper(root, words, next, ignors);
            }
            totalWords -= ignors.size();
        }
        for (Integer count : hitCounts.values()) {
            totalHits += count;
        }
    }
    
    static int helper(Node curr, String[] words, int start, Set<Integer> ignors) {
        int temp = start;
        for (int i = start; i < words.length; i++) {
            Node prev = curr;
            curr = searchWord(curr == null ? root : curr, words[i]);
            Node currPSpace = searchWord(prev == null ? root : prev, words[i].concat(" "));
            //Offset of the ignorance words
            if (ignorances.contains(words[i]) && curr == null) ignors.add(i);

            if (curr != null) {
                if ((i == words.length - 1 || currPSpace == null || searchWord(currPSpace, words[i + 1]) == null) && curr.primaryName != null) {
                    hitCounts.put(curr.primaryName, hitCounts.get(curr.primaryName) + 1);
                    return temp + 1;
                    //curr = root;
                } else if (currPSpace != null){
                    curr = currPSpace;
                }
            } else {
                return temp + 1;
            }
        }
        return temp + 1;
    }

    public static void printResults() {
        DecimalFormat df = new DecimalFormat("##0.0000");
        String format = "%-11s %-6s %-8s %-2s %-8s %n";
        System.out.println("Companies\t|\tHit Counts\t|\tRelevances");
        System.out.println("------------------------------------------");
        for (String key : keys) {
            int count = hitCounts.get(key);
            //String percentage = String.format("%.4g", 100 * ((float) count / (float) totalWords));
            String percentage = df.format(100 * ((float) count / (float) totalWords)).trim().substring(0, 5);
            System.out.printf(format, key, "|", count, "|", percentage + "%");
        }
        //String percentage = String.format("%.4g", 100 * ((float) totalHits / (float)totalWords));
        String percentage = df.format(100 * ((float) totalHits / (float)totalWords)).trim().substring(0, 5);
        System.out.println("------------------------------------------");
        System.out.println("Total\t\t|\t   " + totalHits +"\t\t|  " + percentage + "%");
        System.out.println("Total Words\t\t\t|\t" + totalWords);
    }

    public static void main(String[] args) throws IOException {
        readCompanies();
        buildTrie();
        readArticle();
        printResults();
    }
}
