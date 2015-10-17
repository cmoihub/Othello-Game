import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
/**
 * Write a description of class GraphicalOthello here.
 * 
 * @author Craig Isesele
 * @version Assign5
 */
public class GraphicalOthello extends Othello implements ActionListener
{
   JFrame gameFrame;
   JButton[][] buttons;
   JPanel othelloPanel;
   JPanel movePanel;
   JButton greed, first, random;
   public static int size = 4;
   private Graphics g;
   private BufferedImage black;
   private BufferedImage white;
   private BufferedImage empty;
   /**
     * Constructor for objects of class GraphicalOthello
     */
    public GraphicalOthello()
    {
        gameFrame = new JFrame(" Othello > Reversi? ");
        othelloPanel = new JPanel();
        movePanel = new JPanel();
        buttons = new JButton[SIZE][SIZE];
        othelloPanel.setLayout(new GridLayout(SIZE, SIZE));
        movePanel.setLayout(new GridLayout(1,3));
        gameFrame.setLayout(new BorderLayout());
        black = null;
        try{black = ImageIO.read(new File("black.jpg"));}catch(IOException e){}
        
        white = null;
        try{white = ImageIO.read(new File("white.jpg"));}catch(IOException e){}
        
        empty = null;
        try{empty = ImageIO.read(new File("null.png"));}catch(IOException e){}
        
        
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                String place = "" + grid[r][c];
                buttons[r][c] = new JButton (place);
                buttons[r][c].setBackground(Color.white);
                buttons[r][c].addActionListener(this);
                buttons[r][c].setActionCommand("" + r + c);
                othelloPanel.add(buttons[r][c]);
                if(grid[r][c] == 'x'){
                buttons[r][c].setIcon(new ImageIcon(black));
                  buttons[r][c].setPreferredSize(new Dimension(size,size));
               }else if(grid[r][c] == 'o'){
                buttons[r][c].setIcon(new ImageIcon(white));
                buttons[r][c].setPreferredSize(new Dimension(size,size));
               }else if(grid[r][c] == '_'){
                buttons[r][c].setIcon(new ImageIcon(empty));
                buttons[r][c].setPreferredSize(new Dimension(size,size));
                }
            }
        }
        greed = new JButton ("Greedy");
        first = new JButton ("First");
        random = new JButton ("Random");
        first.addActionListener(this);
        first.setActionCommand("FirstAvailableMove");
        random.addActionListener(this);
        random.setActionCommand("RandomMove");
        greed.addActionListener(this); 
        greed.setActionCommand("GreedyMove");
        movePanel.add(greed);
        movePanel.add(first);
        movePanel.add(random);
        gameFrame.getContentPane().add(othelloPanel, BorderLayout.CENTER);
        gameFrame.getContentPane().add(movePanel,BorderLayout.NORTH);
        gameFrame.setPreferredSize(new Dimension(400, 400));
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.pack();
        start();
        gameFrame.setVisible(true);
    }
    
   public static void main(){GraphicalOthello gui = new GraphicalOthello();}
    
   public void copyGrid()
    {
        for(int r = 0; r < SIZE ;r++){
            for(int c =0 ; c < SIZE; c++){
                String place = "" + grid[r][c];
                //buttons[r][c].setText(place);
           if(grid[r][c] == 'x'){
                buttons[r][c].setIcon(new ImageIcon(black));
                  buttons[r][c].setPreferredSize(new Dimension(size,size));
               }else if(grid[r][c] == 'o'){
                buttons[r][c].setIcon(new ImageIcon(white));
                buttons[r][c].setPreferredSize(new Dimension(size,size));
               }else if(grid[r][c] == '_'){
                buttons[r][c].setIcon(new ImageIcon(empty));
                buttons[r][c].setPreferredSize(new Dimension(size,size));
                }
            }
        }
    }
    
   public void print()
    {
        for(int r = 0; r < 8; r++){
           for(int c = 0; c < 8; c++){
               //if(grid[r][c] == 'x')buttons[r][c].setText("x");
               //else if(grid[r][c] == 'o')buttons[r][c].setText("o");
         if(grid[r][c] == 'x'){
                buttons[r][c].setIcon(new ImageIcon(black));
                  buttons[r][c].setPreferredSize(new Dimension(size,size));
               }else if(grid[r][c] == 'o'){
                buttons[r][c].setIcon(new ImageIcon(white));
                buttons[r][c].setPreferredSize(new Dimension(size,size));
               }else if(grid[r][c] == '_'){
                buttons[r][c].setIcon(new ImageIcon(empty));
                buttons[r][c].setPreferredSize(new Dimension(size,size));
                }
           }
        }
    }
    
   public void actionPerformed(ActionEvent event)
    {
       JButton button = (JButton) event.getSource();
       String command = event.getActionCommand();
       if (command == "FirstAvailableMove") setMoveStrategy(new FirstAvailableMove());
       else if (command == "RandomMove")setMoveStrategy(new RandomMove());
       else if (command == "GreedyMove")setMoveStrategy(new GreedyMove());
       else{
       
       int r = Integer.parseInt(command.substring(0, 1));
       int c = Integer.parseInt(command.substring(1, 2));
       Move move = new Move(r, c);
       int status;
        if(canPlay(move)){
        status = play(move);
        copyGrid();
        toggleTurn();
       } else{ return;}
       
       status = ONGOING;
       while (status == ONGOING){
          if(!(generateMoves().isEmpty())){ 
            status = machinePlay(); 
            copyGrid();
            print();
            toggleTurn();            
            if(!(generateMoves().isEmpty())){  
                if(canPlay(move)){  
                status = play(move);
                copyGrid();
                toggleTurn();
              }
              else{ return;}
            }
            else if(!(generateMoves().isEmpty())) { 
                status = machinePlay();
                copyGrid();
                print();
                toggleTurn();
                determineWinner();
            }
            else { 
                print();
                status = GAME_OVER;
                JOptionPane.showMessageDialog(gameFrame,determineGame() + "","Game Over", JOptionPane.INFORMATION_MESSAGE);
                restart();
            }  
          }
          else if(!(generateMoves().isEmpty())){ 
            if(canPlay(move)){  
              status = play(move);
              copyGrid();
              toggleTurn();
             } else{ return;}
          }
          else{
            print();
            status = GAME_OVER;
            JOptionPane.showMessageDialog(gameFrame,determineGame() + "","Game Over", JOptionPane.INFORMATION_MESSAGE);
            restart();
          }
        }
    }}
    
    
   private void start()
    {
        int question = JOptionPane.showConfirmDialog(gameFrame,"Do you want to start Othello?","Start Game",JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE);     
        if(question == JOptionPane.YES_OPTION)return;
        else if(question == JOptionPane.NO_OPTION){
            status = machinePlay();
            copyGrid();
            print();
            toggleTurn();
            return;
        }else{System.exit(0);}
    }
    
   private void restart()
    {
        int question = JOptionPane.showConfirmDialog(gameFrame, "Do you want to restart Othello?","Start Game", JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE);        
        if(question == JOptionPane.YES_OPTION){
            gameFrame.dispose();
            GraphicalOthello GO = new GraphicalOthello();
        }
        //else if(question == JOptionPane.NO_OPTION){System.exit(0);}
        else{System.exit(0);}
    }
    
   private String determineGame() {
        determineWinner();
        String str = "";
        if(status == X_WON)  return str = "Batman won!!!";
        else if (status == O_WON) return str = "Superman?!!!"; 
        else if (status == TIE)  return str = "I guess they tied the knot ;)"; 
        else return str; 
    }
} 