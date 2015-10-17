import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphicalOthello2 extends Othello implements ActionListener {
    JFrame gameFrame;
    JButton[][] buttons;
    JPanel gridPanel;
    JPanel strategyPanel;
    public GraphicalOthello2() {
        gameFrame = new JFrame("Othello!");
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(grid.length, grid.length));
        gameFrame.add(gridPanel, BorderLayout.NORTH);
        buttons = new JButton[grid.length][grid.length];
        JButton button;
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                button = new JButton(" ");
                button.setActionCommand(i + " " + j);
                buttons[i][j] = button;
                gridPanel.add(button);
                button.addActionListener(this);
            }
        }
        strategyPanel = new JPanel();
        strategyPanel.setLayout(new GridLayout(1, 3));
        gameFrame.add(strategyPanel, BorderLayout.SOUTH);
        JButton strategyButton = new JButton("Random");
        //using anonymous inner classes here (see textbook)
        //but instead could have used a separate class implementing the listener as well
        strategyButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) { setMoveStrategy(new RandomMove());}
        });
        strategyPanel.add(strategyButton);
        
        strategyButton = new JButton("First Available");
        strategyButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) { setMoveStrategy(new FirstAvailableMove());}
        });
        strategyPanel.add(strategyButton);
        
        strategyButton = new JButton("Greedy");
        strategyButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) { setMoveStrategy(new GreedyMove());}
        });
        strategyPanel.add(strategyButton);
        
        gameFrame.setSize(200, 200);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);
        print();
        
        int choice = JOptionPane.showOptionDialog(null, "Would you like to start?", null, 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (choice == JOptionPane.NO_OPTION) {
            machinePlay(); 
            toggleTurn(); 
            print();
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
            if (!generateMoves().isEmpty()) return; //human can play
            else toggleTurn(); //machine's turn again!
        }
        
        // we get here if computer can't play
        toggleTurn();
        if (generateMoves().isEmpty()) { //both parties have passed!
            determineWinner();
            displayStatus(status);            
            //somehow exit (an improvment would be to reset the game)
            System.exit(0);
        }
        
    }
    
    @Override
    public void print() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                buttons[i][j].setText(grid[i][j] + "");
            }
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
    
    public static void main() {new GraphicalOthello2();}
}