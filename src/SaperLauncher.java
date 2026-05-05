import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;

public class SaperLauncher {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); //Nimbus - styl wizualny komponentow swing
        } catch (Exception ignored) {} //w razie braku Nimbus program nie przestanie dzialac

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SaperLauncher.showWelcome();
            }
        });
        //wykonanie kodu wewnatrz Runnable-brak problemow z wielowatkowoscia i rys. interfejsu
    }

    private static void showWelcome() { //metoda tworzy i wysw okno "menu"
        //glowne okno aplikacji
        JFrame welcome = new JFrame("Prosty Saper");
        welcome.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcome.setLayout(new BorderLayout()); //podzial ukladu na north,south,center ...
        welcome.setResizable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(0x3498db)); //jasnoniebieski kolor
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); //układ pionowy
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20)); //marginesy


        //bialy napis tytulowy w menu "Saper"
        JLabel title = new JLabel("Saper");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        //wybor poziomu trudnosci
        String[] levels = {"Łatwy (9x9, 10 bomb)", "Średni (16x16, 40 bomb)", "Trudny (24x24, 99 bomb)"};
        JComboBox<String> levelSelect = new JComboBox<>(levels);
        levelSelect.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        levelSelect.setMaximumSize(new Dimension(200, 30));
        levelSelect.setAlignmentX(Component.CENTER_ALIGNMENT);

        // przycisk startu
        JButton startBtn = new JButton("Rozpocznij grę");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        startBtn.setBackground(new Color(0x2ecc71));
        startBtn.setForeground(Color.WHITE);
        startBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //po kliknieciu:
                welcome.dispose(); //1.zamyka okno powitalne
                int[] config;
                switch (levelSelect.getSelectedIndex()) { //2.wybiera rozm. planszy i l. bomb na podstawie wybranego indeksu
                    case 0:
                        config = new int[]{9, 9, 10};
                        break;
                    case 1:
                        config = new int[]{16, 16, 40};
                        break;
                    case 2:
                        config = new int[]{24, 24, 99};
                        break;
                    default:
                        config = new int[]{9, 9, 10};
                        break;
                }

                new ProstySaperGUI(config[0], config[1], config[2]); //3. tworzy nowy obiekt ProstySaperGUI z odp. konfiguracja
            }
        });




        //dodanie tych komponentow do panelu
        contentPanel.add(Box.createVerticalGlue());//elast. przestrzen ktora ustawia el. na srodek
        contentPanel.add(title);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));//Box.createRigidArea - odstepy pionowe miedzy komponentami
        contentPanel.add(levelSelect);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(startBtn);
        contentPanel.add(Box.createVerticalGlue());

        //
        welcome.add(contentPanel, BorderLayout.CENTER); //umieszcza panel w srodku okna
        welcome.setSize(480,480);
        welcome.setLocationRelativeTo(null); //centruje okno na ekranie komputera
        welcome.setVisible(true);
    }
}