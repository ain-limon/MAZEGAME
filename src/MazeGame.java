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
            this.rows = 50;
            this.cols = 50;
        } else {
            this.rows = Math.min(rows, 100);
            this.cols = Math.min(cols, 100);
        }
        setPreferredSize(new Dimension(this.cols * CELL_SIZE, this.rows * CELL_SIZE));
        setFocusable(true);
        start = new Point(1, 1);
        exit = new Point(this.rows - 2, this.cols - 2);
        generateMaze();
        findPath();
    }

    private void generateMaze() {
        maze = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                maze[r][c] = 1; // WALL
            }
        }
        recursiveBacktracking(1, 1);
        maze[start.x][start.y] = 0; // PATH
        maze[exit.x][exit.y] = 3;  // EXIT
        printMazeToConsole(); // Оставил для общей информации о лабиринте
    }

    private void recursiveBacktracking(int r, int c) {
        int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        shuffleArray(directions);
        maze[r][c] = 0; // PATH

        for (int[] d : directions) {
            int nr = r + d[0], nc = c + d[1];
            if (nr > 0 && nr < rows - 1 && nc > 0 && nc < cols - 1 && maze[nr][nc] == 1) {
                maze[r + d[0] / 2][c + d[1] / 2] = 0; // PATH
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
                    case 3: g.setColor(Color.YELLOW); break;
                    case 4: g.setColor(new Color(139, 69, 19)); break;
                    default: g.setColor(Color.WHITE);
                }
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        g.setColor(Color.RED);
        int startX = start.y * CELL_SIZE + CELL_SIZE / 2;
        int startY = start.x * CELL_SIZE + CELL_SIZE / 2;
        int[] xPoints = {startX - CELL_SIZE / 2, startX, startX + CELL_SIZE / 2, startX};
        int[] yPoints = {startY, startY - CELL_SIZE / 2, startY, startY + CELL_SIZE / 2};
        g.fillPolygon(xPoints, yPoints, 4);

        g.setColor(Color.YELLOW);
        g.fillRect(exit.y * CELL_SIZE, exit.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        g.setColor(new Color(139, 69, 19));
        if (solutionPath != null) {
            for (Point p : solutionPath) {
                g.fillRect(p.y * CELL_SIZE, p.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
            g.setColor(Color.RED);
            g.fillPolygon(xPoints, yPoints, 4);
            g.setColor(Color.YELLOW);
            g.fillRect(exit.y * CELL_SIZE, exit.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    private void printMazeToConsole() {
        System.out.println("Сгенерированный лабиринт (консоль):");
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                System.out.print(maze[r][c] == 1 ? "#" : (maze[r][c] == 3 ? "E" : (maze[r][c] == 4 ? "*" : " ")));
            }
            System.out.println();
        }
        System.out.println("Старт: " + start.x + "," + start.y);
        System.out.println("Выход: " + exit.x + "," + exit.y);
        // System.out.println("Путь найден: " + (solutionPath != null && !solutionPath.isEmpty())); // Убрано
    }

    public static void main(String[] args) {
        int rows = 0, cols = 0;
        int defaultSize = 50;
        int minSize = 5;
        int maxSize = 100;

        try {
            String rowsStr = JOptionPane.showInputDialog("Введите количество строк (минимум " + minSize + ", максимум " + maxSize + "):");
            if (rowsStr != null) {
                int enteredRows = Integer.parseInt(rowsStr);
                rows = (enteredRows >= minSize && enteredRows <= maxSize) ? enteredRows : defaultSize;
                if (enteredRows < minSize || enteredRows > maxSize) {
                    JOptionPane.showMessageDialog(null, "Некорректный ввод строк. Использовано значение по умолчанию (" + defaultSize + ").");
                }
            } else {
                rows = defaultSize;
            }

            String colsStr = JOptionPane.showInputDialog("Введите количество столбцов (минимум " + minSize + ", максимум " + maxSize + "):");
            if (colsStr != null) {

                int enteredCols = Integer.parseInt(colsStr);
                cols = (enteredCols >= minSize && enteredCols <= maxSize) ? enteredCols : defaultSize;
                if (enteredCols < minSize || enteredCols > maxSize) {
                    JOptionPane.showMessageDialog(null, "Некорректный ввод столбцов. Использовано значение по умолчанию (" + defaultSize + ").");

                }
            } else {
                cols = defaultSize;
            }
        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(null, "Некорректный ввод размеров. Использованы значения по умолчанию (" + defaultSize + "x" + defaultSize + ").");
            rows = defaultSize;
            cols = defaultSize;

        }

        JFrame frame = new JFrame("Maze Solver");
        MazeGame game = new MazeGame(rows, cols);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}