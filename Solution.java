import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author LivArmandNoumi_(l-a-n)
 * 
 *         Facebook hiring sample test
 * 
 *         There are K pegs. Each peg can hold discs in decreasing order of
 *         radius when looked from bottom to top of the peg. There are N discs
 *         which have radius 1 to N; Given the initial configuration of the pegs
 *         and the final configuration of the pegs, output the moves required to
 *         transform from the initial to final configuration. You are required
 *         to do the transformations in minimal number of moves.
 * 
 *         A move consists of picking the topmost disc of any one of the pegs
 *         and placing it on top of anyother peg. At anypoint of time, the
 *         decreasing radius property of all the pegs must be maintained.
 * 
 *         Constraints: 1<= N<=8 3<= K<=5
 * 
 *         Input Format: N K 2nd line contains N integers. Each integer in the
 *         second line is in the range 1 to K where the i-th integer denotes the
 *         peg to which disc of radius i is present in the initial
 *         configuration. 3rd line denotes the final configuration in a format
 *         similar to the initial configuration.
 * 
 *         Output Format: The first line contains M - The minimal number of
 *         moves required to complete the transformation. The following M lines
 *         describe a move, by a peg number to pick from and a peg number to
 *         place on. If there are more than one solutions, it's sufficient to
 *         output any one of them. You can assume, there is always a solution
 *         with less than 7 moves and the initial confirguration will not be
 *         same as the final one.
 * 
 *         Sample Input #00:
 * 
 *         2 3 
 *         1 1 
 *         2 2
 * 
 *         Sample Output #00:
 * 
 *         3 
 *         1 3 
 *         1 2 
 *         3 2
 * 
 *         Sample Input #01:
 * 
 *         6 4 
 *         4 2 4 3 1 1 
 *         1 1 1 1 1 1
 * 
 *         Sample Output #01:
 * 
 *         5 
 *         3 1 
 *         4 3 
 *         4 1 
 *         2 1 
 *         3 1
 * 
 *         NOTE: You need to write the full code taking all inputs are from
 *         stdin and outputs to stdout If you are using "Java", the classname is
 *         "Solution"
 *
 */
 
 /* Represents a move in a path candidate solution by its start and end pegs 
    and its position or depth in said path which is of type ArrayList<Node> */
class Node {
    int from;
    int to;
    int depth;

    public Node(int from, int to, int depth) {
        this.from = from;
        this.to = to;
        this.depth = depth;
    }
    public int getFrom() {
        return from;
    }
    public int getTo() {
        return to;
    }
    public int getDepth() {
        return depth;
    }
}

/* The main class*/
public class Solution {
    private int discs; /* the number of discs, each one being represented by 
                          its radius of type integer between 1 and the value of discs */
    private int pegs; /* the number of pegs, each one being represented by 
                         an integer between 1 and the value of pegs */
    private List<Node> path; /* will contain the succession of moves solving 
                                the problem if there's one for the specified
                                maximum number of moves*/
    private int[] in; /* represents the initial configuration */
    private int[] out; /* represents the final configuration */
    private int[] tops; /* contains for each peg the radius (hence the number) of its topmost disc */
    private boolean limitReached; /* set at true whenever the maximum number of moves 
                                     have been reached for the path that is being built at that moment */
    private int previousMoveStart; /* the number of the initial disc in the last performed move; 
                                      is used to avoid making two opposite moves one right after the other */
    private int previousMoveEnd; /* the number of the final disc in the last performed move;
                                      is used to avoid making two opposite moves one right after the other */
    public final static int MAX_MOVES = 5; /* the desired maximum number of moves for the solution */

    /* Constructor. Initializes what needs to be */
    public Solution(int discs, int pegs) {
        this.discs = discs;
        this.pegs = pegs;
        init();
    }

    /* Initialization */
    private void init() {
        path = new ArrayList<Node>(MAX_MOVES);
        limitReached = false; /* not really necessary according to the Java language spec but still */
        in = new int[discs];
        out = new int[discs];
        tops = new int[pegs];
        previousMoveStart = -1;
        previousMoveEnd = -1;
    }

    /* Used for filling up an array of integers with integers */
    private void fillTab(int [] tab, int elt, int idx) {
        if ((idx >= 0) && (idx < tab.length))
            tab[idx] = elt;
    }

    /* Used for filling up the in array */
    public void fillIn(int elt, int idx) {
        fillTab(in, elt, idx);
    }

    /* Used for filling up the out array */
    public void fillOut(int elt, int idx) {
        fillTab(out, elt, idx);
    }

    /* Fills up the tops array */
    public void fillTops() {
        int i;

        for (i=0; i<pegs; i++) {
            tops[i] = getTop(in, i);
        }
    }

    /* Returns the number of the topmost disc of a given peg for a given configuration */
    public int getTop(int [] tab, int peg) {
        int i, result = -1;

        for(i=0; i<tab.length; i++) {
            if (tab[i] == peg + 1) {
                result = i;
                break;
            }
        }
        return result;
    }

    /* Performs a move from peg1 to peg2 */
    public void move(int [] tabIn, int [] tabTops, int peg1, int peg2) {
        tabIn[tabTops[peg1]] = peg2 + 1;
        tabTops[peg1] = getTop(tabIn, peg1);;
        tabTops[peg2] = getTop(tabIn, peg2);
        previousMoveStart = peg1;
        previousMoveEnd = peg2;
    }

    /* The procedure doing the main job. 
       For a given temporary path being built and the current configuration of
       the discs and pegs, attempts to perform a move from peg1 to peg2 if the
       limit of moves hasn't been reached yet. If the move is possible, it is
       performed and a recursive call is made only if neither the subsequent
       matches the final configuration nor the limit of moves is reached */
    private void step(List<Node> l, int [] tabIn, int [] tabTops, int peg1, 
                        int peg2) {
        List<Node> tmp = new ArrayList<Node>(l), tmp_;
        int [] tmpIn = Arrays.copyOf(tabIn, tabIn.length), tmpIn_;
        int [] tmpTops = Arrays.copyOf(tabTops, tabTops.length), tmpTops_;
        int top1, top2;

        if (tmp.size() >= MAX_MOVES)
            limitReached = true;
        else if (limitReached)
            limitReached = false; /* This is useful for making sure we backtrack for just one step when a recurvive call returns */
        top1 = getTop(tmpIn, peg1);
        top2 = getTop(tmpIn, peg2);
        if (!limitReached && (peg1 != peg2) && 
            ((top1 >=0) && ((top1 < top2) || (top2 == -1))) && 
            !((peg1 == previousMoveEnd) && (peg2 == previousMoveStart))) {
            move(tmpIn, tmpTops, peg1, peg2);
            tmp.add(new Node(peg1 + 1, peg2 + 1, tmp.size() + 1));
            if (tmp.size() == MAX_MOVES) {
                limitReached = true;
            }
            if (Arrays.equals(tmpIn,out)) {
                if (path.isEmpty() || (path.size() > tmp.size())) {
                    path = tmp;
                }
                return;
            } else {
                if (limitReached)
                    return;
                int a, b, i, j;
                for (i=0; i<pegs; i++) {
                    tmp_ = new ArrayList<Node>(tmp);
                    tmpIn_ = Arrays.copyOf(tmpIn, tmpIn.length);
                    tmpTops_ = Arrays.copyOf(tmpTops, tmpTops.length);
                    a = getTop(tmpIn, i);
                    for (j=0; j<pegs; j++) {
                        b = getTop(tmpIn, j);
                        if (!((i == peg2) && (j == peg1)) && ((a >= 0) && 
                            ((b == -1) || (a < b)))) {
                            step(tmp_, tmpIn_, tmpTops_, i, j);
                        }
                    }
                }
            }
        }
    }

    // Path builder
    public void buildPath() {
        List<Node> path_ = new ArrayList<Node>(MAX_MOVES);
        int i, j;
        for (i=0; i<pegs; i++) {
            if (!path_.isEmpty())
                path_.clear();
            for (j=0; (j<pegs); j++) {
                step(path_, in, tops, i, j);
            }
        }
    }

    /* If a solution was found, displays its moves */
    public void display() {
        if (!path.isEmpty()) {
            System.out.println(path.get(path.size() - 1).getDepth());
            for (Node n: path) {
                System.out.println(n.getFrom() + " " + n.getTo());
            }
        }
    }

    /* Reads the input data, calculates a solution and eventually displays it */
    public static void main(String[] args) {
        int i, discs, pegs;
        Scanner scanner = new Scanner(System.in);

        discs = scanner.nextInt();
        pegs = scanner.nextInt();
        if (discs * pegs <= 0){
            System.err.println("error: number of discs or pegs must be > 0");
            return;
        }
        Solution solution = new Solution(discs, pegs);
        for (i=0; i<discs; i++) {
            solution.fillIn(scanner.nextInt(), i);
        }
        for (i=0; i<discs; i++) {
            solution.fillOut(scanner.nextInt(), i);
        }
        solution.fillTops();
        double startTime = System.nanoTime();
        solution.buildPath();
        double duration = System.nanoTime() - startTime;
        solution.display();
        System.out.println("computation performed in " +
                           (duration / 1000000000) + " second(s)"); /* Displays the computation time */
    }
}

