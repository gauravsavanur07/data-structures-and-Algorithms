// Java program to find height
// of complete binary tree
// from total nodes.
import java.lang.*;

class GFG {

    // Function to calculate height
    static int height(int N)
    {
        return (int)Math.ceil(Math.log(N + 1) / Math.log(2)) - 1;
    }

    // Driver Code
    public static void main(String[] args)
    {
        int N = 6;
        System.out.println(height(N));
    }
}
