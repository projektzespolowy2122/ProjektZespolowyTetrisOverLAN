import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyListener implements ActionListener {

    private FirstWindow firstWindow;

    MyListener(FirstWindow firstWindow){
       this.firstWindow = firstWindow;
   }

   //opens a new frame with the game, the main frame closes
    @Override
    public void actionPerformed(ActionEvent e) {
        new Window();
        firstWindow.dispose();
    }
}
