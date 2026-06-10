import java.util.Scanner;
public class SilverSpiral {
    public static void main(String[] args) {
        int n = getMatrixSize();

        int[][] matrix = fillCounterClockwiseSpiral(n);

        System.out.println("\nCounter-Clockwise Lucas Spiral (" + n + "x" + n + "):");
        printMatrix(matrix);

        int diagSum = calculateDiagonalSum(matrix);
        System.out.println("\nDiagonal Sum: " + diagSum);
    }

    public static int getMatrixSize() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the size of the matrix (1-6): ");
        int n = scanner.nextInt();

        if (n < 1 || n > 6) {
            System.out.println("ERROR: N must be between 1 and 6.");
            System.exit(0);
        }

        return n;
    }

    public static int getLucas(int n) {
        if (n == 0) return 2;
        if (n == 1) return 1;

        int prev2 = 2;
        int prev1 = 1;
        int current = 0;

        for (int i = 2; i <= n; i++) {
            current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }
        return current;
    }

    /*
     * Counter-clockwise order: DOWN (left col) → RIGHT (bottom row)
     *                          → UP (right col) → LEFT (top row)
     */
    public static int[][] fillCounterClockwiseSpiral(int n) {
        int[][] matrix = new int[n][n];
        int top = 0;
        int bottom = n - 1;
        int left = 0;
        int right = n - 1;

        int lucasIndex = 0;
        while (top <= bottom && left <= right) {

            // 1. Move down along the left column (top → bottom)
            for (int row = top; row <= bottom; row++) {
                matrix[row][left] = getLucas(lucasIndex++);
            }
            left++;

            // 2. Move right along the bottom row (left → right)
            for (int col = left; col <= right; col++) {
                matrix[bottom][col] = getLucas(lucasIndex++);
            }
            bottom--;

            // 3. Move up along the right column (bottom → top)
            if (left <= right) {
                for (int row = bottom; row >= top; row--) {
                    matrix[row][right] = getLucas(lucasIndex++);
                }
                right--;
            }

            // 4. Move left along the top row (right → left)
            if (top <= bottom) {
                for (int col = right; col >= left; col--) {
                    matrix[top][col] = getLucas(lucasIndex++);
                }
                top++;
            }
        }
        return matrix;
    }

    public static int calculateDiagonalSum(int[][] matrix) {
        int n = matrix.length;
        int sum = 0;

        for (int i = 0; i < n; i++) {
            sum += matrix[i][i];           // main diagonal  (top-left → bottom-right)
            sum += matrix[i][n - 1 - i];   // anti-diagonal  (top-right → bottom-left)
        }

        // If N is odd, the center element was counted twice
        if (n % 2 != 0) {
            int mid = n / 2;
            sum -= matrix[mid][mid];
        }

        return sum;
    }

    public static void printMatrix(int[][] matrix) {
        int n = matrix.length;
        int maxVal = 0;
        for (int[] row : matrix)
            for (int val : row)
                if (val > maxVal) maxVal = val;
        int width = String.valueOf(maxVal).length() + 1;

        for (int[] row : matrix) {
            for (int val : row) {
                System.out.printf("%" + width + "d", val);
            }
            System.out.println();
        }
    }
}





