package twitterGUI;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class profile extends JFrame {

	  Statement stmt=null;
      ResultSet rs=null;
      PreparedStatement pstm=null;
      String sqlStat=null;
      String op=null;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel myBoard;
	


	/**
	 * Create the frame.
	 */
	public profile() {
		//window title
		super("My board");
		
		//setting window size& close operation
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 583, 704);
		
		//panel setting- myBoard
		myBoard = new JPanel();
		myBoard.setBackground(new Color(210, 218, 234));
		myBoard.setBorder(new EmptyBorder(5, 5, 5, 5));
		myBoard.setLayout(new BoxLayout(myBoard, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(myBoard);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setContentPane(scrollPane);
		
		JMenuBar menuBar = new JMenuBar();
		scrollPane.setColumnHeaderView(menuBar);
		
		//back
		JMenu Back_Menu = new JMenu("Back");
		menuBar.add(Back_Menu);
		
		JMenuItem back_MenuItem = new JMenuItem("Back to main");
		back_MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new timeline();
				setVisible(false);
			}
		});
		Back_Menu.add(back_MenuItem);
		
		//show follower,following
		JMenu show_Menu = new JMenu("Show");
		menuBar.add(show_Menu);
		
		
		JMenuItem followering_MenuItem = new JMenuItem("Show Follower/Following list ");
		followering_MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					seeFollowList();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		show_Menu.add(followering_MenuItem);
		
		//show my info
		JMenuItem info_MenuItem = new JMenuItem("Show my information");
		info_MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JOptionPane.showMessageDialog(null,"ID: "+TwitterMain.userId, "information", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		show_Menu.add(info_MenuItem);
		
		
		//write
		JMenu write_Menu = new JMenu("Write");
		menuBar.add(write_Menu);
		
		JMenuItem write_MenuItem = new JMenuItem("Write Post&Comment");
		write_MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					writeText();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		write_Menu.add(write_MenuItem);
		
		//latest display
		try {
			showMyBoard();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//visible window
		setVisible(true);
	}

	
	//showMyBoard
		void showMyBoard() throws Exception{
			
			 stmt=TwitterMain.conn.createStatement();
	         sqlStat="SELECT * FROM (\r\n"
	                 + "(SELECT Text, Writer_ID,Date_time,P_ID,\"P\" FROM Posts WHERE Writer_ID=\""+TwitterMain.userId+"\") \r\n"
	                 + "UNION \r\n"
	                 + "(SELECT Text, Commenter_ID,Date_time,C_ID,\"C\" FROM Comments WHERE Commenter_ID=\""+TwitterMain.userId+"\")\r\n"
	                 + ") AS mpc\r\n"
	                 + "ORDER BY mpc.DATE_time DESC";

	         rs=stmt.executeQuery(sqlStat);
	         //display text
	         String Text=null;
	         String Writer_ID=null;
	         String Date_time=null;
	         String Tid=null;
	         String TextType=null;
	         int Like=0;
	         int Repost=0;
	         int Comment=0;
	         while(rs.next()) {
	             Text=rs.getString(1);
	             Writer_ID=rs.getString(2);
	             Date_time=rs.getString(3);
	             Tid=rs.getString(4);
	             TextType=rs.getString(5);

	             //aggregate comment
	             stmt=TwitterMain.conn.createStatement();
	             //if Post?
	             if(TextType.equalsIgnoreCase("P")) {

	                 sqlStat="SELECT COUNT(C_ID) FROM Comments WHERE P_ID=\""+Tid+"\"";
	                 ResultSet rs1=stmt.executeQuery(sqlStat);
	                 if(rs1.next()) Comment=rs1.getInt(1);

	             }
	             //if Comment?
	             if(TextType.equalsIgnoreCase("C")) {

	                 sqlStat="SELECT COUNT(C_ID) FROM Comments WHERE Parent_C_ID=\""+Tid+"\"";
	                 ResultSet rs1=stmt.executeQuery(sqlStat);
	                 if(rs1.next()) Comment=rs1.getInt(1);

	             }
	             
	             
	             //aggregate like
	             stmt=TwitterMain.conn.createStatement();
	             //if Post?
	             if(TextType.equalsIgnoreCase("P")) {

	                 sqlStat="SELECT COUNT(User_ID) FROM Post_like WHERE P_ID=\""+Tid+"\"";
	                 ResultSet rs2=stmt.executeQuery(sqlStat);
	                 if(rs2.next()) Like=rs2.getInt(1);

	             }
	             //if Comment?
	             if(TextType.equalsIgnoreCase("C")) {

	                 sqlStat="SELECT COUNT(User_ID) FROM Comment_like WHERE C_ID=\""+Tid+"\"";
	                 ResultSet rs2=stmt.executeQuery(sqlStat);
	                 if(rs2.next()) Like=rs2.getInt(1);

	             }


	             //aggregate repost
	             stmt=TwitterMain.conn.createStatement();
	             //if Post?
	             if(TextType.equalsIgnoreCase("P")) {

	                 sqlStat="SELECT COUNT(User_ID) FROM Repost_post WHERE P_ID=\""+Tid+"\"";
	                 ResultSet rs3=stmt.executeQuery(sqlStat);
	                 if(rs3.next()) Repost=rs3.getInt(1);

	             }
	             //if Comment?
	             if(TextType.equalsIgnoreCase("C")) {

	                 sqlStat="SELECT COUNT(User_ID) FROM Repost_comment WHERE C_ID=\""+Tid+"\"";
	                 ResultSet rs3=stmt.executeQuery(sqlStat);
	                 if(rs3.next()) Repost=rs3.getInt(1);

	             }
	             
	             //display post
	             
	             myBoard.add(new TextFormat(Text, "@"+Writer_ID, Date_time, ""+Comment, ""+Repost, ""+Like, TextType,Tid, false, false));
	             myBoard.revalidate();
	             myBoard.repaint();
	  		    
	         }

		}
		
		//see following/follower
		void seeFollowList() throws Exception{
			

             try {
                 // match check1: exist user?
                 sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
                 try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                     ps.setString(1, TwitterMain.userId);
                     try (ResultSet rs = ps.executeQuery()) {
                         if (!rs.next()) {
                        	 JOptionPane.showMessageDialog(null,"This ID does not exist", "ERROR", JOptionPane.ERROR_MESSAGE);
                             return;
                         }
                     }
                 }

                 // show list

                 // follower list
                 sqlStat = "SELECT Following_ID FROM Follow WHERE Followed_ID = ?";
                 try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                     ps.setString(1, TwitterMain.userId);
                     try (ResultSet rs = ps.executeQuery()) {
                         while (rs.next()) {
                        	 Follow.Follower.addElement(rs.getString(1));
                             
                         }
                         
                     }
                 }

                 // following list
                 sqlStat = "SELECT Followed_ID FROM Follow WHERE Following_ID = ?";
                 try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                     ps.setString(1, TwitterMain.userId);
                     try (ResultSet rs = ps.executeQuery()) {
                         while (rs.next()) {
                        	 Follow.Following.addElement(rs.getString(1));
                         }
                       
                     }
                 }
                 
                 //display
                 new Follow();
                 

             } catch (SQLException e) {
                 e.printStackTrace();
             }

			
		}
		
		//writeText
		static void writeText() throws Exception{
			
			String sqlStat;
			
			  String line=null;
              String op2=null;
              op2=JOptionPane.showInputDialog(null,"Select task: 1.Write Post | 2.Write Comment", "Write Post&Comment", JOptionPane.QUESTION_MESSAGE);

              //write post
              if(op2.equals("1")) {
                  String postText="";
                  String subop=null;
                  String targetId=null;
                  subop=JOptionPane.showInputDialog(null,"Select task: 1. Write post on my board | 2.Write on following's board(@ID)", "Write Post", JOptionPane.QUESTION_MESSAGE);
                  
                  // line write -> concat \n -> save to text
                  int firstLine=0;

                  // if subop = 1 pass

                  //write on following's board
                  if (subop.equals("2")) {
                      targetId =JOptionPane.showInputDialog(null,"Enter following's ID", "Write Post", JOptionPane.QUESTION_MESSAGE);

                      //self-mention check
                      if (targetId.equals(TwitterMain.userId)) {
                      
                      	JOptionPane.showMessageDialog(null,"You can't write @message to yourself", "ERROR", JOptionPane.ERROR_MESSAGE);
                          return;
                      }

                      //check if actually following (query Follow table)
                      sqlStat = "SELECT Followed_ID FROM Follow " +
                              "WHERE Following_ID = ? AND Followed_ID = ?";
                      try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                          ps.setString(1, TwitterMain.userId);
                          ps.setString(2, targetId);
                          try (ResultSet rs = ps.executeQuery()) {
                              if (!rs.next()) {
          
                                  JOptionPane.showMessageDialog(null,"You are not following this user", "ERROR", JOptionPane.ERROR_MESSAGE);
                                  return;
                              }
                          }
                      }


                      //add @ prefix to ID
                      postText = "@" + targetId + " ";
                      firstLine = 1;
                  }

                     //write Text
                  String rcvText=JOptionPane.showInputDialog(null,"Write Post", "Write Post", JOptionPane.PLAIN_MESSAGE);
     
                      if(firstLine==0) {
                          postText=postText+rcvText;
                          firstLine=1;
                      }
                      else {
                          postText=postText+"\r\n";
                          postText=postText+rcvText;
                      }


                  

                  //send to server
                  sqlStat = "INSERT INTO Posts (Text, Writer_ID, Date_time) " +
                          "VALUES (?, ?, CURRENT_TIMESTAMP())";
                  try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                      ps.setString(1, postText);
                      ps.setString(2, TwitterMain.userId);
                      ps.executeUpdate();
                  }

                  JOptionPane.showMessageDialog(null,"Write Post Success!", "Write Post", JOptionPane.INFORMATION_MESSAGE);

              }

              //write comment
              else if(op2.equals("2")) {
                  String commentText="";
                  String textType=null;
                  int TID=0;
                  String writerOfTID=null;
                  int originalPostTID=0;

                
                  //find type
           
                  textType=JOptionPane.showInputDialog(null,"Enter Text Type you want to comment to(P:post C:comment)", "Write Comment", JOptionPane.QUESTION_MESSAGE);
                  TID=Integer.parseInt(JOptionPane.showInputDialog(null,"Enter TID you want to comment", "Write Comment", JOptionPane.QUESTION_MESSAGE));

                  //find writerOfTID
                  if (textType.equalsIgnoreCase("P")) { //comment to post
                      sqlStat = "SELECT Writer_ID FROM Posts WHERE P_ID = ?";
                      try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                          ps.setInt(1, TID);
                          try (ResultSet rs = ps.executeQuery()) {
                              if (rs.next()) {
                                  writerOfTID = rs.getString(1);
                              } else {
                            	  JOptionPane.showMessageDialog(null,"This TID does not exist", "ERROR", JOptionPane.ERROR_MESSAGE);
                                  return;
                              }
                          }
                      }
                  }
                  else if (textType.equalsIgnoreCase("C")) { //comment to comment
                      sqlStat = "SELECT Commenter_ID FROM Comments WHERE C_ID = ?";
                      try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                          ps.setInt(1, TID);
                          try (ResultSet rs = ps.executeQuery()) {
                              if (rs.next()) {
                                  writerOfTID = rs.getString(1);
                              } else {
                            	  JOptionPane.showMessageDialog(null,"This TID does not exist", "ERROR", JOptionPane.ERROR_MESSAGE);
                                  return;
                              }
                          }
                      }
                  } else {
                      JOptionPane.showMessageDialog(null,"Text Type must be P or C", "ERROR", JOptionPane.ERROR_MESSAGE);
                      return;
                  }



                  //comment type must have prefix @~
                  commentText=commentText+"@"+writerOfTID+" ";
                  // line write -> concat \n -> save to text
                  int firstLine=0;

                  
                  //write Text
                  String rcvText=JOptionPane.showInputDialog(null,"Write Comment", "Write Comment", JOptionPane.PLAIN_MESSAGE);
     
                      if(firstLine==0) {
                    	  commentText= commentText+rcvText;
                          firstLine=1;
                      }
                      else {
                    	  commentText=commentText+"\r\n";
                    	  commentText=commentText+rcvText;
                      }


                  


                  //send to server

                  //comment to post
                  if (textType.equalsIgnoreCase("P")) {
                      sqlStat = "INSERT INTO Comments " +
                              "(Text, Commenter_ID, P_ID, Parent_C_ID, Date_time) " +
                              "VALUES (?, ?, ?, 0, CURRENT_TIMESTAMP())";
                      try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                          ps.setString(1, commentText);
                          ps.setString(2, TwitterMain.userId);
                          ps.setInt(3, TID);
                          ps.executeUpdate();
                      }
                  }
                  //comment to comment
                  else if(textType.equalsIgnoreCase("C")) {
                      //find original post
                      while(true) {
                          sqlStat = "SELECT P_ID, Parent_C_ID FROM Comments WHERE C_ID = ?";
                          try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                              ps.setInt(1, TID);
                              try (ResultSet rs = ps.executeQuery()) {
                                  if (rs.next()) {
                                      int parentCid = rs.getInt(2);//if this is first comment?
                                      if (parentCid == 0) {
                                          originalPostTID = rs.getInt(1);
                                          break;
                                      } else {
                                          TID = parentCid; // go up to parent comment
                                      }
                                  } else {
                                	  JOptionPane.showMessageDialog(null,"This TID does not exist", "ERROR", JOptionPane.ERROR_MESSAGE);
                                      return;
                                  }
                              }
                          }
                      }


                      sqlStat = "INSERT INTO Comments " +
                              "(Text, Commenter_ID, P_ID, Parent_C_ID, Date_time) " +
                              "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP())";
                      try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                          ps.setString(1, commentText);
                          ps.setString(2, TwitterMain.userId);
                          ps.setInt(3, originalPostTID);
                          ps.setInt(4, TID); // parent comment ID
                          ps.executeUpdate();
                      }
                  }

                
                  JOptionPane.showMessageDialog(null,"Write Comment Success!", "Write Post&Comment", JOptionPane.INFORMATION_MESSAGE);
              }



			
		}
}
