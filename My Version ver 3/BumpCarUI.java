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

public class BumpCarUI extends JFrame implements Constants{
	BumpCarPort port;
	
	JPanel basePanel;	
	JPanel mainPanel;
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
	JLabel label;
//----------------------

	JPanel level1;
	CardLayout cl;
	CardLayout cl2;

	JFrame pop;
	JTextField nameField;

	JRadioButton defendButton;
	JRadioButton attackButton;

	String server;
	
	//Game game = new Game();

//------- LEVEL PORTION -----
	ArrayList<Tile> tilemap;
	Dimension gridSize = new Dimension(40,40);

	public BumpCarUI(String server, BumpCarPort port){
		super("BumpCar.io");
		this.server = server;

		// ----- BACKGROUND IMAGE ------
		menuPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				int width = getWidth();
    				int height = getHeight();
				try{
					img = ImageIO.read(new File("img/menu/BumpCarLogo.png"));

			 		 super.paintComponent(g);
					 Graphics2D g2d = (Graphics2D) g;
					 int x = (this.getWidth() - img.getWidth(null)) / 2;
					 int y = (this.getHeight() - img.getHeight(null)) / 2;
					 g2d.drawImage(img, x, y, null);
				    
				}catch (IOException e) {
					e.printStackTrace();
				}
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
		playGame = new JButton("GO");
		exitGame = new JButton("STOP");
		
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

		messageBox  = new JTextArea(30,20);
		messageBox.setEditable(false);		
		messageBox.setLineWrap(true);
		scroll = new JScrollPane(messageBox);
		send = new JButton("SEND");
		
		messageField = new JTextField();
		messageField.setPreferredSize(new Dimension(30,20));

		chatPanel.add(scroll, BorderLayout.NORTH);
		chatPanel.add(messageField, BorderLayout.CENTER);
		chatPanel.add(send, BorderLayout.EAST);

		mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

		//gamePanel
		//c.fill = GridBagConstraints.HORIZONTAL;
        //c.gridwidth = 1;
        //c.gridx = 1;
        //c.gridy = 1;
        //c.ipady = 500;
        //c.ipadx = 200;
        //mainPanel.add(game, c); //integrates the game into the JFrame with the chat

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
	
	// set the user's name
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
		//game.start();
	}
	
	public static void main(String args[])throws Exception{
		if(args.length != 1){
			System.out.println("Usage: java BumpCarUI <server address>"); 
			System.exit(1);
		}
		
		BumpCarPort port = new BumpCarPort();
		BumpCarUI game = new BumpCarUI(args[0],port);
	}

}
