package bin;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;

import bin.Packet.PacketTypes;

public class GameClient extends Thread{
	private InetAddress ipAddress;
	private DatagramSocket socket;
	
	private Game game;

	public GameClient(Game game, String ipAddress){
		this.game = game;

		try{
			this.socket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		}catch(Exception ioe){

		}
	}

	public void run(){
		while(true){
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try{
				socket.receive(packet);
			}catch(Exception e){} 
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			//System.out.println("SERVER > "+ new String(packet.getData()));
		}
	}
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String msg =new String(data).trim();
		PacketTypes type = Packet.lookupPacket(msg.substring(0, 2));
		Packet packet;
		switch(type){
			default:
			case INVALID: break;
			case LOGIN: 
				packet = new PacketLogin(data);
				handleLogin((PacketLogin) packet, address, port);
				break;
			case DISCONNECT: 
				packet = new PacketDisconnect(data);				
	            System.out.println(packet.getUsername()+" disconnected...");
	            game.handler.removeObjectFromList(((PacketDisconnect) packet).getUsername());
				break;
			case MOVE:
				 packet = new PacketMove(data);
		         handleMove((PacketMove) packet);
		         break;
			case NUM:
				packet = new PacketPlayerNum(data);
				setPlayerNum((PacketPlayerNum) packet);
			case FOOD:
				packet = new PacketFood(data);
				handleFood((PacketFood)packet);
				break;
			case EAT:
				packet = new PacketEat(data);
				game.handler.removeObject(searchFood(((PacketEat)packet).getXPos(), ((PacketEat)packet).getYPos()));
				break;
		}
		
	}
	private void handleMove(PacketMove packet) {
		 this.game.handler.movePlayer(packet.getUsername(), packet.getX(), packet.getY());
	}

	private void handleLogin(PacketLogin packet, InetAddress address, int port) {
		System.out.println(packet.getUsername()+" joined...");
		CarMP car = new CarMP((float)packet.getX(),(float)packet.getY(), packet.getW(),packet.getH(),packet.getUsername(),packet.getId(),address, port);
		game.handler.addObject(car);
		//game.currentPlayer +=1;
	}

	private void handleFood(PacketFood packet){
		for(int i=0;i<packet.foodCoords.length;i++)
			this.game.handler.addObject(new Food(packet.foodCoords[i][0],packet.foodCoords[i][1],7,7,ObjectId.Food));
	}
	
	private void setPlayerNum (PacketPlayerNum packet){
		game.playerNum = packet.playerNum;
		game.currentPlayer = packet.currentPlayer;
	}

	public void sendData(byte[] data){
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 1331);

		try{
			socket.send(packet);
		}catch(Exception e){
			
		}
	}

	private Food searchFood(float x, float y){
		for(GameObject f : game.handler.object){
			if(f.getId()==ObjectId.Food){
				if(f.getX()==x && f.getY()==y) return (Food)f;
			}
		}
		return null;
	}
}