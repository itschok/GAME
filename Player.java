import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player extends Entity {
    public int x, y;
    public int speed = 3;
    public int hp = 100;
    public String direction = "down";
    public int exp = 0;
    public int level = 1;
    public int expToNextLevel = 50;
    private long lastShotTime = 0;
    public  int shootCooldown = 200; // 200 ms (0.2 วินาที)
    int attackPower = 10;
    int maxHp = 100;

    KeyHandle keyH;

    // ภาพแต่ละทิศ
    BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

    public ArrayList<Bullet> bullets = new ArrayList<>();




    public Player(KeyHandle keyH) {
        this.keyH = keyH;
        this.x = 400;
        this.y = 300;
        getPlayerImage();
    }


    public void getPlayerImage() {
        try {
            down1 = ImageIO.read(new File("res/player/down_1.png"));
            up1 = ImageIO.read(new File("res/player/up_1.png"));
            up2 = ImageIO.read(new File("res/player/up_2.png"));
            down2 = ImageIO.read(new File("res/player/down_2.png"));
            left1 = ImageIO.read(new File("res/player/left_1.png"));
            left2 = ImageIO.read(new File("res/player/left_2.png"));
            right1 = ImageIO.read(new File("res/player/right_1.png"));
            right2 = ImageIO.read(new File("res/player/right_2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (keyH.upPressed) {
            direction = "up";
            y -= speed;
        } else if (keyH.downPressed) {
            direction = "down";
            y += speed;
        } else if (keyH.leftPressed) {
            direction = "left";
            x -= speed;
        } else if (keyH.rightPressed) {
            direction = "right";
            x += speed;
        }
        spriteCounter++;
            if (spriteCounter > 10) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }
        if (keyH.spacePressed) {
           long now = System.currentTimeMillis();
            if (now - lastShotTime >= shootCooldown) {
                bullets.add(new Bullet(x + 20, y + 20, direction,attackPower));
                lastShotTime = now;
                
            }

    }
    }
    public void gainExp(int amount) {
        exp += amount;
        if (exp >= expToNextLevel) {
            exp -= expToNextLevel;
            level++;
            expToNextLevel += 50; // เพิ่มค่าที่ต้องใช้ Level Up
            maxHp += 10;
            hp = maxHp;
            attackPower = (attackPower+(level*2));
            System.out.println("LEVEL UP! → " + level);
            Game.instance.pauseGame(); // หยุดเกมชั่วคราว
            Game.instance.upgradeUI.showChoices(); // แสดง UI สุ่มสกิล
        }
    }
    public void takeDamage(int dmg){
        hp-=dmg;
        if(hp<0) hp =0;
    }
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        // ✅ เลือกรูปตามทิศทาง
        switch (direction) {
            case "up":
                if(spriteNum == 1){
                    image = up1;
                }
                if(spriteNum == 2){
                    image = up2;
                }
                break;
            case "down":
                if(spriteNum == 1){
                    image = down1;
                }
                if(spriteNum == 2){
                    image = down2;
                }
                break;
            case "left":
                if(spriteNum == 1){
                    image = left1;
                }
                if(spriteNum == 2){
                    image = left2;
                }
                break;
            case "right":
                if(spriteNum == 1){
                    image = right1;
                }
                if(spriteNum == 2){
                    image = right2;
                }
                break;
        }
        g2.drawImage(image, x, y-20, 80, 80, null);
    }
}
