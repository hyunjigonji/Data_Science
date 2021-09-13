package cse.ds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // 1. get data and preprocess
        String inputFile = args[0];
        Integer n = Integer.parseInt(args[1]);
        Double eps = Double.parseDouble(args[2]), minPts = Double.parseDouble(args[3]);

        ArrayList<Pair<Double, Double>> points = getPoints(inputFile);
        ArrayList<Integer> corePoints = getCorePoints(points, eps, minPts);
        ArrayList<ArrayList<Integer>> neighbors = getNeighbors(points, eps);

        // 2. bfs for core points
        ArrayList<Boolean> isVisit = new ArrayList<>(Collections.nCopies(points.size()+5, false));
        ArrayList<ArrayList<Integer>> clusters = new ArrayList<>();

        for(int i = 0 ; i < corePoints.size() ; i++){
            Integer nowCorePointIndex = corePoints.get(i);
            if(!isVisit.get(nowCorePointIndex)){ // not visited
                Queue<Pair<Integer,Pair<Double, Double>>> qu = new LinkedList<>();
                qu.add(new Pair<>(nowCorePointIndex, points.get(nowCorePointIndex)));
                isVisit.set(nowCorePointIndex, true);

                ArrayList<Integer> nowCluster = new ArrayList<>();
                while(!qu.isEmpty()){
                    Integer nowIndex = qu.peek().getFirst();
                    Pair<Double, Double> nowPoint = qu.peek().getSecond();
                    nowCluster.add(nowIndex);
                    qu.poll();

                    // add neighbors
                    for(int j = 0 ; j < neighbors.get(nowIndex).size() ; j++){
                        Integer nowNeighbor = neighbors.get(nowIndex).get(j);
                        if(!isVisit.get(nowNeighbor) && !corePoints.contains(nowNeighbor)){
                            nowCluster.add(nowNeighbor);
                            isVisit.set(nowNeighbor, true);
                        }
                    }

                    for(int j = 0 ; j < corePoints.size() ; j++){
                        Integer nextCorePointIndex = corePoints.get(j);
                        if(!nowCorePointIndex.equals(nextCorePointIndex) && !isVisit.get(nextCorePointIndex)){ // not visited
                            Pair<Double, Double> nextPoint = points.get(nextCorePointIndex);
                            if(isDensityReachable(nowPoint, nextPoint, eps)) {
                                isVisit.set(nextCorePointIndex, true);
                                qu.add(new Pair<>(nextCorePointIndex, nextPoint));
                            }
                        }
                    }
                }
                if(nowCluster.size() > 1){
                    clusters.add(nowCluster);
                }
            }
        }

        // 3. remove noise
        ArrayList<ArrayList<Integer>> realClusters = new ArrayList<>();
        for(ArrayList<Integer> cluster : clusters){
            Boolean hasCore = false;
            for(Integer point : cluster){
                if(corePoints.contains(point)) hasCore = true;
            }
            if(hasCore){
                realClusters.add(cluster);
            }
        }

        // 4. remove small cluster
        if(realClusters.size() > n){
            ArrayList<Integer> sizeClusters = new ArrayList<>();
            for(ArrayList<Integer> cluster : realClusters){
                sizeClusters.add(cluster.size());
            }
            Collections.sort(sizeClusters);
            Integer realClusterSize = realClusters.size();
            for(int i = 0 ; i < realClusterSize-n ; i++){
                Integer nowSize = sizeClusters.get(i);
                for(int j = 0 ; j < realClusters.size() ; j++){
                    if(realClusters.get(j).size() == nowSize){
                        realClusters.remove(j);
                        break;
                    }
                }
            }
        }
        // 5. make output file
        getResult(realClusters, inputFile);
    }

    public static ArrayList<Integer> getCorePoints(ArrayList<Pair<Double, Double>> points, Double eps, Double minPts){
        ArrayList<Integer> corePoints = new ArrayList<>();
        for(int i = 0 ; i < points.size() ; i++){
            Pair<Double, Double> nowPoint = points.get(i);
            int neighbors = 0;
            for (Pair<Double, Double> nextPoint : points) {
                if (isDensityReachable(nowPoint, nextPoint, eps)) {
                    neighbors++;
                }
            }
            if(neighbors >= minPts){
                corePoints.add(i);
            }
        }
        return corePoints;
    }

    public static ArrayList<ArrayList<Integer>> getNeighbors(ArrayList<Pair<Double, Double>> points, Double eps){
        ArrayList<ArrayList<Integer>> neighbors = new ArrayList<>();
        for(int i = 0 ; i < points.size() ; i++){
            neighbors.add(new ArrayList<>());
        }

        for(int i = 0 ; i < points.size() ; i++){
            Pair<Double, Double> nowPoint = points.get(i);
            for(int j = 0 ; j < points.size() ; j++){
                Pair<Double, Double> nextPoint = points.get(j);
                if(isDensityReachable(nowPoint, nextPoint, eps)){
                    neighbors.get(i).add(j);
                    neighbors.get(j).add(i);
                }
            }
        }
        return neighbors;
    }

    public static Boolean isDensityReachable(Pair<Double, Double> leftPoint, Pair<Double, Double> rightPoint, Double eps){
        return getDistance(leftPoint, rightPoint) <= eps;
    }

    public static Double getDistance(Pair<Double, Double> leftPoint, Pair<Double, Double> rightPoint){
        Double leftX = leftPoint.getFirst(), leftY = leftPoint.getSecond();
        Double rightX = rightPoint.getFirst(), rightY = rightPoint.getSecond();

        return Math.sqrt(Math.pow((leftX-rightX),2) + Math.pow((leftY-rightY),2));
    }

    public static ArrayList<Pair<Double, Double>> getPoints(String inputFile){
        ArrayList<Pair<Double, Double>> points = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();

            while(line != null){
                StringTokenizer tokenizer = new StringTokenizer(line, "\t");

                Integer index = Integer.parseInt(tokenizer.nextToken());
                Double x = Double.parseDouble(tokenizer.nextToken());
                Double y = Double.parseDouble(tokenizer.nextToken());

                points.add(new Pair<>(x,y));
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return points;
    }

    public static void getResult(ArrayList<ArrayList<Integer>> clusters, String inputFile){
        String outputFile = inputFile.split("\\.")[0];
        for(int i = 0 ; i < clusters.size() ; i++){
            ArrayList<Integer> cluster = clusters.get(i);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile + "_cluster_" + i + ".txt"));

                for(Integer point : cluster){
                    writer.write(String.valueOf(point));
                    writer.newLine();
                }
                writer.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}