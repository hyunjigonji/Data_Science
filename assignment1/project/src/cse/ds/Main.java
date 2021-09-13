package cse.ds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        // 1. Set variables
        Double minSup = Double.valueOf(args[0]);
        String inputFile = args[1], outputFile = args[2];

        Set<Set<Integer>> notFrequent = new HashSet<>();

        // 2. Get transactions from input
        ArrayList<Set<Integer>> transactions = getTransactions(inputFile);

        // 3. Get C1 from transactions
        Map<Set<Integer>, Double> C1 = new HashMap<>();
        for ( Set<Integer> transaction : transactions ) {
            for ( Integer nowItem : transaction ) {
                Set<Integer> nowItemSet = new HashSet<>();
                nowItemSet.add(nowItem);

                Set<Set<Integer>> C1KeySet = C1.keySet();
                if(!C1KeySet.contains(nowItemSet)){
                    Double nowSupport = getSupport(nowItemSet, transactions);
                    C1.put(nowItemSet, nowSupport);
                }
            }
        }

        // 4. Get L1 generated from C1
        Map<Set<Integer>, Double> L1 = new HashMap<>(C1);
        Iterator<Set<Integer>> L1Iter = L1.keySet().iterator();
        while(L1Iter.hasNext()){
            Set<Integer> nowItemSet = L1Iter.next();
            Double nowSupport = L1.get(nowItemSet);

            if(nowSupport < minSup){
                L1Iter.remove();
                notFrequent.add(nowItemSet);
            }
        }

        // 5. Get frequent item sets with loop
        Map<Set<Integer>, Double> C = new HashMap<>(C1);
        Map<Set<Integer>, Double> Lk = new HashMap<>(L1);
        Set<Set<Integer>> L = new HashSet<>(L1.keySet());

        for(int k = 2 ; !C.isEmpty() ; k++){
            C = getC(Lk, k, transactions, notFrequent);
            if(C.isEmpty()) break;

            Lk = getL(C, notFrequent, minSup);
            L.addAll(Lk.keySet());
        }

        // 6. Get association rules
        Set<ArrayList<Set<Integer>>> associations = getAssociations(L);

        // 7. Get the results of specific format
        printOutput(associations, transactions, outputFile);
    }

    public static ArrayList<Set<Integer>> getTransactions(String inputFile){

        ArrayList<Set<Integer>> transactions = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();

            while (line != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                Set<Integer> nowItemSet = new HashSet<>();

                while(tokenizer.hasMoreTokens()){
                    Integer nowItem = Integer.valueOf(tokenizer.nextToken());
                    nowItemSet.add(nowItem);
                }

                transactions.add(nowItemSet);
                line = reader.readLine();
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return transactions;
    }

    public static String makeFormat(ArrayList<Set<Integer>> association, ArrayList<Set<Integer>> transactions){

        Set<Integer> left = association.get(0);
        Set<Integer> right = association.get(1);

        Set<Integer> all = new HashSet<>(left);
        all.addAll(right);
        Double support = getSupport(all, transactions);

        Double confidence = getConfidence(left, right, transactions);

        StringBuilder output = new StringBuilder("{");
        Iterator<Integer> leftIter = left.iterator();
        while(leftIter.hasNext()){
            output.append(leftIter.next().toString());
            if(leftIter.hasNext()){ output.append(","); }
        }
        output.append("}\t{");

        Iterator<Integer> rightIter = right.iterator();
        while(rightIter.hasNext()){
            output.append(rightIter.next().toString());
            if(rightIter.hasNext()){ output.append(","); }
        }
        output.append("}\t");
        output.append(String.format("%.2f", support)).append("\t");
        output.append(String.format("%.2f", confidence));

        return output.toString();
    }

    public static void printOutput(Set<ArrayList<Set<Integer>>> associations, ArrayList<Set<Integer>> transactions, String outputFile){

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            for( ArrayList<Set<Integer>> association : associations ){
                String output = makeFormat(association, transactions);

                writer.write(output);
                writer.newLine();
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static Set<ArrayList<Set<Integer>>> getAssociations(Set<Set<Integer>> L){

        Set<ArrayList<Set<Integer>>> associations = new HashSet<>();

        for( Set<Integer> itemSet : L ){
            Set<Set<Integer>> powerSets = getPowerSet(itemSet);

            for( Set<Integer> powerSet : powerSets ){
                Set<Integer> nowPowerSetLeft = new HashSet<>(powerSet);
                Set<Integer> nowPowerSetRight = new HashSet<>(itemSet);
                nowPowerSetRight.removeAll(nowPowerSetLeft);

                ArrayList<Set<Integer>> nowAssociation = new ArrayList<>();
                nowAssociation.add(nowPowerSetLeft);
                nowAssociation.add(nowPowerSetRight);

                if(!associations.contains(nowAssociation)){ associations.add(nowAssociation); }
            }
        }
        return associations;
    }

    public static Set<Set<Integer>> getPowerSet(Set<Integer> itemSet){

        Set<Set<Integer>> powerSet = new HashSet<>();

        ArrayList<Integer> itemSetArray = new ArrayList<>(itemSet);
        Integer setSize = itemSet.size();
        for(int i = 0 ; i < 1<<setSize ; i++){
            Set<Integer> nowItemSet = new HashSet<>();
            for(int j = 0 ; j < setSize ; j++){
                if((i & 1<<j) != 0){
                    nowItemSet.add(itemSetArray.get(j));
                }
            }
            if(!nowItemSet.isEmpty() && !nowItemSet.equals(itemSet)){ powerSet.add(nowItemSet); }
        }
        return powerSet;
    }

    public static Set<Set<Integer>> getC0(Map<Set<Integer>, Double> L, Integer k, Set<Set<Integer>> notFrequent){

        Set<Set<Integer>> newC = new HashSet<>();

        Set<Set<Integer>> LKeySet = L.keySet();
        for( Set<Integer> LKey1 : LKeySet ){
            for( Set<Integer> LKey2 : LKeySet ){
                Set<Integer> newLKey = new HashSet<>(LKey1);
                newLKey.addAll(LKey2);

                if(newLKey.size() != k) continue;
                if(isNotFrequent(newLKey, notFrequent)) continue;

                if(!newC.contains(newLKey)){ newC.add(newLKey); }
            }
        }
        return newC;
    }

    public static Map<Set<Integer>, Double> getC(Map<Set<Integer>, Double> L, Integer k, ArrayList<Set<Integer>> transactions, Set<Set<Integer>> notFrequent){

        Map<Set<Integer>, Double> newC = new HashMap<>();

        Set<Set<Integer>> C = getC0(L, k, notFrequent);
        for( Set<Integer> CKeySet : C ){
            Double support = getSupport(CKeySet, transactions);
            newC.put(CKeySet, support);
        }
        return newC;
    }

    public static Map<Set<Integer>, Double> getL(Map<Set<Integer>, Double> C, Set<Set<Integer>> notFrequent, Double minSup){

        Map<Set<Integer>, Double> L = new HashMap<>(C);

        Iterator<Set<Integer>> LIter = L.keySet().iterator();
        while(LIter.hasNext()){
            Set<Integer> nowItemSet = LIter.next();
            Double nowSupport = L.get(nowItemSet);

            if(nowSupport < minSup){
                LIter.remove();
                notFrequent.add(nowItemSet);
            }
        }
        return L;
    }

    public static boolean isNotFrequent(Set<Integer> itemSet, Set<Set<Integer>> notFrequent){

        for( Set<Integer> notFrequentItemSet : notFrequent ){
            boolean isNotFrequent = true;
            for( Integer notFrequentItem : notFrequentItemSet ){
                if(!itemSet.contains(notFrequentItem)){ isNotFrequent = false; }
            }
            if(isNotFrequent) return true;
        }
        return false;
    }

    public static Double getSupport(Set<Integer> itemSet, ArrayList<Set<Integer>> transactions){

        Double transactionSize = transactions.size() * 1.0;
        Double containedNum = 0.0;
        for( Set<Integer> transaction : transactions ){
            boolean isContained = true;
            for( Integer item : itemSet ){
                if(!transaction.contains(item)){ isContained = false; }
            }
            if(isContained){ containedNum++; }
        }
        return containedNum / transactionSize * 100;
    }

    public static Double getConfidence(Set<Integer> left, Set<Integer> right, ArrayList<Set<Integer>> transactions){

        Double leftContainedNum = 0.0;
        Double allContainedNum = 0.0;
        for( Set<Integer> transaction : transactions ){
            boolean isLeftContained = true;
            for( Integer leftItem : left ){
                if(!transaction.contains(leftItem)){ isLeftContained = false; }
            }
            if(isLeftContained){
                leftContainedNum++;
                boolean isRightContained = true;
                for( Integer rightItem : right ){
                    if(!transaction.contains(rightItem)){ isRightContained = false; }
                }
                if(isRightContained){ allContainedNum++; }
            }
        }
        return allContainedNum / leftContainedNum * 100;
    }
}
