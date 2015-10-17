import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
/**
 * GUI for the othello class designed by Professor Babak Esfandiari
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
   private BufferedImage black,white,empty;
   public GraphicalOthello()
    {
        gameFrame = new JFrame(" Othello > Reversi? ");
        othelloPanel = new JPanel();
        movePanel = new JPanel();
        buttons = new JButton[SIZE][SIZE];
        othelloPanel.setLayout(new GridLayout(SIZE, SIZE));
        movePanel.setLayout(new GridLayout(1,3));
        gameFrame.setLayout(new BorderLayout());
        //images are used as icons
       // black = null;
        try{black = ImageIO.read(new File("black.jpg"));}
        catch(IOException e){}
        //white = null;
        try{white = ImageIO.read(new File("white.jpg"));}
        catch(IOException e){}
        //empty = null;
        try{empty = ImageIO.read(new File("null.png"));}
        catch(IOException e){}        
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                String place = "" + grid[r][c];
                buttons[r][c] = new JButton (place);
                buttons[r][c].setBackground(Color.white);
                buttons[r][c].addActionListener(this);
                buttons[r][c].setActionCommand("" + r + c);
                othelloPanel.add(buttons[r][c]);
                fillGrid(r,c,size,black,white,empty,buttons,grid);
            }
        }  
        greed = new JButton ("Greedy");
        greed.setActionCommand("GreedyMove");        
        addButton(greed,this,movePanel);
        first = new JButton ("First");
        first.setActionCommand("FirstAvailableMove");
        addButton(first,this,movePanel);
        random = new JButton ("Random");
        random.setActionCommand("RandomMove");
        addButton(random,this,movePanel);
        gameFrame.getContentPane().add(othelloPanel, BorderLayout.CENTER);
        gameFrame.getContentPane().add(movePanel,BorderLayout.NORTH);
        gameFrame.setPreferredSize(new Dimension(400, 400));
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.pack();
        start();
        gameFrame.setVisible(true);
    }
    
   private void addButton(JButton button, GraphicalOthello GO, 
        JPanel panel)
   {
       button.addActionListener(GO);
       panel.add(button);
    }
    
   public static void main(){
       GraphicalOthello gui = new GraphicalOthello();
    }
       
   private void fillGrid
    (int r, int c, int size, 
        BufferedImage x, BufferedImage o, BufferedImage empty,
        JButton [][] button, char[][] grid){
        // x is the player, o is the computer
        //'_' refers to the free spots
        if(grid[r][c] == 'x'){
            button[r][c].setIcon(new ImageIcon(x));
            button[r][c].setPreferredSize(new Dimension(size,size));
        }else if(grid[r][c] == 'o'){
            button[r][c].setIcon(new ImageIcon(o));
            button[r][c].setPreferredSize(new Dimension(size,size));
        }else if(grid[r][c] == '_'){
            button[r][c].setIcon(new ImageIcon(empty));
            button[r][c].setPreferredSize(new Dimension(size,size));
        }
    }
    
   public void copyGrid()
    {
        for(int r = 0; r < SIZE ;r++){
            for(int c = 0 ; c < SIZE; c++){
                String place = "" + grid[r][c];
                fillGrid(r,c,size,black,white,empty,buttons,grid);
            }
        }
    }
    
   public void print()
    {
        for(int r = 0; r < SIZE; r++){
           for(int c = 0; c < SIZE; c++){
               fillGrid(r,c,size,black,white,empty,buttons,grid);
           }
        }
    }
    
   public void actionPerformed(ActionEvent event)
    {
     JButton button = (JButton) event.getSource();
     String command = event.getActionCommand();
       if (command == "FirstAvailableMove") 
            setMoveStrategy(new FirstAvailableMove());
       else if (command == "RandomMove")
            setMoveStrategy(new RandomMove());
       else if (command == "GreedyMove")
            setMoveStrategy(new GreedyMove());
       else{       
       int r = Integer.parseInt(command.substring(0, 1));
       int c = Integer.parseInt(command.substring(1, 2));
       Move move = new Move(r, c);
       int status;
        if(canPlay(move)){
        status = play(move);
        playerPlaying();
       } else{ return;}
       
       status = ONGOING;
       while (status == ONGOING){
          if(!(generateMoves().isEmpty())){ 
            status = machinePlay();
            machinePlaying();    
            if(!(generateMoves().isEmpty())){  
                if(canPlay(move)){  
                status = play(move);
                playerPlaying();
              }
              else{ return;}
            }
            else if(!(generateMoves().isEmpty())) { 
                status = machinePlay();
                machinePlaying();
                determineWinner();
            }
            else { 
                status = GAME_OVER;
                gameOver();
            }  
          }
          else if(!(generateMoves().isEmpty())){ 
            if(canPlay(move))
            {  
              status = play(move);
              playerPlaying();
             } else{ return;}
          }
          else
          {
            status = GAME_OVER;
            gameOver();
          }
        }
    }
   } 
   
   private void playerPlaying(){
       copyGrid();
       toggleTurn();
    }
   
   private void machinePlaying(){    
        copyGrid();
        print();
        toggleTurn();
    }
   
   private void gameOver()
   {
       print();
       JOptionPane.showMessageDialog(gameFrame,determineGame() 
                + "","Game Over", JOptionPane.INFORMATION_MESSAGE);
       restart();
    }
   
   private void start()
    {
        int question = JOptionPane.showConfirmDialog(gameFrame,
            "Do you want to start Othello?","Start Game",
              JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE);     
        if(question == JOptionPane.YES_OPTION)return;
        else if(question == JOptionPane.NO_OPTION){
            status = machinePlay();
            machinePlaying();
            return;
        }else{System.exit(0);}
    }
    
   private void restart()
    {
        int question = JOptionPane.showConfirmDialog(gameFrame, 
            "Do you want to restart Othello?","Start Game", 
              JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE);        
        if(question == JOptionPane.YES_OPTION){
            gameFrame.dispose(); 
            new GraphicalOthello();
        }else{System.exit(0);}
    }
    
   private String determineGame() {
        determineWinner();
        String str = "";
        if(status == X_WON)  return str = "Batman won!!!";
        else if (status == O_WON) return str = "Superman?!!!"; 
        else if (status == TIE)  return str = "I guess they tied the knot ;-)"; 
        else return str; 
    }
}