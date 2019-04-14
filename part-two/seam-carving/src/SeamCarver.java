import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private double[][] energy;
    private double[][] distTo;
    private int[][] edgeTo;
    private int[][] grid;
    private boolean isTransposed = false;
    private int width;
    private int height;

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

    public int width() {
        return isTransposed ? height : width;
    }

    public int height() {
        return isTransposed ? width : height;
    }

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

    public int[] findHorizontalSeam() {
        return findVerticalSeam(true);
    }

    public int[] findVerticalSeam() {
        return findVerticalSeam(false);
    }

    private int[] findVerticalSeam(boolean fromHorizontal) {
        if (fromHorizontal != isTransposed) transpose();
        initHelperArrays();
        topologicalRelaxation();
        double min = Double.POSITIVE_INFINITY;
        int xMin = 0;
        for (int x = 0; x < width; x++) {
            if (min >= distTo[x][height - 1]) {
                min = distTo[x][height - 1];
                xMin = x;
            }
        }
        distTo = null;
        int[] seam = new int[height];
        int y = height;
        while (y > 0) {
            seam[--y] = xMin;
            xMin = edgeTo[xMin][y];
        }
        edgeTo = null;
        return seam;
    }

    public void removeHorizontalSeam(int[] seam) {
        removeVerticalSeam(seam, true);
    }

    public void removeVerticalSeam(int[] seam) {
        removeVerticalSeam(seam, false);
    }

    private void removeVerticalSeam(int[] seam, boolean fromHorizontal) {
        if (fromHorizontal != isTransposed) transpose();
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

    private void topologicalRelaxation() {
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                if (x > 0) relax(x, y, x - 1, y + 1);
                relax(x, y, x, y + 1);
                if (x < width - 1) relax(x, y, x + 1, y + 1);
            }
        }
    }

    private void relax(int fromX, int fromY, int toX, int toY) {
        if (distTo[toX][toY] > distTo[fromX][fromY] + energy[toY][toX]) {
            distTo[toX][toY] = distTo[fromX][fromY] + energy[toY][toX];
            edgeTo[toX][toY] = fromX;
        }
    }

    private double calculateEnergy(int x, int y) {
        return x > 0 && x < width - 1 && y > 0 && y < height - 1
                ? Math.sqrt(dS(grid[y][x + 1], grid[y][x - 1]) + dS(grid[y - 1][x], grid[y + 1][x]))
                : 1000;
    }

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

    private void validateX(int x) {
        if (0 > x || x >= width)
            throw new IllegalArgumentException();
    }

    private void validateY(int y) {
        if (0 > y || y >= height)
            throw new IllegalArgumentException();
    }

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

    private void validateArg(Object arg) {
        if (arg == null)
            throw new IllegalArgumentException();
    }

}
