import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Window extends Frame {

    Window (){
        setTitle("Tetris for two players");
        setSize(400*2, 600);
        setLocation(100, 100);
        setResizable(false);
        add(new TetrisPanel());
        setVisible(true);

        //closes the application after pressing cross in the upper right corner
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
    }
}
