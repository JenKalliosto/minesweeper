package fi.minesweeper;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel {

  private final int num_images = 12;
  private final int cell_size = 15;

  private final int cover_for_cell = 10;
  private final int mark_for_cell = 10;
  private final int empty_cell = 0;
  private final int mine_cell = 9;
  private final int covered_mine_cell = mine_cell + cover_for_cell;
  private final int marked_mine_cell = covered_mine_cell + mark_for_cell;

  private final int draw_mine = 9;
  private final int draw_cover = 10;
  private final int draw_mark = 11;
  private final int draw_wrong_mark = 12;

  private final int n_mines = 40;
  // private final int n_mines = 80;
  private final int n_rows = 16;
  // private final int n_rows = 32;
  private final int n_cols = 16;
  // private final int n_cols = 32;

  private int[] field;
  private boolean inGame;
  private int mines_left;
  private Image[] img;

  private int all_cells;
  private JLabel statusbar;

  public Board(JLabel statusbar) {
    this.statusbar = statusbar;
    img = new Image[num_images];

    for (int i = 0; i < num_images; i++) {
      img[i] = (new ImageIcon(loadImage(i))).getImage();
    }

    setDoubleBuffered(true);

    addMouseListener(new MinesAdapter());
    newGame();
  }

  private void newGame() {

    Random random;
    int current_col;

    int i = 0;
    int position = 0;
    int cell = 0;

    random = new Random();
    inGame = true;
    mines_left = n_mines;

    all_cells = n_rows * n_cols;
    field = new int[all_cells];

    for (i = 0; i < all_cells; i++) {
      field[i] = cover_for_cell;
    }

    statusbar.setText(Integer.toString(mines_left));

    i = 0;
    while (i < n_mines) {

      position = (int) (all_cells * random.nextDouble());

      if ((position < all_cells)
          && (field[position] != covered_mine_cell)) {

        current_col = position % n_cols;
        field[position] = covered_mine_cell;
        i++;

        if (current_col > 0) {
          cell = position - 1 - n_cols;
          if (cell >= 0) {
            if (field[cell] != covered_mine_cell) {
              field[cell] += 1;
            }
          }
          cell = position - 1;
          if (cell >= 0) {
            if (field[cell] != covered_mine_cell) {
              field[cell] += 1;
            }
          }

          // the bug was here - fixed now May 28, 2017
          cell = position + n_cols - 1;
          if (cell < all_cells) {
            if (field[cell] != covered_mine_cell) {
              field[cell] += 1;
            }
          }
        }

        cell = position - n_cols;
        if (cell >= 0) {
          if (field[cell] != covered_mine_cell) {
            field[cell] += 1;
          }
        }
        cell = position + n_cols;
        if (cell < all_cells) {
          if (field[cell] != covered_mine_cell) {
            field[cell] += 1;
          }
        }

        if (current_col < (n_cols - 1)) {
          cell = position - n_cols + 1;
          if (cell >= 0) {
            if (field[cell] != covered_mine_cell) {
              field[cell] += 1;
            }
          }
          cell = position + n_cols + 1;
          if (cell < all_cells) {
            if (field[cell] != covered_mine_cell) {
              field[cell] += 1;
            }
          }
          cell = position + 1;
          if (cell < all_cells) {
            if (field[cell] != covered_mine_cell) {
              field[cell] += 1;
            }
          }
        }
      }
    }
  }

  public void find_empty_cells(int j) {

    int current_col = j % n_cols;
    int cell;

    if (current_col > 0) {
      cell = j - n_cols - 1;
      if (cell >= 0) {
        if (field[cell] > mine_cell) {
          field[cell] -= cover_for_cell;
          if (field[cell] == empty_cell) {
            find_empty_cells(cell);
          }
        }
      }

      cell = j - 1;
      if (cell >= 0) {
        if (field[cell] > mine_cell) {
          field[cell] -= cover_for_cell;
          if (field[cell] == empty_cell) {
            find_empty_cells(cell);
          }
        }
      }

      cell = j + n_cols - 1;
      if (cell < all_cells) {
        if (field[cell] > mine_cell) {
          field[cell] -= cover_for_cell;
          if (field[cell] == empty_cell) {
            find_empty_cells(cell);
          }
        }
      }
    }

    cell = j - n_cols;
    if (cell >= 0) {
      if (field[cell] > mine_cell) {
        field[cell] -= cover_for_cell;
        if (field[cell] == empty_cell) {
          find_empty_cells(cell);
        }
      }
    }

    cell = j + n_cols;
    if (cell < all_cells) {
      if (field[cell] > mine_cell) {
        field[cell] -= cover_for_cell;
        if (field[cell] == empty_cell) {
          find_empty_cells(cell);
        }
      }
    }
    if (current_col < (n_cols - 1)) {
      cell = j - n_cols + 1;
      if (cell >= 0) {
        if (field[cell] > mine_cell) {
          field[cell] -= cover_for_cell;
          if (field[cell] == empty_cell) {
            find_empty_cells(cell);
          }
        }
      }

      cell = j + n_cols + 1;
      if (cell < all_cells) {
        if (field[cell] > mine_cell) {
          field[cell] -= cover_for_cell;
          if (field[cell] == empty_cell) {
            find_empty_cells(cell);
          }
        }
      }
      cell = j + 1;
      if (cell < all_cells) {
        if (field[cell] > mine_cell) {
          field[cell] -= cover_for_cell;
          if (field[cell] == empty_cell) {
            find_empty_cells(cell);
          }
        }
      }
    }
  }

  @Override
  public void paintComponent(Graphics g) {

    int cell = 0;
    int uncover = 0;

    for (int i = 0; i < n_rows; i++) {
      for (int j = 0; j < n_cols; j++) {

        cell = field[(i * n_cols) + j];

        if (inGame && cell == mine_cell) {
          inGame = false;
        }

        if (!inGame) {
          if (cell == covered_mine_cell) {
            cell = draw_mine;
          } else if (cell == marked_mine_cell) {
            cell = draw_mark;
          } else if (cell > covered_mine_cell) {
            cell = draw_wrong_mark;
          } else if (cell > mine_cell) {
            cell = draw_cover;
          } 

        } else {
          if (cell > covered_mine_cell) {
            cell = draw_mark;
          } else if (cell > mine_cell) {
            cell = draw_cover;
            uncover++;
          }
        }

        g.drawImage(img[cell], (j * cell_size), (i * cell_size), this);
      }
    }

    if (uncover == 0 && inGame) {
      inGame = false;
      statusbar.setText("Game won");
    } else if (!inGame) {
      statusbar.setText("Game lost");
    }
  }

  private byte[] loadImage(int imageNumber) {
    String fileName = String.format("/%d.png", imageNumber);
    System.out.println(fileName);
    try(InputStream in = Board.class.getResourceAsStream(fileName);
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096)) {
      byte[] buf = new byte[2048];
      int bytesRead = in.read(buf);
      while (bytesRead != -1) {
        out.write(buf, 0, bytesRead);
        bytesRead = in.read(buf);
      }
      return out.toByteArray();
    } catch (IOException | NullPointerException e) {
      throw new IOError(e);
    }
  }

  class MinesAdapter extends MouseAdapter {

    @Override
    public void mousePressed(MouseEvent e) {

      int x = e.getX();
      int y = e.getY();

      int cCol = x / cell_size;
      int cRow = y / cell_size;

      boolean rep = false;

      if (!inGame) {
        newGame();
        repaint();
      }

      if ((x < n_cols * cell_size) && (y < n_rows * cell_size)) {

        if (e.getButton() == MouseEvent.BUTTON3) {

          if (field[(cRow * n_cols) + cCol] > mine_cell) {
            rep = true;

            if (field[(cRow * n_cols) + cCol] <= covered_mine_cell) {
              if (mines_left > 0) {
                field[(cRow * n_cols) + cCol] += mark_for_cell;
                mines_left--;
                statusbar.setText(Integer.toString(mines_left));
              } else {
                statusbar.setText("No marks left");
              }
            } else {

              field[(cRow * n_cols) + cCol] -= mark_for_cell;
              mines_left++;
              statusbar.setText(Integer.toString(mines_left));
            }
          }

        } else {

          if (field[(cRow * n_cols) + cCol] > covered_mine_cell) {
            return;
          }

          if ((field[(cRow * n_cols) + cCol] > mine_cell)
              && (field[(cRow * n_cols) + cCol] < marked_mine_cell)) {

            field[(cRow * n_cols) + cCol] -= cover_for_cell;
            rep = true;

            if (field[(cRow * n_cols) + cCol] == mine_cell) {
              inGame = false;
            }
            if (field[(cRow * n_cols) + cCol] == empty_cell) {
              find_empty_cells((cRow * n_cols) + cCol);
            }
          }
        }

        if (rep) {
          repaint();
        }
      }
    }
  }
}
