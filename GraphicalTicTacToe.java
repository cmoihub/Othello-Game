import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
/**
 * Write a description of class GraphicalTicTacToe here.
 * 
 * @author Craig Isesele
 * @version FreeTime
 */
public class GraphicalTicTacToe extends TicTacToe implements ActionListener
{
    JFrame gameFrame;
    JButton[][] buttons;
    JPanel gridPanel;
    JPanel movePanel;
    Image black,white,empty;
    /**
     * Constructor for objects of class GraphicalTicTacToe
     */
    public GraphicalTicTacToe()
    {
        gameFrame = new JFrame("Tic x Tac x Toe");
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(grid.length,grid.length));
        gameFrame.add(gridPanel,BorderLayout.NORTH);
        buttons = new JButton[grid.length][grid.length];
        try{black = ImageIO.read(new File("black.jpg"));}catch(IOException e){}
        try{white = ImageIO.read(new File("white.jpg"));}catch(IOException e){}
        try{empty = ImageIO.read(new File("null.png"));}catch(IOException e){}
        JButton button;
        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons.length; c++) {
                button = new JButton(" ");
                buttons[r][c] = button;
                buttons[r][c].setBackground(Color.white);
                button.addActionListener(this);
                button.setActionCommand(r + " " + c);
                gridPanel.add(button);
            }
        }
        movePanel = new JPanel();
        movePanel.setLayout(new GridLayout(1,3));
        gameFrame.add(movePanel,BorderLayout.SOUTH);
        /////Greedy
        JButton moveButton = new JButton("Greedy");
        moveButton.addActionListener( new ActionListener() 
        {public void actionPerformed(ActionEvent event){
            setMoveStrategy(new GreedyMove());}});
        movePanel.add(moveButton);
        /////Random
        moveButton = new JButton("Random");
        moveButton.addActionListener( new ActionListener() 
        {public void actionPerformed(ActionEvent event) { 
                setMoveStrategy(new RandomMove());}});
        movePanel.add(moveButton);
        /////First
        moveButton = new JButton("First Available");
        moveButton.addActionListener( new ActionListener() 
        {public void actionPerformed(ActionEvent event) { 
                setMoveStrategy(new FirstAvailableMove());}});
        movePanel.add(moveButton);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.pack();
        gameFrame.setVisible(true);
        print();
        int choice=JOptionPane.showOptionDialog(null,"Would you like to start?",null, 
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (choice == JOptionPane.NO_OPTION) {machinePlay(); toggleTurn(); print();}
    }
    
    public static void main() {new GraphicalTicTacToe();}
    
    @Override
    public void print() {
        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons.length; c++) {
                buttons[r][c].setText(grid[r][c] + "");
            }
        }    
    }
    
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        Move m = getMove(s);
        if (!canPlay(m)) {
            JOptionPane.showMessageDialog(null, "Illegal move!");
            return;
        }
        play(m);
        print();
        toggleTurn();
        while (!generateMoves().isEmpty()) {
            machinePlay();
            print();
            toggleTurn(); //human's turn?
            if (!generateMoves().isEmpty()){
                return; //human can play
            }
            else toggleTurn(); //machine's turn again!
        }
        // we get here if computer can't play
        toggleTurn();
        if (generateMoves().isEmpty()) { //both parties have passed!
            determineWinner();
            displayStatus(status);
            //restart();
            int question = JOptionPane.showConfirmDialog(gameFrame, "Do you want to restart?","Start Game", JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE);        
            if(question == JOptionPane.YES_OPTION){
                gameFrame.dispose();new GraphicalTicTacToe();
            }
            else System.exit(0);
        }
    }
    
    @Override
    protected void displayStatus(int s) {
        switch (s) {
            case X_WON: JOptionPane.showMessageDialog(null, "X won!"); print(); break;
            case O_WON: JOptionPane.showMessageDialog(null, "O won!"); print(); break;
            case TIE: JOptionPane.showMessageDialog(null, "It's a tie!"); print(); break;
            case ILLEGAL: JOptionPane.showMessageDialog(null, "Illegal move!"); break;
            case ONGOING: break;
            default: break;
        }
    }
}