package multiSupport;

import java.io.IOException;


import multiSupport.ClientMessages.AddPointClientMessage;

import org.andengine.engine.Engine;
import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;
import org.andengine.extension.multiplayer.protocol.server.IClientMessageHandler;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.SocketServer.ISocketServerListener;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector.ISocketConnectionClientConnectorListener;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;

import scene.MultiScene;

import com.example.manager.SceneManager;

import android.util.Log;

public class MultiServer implements
ISocketServerListener<SocketConnectionClientConnector>,
ISocketConnectionClientConnectorListener {
	private static final String TAG = "SERVER";

	private Engine mEngine;
	
	// Port variable needed for clients to connect to the server
	private int mServerPort;

	// Server's socket meant for handling many simultaneous connections
	private SocketServer<SocketConnectionClientConnector> mSocketServer;

	// Constructor
	public MultiServer(final int pServerPort, final Engine pEngine) {
		this.mServerPort = pServerPort;
		this.mEngine = pEngine;
	}

	// Server initialization method, to be called on server initialization
	public void initServer() {
		
		this.mEngine.runOnUpdateThread(new Runnable(){

			@Override
			public void run() {
				
				// Create the SocketServer, specifying a port, client listener and 
				// a server state listener (listeners are implemented in this class)
				MultiServer.this.mSocketServer = new SocketServer<SocketConnectionClientConnector>(
						MultiServer.this.mServerPort,
						MultiServer.this, MultiServer.this) {

					// Called when a new client connects to the server...
					@Override
					protected SocketConnectionClientConnector newClientConnector(
							SocketConnection pSocketConnection)
							throws IOException {

						// Create a new client connector from the socket connection
						final SocketConnectionClientConnector clientConnector = new SocketConnectionClientConnector(pSocketConnection);
						clientConnector.registerClientMessage(ClientMessages.CLIENT_MESSAGE_ADD_POINT, AddPointClientMessage.class, new IClientMessageHandler<SocketConnection>(){ 
							@Override 
							public void onHandleMessage(ClientConnector<SocketConnection> pClientConnector, IClientMessage pClientMessage) throws IOException { 
								
								AddPointClientMessage message = (AddPointClientMessage) pClientMessage; 
								((MultiScene)SceneManager.getInstance().getCurrentScene()).player2.getBody().setLinearVelocity(message.getX() * 3,
										message.getY() * 3);;
								
							}
						});
						
						// Return the new client connector
						return clientConnector;
					}
				};
				// Start the server once it's initialized
				MultiServer.this.mSocketServer.start();
			}
			
		});

	}

	
	public void sendMessage(ServerMessage pServerMessage) {
		try {
			this.mSocketServer.sendBroadcastServerMessage(pServerMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Terminate the server socket and stop the server thread
	public void terminate(){
		if(this.mSocketServer != null)
		this.mSocketServer.terminate();
	}
	
	// Listener - In the event of a client connecting
	@Override
	public void onStarted(ClientConnector<SocketConnection> pClientConnector) {
		Log.i(TAG, "Client Connected: "
				+ pClientConnector.getConnection().getSocket().getInetAddress()
						.getHostAddress());
	}
	
	

	// Listener - In the event of a client terminating the connection
	@Override
	public void onTerminated(ClientConnector<SocketConnection> pClientConnector) {
		Log.i(TAG, "Client Disconnected: "
				+ pClientConnector.getConnection().getSocket().getInetAddress()
						.getHostAddress());
	}

	// Listener - In the event of the server starting up
	@Override
	public void onStarted(
			SocketServer<SocketConnectionClientConnector> pSocketServer) {
		Log.i(TAG, "Started");
	}

	// Listener - In the event of the server shutting down
	@Override
	public void onTerminated(
			SocketServer<SocketConnectionClientConnector> pSocketServer) {
		Log.i(TAG, "Terminated");
	}

	@Override
	public void onException(
			SocketServer<SocketConnectionClientConnector> pSocketServer,
			Throwable pThrowable) {
		Log.i(TAG, "Exception: ", pThrowable);

	}
}
