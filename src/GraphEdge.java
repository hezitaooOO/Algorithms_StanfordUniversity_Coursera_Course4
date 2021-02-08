/**
 * Project: Coursera Algorithms by Standford University: Shortest Paths Revisited, NP-Complete Problems and What To Do About Them.
 * Description: This class represents an edge in graph. It has start and end node, along with the length.
 *
 * @author : Zitao He
 * @date : 2021-01-24 12:36
 **/
public class GraphEdge implements Comparable<GraphEdge>{
    private int startID;
    private int endID;
    private int length;

    public GraphEdge(int startID, int endID, int length){
        this.startID = startID;
        this.endID = endID;
        this.length = length;
    }
    public int getStartID() {
        return startID;
    }

    public int getEndID() {
        return endID;
    }

    public int getLength() {
        return length;
    }

    @Override
    public int compareTo(GraphEdge other){
        if (this.getLength() < other.getLength()){
            return -1;
        }
        if (this.getLength() > other.getLength()){
            return 1;
        }
        else{
            return 0;
        }
    }

    @Override
    public String toString() {
        return "GraphEdge{" +
                "startID=" + startID +
                ", endID=" + endID +
                ", length=" + length +
                '}';
    }
}
