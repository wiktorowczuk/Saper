import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class ProstySaperGUI extends JFrame {
    private final ImageIcon flagIcon;
    private final ImageIcon bombIcon;
    private final int rows;
    private final int cols;
    private final int bombs;
    private final Cell[][] board;
    private final JButton[][] buttons;//siatka przyciskow
    private int cellsToReveal;
    private boolean isGameOver = false;
    private JLabel bombsCounter;
    private JLabel timeLabel;
    private Timer gameTimer;
    private int elapsedTime;
    private int flagsPlaced;
    private JLabel scoreLabel;
    private int cellsRevealed;

    public ProstySaperGUI(int rows, int cols, int bombs) {
        flagIcon = loadIcon("resources/flag.png");
        bombIcon = loadIcon("resources/bomb.png");

        this.rows = rows;
        this.cols = cols;
        this.bombs = bombs;
        this.board = new Cell[rows][cols];
        this.buttons = new JButton[rows][cols];
        this.cellsToReveal = rows * cols - bombs;
        this.flagsPlaced = 0;
        this.cellsRevealed = 0;

        initBoard();
        initUI();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);  // okno ZAWSZE widoczne
    }


    private ImageIcon loadIcon(String path) {
        java.net.URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.err.println("Brak zasobu: " + path);
            return new ImageIcon();
        }
    }

    private void initBoard() {
        //utworzenie pustych komorek
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = new Cell();
            }
        }
        //bomby utworzone losowo
        Random rand = new Random();
        int placed = 0;
        while (placed < bombs) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            if (!board[r][c].isBomb) {
                board[r][c].isBomb = true;
                placed++;
            }
        }
        //policzenie sasiednich bomb dla kazdego pola
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c].adjacentBombs = countAdjacent(r, c);
            }
        }
    }

    private int countAdjacent(int r, int c) {
        int count = 0;
        //przejrzenie wszystkich 8 sasiednich pozycji
        for (int dr = -1; dr <= 1; dr++) { //dr i dc to przesuniecie, nr przesuniecie wzgledem wierszy, nc przesuniecie wzgledem kolummn
            for (int dc = -1; dc <= 1; dc++) { //przechodzimy 2 razy przez -1,0,1 czyli łącznie 9 iteracji (wszystkie strony i srodek pola)
                int nr = r + dr, nc = c + dc; //obliczenie numeru sasiada
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc].isBomb) {
                    count++;
                }
            }
        }
        return count;
    }

    private void initUI() { //zbudowanie graficznego interfejsu
        setTitle("Prosty Saper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        //licznik bomb, zegar, wynik
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        infoPanel.setBackground(new Color(0x34495e));

        bombsCounter = new JLabel("Pozostało bomb: " + (bombs - flagsPlaced), SwingConstants.CENTER);
        bombsCounter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bombsCounter.setForeground(Color.WHITE);

        timeLabel = new JLabel("Czas: 00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timeLabel.setForeground(Color.WHITE);

        scoreLabel = new JLabel("Wynik: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scoreLabel.setForeground(Color.WHITE);

        infoPanel.add(bombsCounter);
        infoPanel.add(timeLabel);
        infoPanel.add(scoreLabel);

        //siatka przyciskow planszy
        JPanel grid = new JPanel(new GridLayout(rows, cols, 1, 1));
        grid.setBackground(new Color(0x2c3e50));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font btnFont = new Font("Segoe UI", Font.BOLD, 14);
        Border btnBorder = BorderFactory.createLineBorder(Color.BLACK);

        //tworz. przyciskow dla kazdej komorki
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(40, 40));
                btn.setFont(btnFont);
                btn.setBackground(fieldColors[currentColorIndex]);
                btn.setForeground(Color.WHITE);
                btn.setBorder(btnBorder);
                btn.setFocusPainted(false);

                attachMouseHandler(btn, r, c);

                buttons[r][c] = btn;
                grid.add(btn);
            }
        }

        //przyciski na dole:nowa gra, menu, zmiana koloru, zmiana motywu
        JButton reset = new JButton("Nowa gra");
        reset.setFont(new Font("Segoe UI", Font.BOLD, 12));
        reset.setBackground(new Color(0x95a5a6));
        reset.setForeground(Color.BLACK);
        reset.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restart();
            }
        });
        //reset.addActionListener(e -> restart());

        JButton menuBtn = new JButton("Menu");
        menuBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuBtn.setBackground(new Color(0x95a5a6));
        menuBtn.setForeground(Color.BLACK);
        menuBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        menuBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SaperLauncher.main(null);
            }
        });
        /*
        menuBtn.addActionListener(e -> {
            dispose();
            SaperLauncher.main(null);
        }); */

        JButton colorBtn = new JButton("Kolor pola");
        colorBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        colorBtn.setBackground(new Color(0x95a5a6));
        colorBtn.setForeground(Color.BLACK);
        colorBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        colorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeFieldColor();
            }
        });
        //colorBtn.addActionListener(e -> changeFieldColor());

        JButton themeBtn = new JButton("Zmień motyw");
        themeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        themeBtn.setBackground(new Color(0x95a5a6));
        themeBtn.setForeground(Color.BLACK);
        themeBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        themeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeTheme();
            }
        });
        //themeBtn.addActionListener(e -> changeTheme());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(0x34495e));
        bottomPanel.add(reset);
        bottomPanel.add(menuBtn);
        bottomPanel.add(colorBtn);
        bottomPanel.add(themeBtn);

        //dodanie paneli do okna
        add(infoPanel, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();//dopasowanie rozmiaru
        startTimer();//start zegara
    }

    private void attachMouseHandler(JButton btn, int r, int c) {
        //wlasny handler zdarzen myszy - po ty by wiadomo bylo co dzialo sie przy lewym kliku, prawym kliku
        btn.addMouseListener(new FieldMouseHandler(this, btn, r, c));//ref do glownego obiektu gry, sam JButton, (r,c) - wspolrzedne pola planszy
    }

    private void changeFieldColor() {
        //zmkiana koloru nieodkrytych pol
        currentColorIndex = (currentColorIndex + 1) % fieldColors.length;
        Color newColor = fieldColors[currentColorIndex];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = buttons[r][c];
                if (btn.isEnabled() && !board[r][c].isRevealed) {
                    btn.setBackground(newColor);
                    attachMouseHandler(btn, r, c);
                }
            }
        }
    }

    private void changeTheme() {
        //zmiana motywu
        currentThemeIndex = (currentThemeIndex + 1) % themes.length;
        Theme theme = themes[currentThemeIndex];

        getContentPane().setBackground(theme.background);
        timeLabel.setForeground(theme.buttonText);
        bombsCounter.setForeground(theme.buttonText);
        scoreLabel.setForeground(theme.buttonText);

        //aktualizacja kolorow przyciskow
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = buttons[r][c];
                if (!board[r][c].isRevealed) {
                    btn.setBackground(fieldColors[currentColorIndex]);
                    btn.setForeground(theme.buttonText);
                    attachMouseHandler(btn, r, c);
                }
            }
        }

        repaint();
    }

    private void startTimer() {//obsluga zegara
        elapsedTime = 0;
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timeLabel.setText(String.format("Czas: %02d:%02d", elapsedTime / 60, elapsedTime % 60)); //minuty:sekundy
            }
        });
        gameTimer.start();
    }

    public void toggleFlag(int r, int c) {//olbsluga stawiania flagi
        if (board[r][c].isRevealed) return;//czy pole odkryte
        board[r][c].isFlagged = !board[r][c].isFlagged;//przelaczenie stanu flagi
        buttons[r][c].setIcon(board[r][c].isFlagged ? flagIcon : null);//aktualizacja ikony przycisku
        flagsPlaced += board[r][c].isFlagged ? 1 : -1;//aktualizacja licznika flag
        bombsCounter.setText("Pozostało bomb: " + (bombs - flagsPlaced));//odswiezenie stanu pozostalych bomb
    }

    public void revealCell(int r, int c) {//obsluga odsloniecia pola po kliknieciu
        if (board[r][c].isRevealed || board[r][c].isFlagged) return; //jesli pole odkryte lub oflagowane - koniec metody
        board[r][c].isRevealed = true;                  //oznaczenie
        cellsToReveal--;                                //pola
        cellsRevealed++;                                //jako
        scoreLabel.setText("Wynik: " + cellsRevealed);  //odkryte

        JButton btn = buttons[r][c];


        if (board[r][c].isBomb) { //obsluga trafienia w bombe
            btn.setIcon(bombIcon);
            gameOver(false);
            return;
        }

        int adj = board[r][c].adjacentBombs; //wyswietlenie liczby sasiadujacych bomb
        btn.setEnabled(false);
        btn.setBackground(new Color(0xecf0f1));
        if (adj > 0) {
            btn.setText(String.valueOf(adj));
            btn.setForeground(getNumberColor(adj));
        }
        if (adj == 0) {//rekurencyjne odslanianie pustych pol

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr, nc = c + dc;
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                        revealCell(nr, nc);
                    }
                }
            }
        }
    }



    //obsluga koloru cyfr wzgledem numerow oznaczajacych ile bomb sasiadujacych
    private Color getNumberColor(int num) {
        Color color;
        switch (num) {
            case 1:
                color = new Color(0x3498db);
                break;
            case 2:
                color = new Color(0x2ecc71);
                break;
            case 3:
                color = new Color(0xe74c3c);
                break;
            case 4:
                color = new Color(0x9b59b6);
                break;
            case 5:
                color = new Color(0xf1c40f);
                break;
            default:
                color = new Color(0x2c3e50);
                break;
        }
        return color;
    }


    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isCellRevealed(int r, int c) {
        return board[r][c].isRevealed;
    }

    public Color getCurrentFieldColor() {
        return fieldColors[currentColorIndex];
    }

    public void checkWin() {
        if (cellsToReveal == 0 && !isGameOver) {
            gameOver(true);
        }
    }

    private void gameOver(boolean win) { //obsluga konca gry
        isGameOver = true;
        gameTimer.stop();
        //odsloniecie bomb i blokada przyciskow
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c].isBomb) {
                    buttons[r][c].setIcon(bombIcon);
                }
                buttons[r][c].setEnabled(false);
            }
        }

        //wynik
        JDialog resultDialog = new JDialog(this, "Koniec gry", true);
        resultDialog.setLayout(new BorderLayout());
        resultDialog.setSize(300, 200);
        resultDialog.setLocationRelativeTo(this);

        JLabel resultLabel = new JLabel(win ? "Wygrana!" : "Przegrana!", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        resultLabel.setForeground(win ? new Color(0x27ae60) : new Color(0xe74c3c));

        JButton againBtn = new JButton("Jeszcze raz");
        againBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        againBtn.setBackground(new Color(0x3498db));
        againBtn.setForeground(Color.WHITE);
        againBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultDialog.dispose();
                restart();
            }
        });

        /*againBtn.addActionListener(e -> {
            resultDialog.dispose();
            restart();
        });*/

        resultDialog.add(resultLabel, BorderLayout.CENTER);
        resultDialog.add(againBtn, BorderLayout.SOUTH);
        resultDialog.setVisible(true);
    }

    private void restart() {
        dispose();
        new ProstySaperGUI(rows, cols, bombs);  //uruchomienie nowej gry
    }

    //kolory pol do cyklicznej zmiany
    private final Color[] fieldColors = {
            new Color(0x70A1D7),
            new Color(0x58B19F),
            new Color(0xf6b93b),
            new Color(0xd35400)
    };
    private int currentColorIndex = 0;

    //motywy kolorystyczne - definicje
    private final Theme[] themes = {
            new Theme(new Color(0x34495e), new Color(0x2c3e50), new Color(0x3498db), new Color(0x2980b9), Color.WHITE, new Color(0x2c3e50)),
            new Theme(new Color(0x1abc9c), new Color(0x16a085), new Color(0x27ae60), new Color(0x229954), Color.BLACK, new Color(0x16a085)),
            new Theme(new Color(0xf39c12), new Color(0xe67e22), new Color(0xd35400), new Color(0xba4a00), Color.BLACK, new Color(0xe67e22))
    };
    private int currentThemeIndex = 0;
}