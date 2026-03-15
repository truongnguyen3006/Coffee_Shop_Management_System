package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import application.util.AppLogger;

public class Database {
	private static final Logger LOGGER = AppLogger.getLogger(Database.class);

	private Database() {
	}

	public static Connection connect() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = AppConfig.get("db.url");
			String user = AppConfig.get("db.username");
			String password = AppConfig.get("db.password");
			return DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			AppLogger.error(LOGGER, "Không tìm thấy MySQL JDBC Driver", e);
			throw new SQLException("Không tìm thấy MySQL JDBC Driver", e);
		}
	}

	public static void closeConnect(Connection connect) {
		if (connect != null) {
			try {
				connect.close();
			} catch (SQLException e) {
				AppLogger.error(LOGGER, "Đóng kết nối database thất bại", e);
			}
		}
	}
}
