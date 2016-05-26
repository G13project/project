package src;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;


public class Pacmanrun extends JFrame {
	public Pacmanrun() {
        this.setTitle("Pacman");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(new Dimension(500, 500));
        this.setLocationRelativeTo(null); 
        this.getContentPane().setLayout(new BorderLayout()); 
        this.getContentPane().add(new GamePanel());
        this.setVisible(true);
	}
}