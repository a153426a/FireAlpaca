package multiSupport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;


public class ServerMessages {

public static final short SERVER_MESSAGE_ADD_POINT = ClientMessages.CLIENT_FLAG_COUNT;
	
	public static class AddPointServerMessage extends ServerMessage{

		// Member variables to be read in from clients and sent to the server
		private int mID;
		private float mX;
		private float mY;
		private int shoot; 
		private float bX; 
		private float bY; 
		private boolean backKey;
		private float health; 
		
		// Empty constructor needed for message pool allocation
		public AddPointServerMessage(){
			// Do nothing...
		}
		
		// Constructor
		public AddPointServerMessage(final int pID, final float pX, final float pY, final int shoot, final float bX, final float bY, final boolean backKey, final float health){
			this.mID = pID;
			this.mX = pX;
			this.mY = pY;
			this.shoot = shoot; 
			this.bX = bX; 
			this.bY = bY;
			this.backKey = backKey;
			this.health = health; 
			
		}
		
		// A Setter is needed to change values when we obtain a message from the message pool
		public void set(final int pID, final float pX, final float pY, final int shoot, final float bX, final float bY, final boolean backKey, final float health){
			this.mID = pID;
			this.mX = pX;
			this.mY = pY;
			this.shoot = shoot; 
			this.bX = bX; 
			this.bY = bY;
			this.backKey = backKey;
			this.health = health; 
		}
		
		// Getters
		public int getID(){
			return this.mID;
		}
		public float getX(){
			return this.mX;
		}
		public float getY(){
			return this.mY;
		}
		
		public int getShoot() { 
			return this.shoot; 
		}
		
		public float getBX() { 
			return this.bX; 
		}

		public float getBY() {
			return this.bY;
		}
		
		public boolean getBackKey() {
			return this.backKey;
		}
		
		public float getHealth() { 
			return this.health;
		}
		
		// Get the message flag
		@Override
		public short getFlag() {
			return SERVER_MESSAGE_ADD_POINT;
		}

		// Apply the read data to the message's member variables
		@Override
		protected void onReadTransmissionData(DataInputStream pDataInputStream)
				throws IOException {
			this.mID = pDataInputStream.readInt();
			this.mX = pDataInputStream.readFloat();
			this.mY = pDataInputStream. readFloat();
			this.shoot = pDataInputStream.readInt();; 
			this.bX = pDataInputStream.readFloat(); 
			this.bY = pDataInputStream.readFloat();
			this.backKey = pDataInputStream.readBoolean();
			this.health = pDataInputStream. readFloat();
		}

		// Write the message's member variables to the output stream
		@Override
		protected void onWriteTransmissionData(
				DataOutputStream pDataOutputStream) throws IOException {
			pDataOutputStream.writeInt(this.mID);
			pDataOutputStream.writeFloat(this.mX);
			pDataOutputStream.writeFloat(this.mY);
			pDataOutputStream.writeInt(this.shoot); 
			pDataOutputStream.writeFloat(this.bX); 
			pDataOutputStream.writeFloat(this.bY);
			pDataOutputStream.writeBoolean(this.backKey);
			pDataOutputStream.writeFloat(this.health);
		}
	}
	
}
