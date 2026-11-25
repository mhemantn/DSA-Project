
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // Screen settings
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 500;
    static final int UNIT_SIZE = 25;
    static final int HUD_HEIGHT = 50;

    static final int COLUMNS = SCREEN_WIDTH / UNIT_SIZE;
    static final int ROWS = (SCREEN_HEIGHT - HUD_HEIGHT) / UNIT_SIZE;

    // Game objects
    private Snake snake;
    private Point food;
    private Random random;

    // Direction
    private int directionX = 1;
    private int directionY = 0;

    // Score
    private int score = 0;
    private int highScore = 0;

    // Game status
    private boolean running = true;
    private boolean paused = false;

    // Timer
    private Timer timer;
    private int delay = 120;

    // Constructor
    public GamePanel() {

        snake = new Snake();
        random = new Random();

        setPreferredSize(
            new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)
        );

        setBackground(Color.BLACK);
        setFocusable(true);

        placeFood();
        setupControls();

        timer = new Timer(delay, this);
        timer.start();
    }

    // Generate random food
    private void placeFood() {

        do {
            int x = random.nextInt(COLUMNS);
            int y = random.nextInt(ROWS);

            food = new Point(x, y);

        } while (snake.contains(food));
    }

    // Keyboard controls
    private void setupControls() {

        InputMap input = getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        ActionMap actions = getActionMap();

        input.put(KeyStroke.getKeyStroke("UP"), "up");
        input.put(KeyStroke.getKeyStroke("DOWN"), "down");
        input.put(KeyStroke.getKeyStroke("LEFT"), "left");
        input.put(KeyStroke.getKeyStroke("RIGHT"), "right");
        input.put(KeyStroke.getKeyStroke("P"), "pause");
        input.put(KeyStroke.getKeyStroke("R"), "restart");

        actions.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (directionY != 1) {
                    directionX = 0;
                    directionY = -1;
                }
            }
        });

        actions.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (directionY != -1) {
                    directionX = 0;
                    directionY = 1;
                }
            }
        });

        actions.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (directionX != 1) {
                    directionX = -1;
                    directionY = 0;
                }
            }
        });

        actions.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (directionX != -1) {
                    directionX = 1;
                    directionY = 0;
                }
            }
        });

        actions.put("pause", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    paused = !paused;
                    repaint();
                }
            }
        });

        actions.put("restart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
    }

    // Timer event
    @Override
    public void actionPerformed(ActionEvent e) {

        if (running && !paused) {
            moveSnake();
        }

        repaint();
    }

    // Move snake
    private void moveSnake() {

        Point head = snake.getHead();

        Point newHead = new Point(
            head.x + directionX,
            head.y + directionY
        );

        boolean eating = newHead.equals(food);

        // Wall collision
        if (newHead.x < 0 || newHead.x >= COLUMNS ||
            newHead.y < 0 || newHead.y >= ROWS) {

            gameOver();
            return;
        }

        // Self collision
        for (int i = 0; i < snake.getBody().size(); i++) {

            Point part = snake.getBody().get(i);

            if (part.equals(newHead)) {

                boolean isTail =
                    i == snake.getBody().size() - 1;

                if (!isTail || eating) {
                    gameOver();
                    return;
                }
            }
        }

        // Add new head
        snake.addHead(newHead);

        // Remove tail if food is not eaten
        if (!eating) {
            snake.removeTail();
        } else {

            score += 10;

            if (score > highScore) {
                highScore = score;
            }

            // Check if board is full
            if (snake.getBody().size() == COLUMNS * ROWS) {
                running = false;
                timer.stop();
                return;
            }

            placeFood();

            // Increase speed
            if (score % 50 == 0) {
                delay = Math.max(50, delay - 10);
                timer.setDelay(delay);
            }
        }
    }

    // Game over
    private void gameOver() {
        running = false;
        timer.stop();
    }

    // Restart game
    private void restartGame() {

        snake.reset();

        directionX = 1;
        directionY = 0;

        score = 0;
        delay = 120;

        running = true;
        paused = false;

        placeFood();

        timer.setDelay(delay);
        timer.start();

        repaint();
    }

    // Draw graphics
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        // Score panel
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, SCREEN_WIDTH, HUD_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));

        g.drawString("Score: " + score, 20, 32);
        g.drawString("High Score: " + highScore, 430, 32);

        // Grid
        g.setColor(new Color(35, 35, 35));

        for (int x = 0; x < SCREEN_WIDTH; x += UNIT_SIZE) {
            g.drawLine(x, HUD_HEIGHT, x, SCREEN_HEIGHT);
        }

        for (int y = HUD_HEIGHT; y <= SCREEN_HEIGHT; y += UNIT_SIZE) {
            g.drawLine(0, y, SCREEN_WIDTH, y);
        }

        // Food
        g.setColor(Color.RED);

        g.fillOval(
            food.x * UNIT_SIZE + 2,
            food.y * UNIT_SIZE + HUD_HEIGHT + 2,
            UNIT_SIZE - 4,
            UNIT_SIZE - 4
        );

        // Snake
        for (int i = 0; i < snake.getBody().size(); i++) {

            Point part = snake.getBody().get(i);

            if (i == 0) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(new Color(0, 160, 0));
            }

            g.fillRoundRect(
                part.x * UNIT_SIZE + 1,
                part.y * UNIT_SIZE + HUD_HEIGHT + 1,
                UNIT_SIZE - 2,
                UNIT_SIZE - 2,
                8, 8
            );
        }

        // Pause message
        if (paused) {
            showMessage(g2, "PAUSED", "Press P to Resume");
        }

        // Game over or victory
        if (!running) {

            if (snake.getBody().size() == COLUMNS * ROWS) {
                showMessage(g2, "YOU WIN!", "Press R to Restart");
            } else {
                showMessage(g2, "GAME OVER", "Press R to Restart");
            }
        }
    }

    // Display messages
    private void showMessage(
        Graphics2D g, String title, String subtitle
    ) {

        g.setColor(new Color(0, 0, 0, 180));

        g.fillRect(
            0, HUD_HEIGHT,
            SCREEN_WIDTH,
            SCREEN_HEIGHT - HUD_HEIGHT
        );

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));

        FontMetrics metrics = g.getFontMetrics();

        int x = (SCREEN_WIDTH - metrics.stringWidth(title)) / 2;
        int y = SCREEN_HEIGHT / 2;

        g.drawString(title, x, y);

        g.setFont(new Font("Arial", Font.PLAIN, 20));

        metrics = g.getFontMetrics();

        x = (SCREEN_WIDTH - metrics.stringWidth(subtitle)) / 2;

        g.drawString(subtitle, x, y + 40);
    }
}
