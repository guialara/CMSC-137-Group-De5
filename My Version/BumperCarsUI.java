import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.lang.System;
import java.io.IOException;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import javax.imageio.ImageIO;

public class BumperCarsUI extends JFrame implements Constants{
	BumperCarsPort port;

	JPanel basePanel;	
	JPanel mainPanel;
	JPanel gamePanel;
	JPanel menuPanel;

	JButton playGame;
	JButton exitGame;
	JButton send;
	JButton ok;

	BufferedImage img;
//--------------
	JPanel chatPanel;
	JPanel labelPanel;

	JTextArea messageBox;
	JTextField messageField;
	JScrollPane scroll;
	JScrollPane scroll2;
	JLabel label,label2;
//----------------------

	JPanel level1;
	CardLayout cl;
	CardLayout cl2;

	JFrame pop;
	JTextField nameField;

	JRadioButton defendButton;
	JRadioButton attackButton;

	String server;
	
	Game game = new Game();

//------- LEVEL PORTION -----
	ArrayList<Tile> tilemap;
	Dimension gridSize = new Dimension(40,40);

	public BumperCarsUI(String server, BumperCarsPort port){
		super("Bumper Cars");
		this.server = server;

		// ----- BACKGROUND IMAGE ------
		try{
			img = ImageIO.read(new File("img/menu/BumpCarLogo.png"));
		}catch (IOException e) {
			e.printStackTrace();
		}
		menuPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				//int bgX = (mainPanel.getWidth() - img.getWidth(null)) / 2;
				//int bgY = (mainPanel.getHeight() - img.getHeight(null)) / 2;
				g.drawImage(img,0,0,null);
			}
		};
		menuPanel.setBackground(Color.BLACK);

		cl = new CardLayout();
		basePanel = new JPanel(cl);		
		mainPanel = new JPanel();	
		basePanel.add(menuPanel,MENU);
		basePanel.add(mainPanel,GAME);

		// ------ MENU PANEL ------
		menuPanel.setLayout(null);
		playGame = new JButton("PLAY");
		exitGame = new JButton("EXIT");
		
		//setBounds (x, y, length, height)
		playGame.setBounds(240, 490, 180, 50);
		playGame.setBackground(Color.GREEN);
		exitGame.setBounds(460, 490, 180, 50);
		exitGame.setBackground(Color.RED);

		menuPanel.add(playGame);
		menuPanel.add(exitGame);

		ok = new JButton("SUBMIT");

		//------ MAIN PANEL ------
		chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setBackground(new Color(200,180,230));

		labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		label = new JLabel("CHARACTER: ", SwingConstants.CENTER);
		label2 = new JLabel("SCORE: 000", SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(450,30));
		label2.setPreferredSize(new Dimension(450,30));
		labelPanel.add(label, BorderLayout.WEST);
		labelPanel.add(label2, BorderLayout.EAST);
		labelPanel.setBackground(Color.RED);

		messageBox  = new JTextArea(30,20);
		messageBox.setEditable(false);		
		messageBox.setLineWrap(true);
		scroll = new JScrollPane(messageBox);
		send = new JButton("SEND");
		
		//integrate the game into the gamePanel
		gamePanel = new JPanel();
		//gamePanel.setFocusable(true);
		gamePanel.add(game);

		messageField = new JTextField();
		messageField.setPreferredSize(new Dimension(30,20));

		chatPanel.add(scroll, BorderLayout.NORTH);
		chatPanel.add(messageField, BorderLayout.CENTER);
		chatPanel.add(send, BorderLayout.EAST);

		mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
		
		//labelPanel
        c.weightx = 0;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = 10;
        c.ipadx = 1000;
        mainPanel.add(labelPanel, c);
		
		//gamePanel
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.ipady = 500;
        c.ipadx = 200;
        mainPanel.add(gamePanel, c);

		//chatPanel
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.ipady = 500;
        c.ipadx = 300;
        mainPanel.add(chatPanel, c);

		this.add(basePanel);
		this.setSize(900,600);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		this.port = port;
		port.setUI(this);
		port.setServer(server);
		port.addListenerToButtons(playGame,exitGame,send,ok);
	}

	public void showPopup() {
		JPanel panel = new JPanel(new GridLayout(3,1));
		JPanel panel2 = new JPanel();
		JLabel label = new JLabel("Enter your name");
		nameField = new JTextField();
		
		pop = new JFrame();
		panel.add(label);
		panel.add(nameField);
		panel.add(ok);
		pop.add(panel);

		pop.setSize(new Dimension(300,200));
		pop.setLocationRelativeTo(null);
		pop.setVisible(true);		
	}

	public void initGamePanel(){		
		game.start();
	}
	
	public static void main(String args[])throws Exception{
		if(args.length != 1){
			System.out.println("Usage: java BumperCarsUI <server address>"); 
			System.exit(1);
		}
		
		BumperCarsPort port = new BumperCarsPort();
		BumperCarsUI game = new BumperCarsUI(args[0],port);
	}

}