import javax.swing.JFrame;

public class Display extends JFrame {
    public static void main(String[] arg){
        JFrame window = new JFrame();
        window.setTitle("My game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        Game game = new Game();
        window.add(game);
        window.pack();


        window.setLocationRelativeTo(null);
        window.setVisible(true);

        game.requestFocusInWindow();
        
        game.startGameThread(); 
    }
}