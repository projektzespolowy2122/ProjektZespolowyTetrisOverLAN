import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MenuPanel extends Panel implements KeyListener {

    // constants for panel
    private final Color background = Color.BLACK;
    private final Color foreground = Color.LIGHT_GRAY;

    MenuPanel(FirstWindow firstWindow){

        //sets the panel layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(background);

        //creates an array of labels
        Label[] labels = new Label[6];
        Font font = new Font(Font.DIALOG, Font.BOLD, 15);

        for(int i=0; i<6;i++) {
            labels[i] = new Label();
            labels[i].setForeground(foreground);
            labels[i].setFont(font);
        }

        labels[0].setText("Online game Tetris for two players");
        labels[0].setAlignment(Label.CENTER);
        labels[0].setAlignment(Label.CENTER);
        labels[1].setText("Authors:");
        labels[2].setText("- Kamil Wróblewski");
        labels[3].setText("- Mateusz Wieremiej");
        labels[4].setText("- Natalia Koćwin");
        labels[5].setText("- Szymon Kasiorek");

        //a button to start the game
        Button playButton = new Button("Play");
        ActionListener listener = new MyListener(firstWindow);
        playButton.addActionListener(listener);
        playButton.setBackground(foreground);
        playButton.setFont(font);

        //adds elements to the panel
        for(int i=0; i<6;i++)
            add(labels[i]);
        add(playButton);
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
