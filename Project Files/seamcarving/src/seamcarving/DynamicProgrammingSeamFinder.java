package seamcarving;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dynamic programming implementation of the {@link SeamFinder} interface.
 *
 * @see SeamFinder
 * @see SeamCarver
 */
public class DynamicProgrammingSeamFinder implements SeamFinder {
    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        int width = energies.length;
        int height = energies[0].length;

        double[][] dp = new double[width][height];
        int[][] path = new int[width][height];

        for (int y = 0; y < height; y++) {
            dp[0][y] = energies[0][y];
        }

        for (int x = 1; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int yLower = Math.max(0, y - 1);
                int yUpper = Math.min(height - 1, y + 1);

                dp[x][y] = Double.POSITIVE_INFINITY;
                for (int yp = yLower; yp <= yUpper; yp++) {
                    if (dp[x - 1][yp] + energies[x][y] < dp[x][y]) {
                        dp[x][y] = dp[x - 1][yp] + energies[x][y];
                        path[x][y] = yp;
                    }
                }
            }
        }

        double minEnergy = Double.POSITIVE_INFINITY;
        int minY = 0;
        for (int y = 0; y < height; y++) {
            if (dp[width - 1][y] < minEnergy) {
                minEnergy = dp[width - 1][y];
                minY = y;
            }
        }

        List<Integer> seam = new ArrayList<>();
        for (int x = width - 1; x >= 0; x--) {
            seam.add(minY);
            minY = path[x][minY];
        }
        Collections.reverse(seam);

        return seam;
    }

    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        int width = energies.length;
        int height = energies[0].length;

        // Transpose the energy matrix for easier handling
        double[][] energiesTransposed = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                energiesTransposed[y][x] = energies[x][y];
            }
        }

        // Use the horizontal seam finding method on the transposed matrix
        List<Integer> seam = findHorizontalSeam(energiesTransposed);

        return seam;
    }
}
