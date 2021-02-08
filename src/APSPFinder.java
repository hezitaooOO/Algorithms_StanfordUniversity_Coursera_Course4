import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

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
    //private HashMap<Integer, ArrayList<Integer>> vertices; //Adjacency list of the graph. Key is vertex ID and value is outgoing vertices
    private HashMap<String, GraphEdge> edges; //Edges in this graph
    //private GraphEdge[][] edgeMatrix; //edge matrix for fast edge look up
    private int[][][] solutionMatrix;

    public APSPFinder(String fileInputName) throws FileNotFoundException {
        Scanner fileScanner;
        //vertices = new HashMap<>();
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
        //add vertices and build edge matrix for fast edge look up
//        edgeMatrix = new GraphEdge[numVertices][numVertices];
//        for (GraphEdge edge : edges){
//            int start = edge.getStartID();
//            int end = edge.getEndID();
//            if (!vertices.containsKey(start+"->"+end)){
//                vertices.put(edge.getStartID(), new ArrayList<>());
//            }
//            vertices.get(edge.getStartID()).add(edge.getEndID());
//            edgeMatrix[edge.getStartID()-1][edge.getEndID()-1] = edge;
//        }
        solutionMatrix = new int[numVertices+1][numVertices+1][numVertices+1];

    }

    public void runAPSP(){
        //initialize the 3D array for solution matrix
        for (int i = 1; i <= numVertices; i++){
            for (int j = 1; j<= numVertices; j++){
                if (i == j){
                    solutionMatrix[i][j][0] = 0;
                }
                if (edges.keySet().contains(i+"->"+j)){
                    solutionMatrix[i][j][0] = edges.get(i+"->"+j).getLength();
                }
                if (i != j && !edges.keySet().contains(i+"->"+j)){
                    //solutionMatrix[i][j][0] = Integer.MAX_VALUE;
                    solutionMatrix[i][j][0] = 1000000000;

                }
            }
        }

        //main loop of Floyd-Warshall algorithm
        for (int k = 1; k <= numVertices; k ++){
            for (int i = 1; i <= numVertices; i ++){
                for (int j = 1; j <= numVertices; j ++){
                    solutionMatrix[i][j][k] = Math.min(solutionMatrix[i][j][k-1],
                            solutionMatrix[i][k][k-1] + solutionMatrix[k][j][k-1]);
                }
            }
        }
    }

    public int getShortestPath(){
        int minPath = Integer.MAX_VALUE;
        for (int i = 1; i <= numVertices; i ++){
            if (solutionMatrix[i][i][numVertices] < 0){
//                System.out.println("i value = " + i);
//                System.out.println("solutionMatrix[i][i][numVertices] = " + solutionMatrix[i][i][numVertices]);
//                System.out.println("Integer.MIN = " + Integer.MIN_VALUE);
//                System.out.println("Integer.MAX = " + Integer.MAX_VALUE);
                return -1;
            }
        }

        for (int i = 1; i <= numVertices; i ++){
            for (int j = 1; j <= numVertices; j ++){
                if (solutionMatrix[i][j][numVertices] < minPath){
                    minPath = solutionMatrix[i][j][numVertices];
                }
            }
        }
        return minPath;
    }

    public void printGraph(){
        System.out.println("********************   Printing graph info    ********************");
        System.out.println("Number of vertices: " + numVertices);
        System.out.println("Number of edges: " + numEdges);
        System.out.println("********************   Printing edge list    ********************");
        for (String edgeKey : edges.keySet()){
            System.out.println(edgeKey + " : " + edges.get(edgeKey));
        }
    }

    public void test(){
        System.out.println(Arrays.deepToString(solutionMatrix));
    }

    public static void main(String[] args) throws FileNotFoundException {
        //this test file has negative cycle
        //APSPFinder tester = new APSPFinder("data/g-test1.txt");

        //this test file has no negative cycle and expected result is -2
        APSPFinder tester = new APSPFinder("data/g-test2.txt");

        //APSPFinder tester = new APSPFinder("data/g1.txt");

        tester.printGraph();
        tester.runAPSP();
        //tester.test();

        System.out.println(tester.getShortestPath());
    }

}
