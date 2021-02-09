import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Project: Coursera Algorithms by Standford University: Shortest Paths Revisited, NP-Complete Problems and What To Do About Them.
 * Description: Week 1 Programming Assignment.
 *              In this assignment you will implement one or more algorithms for the all-pairs shortest-path problem.
 *              Here are data files describing three graphs(find them in data folder):
 *
 *              g1.txt
 *              g2.txt
 *              g3.txt
 *
 *              The first line indicates the number of vertices and edges, respectively.
 *              Each subsequent line describes an edge (the first two numbers are its tail and head, respectively)
 *              and its length (the third number).
 *              NOTE: some of the edge lengths are negative.
 *              NOTE: These graphs may or may not have negative-cost cycles.
 *
 *              Your task is to compute the "shortest shortest path".
 *              Precisely, you must first identify which, if any, of the three graphs have no negative cycles.
 *              For each such graph, you should compute all-pairs shortest paths
 *              and remember the smallest one (i.e., compute min_d(u, v) where d(u,v) denotes the shortest-path distance from u to v).
 *
 *              If each of the three graphs has a negative-cost cycle,
 *              then enter "NULL" in the box below.
 *              If exactly one graph has no negative-cost cycles,
 *              then enter the length of its shortest shortest path in the box below.
 *              If two or more of the graphs have no negative-cost cycles,
 *              then enter the smallest of the lengths of their shortest shortest paths in the box below.
 *
 * @author : Zitao He
 * @date : 2021-02-07 18:39
 **/
public class APSPFinder {

    private int numVertices;
    private int numEdges;
    private HashMap<String, GraphEdge> edges; //Edges in this graph
    private int[][][] solutionMatrix;

    /**
     * Constructor of APSP(all pairs shortest path) finder. The file format can be found in class description
     * @param fileInputName file name to be used
     * @throws FileNotFoundException throws error if file not found
     */
    public APSPFinder(String fileInputName) throws FileNotFoundException {
        Scanner fileScanner;
        edges = new HashMap<>();
        try {
            fileScanner = new Scanner(new File(fileInputName));
        } catch (IOException e) {
            throw new FileNotFoundException("Error: Input file is not found.");
        }
        String[] firstLine = fileScanner.nextLine().split(" ");
        numVertices = Integer.parseInt(firstLine[0]);
        numEdges = Integer.parseInt(firstLine[1]);

        while(fileScanner.hasNextLine()){
            String[] info = fileScanner.nextLine().split(" ");

            int edgeStart = Integer.parseInt(info[0]);
            int edgeEnd = Integer.parseInt(info[1]);
            int edgeLength = Integer.parseInt(info[2]);

            GraphEdge edge = new GraphEdge(edgeStart, edgeEnd, edgeLength);
            String key = info[0]+"->"+info[1];
            edges.put(key, edge);
        }
        solutionMatrix = new int[numVertices+1][numVertices+1][2];
    }

    /**
     * Run Floyd-Warshall algorithm
     */
    public void runAPSP(){
        //HashSet<Integer> diagonalValue = new HashSet<>();
        //initialize the 3D array for solution matrix
        for (int i = 1; i <= numVertices; i++){
            for (int j = 1; j<= numVertices; j++){
                if (i == j){
                    solutionMatrix[i][j][0] = 0;
                }
                if (edges.containsKey(i+"->"+j)){
                    solutionMatrix[i][j][0] = edges.get(i+"->"+j).getLength();
                }
                if (i != j && !edges.containsKey(i+"->"+j)){
                    solutionMatrix[i][j][0] = 999;
                }
            }
        }
        //main loop of Floyd-Warshall algorithm
        int k = 1;
        while(k <= numVertices){
            for (int i = 1; i <= numVertices; i ++){
                for (int j = 1; j <= numVertices; j ++){
                    solutionMatrix[i][j][1] = Math.min(solutionMatrix[i][j][0],
                            ((solutionMatrix[i][k][0]) + (solutionMatrix[k][j][0])));
                }
            }
            for (int i = 1; i <= numVertices; i ++){
                for (int j = 1; j <= numVertices; j ++){
                    solutionMatrix[i][j][0] = solutionMatrix[i][j][1];
                }
            }
            k ++;
        }
    }

    /**
     * Get the shortest path for any possible pairs of vertices
     * @return shortest path for any possible pairs of vertices
     *         if the graph contains negative cycles, Floyd-Warshall algorithm can't be applied and return 99999
     */
    public int getShortestPath(){
        int minPath = Integer.MAX_VALUE;
        for (int i = 1; i <= numVertices; i ++){
            if (solutionMatrix[i][i][1] < 0){
                return 99999;
            }
        }

        for (int i = 1; i <= numVertices; i ++){
            for (int j = 1; j <= numVertices; j ++){
                if (solutionMatrix[i][j][1] < minPath){
                    minPath = solutionMatrix[i][j][1];
                }
            }
        }
        return minPath;
    }

    public static void main(String[] args) throws FileNotFoundException {
        //correct answer is -19 which is from g3 (g1 and g2 have negative cycles and can't be computed)
        APSPFinder tester1 = new APSPFinder("data/g1.txt");
        tester1.runAPSP();
        if (tester1.getShortestPath() == 99999){
            System.out.println("data/g1.txt has negative cycle! Can't compute APSP problem.");
        }
        else{
            System.out.println("data/g1.txt -> shortest shortest path is " + tester1.getShortestPath());
        }

        APSPFinder tester2 = new APSPFinder("data/g2.txt");
        tester2.runAPSP();
        if (tester2.getShortestPath() == 99999){
            System.out.println("data/g2.txt has negative cycle! Can't compute APSP problem.");
        }
        else{
            System.out.println("data/g2.txt -> shortest shortest path is " + tester2.getShortestPath());
        }

        APSPFinder tester3 = new APSPFinder("data/g3.txt");
        tester3.runAPSP();
        if (tester3.getShortestPath() == 99999){
            System.out.println("data/g3.txt has negative cycle! Can't compute APSP problem.");
        }
        else{
            System.out.println("data/g3.txt -> shortest shortest path is " + tester3.getShortestPath());
        }
    }

}
