package multiSupport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

public class ClientMessages {

	public static final short CLIENT_MESSAGE_ADD_POINT = 0;
	
	public static final int CLIENT_FLAG_COUNT = CLIENT_MESSAGE_ADD_POINT + 1;

	// Create a bew client message to be sent to the server when a client adds
	// a point to the screen via touch events
	public static class AddPointClientMessage extends ClientMessage {

		// Member variables to be read in from the server and sent to clients
		private int mID;
		private float mX;
		private float mY;
		private int shoot; 
		private float bX; 
		private float bY; 
		private boolean backKey;
		private float health1;
		private float health2; 
		

		// Empty constructor needed for message pool allocation
		public AddPointClientMessage() {
			// Do nothing...
		}

		// Constructor
		public AddPointClientMessage(final int pID, final float pX,
				final float pY, final int shoot, final float bX, final float bY, final boolean backKey, final float health1, final float health2) {
			this.mID = pID;
			this.mX = pX;
			this.mY = pY;
			this.shoot = shoot; 
			this.bX = bX; 
			this.bY = bY;
			this.backKey = backKey;
			this.health1 = health1; 
			this.health2 = health2;
		}

		// A Setter is needed to change values when we obtain a message from the
		// message pool
		public void set(final int pID, final float pX, final float pY, final int shoot, final float bX, final float bY, final boolean backKey, final float health1, final float health2) {
			this.mID = pID;
			this.mX = pX;
			this.mY = pY;
			this.shoot = shoot; 
			this.bX = bX; 
			this.bY = bY;
			this.backKey = backKey;
			this.health1 = health1; 
			this.health2 = health2;
		}

		// Getters
		public int getID() {
			return this.mID;
		}

		public float getX() {
			return this.mX;
		}

		public float getY() {
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
		
		public float getHealth1() { 
			return this.health1;
		}
		
		public float getHealth2() { 
			return this.health2; 
		}
		
		// Get the message flag
		@Override
		public short getFlag() {
			return CLIENT_MESSAGE_ADD_POINT;
		}

		// Apply the read data to the message's member variables
		@Override
		protected void onReadTransmissionData(DataInputStream pDataInputStream)
				throws IOException {
			this.mID = pDataInputStream.readInt();
			this.mX = pDataInputStream.readFloat();
			this.mY = pDataInputStream.readFloat();
			this.shoot = pDataInputStream.readInt(); 
			this.bX = pDataInputStream.readFloat(); 
			this.bY = pDataInputStream.readFloat();
			this.backKey = pDataInputStream.readBoolean();
			this.health1 = pDataInputStream. readFloat();
			this.health2 = pDataInputStream. readFloat();
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
			pDataOutputStream.writeFloat(this.health1);
			pDataOutputStream.writeFloat(this.health2);
		}
	}
}
