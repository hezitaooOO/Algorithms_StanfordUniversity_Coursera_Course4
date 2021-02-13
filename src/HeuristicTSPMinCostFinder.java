import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.zip.CheckedInputStream;

/**
 * Project: Coursera Algorithms by Standford University: Shortest Paths Revisited, NP-Complete Problems and What To Do About Them.
 * Description: Week 3 Programming Assignment.
 *              In this assignment we will revisit an old friend, the traveling salesman problem (TSP).
 *              This week you will implement a heuristic for the TSP, rather than an exact algorithm,
 *              and as a result will be able to handle much larger problem sizes.
 *              Here is a data file describing a TSP instance
 *              (original source: http://www.math.uwaterloo.ca/tsp/world/bm33708.tsp).
 *
 *              nn.txt
 *
 *              The first line indicates the number of cities.
 *              Each city is a point in the plane, and each subsequent line indicates the x- and y-coordinates of a single city.
 *              The distance between two cities is defined as the Euclidean distance --- that is,
 *              two cities at locations (x,y)(x,y) and (z,w)(z,w) have distance sqrt{(x-z)^2 + (y-w)^2} between them.
 *
 *              You should implement the nearest neighbor heuristic:
 *
 *              1. Start the tour at the first city.
 *
 *              2. Repeatedly visit the closest city that the tour hasn't visited yet.
 *                 In case of a tie, go to the closest city with the lowest index.
 *                 For example, if both the third and fifth cities have the same distance from the first city
 *                 (and are closer than any other city),
 *                 then the tour should begin by going from the first city to the third city.
 *
 *              3. Once every city has been visited exactly once, return to the first city to complete the tour.
 *
 *              In the box below, enter the cost of the traveling salesman tour
 *              computed by the nearest neighbor heuristic for this instance, rounded down to the nearest integer.
 *
 *              [Hint: when constructing the tour,
 *              you might find it simpler to work with squared Euclidean distances
 *              (i.e., the formula above but without the square root) than Euclidean distances.
 *              But don't forget to report the length of the tour in terms of standard Euclidean distance.]
 * @author : Zitao He
 * @date : 2021-02-13 00:03
 **/
public class HeuristicTSPMinCostFinder {
    private int numCities;
    private ArrayList<int[]> cities;
    private HashSet<Integer> visited;
    //double[][] distances;
    //double[][] solutionMatrix;

    public HeuristicTSPMinCostFinder(String fileInputName) throws FileNotFoundException {
        Scanner fileScanner;
        cities = new ArrayList<>();
        try {
            fileScanner = new Scanner(new File(fileInputName));
        } catch (IOException e) {
            throw new FileNotFoundException("Error: Input file is not found.");
        }
        numCities = Integer.parseInt(fileScanner.nextLine());
        //distances = new double[numCities][numCities];
        //solutionMatrix = new double[numCities][numCities];
        while (fileScanner.hasNextLine()){
            double[] city = new double[2];
            String[] cityString = fileScanner.nextLine().split(" ");
            int cityID = Integer.parseInt(cityString[0]);
            int cityX = Integer.parseInt(cityString[1]);
            int cityY = Integer.parseInt(cityString[2]);
            int[] cityCoordinates = {cityID, cityX, cityY};
            cities.add(cityCoordinates);
        }
        visited = new HashSet<>();
    }

    public ArrayList<Integer> runHeuristicTSP(){
        //int[] path = new int[numCities + 1];//number of cities plus one (start city appears twice in path)
        ArrayList<Integer> path = new ArrayList<>();
        int currCityID = cities.get(0)[0]; //initialize the currCity with the first city
        path.add(currCityID);
        while (visited.size() != numCities){

            int leftNearestCityID = -1;
            double leftMinDist = Integer.MAX_VALUE;

            int rightNearestCityID = -1;
            double rightMinDist = Integer.MAX_VALUE;


            int overallNearestCityID = -1;
            double overallMinDist = Integer.MAX_VALUE;

            //iteration to left of curr city, i is the city id, not array index
            for (int i = currCityID - 1; i >= 1; i --){
                if (visited.contains(i)){
                    continue;
                }
                int currCityX = cities.get(currCityID-1)[1];
                int otherCityX = cities.get(i - 1)[1];
                if (getEuclideanDist(currCityID, i) <= leftMinDist){ //if there is a tie, take the city that has smaller ID
                    leftMinDist = getEuclideanDist(currCityID, i);
                    leftNearestCityID = i;
                }
                if (currCityX - otherCityX > leftMinDist){ //The city X value is sorted in original file, so right value of X is always bigger than left value of X.
                    break;                                 //If X difference between two cities is already greater than min disc,
                }                                          // any further cities will be more distant than the city that has min distance.

            }

            //iteration to right of curr city, i is the city id, not array index
            for (int j = currCityID + 1; j <= numCities; j ++){
                if (visited.contains(j)){
                    continue;
                }
                int currCityX = cities.get(currCityID-1)[1];
                int otherCityX = cities.get(j - 1)[1];
                if (getEuclideanDist(currCityID, j) < rightMinDist){
                    rightMinDist = getEuclideanDist(currCityID, j);
                    rightNearestCityID = j;
                }
                if (otherCityX - currCityX > rightMinDist){
                    break;
                }
            }

            if (leftMinDist <= rightMinDist){
                overallMinDist = leftMinDist;
                overallNearestCityID = leftNearestCityID;
            }
            else {
                overallMinDist = rightMinDist;
                overallNearestCityID = rightNearestCityID;
            }
            if (path.size() == numCities){
                path.add(cities.get(0)[0]); //the final city is the start city
                break;
            }
            path.add(overallNearestCityID);
            visited.add(currCityID);
            currCityID = overallNearestCityID;
        }
        return path;
    }

    public double getTSPMinDist(){
        double minDist = 0;
        ArrayList<Integer> shortestPath = runHeuristicTSP();
        System.out.println(shortestPath);
        for (int i = 0; i < shortestPath.size() - 1; i ++){
            int cityID = shortestPath.get(i);
            int nextCityID = shortestPath.get(i + 1);
//            if (i == shortestPath.size() - 2){ // the last city in shortest path is the start city
//                nextCityID = shortestPath.get(0);
//            }
//            else{
//                nextCityID = shortestPath.get(i + 1);
//            }
            //System.out.println(i + " th iteration with city "  + cityID + " " + nextCityID + " " + getEuclideanDist(cityID, nextCityID));
            minDist += getEuclideanDist(cityID, nextCityID);
        }
        return minDist;
    }

    public double getEuclideanDist(int cityID, int otherCityID){
        int cityX = cities.get(cityID-1)[1];
        int cityY = cities.get(cityID-1)[2];

        int otherCityX = cities.get(otherCityID-1)[1];
        int otherCityY = cities.get(otherCityID-1)[2];

        return Math.sqrt(Math.pow(cityX - otherCityX, 2) + Math.pow(cityY - otherCityY, 2));
    }

    public void printCities(){
        for(int i = 0; i < cities.size(); i++){
            System.out.println(cities.get(i)[0] + "th city: " + cities.get(i)[1] + " " + cities.get(i)[2]);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        //nn-test1.txt has computed path: 1 3 2 5 6 4 1, TSP distance:15.2361
        //HeuristicTSPMinCostFinder tester  = new HeuristicTSPMinCostFinder("data/nn-test1.txt");

        HeuristicTSPMinCostFinder tester  = new HeuristicTSPMinCostFinder("data/nn-test1.txt");
        //tester.printCities();
        //System.out.println(tester.runHeuristicTSP());
        System.out.println(tester.getTSPMinDist());
    }
}
