import javax.swing.*;
import java.awt.*;

public class MazeGame extends JPanel {
    private static final int CELL_SIZE = 15;
    private int rows;
    private int cols;
    private int[][] maze;
    private Point start;
    private Point exit;

    public MazeGame(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        setPreferredSize(new Dimension(this.cols * CELL_SIZE, this.rows * CELL_SIZE));
        setFocusable(true);
        start = new Point(1, 1);
        exit = new Point(this.rows - 2, this.cols - 2);
        maze = new int[rows][cols];
    }

    public static void main(String[] args) {
        int rows = 50;
        int cols = 50;
        JFrame frame = new JFrame("Maze Solver");
        MazeGame game = new MazeGame(rows, cols);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}