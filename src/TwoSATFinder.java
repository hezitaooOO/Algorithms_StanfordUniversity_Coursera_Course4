import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Project: Coursera Algorithms by Standford University: Shortest Paths Revisited, NP-Complete Problems and What To Do About Them.
 * Description: Week 4 Programming Assignment.
 *              In this assignment you will implement one or more algorithms for the 2SAT problem.  Here are 6 different 2SAT instances:
 *
 *              2sat1.txt
 *              2sat2.txt
 *              2sat3.txt
 *              2sat4.txt
 *              2sat5.txt
 *              2sat6.txt
 *
 *              The file format is as follows.
 *              In each instance, the number of variables and the number of clauses is the same,
 *              and this number is specified on the first line of the file.
 *              Each subsequent line specifies a clause via its two literals,
 *              with a number denoting the variable and a "-" sign denoting logical "not".
 *              For example, the second line of the first data file is "-16808 75250",
 *              which indicates the clause (¬X_16808 V X_75250). The only case that the clause is not
 *              satisfied is when X_16808 = true AND X_75250 = false.
 *
 *              Your task is to determine which of the 6 instances are satisfiable,
 *              and which are unsatisfiable.  In the box below, enter a 6-bit string,
 *              where the ith bit should be 1 if the ith instance is satisfiable, and 0 otherwise.
 *              For example, if you think that the first 3 instances are satisfiable
 *              and the last 3 are not, then you should enter the string 111000 in the box below.
 *
 *              DISCUSSION: This assignment is deliberately open-ended,
 *              and you can implement whichever 2SAT algorithm you want.
 *              For example, 2SAT reduces to computing the strongly connected components
 *              of a suitable graph (with two vertices per variable and two directed edges per clause,
 *              you should think through the details).
 *              This might be an especially attractive option for those of you
 *              who coded up an SCC algorithm in Part 2 of this specialization.
 *              Alternatively, you can use Papadimitriou's randomized local search algorithm.
 *              (The algorithm from lecture is probably too slow as stated,
 *              so you might want to make one or more simple modifications to it
 *              --- even if this means breaking the analysis given in lecture --- to ensure that
 *              it runs in a reasonable amount of time.)
 *              A third approach is via backtracking.
 *              In lecture we mentioned this approach only in passing;
 *              see Chapter 9 of the Dasgupta-Papadimitriou-Vazirani book,
 *              for example, for more details.
 *
 * @author : Zitao He
 * @date : 2021-02-13 23:04
 **/
public class TwoSATFinder {

    private final ArrayList<int[]> clauses;
    private ArrayList<int[]> reducedClauses;
    private final int numVariables; //number of variables and number of clauses the same

    /**
     * Construct the 2 SAT problem using external txt file. The file format can be found in class description.
     * @param fileInputName
     * @throws FileNotFoundException
     */
    public TwoSATFinder(String fileInputName) throws FileNotFoundException{
        Scanner fileScanner;

        try {
            fileScanner = new Scanner(new File(fileInputName));
        } catch (IOException e) {
            throw new FileNotFoundException("Error: Input file is not found.");
        }

        numVariables = Integer.parseInt(fileScanner.nextLine());
        clauses = new ArrayList<>();
        reducedClauses = new ArrayList<>();

        while (fileScanner.hasNextLine()){
            int[] clause = new int[2];
            String[] clauseString = fileScanner.nextLine().split(" ");
            clause[0] = Integer.parseInt(clauseString[0]);
            clause[1] = Integer.parseInt(clauseString[1]);
            clauses.add(clause);
            reducedClauses.add(clause);
        }
    }

    /**
     * Remove redundant clauses. If there is a clause item that has no its negated clause item appears,
     * the clause can be simply removed because there is always a value for it (1 or 0) that satisfies the clause.
     * Removing process will run for multiple times until there is no more such a clause to reduce.
     */
    private void removeRedundantClauses(){

        HashSet<Integer> uniqueClauseElements = new HashSet<>();
        HashSet<Integer> noNegatedClauseElements = new HashSet<>(); //to store all clause elements that do not have negated clause elements
        ArrayList<int[]> reducedClausesTemp = new ArrayList<>();
        boolean ignite = true; //boolean variable only used to ignite while loop
        while(!noNegatedClauseElements.isEmpty() || ignite){
            ignite = false;
            uniqueClauseElements.clear();
            noNegatedClauseElements.clear();
            reducedClausesTemp.clear();
            for (int[] clause : reducedClauses){
                uniqueClauseElements.add(clause[0]);
                uniqueClauseElements.add(clause[1]);
            }
            for (Integer clauseElement : uniqueClauseElements){
                int negated = clauseElement * (-1);
                if (!uniqueClauseElements.contains(negated)){
                    noNegatedClauseElements.add(clauseElement);
                }
            }
            for (int[] clause : reducedClauses){
                if (!noNegatedClauseElements.contains(clause[0]) && !noNegatedClauseElements.contains(clause[1])){
                    reducedClausesTemp.add(clause);
                }
            }
            reducedClauses.clear();
            reducedClauses.addAll(reducedClausesTemp);
        }
    }

    /**
     * Run Papadimitriou's local search algorithm to find a solution that satisfies all clauses(reduced).
     * @return a valid integer array if a satisfied solution is found and null if not.
     */
    public int[] runTwoSatSearch(){
        removeRedundantClauses();
        int[] assignment = new int[numVariables];
        if (reducedClauses.isEmpty()){
            Arrays.fill(assignment, getRandomOneOrZero());
            return assignment;
        }
        int trials = (int)(Math.log(numVariables) / Math.log(2)) + 1; //Papadimitriou's algorithm. Try log2(n) times of local search.

        BigInteger subTrials = new BigInteger(String.valueOf(numVariables)); //Papadimitriou's algorithm. Try 2*n^2 times to flip one value in arbitrary unsatisfied clause and check all clauses.
        subTrials = subTrials.multiply(new BigInteger(String.valueOf(numVariables))).multiply(new BigInteger(String.valueOf(2)));

        for (int i = 0; i < trials; i ++){
            HashSet<int[]> unsatisfiedClauses = new HashSet<>(); //track unsatisfied clauses. As search goes go, the size of this set will be reduced.
            //choose a random initial assignment for all variables
            Arrays.fill(assignment, getRandomOneOrZero());
            System.out.println("Current trial: " + i + "/" + (trials-1));
            BigInteger k = new BigInteger("0");

            while(k.compareTo(subTrials) < 0){
                unsatisfiedClauses.clear();
                for (int[] clause : reducedClauses){

                    int assignedFirstValue = assignment[Math.abs(clause[0]) - 1];
                    int assignedSecondValue = assignment[Math.abs(clause[1]) - 1];

                    //get the value of first/second clause element(either 1 or 0, depending on clause operator and assigned variable value).
                    int clauseFirstValue = clause[0] < 0 ? 1 - assignedFirstValue : assignedFirstValue;
                    int clauseSecondValue = clause[1] < 0 ? 1 - assignedSecondValue : assignedSecondValue;

                    if (clauseFirstValue == 0 && clauseSecondValue == 0){//the only way to fail the clause is that both clause values are zero
                        unsatisfiedClauses.add(clause);
                    }
                }
                if (unsatisfiedClauses.isEmpty()){ //If a satisfied solution is found, this set goes to empty. Return the current solution as satisfied solution.
                    return assignment;
                }
                //Pick an arbitrary unsatisfied clause and flip the value of one of its variable(choose between the two uniformly at random).
                int[] arbitraryUnsatisfiedClause = getRandomClause(unsatisfiedClauses);
                int variableToFlip = Math.abs(arbitraryUnsatisfiedClause[getRandomOneOrZero()]);
                assignment[variableToFlip - 1] = 1 - assignment[variableToFlip - 1]; // flip the value.
                k = k.add(new BigInteger("1"));
            }
        }
        return null;
    }

    /**
     * Helper method to uniformly, randomly generate an one or a zero.
     * @return random value (either an one or a zero).
     */
    private int getRandomOneOrZero(){
        Random rand = new Random();
        return rand.nextBoolean() ? 1 : 0;
    }

    /**
     * Helper method to uniformly, randomly return a element in passed HashSet.
     * @param clauseSet the set to give random element.
     * @return the random element in set.
     */
    private int[] getRandomClause(HashSet<int[]> clauseSet){
        if (clauseSet.isEmpty()){
            throw new IllegalArgumentException("Error in method getRandomClause(HashSet<int[]>, the set passed in is empty");
        }
        int size = clauseSet.size();
        int randomIndex = new Random().nextInt(size);
        int i = 0;
        for(int[] clause : clauseSet){
            if (i == randomIndex)
                return clause;
            i++;
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        //expected answer for 2sat-test1 to 2sat-test4 is true/true/true/false
        TwoSATFinder tester  = new TwoSATFinder("data/2sat1.txt");
        //tester.removeRedundantClauses();
        //tester.printClauses();
        //tester.printReducedClauses();
        int[] solution = tester.runTwoSatSearch();

        //if the program takes a long time to run, it is most likely there is no satisfied solution.
        //Correct answer to the assignment is 101100.
        if (solution != null){
            System.out.println("Found a satisfied solution for 2 SAT problem.");
        }
        else{
            System.out.println("A satisfied solution for 2 SAT problem does not exist.");
        }
    }
}
