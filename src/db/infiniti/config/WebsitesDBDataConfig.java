package db.infiniti.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WebsitesDBDataConfig {

	public static void main(String[] args) {
		Connection connection = null;

		String url = "jdbc:postgresql://teehuis.ewi.utwente.nl:5432/Vacancies";
		String username = "mohammad";
		String password = "4249324";
		try {
			System.out.println("Connecting database...");
			Class.forName("org.postgresql.Driver");
			if (connection == null) {
				connection = (Connection) DriverManager.getConnection(url,
						username, password);
				System.out.println("Database connected!");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Cannot connect the database!", e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Statement s;
		ResultSet rs = null;
		try {
			String query = "INSERT INTO \"Websites\"(\"name\", description, url, comments, \"template\", item_xp, link_xp, title_xp, description_xp, thumb_xp, status)    " +
			 		"VALUES (" +
			 		"'w3schools.com', " +
			 		"'ICT job vacany in dutch', " +
			 		"'http://www.ictergezocht.nl/', " +
			 		"'', " +
			 		"'http://www.ictergezocht.nl/it-vacatures/?what={query}&where=&r=&storesearch=1&submit=Zoek+Vacatures', " +
			 		"'//div[@id=\"jobresults\"]/div[./ul/li/a]'," +
			 		"'?',  " +
			 		"'?', " +
			 		"'?', " +
			 		"'?', " +
			 		"'?')";
			s = connection.createStatement();
			rs = s.executeQuery(query);
			
/*			UPDATE "Websites"
			SET link_xp = '//div[@id=\"jobresults\"]/div/ul/li/a'
			Where link_xp like '?'*/
			
			
			// s.executeQuery("SELECT table_name FROM information_schema.tables  WHERE table_schema='deb52794_fedweb'");//SELECT
			// * FROM INFORMATION_SCHEMA.TABLES WHERE table_schema =
			// 'deb52794_fedweb'
			//s.executeQuery("SELECT * FROM engine WHERE status LIKE 'OK'");
			// s.execute();
			// s.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'engine';");
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
