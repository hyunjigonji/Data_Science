package cse.ds;

import java.util.*;
import java.util.Map.Entry;

import static cse.ds.Node.attrNode;
import static cse.ds.Node.verNode;

public class DecisionTree {

    public static Integer getTotalSize(List<List<Pair<LinkedHashMap<String, String>, String>>> splitSamples){
        Integer totalSize = 0;
        for(List<Pair<LinkedHashMap<String, String>, String>> subSamples : splitSamples){
            totalSize += subSamples.size();
        }
        return totalSize;
    }

    public static List<Double> getP(List<String> subSampleClassLabels){
        LinkedHashMap<String, Integer> subSampleClasses = new LinkedHashMap<>();
        for(String subSampleClassLabel : subSampleClassLabels){
            Integer classesCounterNum = subSampleClasses.getOrDefault(subSampleClassLabel, 0);
            subSampleClasses.put(subSampleClassLabel, ++classesCounterNum);
        }

        Double subSampleClassSize = subSampleClassLabels.size() * 1.0;
        List<Double> P = new ArrayList<>();
        for(Entry<String, Integer> subSampleClassesEntry : subSampleClasses.entrySet()){
            Integer subSampleClassValue = subSampleClassesEntry.getValue();
            P.add(subSampleClassValue / subSampleClassSize);
        }
        return P;
    }

    public static Double getEntropy(List<Pair<LinkedHashMap<String, String>, String>> subSamples){
        List<String> subSampleClassLabels = new ArrayList<>();
        for(Pair<LinkedHashMap<String, String>, String> subSample : subSamples){
            subSampleClassLabels.add(subSample.getSecond());
        }
        List<Double> P = getP(subSampleClassLabels);

        Double entropy = 0.0;
        for(Double P0 : P){
            if(P0 > 0){
                entropy += (-1) * P0 * (Math.log(P0) / Math.log(2));
            }
        }
        return entropy;
    }

    public static Double getSplitSamplesEntropy(List<List<Pair<LinkedHashMap<String, String>, String>>> splitSamples){
        Integer totalSize = getTotalSize(splitSamples);
        Double entropy = 0.0;
        for(List<Pair<LinkedHashMap<String, String>, String>> subSamples : splitSamples){
            entropy += getEntropy(subSamples) * subSamples.size() / totalSize;
        }
        return entropy;
    }

    public static LinkedHashMap<String, List<Pair<LinkedHashMap<String, String>, String>>> getSplitSamples(List<Pair<LinkedHashMap<String, String>, String>> samples, String attribute){
        LinkedHashMap<String, List<Pair<LinkedHashMap<String, String>, String>>> splitSamples = new LinkedHashMap<>();
        for (Pair<LinkedHashMap<String, String>, String> sample : samples) {
            String key = sample.getFirst().get(attribute);
            if(splitSamples.containsKey(key)){
                splitSamples.get(key).add(sample);
            }
            else{
                List<Pair<LinkedHashMap<String, String>, String>> temp = new ArrayList<>();
                temp.add(sample);
                splitSamples.put(key, temp);
            }
        }
        return splitSamples;
    }

    public static Double informationGain(List<Pair<LinkedHashMap<String, String>, String>> samples, String attribute){
        LinkedHashMap<String, List<Pair<LinkedHashMap<String, String>, String>>> splitSamples = getSplitSamples(samples, attribute);
        List<List<Pair<LinkedHashMap<String, String>, String>>> subSamples = new ArrayList<>();
        for(Entry<String, List<Pair<LinkedHashMap<String, String>, String>>> splitSamplesEntry : splitSamples.entrySet()){
            subSamples.add(splitSamplesEntry.getValue());
        }
        return getSplitSamplesEntropy(subSamples);
    }

    public static List<Pair<String, Integer>> sortMapByValue(LinkedHashMap<String, Integer> map) {
        List<Entry<String, Integer>> entries = new LinkedList<>(map.entrySet());
        entries.sort(Entry.comparingByValue());
        Collections.reverse(entries);

        List<Pair<String, Integer>> valueList = new ArrayList<>();
        for (Entry<String, Integer> entry : entries) {
            valueList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        return valueList;
    }

    public static Node buildDecisionTree(List<Pair<LinkedHashMap<String, String>, String>> samples, List<String> labels, Node node) {
        if (labels.isEmpty()) {
            return node;
        }

        LinkedHashMap<String, Integer> classResult = new LinkedHashMap<>();
        for (Pair<LinkedHashMap<String, String>, String> sample : samples) {
            Integer classResultNum = classResult.getOrDefault(sample.getSecond(), 0);
            classResult.put(sample.getSecond(), ++classResultNum);
        }

        List<Pair<String, Integer>> classReulstList = sortMapByValue(classResult);
        String moreResult = classReulstList.get(0).getFirst();

        Double minSubSampleEntropy = 987654321.0;
        String testAttribute = "";
        for (String label : labels) {
            Double subSampleEntropy = informationGain(samples, label);
            if (subSampleEntropy < minSubSampleEntropy) {
                minSubSampleEntropy = subSampleEntropy;
                testAttribute = label;
            }
        }
        LinkedHashMap<String, List<Pair<LinkedHashMap<String, String>, String>>> splitSamples = getSplitSamples(samples, testAttribute);
        List<String> labels2 = new ArrayList<>(labels);
        labels2.remove(testAttribute);

        for (Entry<String, List<Pair<LinkedHashMap<String, String>, String>>> splitSample : splitSamples.entrySet()) {
            String attribute = splitSample.getKey();
            List<Pair<LinkedHashMap<String, String>, String>> subSamples = splitSample.getValue();
            node.setAttribute(testAttribute);
            Node verNode = verNode(attribute);
            node.getChildren().add(verNode);
            Node newNode = attrNode(moreResult);
            verNode.setNextAttr(newNode);
            buildDecisionTree(subSamples, labels2, newNode);
        }
        node.getChildren().add(verNode(moreResult));
        return node;
    }

    public static Node classify(LinkedHashMap<String, String> test, Node node, List<String> resultList){
        if(node.isAttrNode){
            String attr = node.getAttribute();
            if(resultList.contains(attr)){
                return node;
            } else {
                boolean flag = false;
                for (int i = 0 ; i < node.getChildren().size() ; i++){
                    Node child = node.getChildren().get(i);
                    if(child.getAttribute().equals(test.get(attr))){
                        flag = true;
                        return classify(test, child.getNextAttr(), resultList);
                    }
                }
                if(!flag){
                    return node.getChildren().get(node.getChildren().size()-1);
                }
            }
        }
        return node;
    }
}

