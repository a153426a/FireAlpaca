package com.example.manager;

import java.sql.*;

public class DatabaseManager {

	private Connection c;
	private String username;
	private int level;
	private int totalScore;
	private int totalCoins;
	private float attack;
	private int health;

	private static final DatabaseManager INSTANCE = new DatabaseManager(); 
	
	public boolean openConnection(){
		boolean result = false;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println( "Driver not found: " + e + "\n" + e.getMessage() );
		}
		try {
			c = DriverManager.getConnection("jdbc:postgresql://db.doc.ic.ac.uk/g1227119_u"+
					"?sslfactory=org.postgresql.ssl.NonValidatingFactory"+
					"&ssl=true", "g1227119_u", "0pv1T8NHRp");
			if(c==null){
				System.err.println("Connection not found.");
			}
			result = true;
		} catch (SQLException e) {
			System.err.println("Exception: " + e + "\n" + e.getMessage() );
		}
		return result;
	}
	
	public void afterLevel(int currentLevel, int score, int coins) throws SQLException{
		String sql;
		PreparedStatement pst;
		sql = "SELECT * FROM fireAlcapa WHERE username='" + username+"';";
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql);
		totalScore = rs.getInt("score");
		totalScore += score;
		totalCoins = rs.getInt("coins");
		totalCoins += coins;
		level = rs.getInt("levelnumber");
		if(currentLevel >= level){
			level = currentLevel;
		}
		int attack = rs.getInt("attack");
		int health = rs.getInt("health");
		rs.close();
		st.close();
		sql = "DELETE FROM GameRecord WHERE username='"+username+"';";
		pst = c.prepareStatement(sql);
		pst.executeUpdate();
		pst.close();	
		sql = "INSERT INTO GameRecord VALUES("+username+","+level+","+totalScore+","+totalCoins+attack+","+health+");";
		pst = c.prepareStatement(sql);
		pst.executeUpdate();
		pst.close();
	}
	
	public boolean login(String username, String password) throws SQLException {
		boolean result = false;
		String sql;
		sql = "SELECT COUNT(username) FROM Profile WhERE username='" + username + "';";
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql);
		int number = rs.getInt("count");
		if (number != 0) {
		sql = "SELECT password FROM Profile WhERE username='" + username + "';";
		Statement st1 = c.createStatement();
		ResultSet rs1 = st1.executeQuery(sql);
		if (password == rs1.getNString("password")){
			sql = "SELECT * FROM fireAlcapa WHERE username='" + username+"';";
			Statement newst = c.createStatement();
			ResultSet newrs = newst.executeQuery(sql);
			level = newrs.getInt("levelnumber");
			totalScore = newrs.getInt("score");
			totalCoins = newrs.getInt("coins");
			attack = newrs.getInt("attack");
			health = newrs.getInt("health");
			result = true;
		}
		rs1.close();
		st1.close();
		}
		rs.close();
		st.close();
		return result;
	}
	
	public boolean register(String username, String firstname, String surname, String email, String password) throws SQLException {
		boolean result = false;
		String sql;
		PreparedStatement pst;
		Statement st = c.createStatement();
		sql = "SELECT COUNT(username) FROM Profile WhERE username='" + username + "';";
		ResultSet rs = st.executeQuery(sql);
		int number = rs.getInt("count");
		if (number == 0) {
			sql = "INSERT INTO profile VALUES('"+username+"','"+firstname+"','"+surname+"','"+email+"','"+password+"');";
			pst = c.prepareStatement(sql);
			pst.executeUpdate();
			pst.close();
			sql = "INSERT INTO gameRecord VALUES('"+username+"',1,0,0,1,1);";
			pst = c.prepareStatement(sql);
			pst.executeUpdate();
			pst.close();
			result = true;
		}
		rs.close();
		st.close();
		return result;
	}
	
	public void closeConnection(){
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
public static DatabaseManager getInstance() { 
		
		return INSTANCE; 
		
	}
	
}
