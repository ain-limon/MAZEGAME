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
        generateMaze();
        start = new Point(1, 1);
        findRandomExit();
        maze[start.x][start.y] = -1;
        maze[exit.x][exit.y] = -2;
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
        if (maze[1][1] == 1) maze[1][1] = 0;
        if (exit.x > 0 && exit.x < rows - 1 && exit.y > 0 && exit.y < cols - 1 && maze[exit.x][exit.y] == 1) {
            maze[exit.x][exit.y] = 0;
        } else if ((exit.x == 0 || exit.x == rows - 1 || exit.y == 0 || exit.y == cols - 1) && maze[exit.x][exit.y] == 1) {
            maze[exit.x][exit.y] = 0;
        }
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
                    default: g.setColor(Color.WHITE);
                }
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        g.setColor(new Color(139, 69, 19));
        if (solutionPath != null) {
            for (Point p : solutionPath) {
                g.fillRect(p.y * CELL_SIZE, p.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
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
    }

    public static void main(String[] args) {
        int rows = 0, cols = 0;
        int defaultSize = 50;
        int minSize = 5;
        int maxSize = 100;

        try {
            String rowsStr = JOptionPane.showInputDialog("Введите количество строк (минимум " + minSize + ", максимум " + maxSize + "):");
            if (rowsStr == null) {
                rows = defaultSize;
            } else {
                rows = Integer.parseInt(rowsStr);
                if (rows < minSize || rows > maxSize) {
                    JOptionPane.showMessageDialog(null, "Некорректный ввод строк. Установлено значение по умолчанию: " + defaultSize, "Ошибка", JOptionPane.ERROR_MESSAGE);
                    rows = defaultSize;
                }
            }

            String colsStr = JOptionPane.showInputDialog("Введите количество столбцов (минимум " + minSize + ", максимум " + maxSize + "):");
            if (colsStr == null) {
                cols = defaultSize;
            } else {
                cols = Integer.parseInt(colsStr);
                if (cols < minSize || cols > maxSize) {
                    JOptionPane.showMessageDialog(null, "Некорректный ввод столбцов. Установлено значение по умолчанию: " + defaultSize, "Ошибка", JOptionPane.ERROR_MESSAGE);
                    cols = defaultSize;
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Некорректный формат ввода. Используются размеры по умолчанию: " + defaultSize + "x" + defaultSize, "Ошибка", JOptionPane.ERROR_MESSAGE);
            rows = defaultSize;
            cols = defaultSize;
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Maze Solver");
            MazeGame game = new MazeGame(rows, cols);
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}