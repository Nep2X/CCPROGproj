import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryGameApp {
    JFrame firstFrame, secondFrame, thirdFrame, gameFrame, resultFrame;
    JTextField[][] inputFields;
    ArrayList<String> gameData;
    int rows = 4, columns = 4;
    Timer gameTimer;
    int secondsElapsed = 0;
    JLabel timerLabel;
    JButton[][] gameButtons;
    ArrayList<JButton> revealedButtons = new ArrayList<>();
    ArrayList<String> revealedTexts = new ArrayList<>();

    public MemoryGameApp() {
        createFirstFrame();
    }

    private void createFirstFrame() {
        firstFrame = new JFrame("Memory Game");
        firstFrame.setSize(300, 200);
        firstFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        firstFrame.setLayout(new BorderLayout());

        JLabel rememberLabel = new JLabel("REMEMBER", SwingConstants.CENTER);
        rememberLabel.setFont(new Font("Arial", Font.BOLD, 24));
        firstFrame.add(rememberLabel, BorderLayout.CENTER);

        JButton createButton = new JButton("CREATE");
        createButton.addActionListener(e -> {
            firstFrame.setVisible(false);
            createSecondFrame();
        });
        firstFrame.add(createButton, BorderLayout.SOUTH);

        firstFrame.setLocationRelativeTo(null);
        firstFrame.setVisible(true);
    }

    private void createSecondFrame() {
        secondFrame = new JFrame("Input Partners");
        secondFrame.setSize(500, 500);
        secondFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        secondFrame.setLayout(new BorderLayout());

        JLabel inputLabel = new JLabel("INPUT PARTNERS", SwingConstants.CENTER);
        inputLabel.setFont(new Font("Arial", Font.BOLD, 18));
        secondFrame.add(inputLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(rows * 2, columns, 10, 10));
        inputFields = new JTextField[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j += 2) {
                JPanel pairPanel = new JPanel(new BorderLayout());
                
                // First box in pair
                inputFields[i][j] = new JTextField(10);
                inputFields[i][j].setFont(new Font("Arial", Font.PLAIN, 16));
                pairPanel.add(inputFields[i][j], BorderLayout.WEST);

                // Arrow Label
                JLabel arrowLabel = new JLabel("→", SwingConstants.CENTER);
                arrowLabel.setFont(new Font("Arial", Font.BOLD, 18));
                pairPanel.add(arrowLabel, BorderLayout.CENTER);

                // Second box in pair
                inputFields[i][j + 1] = new JTextField(10);
                inputFields[i][j + 1].setFont(new Font("Arial", Font.PLAIN, 16));
                pairPanel.add(inputFields[i][j + 1], BorderLayout.EAST);

                gridPanel.add(pairPanel);
            }
        }

        secondFrame.add(gridPanel, BorderLayout.CENTER);

        JButton submitButton = new JButton("SUBMIT");
        submitButton.addActionListener(e -> {
            gameData = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    String text = inputFields[i][j].getText().trim();
                    gameData.add(text.isEmpty() ? " " : text); // Add blank if input is empty
                }
            }
            secondFrame.setVisible(false);
            createThirdFrame();
        });
        secondFrame.add(submitButton, BorderLayout.SOUTH);

        secondFrame.setLocationRelativeTo(null);
        secondFrame.setVisible(true);
    }

    private void createThirdFrame() {
        thirdFrame = new JFrame("Shuffling");
        thirdFrame.setSize(300, 200);
        thirdFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        thirdFrame.setLayout(new BorderLayout());

        JLabel shufflingLabel = new JLabel("SHUFFLING", SwingConstants.CENTER);
        shufflingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        thirdFrame.add(shufflingLabel, BorderLayout.CENTER);

        thirdFrame.setLocationRelativeTo(null);
        thirdFrame.setVisible(true);

        Timer timer = new Timer(1000, e -> {
            thirdFrame.setVisible(false);
            createGameFrame();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void createGameFrame() {
        gameFrame = new JFrame("Memory Game");
        gameFrame.setSize(500, 550);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLayout(new BorderLayout());

        JLabel gameLabel = new JLabel("MEMORY GAME", SwingConstants.CENTER);
        gameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gameFrame.add(gameLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(rows, columns));
        gameButtons = new JButton[rows][columns];
        ArrayList<String> shuffledData = new ArrayList<>(gameData);
        Collections.shuffle(shuffledData);

        timerLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        gameFrame.add(timerLabel, BorderLayout.SOUTH);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                JButton button = new JButton("?");
                String text = shuffledData.get(i * columns + j);

                button.setFont(new Font("Arial", Font.PLAIN, 14));
                
                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!revealedButtons.contains(button)) {
                            button.setText(text);
                            revealedButtons.add(button);
                            revealedTexts.add(text);

                            if (revealedButtons.size() == 2) {
                                checkMatch();
                            }
                        }
                    }
                });
                gameButtons[i][j] = button;
                gridPanel.add(button);
            }
        }

        gameFrame.add(gridPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> {
            stopTimer();
            gameFrame.setVisible(false);
            createFirstFrame();
        });
        gameFrame.add(backButton, BorderLayout.SOUTH);

        startTimer();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }

    private void checkMatch() {
        if (revealedTexts.get(0).equals(revealedTexts.get(1))) {
            // Disable matched buttons
            for (JButton button : revealedButtons) {
                button.setEnabled(false);
            }
            revealedButtons.clear();
            revealedTexts.clear();

            if (allMatched()) {
                stopTimer();
                JOptionPane.showMessageDialog(gameFrame, "GOOD JOB");
                Timer delay = new Timer(3000, e -> {
                    gameFrame.setVisible(false);
                    createResultFrame();
                });
                delay.setRepeats(false);
                delay.start();
            }
        } else {
            // Hide unmatched buttons after a short delay
            Timer hideTimer = new Timer(500, evt -> {
                for (JButton btn : revealedButtons) {
                    btn.setText("?");
                }
                revealedButtons.clear();
                revealedTexts.clear();
            });
            hideTimer.setRepeats(false);
            hideTimer.start();
        }
    }

    private boolean allMatched() {
        for (JButton[] buttons : gameButtons) {
            for (JButton button : buttons) {
                if (button.isEnabled()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void startTimer() {
        secondsElapsed = 0;
        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed + "s");
        });
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void createResultFrame() {
        resultFrame = new JFrame("Congratulations");
        resultFrame.setSize(400, 200);
        resultFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        resultFrame.setLayout(new GridLayout(3, 1));

        JLabel congratsLabel = new JLabel("CONGRATULATIONS YOU", SwingConstants.CENTER);
        JLabel timeLabel = new JLabel("FINISHED IN " + secondsElapsed + " SECONDS", SwingConstants.CENTER);
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton createNewButton = new JButton("CREATE NEW");
        createNewButton.addActionListener(e -> {
            resultFrame.setVisible(false);
            createSecondFrame();
        });

        JButton restartButton = new JButton("RESTART");
        restartButton.addActionListener(e -> {
            resultFrame.setVisible(false);
            createGameFrame();
        });

        buttonPanel.add(createNewButton);
        buttonPanel.add(restartButton);

        resultFrame.add(congratsLabel);
        resultFrame.add(timeLabel);
        resultFrame.add(buttonPanel);

        resultFrame.setLocationRelativeTo(null);
        resultFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGameApp::new);
    }
}
