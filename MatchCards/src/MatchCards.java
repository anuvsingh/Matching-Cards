import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = {
        "darkness",
        "double",
        "fairy",
        "fighting",
        "fire",
        "grass",
        "lightning",
        "metal",
        "psychic",
        "water"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth;
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame("Pokemon Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;
    JButton card1Selected;
    JButton card2Selected;

    MatchCards() {
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Mistakes text label customization
        textLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18)); // Changed font-family
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Mistakes: " + errorCount); // Updated text
        textLabel.setForeground(new Color(138, 43, 226)); // Set font color to Blue-Violet

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        // Card game board
        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (card2Selected == null) {
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                errorCount += 1;
                                textLabel.setText("Mistakes: " + errorCount);
                                hideCardTimer.start();
                            } else {
                                card1Selected = null;
                                card2Selected = null;

                                // Check if the player has won
                                if (isGameWon()) {
                                    showWinCelebration();
                                }
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        // Restart game button - Enhanced UI with Hover Effect
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 50));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.setBackground(new Color(0, 128, 128)); // Teal background
        restartButton.setForeground(Color.WHITE);
        restartButton.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 128), 2, true)); // Rounded corners

        // Adding hover effect on button
        restartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                restartButton.setBackground(new Color(32, 178, 170)); // Lighter Teal when hovered
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                restartButton.setBackground(new Color(0, 128, 128)); // Back to original Teal
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }

                gameReady = false;
                restartButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
                shuffleCards();

                // Reassign buttons with new cards
                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardSet.get(i).cardImageIcon);
                }

                errorCount = 0;
                textLabel.setText("Mistakes: " + errorCount);
                hideCardTimer.start();
            }
        });

        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        // Start game
        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    void setupCards() {
        cardSet = new ArrayList<>();
        for (String cardName : cardList) {
            Image cardImg = new ImageIcon(getClass().getResource("./img/" + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));

            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);

        Image cardBackImg = new ImageIcon(getClass().getResource("./img/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }

    void hideCards() {
        if (gameReady && card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        } else {
            for (JButton button : board) {
                button.setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }

    boolean isGameWon() {
        for (JButton tile : board) {
            if (tile.getIcon() == cardBackImageIcon) {
                return false; // If any card is still face down, game isn't won
            }
        }
        return true;
    }

    void showWinCelebration() {
        JDialog winDialog = new JDialog(frame, "Congratulations!", true);
        winDialog.setLayout(new BorderLayout());
        winDialog.setSize(500, 400);
        winDialog.setLocationRelativeTo(frame);

        // Panel for background and layout
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245)); // Light gray

        // Add a hand clapping animation (resized GIF)
        ImageIcon originalGif = new ImageIcon(getClass().getClassLoader().getResource("./img/Pikachu.gif"));
        Image resizedGif = originalGif.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel clappingLabel = new JLabel(new ImageIcon(resizedGif));
        clappingLabel.setHorizontalAlignment(JLabel.CENTER);
        clappingLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Add a stylish congratulatory text
        JLabel congratsLabel = new JLabel(" Congratulations! You Win! ");
        congratsLabel.setFont(new Font("Verdana", Font.BOLD, 28));
        congratsLabel.setForeground(new Color(34, 139, 34)); // Forest Green
        congratsLabel.setHorizontalAlignment(JLabel.CENTER);
        congratsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        // Add a button for closing the dialog
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setBackground(new Color(255, 140, 0)); // Dark Orange
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeButton.addActionListener(e -> winDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(closeButton);

        // Add components to content panel
        contentPanel.add(congratsLabel, BorderLayout.NORTH);
        contentPanel.add(clappingLabel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        winDialog.add(contentPanel);
        winDialog.setVisible(true);
    }


    public static void main(String[] args) {
        new MatchCards();
    }
}
