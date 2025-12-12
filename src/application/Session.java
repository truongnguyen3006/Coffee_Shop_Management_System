package application;

public class Session {
	private static int userID;
	private static String userName;
	
    public static void setUserID(int id) {
        userID = id;
    }

    public static int getUserID() {
        return userID;
    }
     
    public static void setUserName(String name) {
        userName = name;
    }

    public static String getUserName() {
        return userName;
    }
    
}
