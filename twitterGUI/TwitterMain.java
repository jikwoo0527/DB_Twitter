package twitterGUI;

import java.awt.EventQueue;
import java.sql.*;



//connection & core info& first window display

public class TwitterMain {

	
	   // user's info (used mostly)
    static String userId=null;
    static String userPw=null;


    //core components
    static Connection conn=null;
    static boolean login=false;
	
	
	
	public static void main(String[] args) throws Exception{
		
	    //connect to SQLserver
        conn=null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url="jdbc:mysql://localhost/twitter0";
            String user="root",passwd="0000";
            conn=DriverManager.getConnection(url,user,passwd);
            System.out.println(conn);
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
		
		
		
		//window display: LoginSignup
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					LoginSignup frame = new LoginSignup();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		
	 /*
        try {
            if(conn!=null && !conn.isClosed()) conn.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
		*/
		
	}

}
