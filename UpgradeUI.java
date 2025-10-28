import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JPanel;

public class UpgradeUI extends JPanel {
    private ArrayList<Skill> allSkills;
    private ArrayList<Skill> currentChoices;
    private Game game;

    public UpgradeUI(Game game, ArrayList<Skill> allSkills) {
        this.game = game;
        this.allSkills = allSkills;
        this.currentChoices = new ArrayList<>();

        setBackground(new Color(0, 0, 0, 180));
        setLayout(null);
        setVisible(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < currentChoices.size(); i++) {
                    int bx = 200;
                    int by = 200 + i * 120;
                    int bw = 400;
                    int bh = 80;

                    if (e.getX() > bx && e.getX() < bx + bw && e.getY() > by && e.getY() < by + bh) {
                        chooseSkill(i);
                        break;
                    }
                }
            }
        });
    }

    public void showChoices() {
        currentChoices.clear();
        Collections.shuffle(allSkills);
        for (int i = 0; i < 3 && i < allSkills.size(); i++) {
            currentChoices.add(allSkills.get(i));
        }
        setVisible(true);
        repaint();
    }

    public void chooseSkill(int index) {
        currentChoices.get(index).apply();
        setVisible(false);
        game.resumeGame();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isVisible()) return;
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2.drawString("Choose Your Upgrade", 260, 150);

        for (int i = 0; i < currentChoices.size(); i++) {
            Skill s = currentChoices.get(i);
            int bx = 200;
            int by = 200 + i * 120;
            int bw = 400;
            int bh = 80;

            g2.setColor(new Color(60, 60, 60, 200));
            g2.fillRoundRect(bx, by, bw, bh, 20, 20);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(bx, by, bw, bh, 20, 20);

            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString(s.name, bx + 20, by + 35);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.drawString(s.description, bx + 20, by + 60);
        }
    }
}
