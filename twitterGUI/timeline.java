package twitterGUI;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class timeline extends JFrame {

	  Statement stmt=null;
      ResultSet rs=null;
      PreparedStatement pstm=null;
      String sqlStat=null;
      String op=null;
      
	private static final long serialVersionUID = 1L;
	private JPanel timeLine;



	/**
	 * Create the frame.
	 */
	public timeline() {
		
		//window title
				super("Timeline");
				
				//setting window size& close operation
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setBounds(100, 100, 583, 704);
				
				//panel setting- timeline
				timeLine = new JPanel();
				timeLine.setBackground(new Color(210, 218, 234));
				timeLine.setBorder(new EmptyBorder(5, 5, 5, 5));
				timeLine.setLayout(new BoxLayout(timeLine, BoxLayout.Y_AXIS));
				JScrollPane scrollPane = new JScrollPane(timeLine);
				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				setContentPane(scrollPane);
				
				JMenuBar menuBar = new JMenuBar();
				scrollPane.setColumnHeaderView(menuBar);
				
				//show board, timeline
				JMenu Show_menu = new JMenu("Show");
				menuBar.add(Show_menu);
				
				//show timeline
				JMenuItem timeLine_MenuItem = new JMenuItem("Show timeline");
				timeLine_MenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						try {
							showTimeline();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}
				});
				Show_menu.add(timeLine_MenuItem);
				
				//show my board
				JMenuItem myBoard_MenuItem = new JMenuItem("Show my board");
				myBoard_MenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						//profile window
							new profile();
							setVisible(false);
					
					}
				});
				Show_menu.add(myBoard_MenuItem);
				
				//show other board
				JMenuItem otherBoardMenuItem = new JMenuItem("Show other user's board");
				otherBoardMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							
							String wantId;
		                        wantId=JOptionPane.showInputDialog(null,"Enter User ID you want to see", "Show other user's board", JOptionPane.QUESTION_MESSAGE);
		                        //check is it existing user?

		                        boolean exists = false;
		                        //prepare sql
		                        sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
		                        try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
		                            ps.setString(1, wantId);
		                            rs = ps.executeQuery();
		                            exists = rs.next();
		                        }
		                        if(exists) {
		                        	//profile window
									new OtherBoard(wantId);
									setVisible(false);
		                        }
		                        else JOptionPane.showMessageDialog(null,"This ID does not exist", "ERROR", JOptionPane.ERROR_MESSAGE);
							
							
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				Show_menu.add(otherBoardMenuItem);
				
				//search 
				JMenu Search_Menu = new JMenu("Search");
				menuBar.add(Search_Menu);
				
				JMenuItem search_menuItem = new JMenuItem("Search post/comment");
				search_menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						  String searchId=null;
		                    String searchWord=null;
		            
		                    search(searchId,searchWord);
		                    
		                   
		                    
		                    
					}
				});
				Search_Menu.add(search_menuItem);
				
				//DM
				JMenu DM_menu = new JMenu("DM");
				menuBar.add(DM_menu);
				
				JMenu follow_menu = new JMenu("Follow");
				menuBar.add(follow_menu);
				
				JMenuItem follow_MenuItem = new JMenuItem("Follow/Unfollow");
				
				//follow/unfollow mechanism
				follow_MenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						 String followedId=null;
		                  
		                    followedId=JOptionPane.showInputDialog(null,"Enter User ID you want to follow/unfollow", "Follow/Unfollow", JOptionPane.QUESTION_MESSAGE);

		                    //self-follow check
		                    if (followedId.equals(TwitterMain.userId)) {
		                    	JOptionPane.showMessageDialog(null,"You can't follow yourself", "ERROR", JOptionPane.ERROR_MESSAGE);
		                        return;
		                    }

		                    try {
		                        //check user exists
		                        sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
		                        try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
		                            ps.setString(1, followedId);
		                            try (ResultSet rs = ps.executeQuery()) {
		                                if (!rs.next()) {
		                                	JOptionPane.showMessageDialog(null,"This ID does not exist", "ERROR", JOptionPane.ERROR_MESSAGE);
		                                    return;
		                                }
		                            }
		                        }

		                        //check follow status
		                        sqlStat = "SELECT Followed_ID FROM Follow WHERE Following_ID = ? AND Followed_ID = ?";
		                        boolean alreadyFollowing;
		                        try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
		                            ps.setString(1, TwitterMain.userId);
		                            ps.setString(2, followedId);
		                            try (ResultSet rs = ps.executeQuery()) {
		                                alreadyFollowing = rs.next();
		                            }
		                        }

		                        //toggle follow / unfollow
		                        if (alreadyFollowing) {
		                          

		                            sqlStat = "DELETE FROM Follow WHERE Following_ID = ? AND Followed_ID = ?";
		                            try (PreparedStatement del = TwitterMain.conn.prepareStatement(sqlStat)) {
		                                del.setString(1, TwitterMain.userId);
		                                del.setString(2, followedId);
		                                del.executeUpdate();
		                            }
		                            JOptionPane.showMessageDialog(null,"Unfollow Success!", "Unfollow", JOptionPane.INFORMATION_MESSAGE);
		                        } else {
		                            

		                            sqlStat = "INSERT INTO Follow (F_ID, Following_ID, Followed_ID) " +
		                                    "VALUES (NULL, ?, ?)";
		                            try (PreparedStatement ins = TwitterMain.conn.prepareStatement(sqlStat)) {
		                                ins.setString(1, TwitterMain.userId);
		                                ins.setString(2, followedId);
		                                ins.executeUpdate();
		                            }

		                            JOptionPane.showMessageDialog(null,"Follow Success!", "Follow", JOptionPane.INFORMATION_MESSAGE);
		                        }

		                    } catch (SQLException e1) {
		                        e1.printStackTrace();
		                    }
					}
				});
				follow_MenuItem.setHorizontalAlignment(SwingConstants.CENTER);
				follow_menu.add(follow_MenuItem);
				
				//show follower/following
				JMenuItem showFollow_MenuItem = new JMenuItem("Show Follower/Following list ");
				showFollow_MenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						 String wantShowFollowId=null;
						 wantShowFollowId=JOptionPane.showInputDialog(null,"Enter User ID you want to see follower/following list", "Following/Follower", JOptionPane.QUESTION_MESSAGE);
						 
						    try {
				                 // match check1: exist user?
				                 sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
				                 try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
				                     ps.setString(1, wantShowFollowId);
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
				                     ps.setString(1, wantShowFollowId);
				                     try (ResultSet rs = ps.executeQuery()) {
				                         while (rs.next()) {
				                        	 Follow.Follower.addElement(rs.getString(1));
				                             
				                         }
				                         
				                     }
				                 }

				                 // following list
				                 sqlStat = "SELECT Followed_ID FROM Follow WHERE Following_ID = ?";
				                 try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
				                     ps.setString(1, wantShowFollowId);
				                     try (ResultSet rs = ps.executeQuery()) {
				                         while (rs.next()) {
				                        	 Follow.Following.addElement(rs.getString(1));
				                         }
				                       
				                     }
				                 }
				                 
				                 //display
				                 new Follow();
				                 

				             } catch (SQLException e1) {
				                 e1.printStackTrace();
				             }


						
					}
				});
				follow_menu.add(showFollow_MenuItem);
				
				//logout menu
				JMenu logout_menu = new JMenu("Log-out");
				menuBar.add(logout_menu);
				
				JMenuItem Logout_menuItem = new JMenuItem("Log-out");
				Logout_menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TwitterMain.login=false;
						TwitterMain.userId=null;
						TwitterMain.userPw=null;
						 JOptionPane.showMessageDialog(null,"Log-out Success!", "Logout", JOptionPane.INFORMATION_MESSAGE);
						new LoginSignup();
						setVisible(false);
						
					}
				});
				logout_menu.add(Logout_menuItem);
				
				
			   
			  
				
				//visible window
				setVisible(true);

				

	}

	
	
	void search(String searchId,String searchWord) {
		
		 searchId=JOptionPane.showInputDialog(null,"Enter User ID you want to search its Post&Comment", "Search", JOptionPane.QUESTION_MESSAGE);
         searchWord=JOptionPane.showInputDialog(null,"Enter Word you want to search", "Search", JOptionPane.QUESTION_MESSAGE);
         
         try {
             // match check1: exist user?
             sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
             try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                 ps.setString(1, searchId);
                 try (ResultSet rs = ps.executeQuery()) {
                     if (!rs.next()) {
                     	JOptionPane.showMessageDialog(null,"This ID does not exist", "ERROR", JOptionPane.ERROR_MESSAGE);
                         return;
                     }
                 }
             }

             // make search string operation
             String searchOp = "%" + searchWord + "%";

             // show search result in latest order

             sqlStat =
                     "SELECT * FROM ( \r\n" +
                             "  (SELECT Text, Writer_ID, Date_time, P_ID, \"P\" " +
                             "     FROM Posts WHERE Text LIKE ? AND Writer_ID = ?) \r\n" +
                             "  UNION \r\n" +
                             "  (SELECT Text, Commenter_ID, Date_time, C_ID, \"C\" " +
                             "     FROM Comments WHERE Text LIKE ? AND Commenter_ID = ?) \r\n" +
                             ") AS lpc \r\n" +
                             "ORDER BY lpc.Date_time DESC";

             try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
                 ps.setString(1, searchOp);  // Posts.TEXT LIKE ?
                 ps.setString(2, searchId);  // Posts.Writer_ID = ?
                 ps.setString(3, searchOp);  // Comments.TEXT LIKE ?
                 ps.setString(4, searchId);  // Comments.Commenter_ID = ?

                 try (ResultSet rs = ps.executeQuery()) {
                     String Text = null;
                     String Writer_ID = null;
                     String Date_time = null;
                     String Tid = null;
                     String TextType = null;
                     int Like = 0;
                     int Repost = 0;
                     int Comment=0;

                     while (rs.next()) {
                         Text = rs.getString(1);
                         Writer_ID = rs.getString(2);
                         Date_time = rs.getString(3);
                         Tid = rs.getString(4);
                         TextType = rs.getString(5);

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
                         
                         
                         // aggregate like
                         Like = 0;
                         if (TextType.equalsIgnoreCase("P")) {
                             String likeSql = "SELECT COUNT(User_ID) FROM Post_like WHERE P_ID = ?";
                             try (PreparedStatement likePs = TwitterMain.conn.prepareStatement(likeSql)) {
                                 likePs.setInt(1, Integer.parseInt(Tid));
                                 try (ResultSet rs2 = likePs.executeQuery()) {
                                     if (rs2.next()) Like = rs2.getInt(1);
                                 }
                             }
                         } else if (TextType.equalsIgnoreCase("C")) {
                             String likeSql = "SELECT COUNT(User_ID) FROM Comment_like WHERE C_ID = ?";
                             try (PreparedStatement likePs = TwitterMain.conn.prepareStatement(likeSql)) {
                                 likePs.setInt(1, Integer.parseInt(Tid));
                                 try (ResultSet rs2 = likePs.executeQuery()) {
                                     if (rs2.next()) Like = rs2.getInt(1);
                                 }
                             }
                         }

                         // aggregate repost
                         Repost = 0;
                         if (TextType.equalsIgnoreCase("P")) {
                             String repostSql = "SELECT COUNT(User_ID) FROM Repost_post WHERE P_ID = ?";
                             try (PreparedStatement rpPs = TwitterMain.conn.prepareStatement(repostSql)) {
                                 rpPs.setInt(1, Integer.parseInt(Tid));
                                 try (ResultSet rs3 = rpPs.executeQuery()) {
                                     if (rs3.next()) Repost = rs3.getInt(1);
                                 }
                             }
                         } else if (TextType.equalsIgnoreCase("C")) {
                             String repostSql = "SELECT COUNT(User_ID) FROM Repost_comment WHERE C_ID = ?";
                             try (PreparedStatement rpPs = TwitterMain.conn.prepareStatement(repostSql)) {
                                 rpPs.setInt(1, Integer.parseInt(Tid));
                                 try (ResultSet rs3 = rpPs.executeQuery()) {
                                     if (rs3.next()) Repost = rs3.getInt(1);
                                 }
                             }
                         }

                         
                         //display
                         timeLine.add(new TextFormat(Text, "@"+Writer_ID, Date_time, ""+Comment, ""+Repost, ""+Like, TextType,Tid, false, false));
               		   timeLine.revalidate();
               		    timeLine.repaint();
                     }
                 }
             }

             

         } catch (SQLException e) {
             e.printStackTrace();
         }


		
	}
	
	//showTimeline
	void showTimeline() throws Exception{
		    
		    
		    stmt=TwitterMain.conn.createStatement();
            sqlStat="SELECT * FROM (\r\n"
                    + "(SELECT Text, Writer_ID,Date_time,P_ID,\"P\" FROM Posts WHERE Writer_ID=\""+TwitterMain.userId+"\") \r\n"
                    + "UNION \r\n"
                    + "(SELECT Text, Commenter_ID,Date_time,C_ID,\"C\" FROM Comments WHERE commenter_ID=\""+TwitterMain.userId+"\")\r\n"
                    + "UNION\r\n"
                    + "(SELECT Text, Writer_ID,Date_time,P_ID,\"P\" FROM Posts INNER JOIN Follow ON Posts.Writer_ID=Follow.Followed_ID WHERE Following_ID=\""+TwitterMain.userId+"\") \r\n"
                    + "UNION\r\n"
                    + "(SELECT Text, Commenter_ID,Date_time,C_ID,\"C\" FROM Comments INNER JOIN Follow ON Comments.Commenter_ID=Follow.Followed_ID WHERE Following_ID=\""+TwitterMain.userId+"\") \r\n"
                    + ")\r\n"
                    + "AS mfpc\r\n"
                    + "ORDER BY mfpc.DATE_time DESC";
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
                
                timeLine.add(new TextFormat(Text, "@"+Writer_ID, Date_time, ""+Comment, ""+Repost, ""+Like, TextType,Tid, false, false));
     		   timeLine.revalidate();
     		    timeLine.repaint();
                
	}
            
	}
	
	
	
	
	
}
