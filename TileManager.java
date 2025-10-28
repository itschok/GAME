import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;

public class TileManager {

    Game game;
    Tile[] tile;
    int[][] mapTileNum;

    public TileManager(Game game) {
        this.game = game;

        tile = new Tile[10]; // ใส่ได้สูงสุด 10 ชนิด
        mapTileNum = new int[game.maxScreenCol][game.maxScreenRow];

        loadTileImages();
        generateSimpleMap(); // สร้างฉากแบบสุ่มง่าย ๆ
    }

    private void loadTileImages() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(new File("res/tiles/floor_1.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(new File("res/tiles/floor_2.png"));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(new File("res/tiles/floor_3.png"));
            tile[2].collision = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // สร้างฉากแบบสุ่ม (ใช้ tile แบบง่าย)
    private void generateSimpleMap() {
        for (int col = 0; col < game.maxScreenCol; col++) {
            for (int row = 0; row < game.maxScreenRow; row++) {

                if (col == 0 || row == 0 || col == game.maxScreenCol - 1 || row == game.maxScreenRow - 1) {
                    mapTileNum[col][row] = 2; // wall รอบนอก
                } else {
                    mapTileNum[col][row] = (Math.random() < 0.8) ? 0 : 1; // grass หรือ dirt
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < game.maxScreenCol && row < game.maxScreenRow) {
            int tileNum = mapTileNum[col][row];
            g2.drawImage(tile[tileNum].image, x, y, game.tileSize, game.tileSize, null);

            col++;
            x += game.tileSize;

            if (col == game.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += game.tileSize;
            }
        }
    }
}
