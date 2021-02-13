import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

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
    private final int numCities;
    private ArrayList<double[]> cities;
    private HashSet<Integer> visited;
    private final int startCity;

    /**
     * Construct the TSP problem using external txt file (file format can be found in class description)
     * @param fileInputName external txt file name
     * @throws FileNotFoundException throws error if file is not found
     */
    public HeuristicTSPMinCostFinder(String fileInputName) throws FileNotFoundException {
        Scanner fileScanner;
        cities = new ArrayList<>();
        startCity = 1;
        try {
            fileScanner = new Scanner(new File(fileInputName));
        } catch (IOException e) {
            throw new FileNotFoundException("Error: Input file is not found.");
        }
        numCities = Integer.parseInt(fileScanner.nextLine());
        while (fileScanner.hasNextLine()){
            String[] cityString = fileScanner.nextLine().split(" ");
            double cityX = Double.parseDouble(cityString[1]);
            double cityY = Double.parseDouble(cityString[2]);
            double[] cityCoordinates = {cityX, cityY};
            cities.add(cityCoordinates);
        }
        visited = new HashSet<>();
    }

    /**
     * Run the heuristic greedy TSP algorithm
     * @return The array list that represents shortest path computed from the heuristic greedy TSP algorithm.
     *         The list starts with start city 1 and ends with start city 1.
     */
    public ArrayList<Integer> runHeuristicTSP(){

        //int[] path = new int[numCities + 1];//number of cities plus one (start city appears twice in path)
        ArrayList<Integer> path = new ArrayList<>();
        int currCityID = startCity; //initialize the currCity with the first city
        path.add(currCityID);
        while (visited.size() != numCities){

            int leftNearestCityID = -1;
            double leftMinDist = Integer.MAX_VALUE;

            int rightNearestCityID = -1;
            double rightMinDist = Integer.MAX_VALUE;

            //iteration to left of curr city, i is the city id, not array index
            for (int i = currCityID - 1; i >= 1; i --){
                if (visited.contains(i)){
                    continue;
                }
                double currCityX = cities.get(currCityID-1)[0];
                double otherCityX = cities.get(i - 1)[0];
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
                double currCityX = cities.get(currCityID-1)[0];
                double otherCityX = cities.get(j - 1)[0];
                if (getEuclideanDist(currCityID, j) < rightMinDist){
                    rightMinDist = getEuclideanDist(currCityID, j);
                    rightNearestCityID = j;
                }
                if (otherCityX - currCityX > rightMinDist){
                    break;
                }
            }

            visited.add(currCityID);

            //next iteration is on the closer city between left nearest city and right nearest city.
            if (leftMinDist <= rightMinDist){
                path.add(leftNearestCityID);
                currCityID = leftNearestCityID;
            }
            else {
                path.add(rightNearestCityID);
                currCityID = rightNearestCityID;
            }

            if (path.size() == numCities){
                path.add(startCity); //the final city is the start city
                break;
            }
        }
        return path;
    }

    /**
     * Compute the total distance of the shortest path
     * @return The total distance of the shortest path
     */
    public double getTSPMinDist(){
        double minDist = 0;
        ArrayList<Integer> shortestPath = runHeuristicTSP();
        for (int i = 0; i < shortestPath.size() - 1; i ++){
            int cityID = shortestPath.get(i);
            int nextCityID = shortestPath.get(i + 1);
            minDist += getEuclideanDist(cityID, nextCityID);
        }
        return minDist;
    }

    /**
     * Compute the Euclidean distance between two cities.
     * @param cityID ID of one of the two cities
     * @param otherCityID ID of the other city
     * @return  Euclidean distance between two cities.
     */
    public double getEuclideanDist(int cityID, int otherCityID){
        double cityX = cities.get(cityID-1)[0];
        double cityY = cities.get(cityID-1)[1];
        double otherCityX = cities.get(otherCityID-1)[0];
        double otherCityY = cities.get(otherCityID-1)[1];
        return Math.sqrt(Math.pow(cityX - otherCityX, 2) + Math.pow(cityY - otherCityY, 2));
    }

    public static void main(String[] args) throws FileNotFoundException {
        //nn-test1.txt has computed path: 1 3 2 5 6 4 1, TSP distance:15.2361
        //HeuristicTSPMinCostFinder tester  = new HeuristicTSPMinCostFinder("data/nn-test1.txt");

        //Correct answer for assignment is 1203406
        HeuristicTSPMinCostFinder tester  = new HeuristicTSPMinCostFinder("data/nn.txt");
        long clockStart = System.currentTimeMillis();
        System.out.println("Computed TPS minimum distance is: " + tester.getTSPMinDist());
        long clockEnd = System.currentTimeMillis();
        long runTime = clockEnd - clockStart;
        System.out.println("Run time is: " + (double)runTime/1000 + " seconds");
    }
}
