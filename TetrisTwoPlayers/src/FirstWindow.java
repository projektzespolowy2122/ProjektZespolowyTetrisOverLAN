import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FirstWindow extends Frame  {

    FirstWindow(){

        setTitle("Tetris for two players");
        setSize(400, 300);
        setLocation(100, 100);
        setResizable(false);
        add(new MenuPanel(this));
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
