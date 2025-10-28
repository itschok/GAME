import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet {
    public int x, y;
    public int speed = 5;
    public boolean active = true;
    public String direction;
    public BufferedImage image;
    public int damage;
    public Bullet(int x, int y, String direction,int damage) {
        this.x = x;
        this.y = y;
        this.direction= direction;
        this.damage = damage;
    }

    public void update() {
        switch (direction) {
            case "up":    y -= speed; break;
            case "down":  y += speed; break;
            case "left":  x -= speed; break;
            case "right": x += speed; break;
        }

        // ถ้าออกนอกจอให้ลบ
        if (x < 0 || y < 0 || x > 800 || y > 600) {
            active = false;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        g2.fillOval(x, y, 10, 10);
    }
}

