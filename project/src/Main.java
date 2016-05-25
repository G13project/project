import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		
		Frame frame = new Frame();
		
		frame.setSize(new Dimension(500, 500));
        frame.setVisible(true);
		frame.setTitle("Pacman");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  	    frame.setLocationRelativeTo(null); // µøµ¡¦ì¸m¸m¤¤
	    frame.getContentPane().setLayout(new BorderLayout()); 
	    frame.getContentPane().add(new GamePanel());
		        
		    }

	}


