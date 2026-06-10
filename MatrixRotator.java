public class MatrixRotator {
    public static void main(String[] args) {
        System.out.println("=== SMART MATRIX ROTATOR TESTS ===");
        // Test Matrisi (3x3)
        int[][] input = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 9 }
        };
        System.out.println("--- Original Matrix ---");
        printMatrix(input);

        System.out.println("--- Rotating 90 Degrees Right ---");
        int[][] result90 = rotate(input, 90);
        printMatrix(result90);

        System.out.println("--- Rotating 180 Degrees Right ---");
        int[][] result180 = rotate(input, 180);
        printMatrix(result180);

        System.out.println("--- Rotating 270 Degrees Right ---");
        int[][] res270 = rotate(input, 270);
        printMatrix(res270);

        System.out.println("--- Rotating 360 Degrees Right ---");
        int[][] res360 = rotate(input, 360);
        printMatrix(res360);

        System.out.println("--- Rotating 450 Degrees Right ---");
        int[][] res450 = rotate(input, 450);
        printMatrix(res450);
    }
    public static int[][] rotate(int[][] matrix, int degrees) {
        int rotations = (degrees / 90) % 4;
        if (rotations == 0) {
            return matrix;
        }
        int n = matrix.length;
        int[][] currentMatrix = matrix;
        for (int r = 0; r < rotations; r++) {
            int[][] newMatrix = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    newMatrix[i][j] = currentMatrix[n - 1 - j][i];
                }
            }
            currentMatrix = newMatrix;
        }
        return currentMatrix;
    }
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.print("[ ");
            for (int element : row) {
                System.out.print(element + " ");
            }
            System.out.println("]");
        }
    }
}