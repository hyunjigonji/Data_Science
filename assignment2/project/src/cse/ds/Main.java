package cse.ds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static cse.ds.DecisionTree.*;

public class Main {

    public static void main(String[] args) {
        String trainFile = args[0], testFile = args[1], outputFile = args[2];

        // get Data from files
        List<String> labels = getLabels(trainFile);
        String classLabel = getClassLabel(trainFile);
        List<Pair<LinkedHashMap<String, String>, String>> samples = getSamples(trainFile, labels);
        List<LinkedHashMap<String, String>> tests = getTests(testFile, labels);
        List<String> resultList = getResultList(samples);

        // build Decision Tree
        Node tree = buildDecisionTree(samples, labels, new Node());

        // get Answers with the above decision tree
        List<LinkedHashMap<String, String>> answers = getAnswers(tests, tree, resultList, classLabel);
        makeOutputFile(answers, labels, classLabel, outputFile);
    }

    private static List<String> getLabels(String trainFile){
        List<String> labels = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(trainFile));
            String line = reader.readLine();

            StringTokenizer tokenizer = new StringTokenizer(line, "\t");
            while(tokenizer.hasMoreTokens()){
                String label = tokenizer.nextToken();
                if(!tokenizer.hasMoreTokens()) break;
                labels.add(label);
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return labels;
    }

    private static String getClassLabel(String trainFile){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(trainFile));
            String line = reader.readLine();

            StringTokenizer tokenizer = new StringTokenizer(line, "\t");
            while(tokenizer.hasMoreTokens()){
                String label = tokenizer.nextToken();
                if(!tokenizer.hasMoreTokens()){
                    return label;
                }
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return "classLabel";
    }

    private static List<String> getResultList(List<Pair<LinkedHashMap<String, String>, String>> samples){
        List<String> resultList = new ArrayList<>();
        for(Pair<LinkedHashMap<String, String>, String> sample : samples){
            String result = sample.getSecond();
            if(!resultList.contains(result)) resultList.add(result);
        }
        return resultList;
    }

    private static List<Pair<LinkedHashMap<String, String>, String>> getSamples(String trainFile, List<String> labels){
        List<Pair<LinkedHashMap<String, String>, String>> samples = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(trainFile));
            String line = reader.readLine();
            line = reader.readLine(); // pass for the first line (labels)

            while(line != null){
                StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                Pair<LinkedHashMap<String, String>, String> attributes = new Pair<>(new LinkedHashMap<>(), "");

                Integer index = 0;
                while(tokenizer.hasMoreTokens()){
                    String attribute = tokenizer.nextToken();
                    if(!tokenizer.hasMoreTokens()){
                        attributes.setSecond(attribute);
                    } else {
                        attributes.getFirst().put(labels.get(index++), attribute);
                    }
                }
                samples.add(attributes);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return samples;
    }

    private static List<LinkedHashMap<String, String>> getTests(String testFile, List<String> labels){
        List<LinkedHashMap<String, String>> tests = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(testFile));
            String line = reader.readLine();
            line = reader.readLine(); // pass for the first line (labels)

            while(line != null){
                StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

                Integer index = 0;
                while(tokenizer.hasMoreTokens()){
                    String attribute = tokenizer.nextToken();
                    attributes.put(labels.get(index++), attribute);
                }
                tests.add(attributes);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return tests;
    }

    private static List<LinkedHashMap<String, String>> getAnswers(List<LinkedHashMap<String, String>> tests, Node tree, List<String> resultList, String classLabel){
        List<LinkedHashMap<String, String>> answers = new ArrayList<>();
        for(Integer i = 0 ; i < tests.size() ; i++){
            LinkedHashMap<String, String> test = tests.get(i);
            Node answerNode = classify(test, tree, resultList);
            LinkedHashMap<String, String> answer = new LinkedHashMap<>(test);
            answer.put(classLabel, answerNode.getAttribute());
            answers.add(answer);
        }
        return answers;
    }

    private static void makeOutputFile(List<LinkedHashMap<String, String>> answers, List<String> labels, String classLabel, String outputFile){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            for(String label : labels){
                writer.write(label + "\t");
            }
            writer.write(classLabel);
            writer.newLine();

            for(LinkedHashMap<String, String> answer : answers){
                List<String> values = new ArrayList<>(answer.values());
                for(Integer i = 0 ; i < values.size() ; i++){
                    String value = values.get(i);
                    if(i == values.size()-1) {
                        writer.write(value);
                    } else{
                        writer.write(value + "\t");
                    }
                }
                writer.newLine();
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
