package src;

import edu.princeton.cs.algs4.Picture;

/**
 * The {@code SeamCarver} class represents a solution to
 * <a href=http://coursera.cs.princeton.edu/algs4/assignments/seam.html>seam carving</a> problem.
 * Develop a seam carving, content-aware image scaling.
 * Program allows both vertical and horizontal seam carving. In order to support these operations,
 * it fully implements vertical SC; for horizontal SC, program transposes picture and treats it
 * as a vertical, quite elegantly reducing duplicate code.
 * However, API assumes that picture always remains in given orientation, and public methods
 * should restore its original state (if necessary).
 * Score: 100/100
 *
 * @author andrhua
 * @version Thank you, Dark souls.
 */
public class SeamCarver {
    private double[][] energy;
    private double[][] distTo;
    private int[][] edgeTo;
    private int[][] grid;
    private boolean isTransposed = false;
    private int width;
    private int height;

    /**
     * Converts {@code Picture} instance to 2D int array,
     * calculates energy grid to be used for searching minimal seam.
     * @param picture image to be scaled
     * @throws IllegalArgumentException if {@code picture} is null
     */
    public SeamCarver(Picture picture) {
        validateArg(picture);
        this.width = picture.width();
        this.height = picture.height();
        grid = new int[height][width];
        energy = new double [height][width];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[y][x] = picture.getRGB(x, y);
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                energy[y][x] = calculateEnergy(x, y);
            }
        }
    }

    /**
     * Returns new {@code Picture} from 2D pixel array.
     * @return new {@code Picture} from 2D pixel array
     */
    public Picture picture() {
        if (isTransposed)
            transpose();
        Picture picture = new Picture(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++)
                picture.setRGB(x, y, grid[y][x]);
        }
        return picture;
    }

    /**
     * Returns current {@code Picture} width.
     * @return current {@code Picture} width
     */
    public int width() {
        return isTransposed ? height : width;
    }

    /**
     * Returns current {@code Picture} height.
     * @return current {@code Picture} height
     */
    public int height() {
        return isTransposed ? width : height;
    }

    /**
     * Returns energy value at ({@param x}, {@param y}) pixel.
     * @param x width value
     * @param y height value
     * @return energy value at ({@param x}, {@param y}) pixel
     */
    public double energy(int x, int y) {
        if (isTransposed) {
            int tmp = x;
            x = y;
            y = tmp;
        }
        validateX(x);
        validateY(y);
        return energy[y][x];
    }

    /**
     * Returns a minimal horizontal seam.
     * @return a minimal horizontal seam
     */
    public int[] findHorizontalSeam() {
        return findSeam(true);
    }

    /**
     * Returns a minimal vertical seam.
     * @return a minimal vertical seam
     */
    public int[] findVerticalSeam() {
        return findSeam(false);
    }

    /**
     * Returns a minimal vertical seam relatively to current orientation
     * by calculating shortest path in picture's energy graph.
     * @param isHorizontal whether seam meant to be horizontal
     * @return a minimal vertical seam relatively to current orientation
     */
    private int[] findSeam(boolean isHorizontal) {
        if (isHorizontal != isTransposed)
            transpose();
        initHelperArrays();
        topologicalRelaxation();
        // after relaxation, we need to find minimal distance in last row of pixels
        double min = Double.POSITIVE_INFINITY;
        int xMin = 0;
        for (int x = 0; x < width; x++) {
            if (min >= distTo[x][height - 1]) {
                min = distTo[x][height - 1];
                xMin = x;
            }
        }
        // and build path from that pixel to the top of image.
        int[] seam = new int[height];
        int y = height;
        while (y > 0) {
            seam[--y] = xMin;
            xMin = edgeTo[xMin][y];
        }
        // prevent loitering
        distTo = null;
        edgeTo = null;
        return seam;
    }

    /**
     * Removes given horizontal {@param seam} from picture.
     * @param seam array of y-indices corresponding to
     *             every column, where any two adjacent
     *             elements differs at max by 1
     */
    public void removeHorizontalSeam(int[] seam) {
        removeSeam(seam, true);
    }

    /**
     * Removes given vertical {@param seam} from picture.
     * @param seam array of x-indices corresponding to
     *             every row, where any two adjacent
     *             elements differs at max by 1
     */
    public void removeVerticalSeam(int[] seam) {
        removeSeam(seam, false);
    }

    /**
     * Removes a vertical seam relatively to current orientation.
     * In every row shifts pixels on right side of the seam to fill in gaps
     * and recalculates energy values along this seam.
     * @param seam array of y-indices (relatively to current orientation)
     * @param isHorizontal whether seam meant to be horizontal
     * @throws IllegalArgumentException if seam is not correct
     * or if picture has the only column left
     */
    private void removeSeam(int[] seam, boolean isHorizontal) {
        restoreOrientation(isHorizontal);
        if (width <= 1) throw new IllegalArgumentException();
        validateSeam(seam);
        for (int y = 0; y < height; y++) {
            int x = seam[y];
            int len = width - x - 1;
            System.arraycopy(grid[y], x + 1, grid[y], x, len);
            System.arraycopy(energy[y], x + 1, energy[y], x, len);
        }
        width--;
        for (int y = 1; y < height - 1; y++) {
            int x = seam[y];
            energy[y][x] = calculateEnergy(x, y);
            if (x > 0) energy[y][x - 1] = calculateEnergy(x - 1, y);
        }
    }

    /**
     * Runs a topological sort on picture's energy grid
     */
    private void topologicalRelaxation() {
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                if (x > 0) relax(x, y, x - 1, y + 1);
                relax(x, y, x, y + 1);
                if (x < width - 1) relax(x, y, x + 1, y + 1);
            }
        }
    }

    /**
     * Relaxes a vertex ({@code toX}, {@code toY})
     */
    private void relax(int fromX, int fromY, int toX, int toY) {
        if (distTo[toX][toY] > distTo[fromX][fromY] + energy[toY][toX]) {
            distTo[toX][toY] = distTo[fromX][fromY] + energy[toY][toX];
            edgeTo[toX][toY] = fromX;
        }
    }

    /**
     * If {@param isHorizontal} XOR {@code isTransposed} equals 1, it means
     * that pixel matrix should be transposed before any processing.
     */
    private void restoreOrientation(boolean isHorizontal) {
        if (isHorizontal != isTransposed) transpose();
    }

    /**
     * Calculates energy at given pixel. Boundary row and columns has energy of 1000,
     * which is knowingly more than any internal pixel's energy, thereby excluding
     * themselves from any minimal seam.
     */
    private double calculateEnergy(int x, int y) {
        return x > 0 && x < width - 1 && y > 0 && y < height - 1
                ? Math.sqrt(dS(grid[y][x + 1], grid[y][x - 1]) + dS(grid[y - 1][x], grid[y + 1][x]))
                : 1000;
    }

    /**
     * Optimized calculation of sum of squared color channels deltas.
     * {@param a} and {@param b} are integers with encoded channel values:
     * first octet of bits is red, second 8 is green, third 8 is blue (or vice versa, does not matter).
     * In loop program extracts unsigned 8 bits of every color and calculates delta, squares it and sums.
     */
    private int dS(int a, int b) {
        int s = 0;
        for (int i = 16; i >= 0; i -= 8) {
            int ci = ((a >> i) & 0xFF) - ((b >> i) & 0xFF);
            s += ci * ci;
        }
        return s;
    }


    private void initHelperArrays() {
        distTo = new double[width][height];
        edgeTo = new int[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                distTo[x][y] = y == 0
                        ? 1000
                        : Double.POSITIVE_INFINITY;
    }

    /**
     * Transposes pixel and energy matrices, exchanges metrics values.
     * {@code removeSeam} does not actually resize array,
     * it happens here with creation of new one.
     */
    private void transpose() {
        isTransposed = !isTransposed;
        int[][] g = new int[width][height];
        double[][] e = new double[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g[x][y] = grid[y][x];
                e[x][y] = energy[y][x];
            }
        }
        grid = g;
        energy = e;
        int tmp = width;
        width = height;
        height = tmp;
    }

    /**
     * @throws IllegalArgumentException if {@param x} is not within width range
     */
    private void validateX(int x) {
        if (0 > x || x >= width)
            throw new IllegalArgumentException();
    }

    /**
     * @throws IllegalArgumentException if {@param   y} is not within height range
     */
    private void validateY(int y) {
        if (0 > y || y >= height)
            throw new IllegalArgumentException();
    }

    /**
     * Validates correctness of vertical {@param seam}:
     * length of array equals height of current picture,
     * every value within picture's width,
     * any two adjacent values differs at max by 1.
     * @throws IllegalArgumentException if any of those constraints is violated.
     */
    private void validateSeam(int[] seam) {
        validateArg(seam);
        if (seam.length != height)
            throw new IllegalArgumentException();
        int prev = seam[0];
        for (int x: seam) {
            validateX(x);
            if (Math.abs(x - prev) > 1)
                throw new IllegalArgumentException();
            prev = x;
        }
    }

    /**
     * @throws IllegalArgumentException if argument is null.
     */
    private void validateArg(Object arg) {
        if (arg == null)
            throw new IllegalArgumentException();
    }

}
