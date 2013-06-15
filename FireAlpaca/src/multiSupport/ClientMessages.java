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

		// Empty constructor needed for message pool allocation
		public AddPointClientMessage() {
			// Do nothing...
		}

		// Constructor
		public AddPointClientMessage(final int pID, final float pX,
				final float pY) {
			this.mID = pID;
			this.mX = pX;
			this.mY = pY;
		}

		// A Setter is needed to change values when we obtain a message from the
		// message pool
		public void set(final int pID, final float pX, final float pY) {
			this.mID = pID;
			this.mX = pX;
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
		}

		// Write the message's member variables to the output stream
		@Override
		protected void onWriteTransmissionData(
				DataOutputStream pDataOutputStream) throws IOException {
			pDataOutputStream.writeInt(this.mID);
			pDataOutputStream.writeFloat(this.mX);
			pDataOutputStream.writeFloat(this.mY);
		}
	}
}
