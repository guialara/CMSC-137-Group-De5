package bin;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferStrategy;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
//import java.util.concurrent.TimeUnit;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;
	private boolean running = false;
	private Thread thread;
	Handler handler;
	public static double delta;
	private String fps;
	public static int WIDTH, HEIGHT;
	public static Game game;
	public GameClient gameClient;
	public WindowControl windowCtrl;
	public EndGameToDo endGame;
	private GameServer gameServer;
	public JFrame frame;
	public String pName;
	public Car car;
	public boolean debug = true;
	public KeyInput input;
	public int playerNum;
	public int currentPlayer;
	public Map<String, Integer> ranking;
	 
	public Game(int w, int h, String title, String pName){
		this.setPreferredSize(new Dimension(w, h));
		this.setMaximumSize(new Dimension(w, h));
		this.setMinimumSize(new Dimension(w, h));
		this.pName = pName;
		this.game = this;
		
		frame = new JFrame("AgarDown");
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		this.start();
	}

	private void init(){
		game=this;
		currentPlayer = 0;
		WIDTH = getWidth();
		HEIGHT = getHeight();
		int randX = new Random().nextInt(WIDTH-20)+10;
		int randY = new Random().nextInt(HEIGHT-20)+10;

		handler = new Handler();
		windowCtrl = new WindowControl(this);
		endGame = new EndGameToDo(this);
		input = new KeyInput(handler,pName);
		ranking = new HashMap<String, Integer>();

		handler.createLevel();
		car = new CarMP(randX,randY,50,50,handler,pName,ObjectId.Car,null, -1, 0);
		handler.addObject(car);
		PacketLogin loginPacket = new PacketLogin(car.pName,(int) car.getX(),(int) car.getY(), car.getWidth(), car.getHeight(), car.getScore());
		if(gameServer!=null){
			gameServer.addConnection((CarMP) car, loginPacket);
		}
		loginPacket.writeData(gameClient);
		for(int i=0;i<10;i++)
			handler.createFood();
		
		this.addKeyListener(input);
	}

	public synchronized void start(){
		if(running) return;
		running = true;
		
		if (JOptionPane.showConfirmDialog(this, "Do you want to run the server") == 0) {
			String tempNum = JOptionPane.showInputDialog("Please enter number of players");
			int tempplayerNum = Integer.parseInt(tempNum);
			gameServer = new GameServer(this, tempplayerNum);
            gameServer.start();
            this.playerNum = gameServer.playerNum;
        }
		String serverAddress = JOptionPane.showInputDialog("Enter Server Address: ");
		gameClient = new GameClient(this, serverAddress);
		gameClient.start();
		thread = new Thread(this);
		thread.start();
	}
	 public synchronized void stop() {
	        running = false;

	        try {
	            thread.join();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	public void run(){
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		int  ctrl = 1;
		
		init();
		
		while (running) {
			
			System.out.println("LIMIT: "+playerNum);
			System.out.println("CURRENT: "+currentPlayer);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			while(playerNum == currentPlayer){
				if(ctrl == 1){
					Timer timerGame = new Timer();
					timerGame.schedule(new GameTimer(endGame), 100000);
					ctrl+=1;
				}
	            long now = System.nanoTime();
	            delta += (now - lastTime) / ns;
	            lastTime = now;
	            boolean shouldRender = true;
	
	            while (delta >= 1) {
	                tick();
	                updates++;
	                delta -= 1;
	                shouldRender = true;
	            }
	
	            try {
	                Thread.sleep(2);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	
	            if (shouldRender) {
	                frames++;
	                render();
	            }
	
	            if (System.currentTimeMillis() - timer >= 1000) {
	                timer += 1000;
	                frames = 0;
	                updates = 0;
	            }
	        }
		}

	}
	
	public static double getDelta(){
		return delta;
	}

	public void tick(){
		handler.tick();
	}

	public void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null){
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.black);
		g.fillRect(0,0,getWidth(),getHeight());
		handler.render(g);

		g.dispose();
		bs.show();
	}

	public static void main(String[] args){
		String name = JOptionPane.showInputDialog("Please enter a username");
		if(name.isEmpty()){
			name = "AnonyMonkey";
		}
		new Game(800,600,"BumpCar.io",name);
	}
}