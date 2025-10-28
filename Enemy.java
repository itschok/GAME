import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Enemy extends Entity {
    public int x, y;
    public int speed = 1;
    public int hp = 20;
    public boolean alive = true;
    public int expReward = 20;
    BufferedImage downe1;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        loadImages();
    }
    

    public void update(Player player) {
        if (!alive) return;

        // เคลื่อนไปหาผู้เล่น
        if (player.x > x) x += speed;
        if (player.x < x) x -= speed;
        if (player.y > y) y += speed;
        if (player.y < y) y -= speed;
    }

    // ✅ ฟังก์ชันตรวจโดนกระสุน
    public void takeDamage(int damage) {
    hp -= damage;
    if (hp <= 0) {
        hp = 0;
        alive = false;
    }
    }
    private void loadImages() {
        try {
            downe1 = ImageIO.read(new File("res/enemy/downe1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void draw(Graphics2D g2) {
        if (!alive) return;
        BufferedImage image = downe1;
        g2.drawImage(image, x, y, 30, 30, null);

        // แถบเลือด
        g2.setColor(Color.GREEN);
        g2.fillRect(x - 5, y - 10, hp * 2, 5);
    }

    // ✅ ใช้ตรวจการชนแบบกล่อง (rectangle)
    public Rectangle getBounds() {
        return new Rectangle(x, y, 30, 30);
    }
}
