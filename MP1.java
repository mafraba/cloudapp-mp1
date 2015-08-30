import java.io.File;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Files;
import java.nio.charset.*;
import java.util.*;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];
        Map<String, Integer> words = new HashMap<>();

        // Read all titles
        List<String> titles = Files.readAllLines(new File(inputFileName).toPath(), Charset.defaultCharset());

        // Now process selected indexes
        for (Integer index : getIndexes()) {
          String title = titles.get(index);
          // Process title
          StringTokenizer st = new StringTokenizer(title, delimiters);
          while (st.hasMoreTokens()) {
            // Process each word
            String token = st.nextToken();
            token = token.toLowerCase();
            token = token.trim();
            if (words.containsKey(token)) {
              Integer count = words.get(token);
              words.put(token, count+1);
            } else {
              words.put(token, 1);
            }
          }
        }

        // Ignore "stopWordsArray" words
        for (String stopWord : stopWordsArray) {
          words.remove(stopWord);
        }

        // Transform words to list
        List<Map.Entry<String, Integer>> wordsList = new LinkedList<>(words.entrySet());

        // Sort it
        Collections.sort(wordsList, new Comparator<Map.Entry<String, Integer>>() {
          public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
            if (e1.getValue() == e2.getValue()) {
              // Lexic
              return e1.getKey().compareTo(e2.getKey());
            } else {
              // Count (bigger count first)
              return e1.getValue() < e2.getValue() ? 1 : -1;
            }
          }
        });

        // Select top 20
        for (int i=0; i<20 ; i++) {
          ret[i] = wordsList.get(i).getKey();
        }

        return ret;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }
}
