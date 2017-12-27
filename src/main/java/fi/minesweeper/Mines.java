package fi.minesweeper;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class Mines extends JFrame {

  private final int frame_width = 250;
  // private final int frame_width = 500;
  private final int frame_height = 290;
  // private final int frame height = 540;

  private final JLabel statusbar;

  public Mines() {

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(frame_width, frame_height);
    setLocationRelativeTo(null);
    setTitle("Minesweeper");

    statusbar = new JLabel("");
    add(statusbar, BorderLayout.SOUTH);

    add(new Board(statusbar));

    setResizable(false);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        JFrame ex = new Mines();
        ex.setVisible(true);
      }
    });

  }

}
