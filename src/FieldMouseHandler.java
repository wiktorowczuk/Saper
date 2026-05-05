import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FieldMouseHandler extends MouseAdapter {
    private final JButton button;
    private final int row, col;
    private final ProstySaperGUI game;//ref do glownego GUI gry

    public FieldMouseHandler(ProstySaperGUI game, JButton button, int row, int col) {
        this.game = game;//referencja zeby wywolac metody gry
        this.button = button;
        this.row = row;
        this.col = col;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (game.isGameOver()) return; //jesli gra zak. to ignorujemy klikniecia

        if (SwingUtilities.isRightMouseButton(e)) {
            //prawy przycisk - stawianie, usuwanie flagi
            game.toggleFlag(row, col);
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            //lewy przycisk - dosloniecie pola
            game.revealCell(row, col);
            game.checkWin();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {//podswietlenie pola tylko gdy pole = aktywne i nieodsloniete
        if (!button.isEnabled() || game.isCellRevealed(row, col)) return;
        button.setBackground(game.getCurrentFieldColor().darker());
    }

    @Override
    public void mouseExited(MouseEvent e) { //przywrocenie koloru tla po zejsciu kursora
        if (!button.isEnabled() || game.isCellRevealed(row, col)) return;
        button.setBackground(game.getCurrentFieldColor());
    }
}