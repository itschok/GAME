import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Game extends JPanel implements Runnable {
    // ==========================
    // 🔧 SET SCREEN
    // ==========================
    final int originalTileSize = 16;
    final int scale = 3;
    public static Game instance;
    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    int score = 0;
    long startTime = System.currentTimeMillis();
    // 🔹 Spawn Enemy Timer
    int spawnTimer = 0;
    int spawnInterval = 40; // ทุก 1 วินาที (60 เฟรม)

    TileManager tileM = new TileManager(this);

    // FPS
    int FPS = 60;


    //UPGRADE
    public UpgradeUI upgradeUI;
    ArrayList<Skill> allSkills = new ArrayList<>();
    private boolean paused = false;

    public void pauseGame() {
        paused = true;
    }

    public void resumeGame() {
        paused = false;
    }

    KeyHandle keyH = new KeyHandle();
    Thread gameThread;
    Player player = new Player(keyH);
    ArrayList<Enemy> enemies = new ArrayList<>();

    boolean gameOver = false;

    // ==========================
    // 🔧 CONSTRUCTOR
    // ==========================
    public Game() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        instance = this; 

        // 👾 สร้างศัตรูเริ่มต้น
        enemies.add(new Enemy(200, 100));
        enemies.add(new Enemy(500, 300));
        enemies.add(new Enemy(600, 200));

        //UI UPGRADE
        allSkills.add(new Skill("Speed Up", "MovementSpeed +1", () -> player.speed += 1));
        allSkills.add(new Skill("Max HP Up", "MAX HP+20", () -> player.hp += 20));
        allSkills.add(new Skill("Cooldown Up", "Cooldown 20%", () -> player.shootCooldown *= 0.8));
        allSkills.add(new Skill("Attack Up", "Attack 5%", () -> player.attackPower+=10));
        // ✅ สร้าง Upgrade UI
        upgradeUI = new UpgradeUI(this, allSkills);
        upgradeUI.setBounds(0, 0, screenWidth, screenHeight);
        this.setLayout(null);
        this.add(upgradeUI);
    }

    // ==========================
    // 🔁 GAME LOOP
    // ==========================
    public void startGameThread() {
        this.requestFocusInWindow();
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // ==========================
    // 🎮 UPDATE GAME STATE
    // ==========================
public void update() {
    if (paused) return; // หยุดทุกอย่างถ้าอยู่ใน UI

    player.update();
    if (gameOver)  this.requestFocusInWindow();
    player.update();

    // 🔹 Spawn ศัตรู
    spawnTimer++;
    if (spawnTimer >= spawnInterval) {
        spawnEnemy();
        spawnTimer = 0;
    }

    // 🔹 อัปเดตกระสุน
    for (int i = 0; i < player.bullets.size(); i++) {
        Bullet b = player.bullets.get(i);
        b.update();

        if (!b.active) {
            player.bullets.remove(i);
            i--;
            continue;
        }

        // ตรวจชนกับศัตรู
        for (Enemy e : enemies) {
            if (e.alive && b.active) {
                Rectangle bulletRect = new Rectangle(b.x, b.y, 10, 10);
                if (bulletRect.intersects(e.getBounds())) {
                    e.takeDamage(b.damage);
                    if (!e.alive) player.gainExp(e.expReward);score += 10;
                    b.active = false;
                }
            }
        }
    }

    // 🔹 อัปเดตศัตรูและตรวจชน Player
    for (Enemy e : enemies) {
        e.update(player);
        if (e.alive && player.hp > 0) {
            Rectangle enemyRect = e.getBounds();
            Rectangle playerRect = new Rectangle(player.x, player.y, 48, 48);
            if (enemyRect.intersects(playerRect)) {
                player.takeDamage(1);
                if (player.hp <= 0) gameOver = true;
            }
        }
    }

    // 🔹 ลบศัตรูที่ตายแล้ว
    enemies.removeIf(e -> !e.alive);

    // 🔹 รีสตาร์ทเกม
    if (gameOver && keyH.rPressed) {
        restartGame();
        this.requestFocusInWindow();
}
}

    // ==========================
    // 👾 สุ่ม Spawn ศัตรู
    // ==========================
    private void spawnEnemy() {
        int x, y;
        do {
            x = (int) (Math.random() * screenWidth);
            y = (int) (Math.random() * screenHeight);
        } while (Math.abs(x - player.x) < 100 && Math.abs(y - player.y) < 100);

        Enemy e = new Enemy(x, y);

        // ปรับความยากตาม Level
        e.hp += player.level * 5;
        e.speed += player.level / 2;

        enemies.add(e);
    }

    // ==========================
    // 🧱 วาด HUD (HP, EXP, LV)
    // ==========================
    private void drawHUD(Graphics2D g2) {
        int barX = 20;
        int barY = 20;
        int barWidth = 200;
        int barHeight = 20;

        // ---- HP Bar ----
        g2.setColor(Color.GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);

        int hpWidth = (int) ((double) player.hp / player.maxHp * barWidth);
        g2.setColor(Color.RED);
        g2.fillRect(barX, barY, hpWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);
        g2.drawString("HP: " + player.hp + " / " + player.maxHp, barX + 60, barY + 15);

        // ---- EXP Bar ----
        int expY = barY + 40;
        g2.setColor(Color.GRAY);
        g2.fillRect(barX, expY, barWidth, barHeight);

        int expWidth = (int) ((double) player.exp / player.expToNextLevel * barWidth);
        g2.setColor(Color.CYAN);
        g2.fillRect(barX, expY, expWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.drawRect(barX, expY, barWidth, barHeight);
        g2.drawString("EXP: " + player.exp + " / " + player.expToNextLevel, barX + 45, expY + 15);

        // ---- Level ----
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.YELLOW);
        g2.drawString("LV " + player.level, barX + 230, barY + 15);
    }
    public void restartGame() {
        gameOver = false;
        spawnTimer = 0;
        score = 0;
        startTime = System.currentTimeMillis();
        player = new Player(keyH);
        enemies.clear();

        enemies.add(new Enemy(200, 100));
        enemies.add(new Enemy(500, 300));

        player.exp = 0;
        player.level = 1;
        player.hp = player.maxHp;
    }


    // ==========================
    // 🖼️ DRAW EVERYTHING
    // ==========================
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tileM.draw(g2);
        // Player
        player.draw(g2);

        // Bullets
        for (Bullet b : player.bullets) {
            b.draw(g2);
        }

        // Enemies
        for (Enemy e : enemies) {
            e.draw(g2);
        }

        // HUD
        drawHUD(g2);

        // Game Over
        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.drawString("GAME OVER", screenWidth / 2 - 150, screenHeight / 2);

            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(20f));
            g2.drawString("Press R to Restart", screenWidth / 2 - 100, screenHeight / 2 + 30);
        }
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 20, 120);

        // ---- TIMER ----
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        long minutes = elapsedTime / 60;
        long seconds = elapsedTime % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);
        g2.drawString(timeText, 20, 140);
        g2.dispose();
    }
}
