import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.*;
import java.text.DecimalFormat;
import java.io.PrintWriter;





/**
 * Created by arietd on 11/10/2017.
 */


public class MySearchEngine {

    public static void showMenu(){

        Scanner input = new Scanner(System.in);

        /***************************************************/
        System.out.println("-------------------------");
        System.out.println("My Search engine");
        System.out.println("-------------------------");
        System.out.println("Commands");
        System.out.println("\t -index");
        System.out.println("\t -search");
        System.out.println("\t -exit");
    }


    public static void commandInput(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter search terms.");
        String input = sc.nextLine();
        List<String> q1 = tokenize(input);
        String[] q2 = removeWords(q1);
        String[] q3 = stemWordArray(q2);


        String[] inputArr = q3;
        if (input.startsWith("search ")){
            searchQuery(Arrays.copyOfRange(inputArr, 1, inputArr.length));
        }else if(input.startsWith("index")){
            System.out.print("Indexing");
        }
        else if(input.startsWith("exit")){
            System.exit(0);
        }
        else{
            System.out.println("try again");
            commandInput();
        }
    }

    public static String[] indexingInput(String[] input){
        return input;
    }



    public static void searchQuery(String[] input){
        String[] index = readIndex();
        //System.out.println(Arrays.toString(index));

        Double[] weights = new Double[15];
        Arrays.fill(weights, 0.0);

        for(String s: input){
            for (int i = 0; i < index.length; i++){
                String row = index[i];

                String[] rowTokens = row.split(",");
                String term = rowTokens[0];

                if (term.equals(s)) {
                    System.out.println("terms are a match");
                    double idf = Double.parseDouble(rowTokens[rowTokens.length - 1]);

                    for (int j = 1; j < rowTokens.length - 2; j = j + 2) {
                        // System.out.println(rowTokens[j] + " : " + rowTokens[j + 1]);
                        int docId =  Integer.parseInt(rowTokens[j].substring(1));
                        double tf = Integer.parseInt(rowTokens[j+1]);
                        weights[docId-1] =+ (tf*idf);
                    }


                }
            }
        }
        DecimalFormat f = new DecimalFormat("##.000");
        Double[] sortedWeights = weights.clone();
        Arrays.sort(sortedWeights,Collections.reverseOrder());
        Set<Double> weightSetSorted= new LinkedHashSet<>();

        for(Double w: sortedWeights){
            weightSetSorted.add(w);
        }
        Iterator iterator = weightSetSorted.iterator();

        // check values
        int docNumberPrint = 1;
        while (iterator.hasNext()){
            Double iterVal = Double.parseDouble(iterator.next().toString());
            String docName = "d" + (Arrays.asList(weights).indexOf(iterVal) + 1);
            if (iterVal>0 & docNumberPrint < 11) {
                 System.out.println(docNumberPrint + ". " + docName + ": relevance score " + f.format(iterVal));
            }
            docNumberPrint = docNumberPrint+1;
        }

        showMenu();
        commandInput();
    }

    public static String[] readIndex(String filePath){
        String indexFile = readFile(filePath);
        String[] indexArr1= indexFile.split("\\s");

        return indexArr1;
    }


    public static String[] readIndex(){
        String indexFile = readFile("../index.txt");
        String[] indexArr1= indexFile.split("\\s");

        return indexArr1;
    }

    /*
     * Parse dir with path
     */

    public static Map<String, String> parseDir(String dirPath){
        File folder = new File(dirPath);
        File[] listOfFiles = folder.listFiles();

        Map<String,String> documentMap = new HashMap<String,String>();
        //HashMap documentMap = new HashMap();

        if (!folder.isDirectory())
            System.out.println("Not a valid file path!");
        if (folder.exists()){
            System.out.println("reading files");

            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];
                if (file.isFile() && file.getName().endsWith(".txt")) {

                    if(file.getName().startsWith("stopwords")) {
                        // file is ignored
                    } else {
                        String filePath = file.getAbsolutePath();
                        String content = readFile(filePath);

                        // System.out.println(file.getName());

                        documentMap.put(file.toString(), content);
                    }
                }
            }

        }
        return documentMap;
    }
    /*
     * PARSING

     * Parse Dir reads a folder full of text files ignoring "stopwords.txt"
     * stores them in a Hashmap of string key and and string value
     */
    public static Map<String, String> parseDir(){
        File folder = new File("Files");
        File[] listOfFiles = folder.listFiles();

        Map<String,String> documentMap = new HashMap<String,String>();
        //HashMap documentMap = new HashMap();

        if (!folder.isDirectory())
            System.out.println("Not a valid file path!");
        if (folder.exists()){
            System.out.println("reading files");

            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];
                if (file.isFile() && file.getName().endsWith(".txt")) {

                    if(file.getName().startsWith("stopwords")) {
                        // file is ignored
                    } else {
                        String filePath = file.getAbsolutePath();
                        String content = readFile(filePath);

                        // System.out.println(file.getName());

                        documentMap.put(file.toString(), content);
                    }
                }
            }

        }
        return documentMap;
    }





    // readFile takes a path and reads the file
    // this is meant for txt files only
    public static String readFile(String path){
        // reads a .txt file
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return content;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*
     * STOP WORDS REMOVAL
     *
     *
     */


    public static String[] readStopwords(String filePath){
        String file1 = readFile(filePath);

        String tokens[] = new String[1999];

        tokens = file1.split("[\\s]+");
        String[] trimmedTokens = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            trimmedTokens[i] = tokens[i].trim();
        }
        return tokens;
    }



    public static String[] readStopwords(){
        String file1 = readFile("Files/stopwords1.txt");

        String tokens[] = new String[1999];

        tokens = file1.split("[\\s]+");
        String[] trimmedTokens = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            trimmedTokens[i] = tokens[i].trim();
        }
        return tokens;
    }


    public static String[] removeWords(List<String> stringList){

        String[] stopwords = readStopwords();
        // System.out.println("length of stoprwords" + stopwords.length);

        // ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(strArray));
        int i;
        for (i=0; i < stopwords.length; i++){


            if (stringList.contains(stopwords[i])) {
                stringList.removeAll(Collections.singleton(stopwords[i]));//remove it
                //System.out.println(stringList.contains(stopwords[i]));
            }
            //stringList.contains(stopwords[i]);


            // System.out.println(stopwords[i]);
        }

        String[] strings = stringList.stream().toArray(String[]::new);
        return strings;
    }

    /*
     * TOKENIZATION
     *
     *
     */


    public static List<String> tokenize(String textFile) {
        // create Tokens Array
        textFile = textFile.toLowerCase();
        textFile = textFile.replaceAll("_", " ");

        List<String> tokensList = new ArrayList<>();;

        // New line break hyphenated words
        Pattern rule1 = Pattern.compile("(\\w+-\\r?\\n\\w+)");

        Matcher m1 = rule1.matcher(textFile);
        while (m1.find()) {
            String originalStr = m1.group();
            String finalStr = m1.group();
            finalStr = finalStr.replace("-", "").replace("\n", "").replace("\r", "");


            textFile.replaceAll(originalStr, finalStr);
        }


        // Acronyms
        Pattern rule2 = Pattern.compile("((?:[a-zA-Z]\\.){2,})");

        Matcher m2 = rule2.matcher(textFile);
        while (m2.find()) {
            String originalStr = m2.group();
            String finalStr = m2.group();
            finalStr = finalStr.replace(".", "");
            textFile.replaceAll(originalStr, finalStr);
        }



        /*
         * dont split words just match all tokens other crap like dots are not useful just text
         */
//        Pattern p = Pattern.compile("([$\\d.,]+)|([\\w\\d!$]+)");
//        Matcher m = p.matcher(textFile);
//        while(m.find()) {
//            System.out.println("token: " + m.group());
//        }
        int j = 0;

        Pattern p = Pattern.compile("(url)|(\\w+)|(([A-Z]{1}[(a-z+)]+\\s){2,})|((?:[a-zA-Z]\\.){2,})|(ip_pattern)|(urlPattern)|(^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$)");
        Matcher m = p.matcher(textFile);
        while (m.find()) {
//            System.out.println("token: " + m.group());
            tokensList.add(m.group());

            j = j + 1;
        }
            //tokens1 = textFile.split("\\W+");




            // return tokens
            return tokensList;
    }


    /*
     * STEMMER
     */
    public static String stemWord(String str1){
        if (str1.isEmpty()){
            return str1;
        }else {
            Stemmer s = new Stemmer();
            int j = str1.length();
            for (int c = 0; c < j; c++) s.add(str1.charAt(c));
            s.stem();
            String finalStr;
            finalStr = s.toString();
            return finalStr;
        }
    }


    // take a String Array and stem each word
    public static String[] stemWordArray(String[] stringArray){

        // for each word stem
        for(int i = 0; i < stringArray.length; i++){
            stringArray[i] = stemWord(stringArray[i]);
        }

        return stringArray;
    }


    // tf

    public static Map<String, Integer> termFrequency(String[] tokenArray){

        Map<String, Integer> tfMap = new HashMap<>();


        List<String> asList = Arrays.asList(tokenArray);
        Set<String> mySet = new HashSet<String>(asList);


        for(String s: mySet){
            tfMap.put(s, Collections.frequency(asList, s));
        }

        return tfMap;
    }



    // COSINE SIMILARITY
    public static void cosineSiml(String[] words){
        for(String word: words){



        }
    }

    /*
     * MAIN
     *
     *
     */
    public static void main(String[] args) {

        showMenu();
        commandInput();



        String[] tfIdfIndex = readIndex();


        System.out.print(".");
        // STEP 1 - READ FILES
        Map<String, String> documentMap = new HashMap<>();
        documentMap = parseDir();
        int numDocs = documentMap.size();

        Map<String, String> docNumName = new HashMap<>();


        System.out.print(".");
              Set<String> keys = documentMap.keySet();
        String[] docTitles = keys.toArray(new String[keys.size()]);
        int docNum = 1;
        for(String s: docTitles){
            docNumName.put(s,"d"+docNum);
            docNum = docNum +1;
        }


        System.out.print(".");


        // STEP 2 - TOKENIZE
        HashMap<String, String[]> tokenized_docMap = new HashMap<>();
        Map<String, Map<String, Integer>> tfMap = new LinkedHashMap<>();


        // TERM FREQUENCY
        System.out.print(".");
        for (Map.Entry<String, String> entry : documentMap.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();

            // tokenize
            List<String> tokenizedArray = tokenize(value); // String Array

            // remove stopwords
            String[] tokenizedFilteredArray = removeWords(tokenizedArray); // String array converted to list

            // stem the words
            String[] stemmedArray = stemWordArray(tokenizedFilteredArray);

            //tf
            Map<String, Integer> docTF =  termFrequency(stemmedArray);

            tfMap.put(key, docTF);
            // store in new hashmap<String, String[]> where <Title, Tokens>
            tokenized_docMap.put(key, stemmedArray);
            System.out.print(".");
        }



        System.out.print(".");
        /*
         * TERM LIST
         */
        Set<String> linkedTermSet = new LinkedHashSet<>();
        for (Map.Entry<String, String[]> entry : tokenized_docMap.entrySet()) {
            String[] value = entry.getValue();
            for(String s:  value){
                linkedTermSet.add(s);
            }
        }
        linkedTermSet.remove("");


        /*
         * INVERSE DOCUMENT FREQUENCY
         */
        Map<String, Double> idfMap =  new HashMap<>();
        Iterator<String> it = linkedTermSet.iterator();
        double docFreq;
        System.out.print("*");
        while(it.hasNext()){
            docFreq = 0;
            String termString = it.next();
            for (Map.Entry<String, String[]> entry : tokenized_docMap.entrySet()) {
                String[] value = entry.getValue();
                if (Arrays.asList(value).contains(termString)){
                    docFreq = docFreq +1;
                }
            }

            if(docFreq > 0) {
                idfMap.put(termString,(Math.log(numDocs / docFreq)));
            }
        }


        // term list
        DecimalFormat f = new DecimalFormat("##.000");
        String outputFile = "";


        for(String term: linkedTermSet){
           // System.out.print(term+",");
            outputFile = outputFile + term +",";
            for (Map.Entry<String, Map<String, Integer>> entry: tfMap.entrySet()){
                String title = entry.getKey();
               // System.out.print(title + ":");
                String docId = docNumName.get(title).toString();
               // System.out.println(docId);

                Map<String, Integer> docTfMap = entry.getValue();
                if (docTfMap.get(term) != null){
                    String tfValue = docTfMap.get(term).toString();
                  //  System.out.print(docId+","+ tfValue+",");
                    outputFile = outputFile + docId+","+ tfValue +",";
                }
            }
            Double idfValue = Double.parseDouble(idfMap.get(term).toString());
            String idfString= String.format("%.3f", idfValue);
          //  System.out.println(f.format(idfValue));
            outputFile = outputFile + idfString + "\n";
        }

        try(  PrintWriter out = new PrintWriter( "../index.txt" )  ){
            out.println( outputFile );
        }  catch (FileNotFoundException ex) {
            System.out.println("Error");
        }

        System.out.print(".! Done");
        showMenu();
        commandInput();
    }
}
