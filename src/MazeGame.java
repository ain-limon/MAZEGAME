import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeGame extends JPanel {
    private static final int CELL_SIZE = 15;
    private int rows;
    private int cols;
    private int[][] maze;
    private Point start;
    private Point exit;
    private List<Point> solutionPath;

    public MazeGame(int rows, int cols) {
        if (rows < 5 || cols < 5) {
            System.out.println("Предупреждение: Заданные размеры лабиринта слишком малы. Установлены размеры по умолчанию: 50x50.");
            this.rows = 50;
            this.cols = 50;
        } else {
            this.rows = Math.min(rows, 100);
            this.cols = Math.min(cols, 100);
        }
        setPreferredSize(new Dimension(this.cols * CELL_SIZE, this.rows * CELL_SIZE));
        setFocusable(true);
        start = new Point(1, 1);
        findRandomExit();
        generateMaze();
        if (isValid(start.x, start.y)) maze[start.x][start.y] = 2;
        if (isValid(exit.x, exit.y)) maze[exit.x][exit.y] = 3;
        findPath();
    }

    private void findRandomExit() {
        Random random = new Random();
        int side = random.nextInt(4);
        int r, c;
        switch (side) {
            case 0: r = 0; c = random.nextInt(cols - 2) + 1; break;
            case 1: r = random.nextInt(rows - 2) + 1; c = cols - 1; break;
            case 2: r = rows - 1; c = random.nextInt(cols - 2) + 1; break;
            case 3: r = random.nextInt(rows - 2) + 1; c = 0; break;
            default: r = rows - 1; c = cols - 1;
        }
        exit = new Point(r, c);
    }

    private void generateMaze() {
        maze = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                maze[r][c] = 1;
            }
        }
        recursiveBacktracking(1, 1);
        if (isValid(start.x, start.y) && maze[start.x][start.y] == 1) maze[start.x][start.y] = 0;
        if (isValid(exit.x, exit.y) && maze[exit.x][exit.y] == 1) maze[exit.x][exit.y] = 0;
    }

    private void recursiveBacktracking(int r, int c) {
        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        shuffleArray(directions);
        maze[r][c] = 0;

        for (int[] d : directions) {
            int nr = r + d[0], nc = c + d[1];
            if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1 && maze[nr][nc] == 1) {
                maze[r + d[0] / 2][c + d[1] / 2] = 0;
                recursiveBacktracking(nr, nc);
            }
        }
    }

    private boolean findPath() {
        solutionPath = new ArrayList<>();
        boolean[][] visited = new boolean[rows][cols];
        return solveMazeRecursive(start.x, start.y, visited);
    }

    private boolean solveMazeRecursive(int r, int c, boolean[][] visited) {
        if (!isValid(r, c) || maze[r][c] == 1 || visited[r][c]) {
            return false;
        }
        visited[r][c] = true;
        solutionPath.add(new Point(r, c));

        if (r == exit.x && c == exit.y) {
            return true;
        }

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            if (solveMazeRecursive(r + dr[i], c + dc[i], visited)) {
                return true;
            }
        }

        solutionPath.remove(solutionPath.size() - 1);
        return false;
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private void shuffleArray(int[][] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                switch (maze[r][c]) {
                    case 1: g.setColor(Color.GREEN); break;
                    case 0: g.setColor(Color.WHITE); break;
                    case 2: g.setColor(Color.BLUE); break;
                    case 3: g.setColor(Color.YELLOW); break;
                    default: g.setColor(Color.WHITE);
                }
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int rows = 30;
            int cols = 40;
            JFrame frame = new JFrame("Maze Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new MazeGame(rows, cols));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}