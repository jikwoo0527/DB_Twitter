package twitterGUI;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.ImageIcon;

public class TextFormat extends JPanel {

	String type; //post? comment?
	String TID; //text ID
	public boolean already_repost;
	public boolean already_like;
	private static final long serialVersionUID = 1L;
	private JButton like_button;
	private JButton repost_button;
	JLabel repost_label;
	JLabel like_label;

	/**
	 * Create the panel.
	 * @throws SQLException 
	 */
	
	public TextFormat(String text, String id, String date,String comment,String repost,String like,String type,String TID,boolean already_repost, boolean already_like) throws SQLException {
		
		this.type=type;
		this.TID=TID;
		this.already_repost=already_repost;
		this.already_like=already_like;
		
		
		setBackground(new Color(215, 238, 255));
		setForeground(new Color(215, 238, 255));
		
		//setting panel size
		//setBounds(100, 100, 550, 300);
		setLayout(null);
		setPreferredSize(new Dimension(540, 300));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
		//panel effect
		LineBorder tllb=new LineBorder(new Color(69, 100, 152),2,true);
		setBorder(tllb);
		
		//post,comment textarea
		JTextArea text_textArea = new JTextArea();
		text_textArea.setBounds(12, 73, 516, 170);
		text_textArea.setText(text);
		add(text_textArea);
		
		text_textArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
		
		//userImg button
		JButton userImg_Button = new JButton("");
		userImg_Button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/user.png")));
		userImg_Button.setBounds(12, 18, 62, 45);
		add(userImg_Button);

		
		//repost button
		repost_button = new JButton("");
		
		String sqlStat;
		  //check already repost
        if (type.equalsIgnoreCase("P")) {
            sqlStat = "SELECT User_ID FROM Repost_post WHERE P_ID = ? AND User_ID = ?";
        } else { // C
            sqlStat = "SELECT User_ID FROM Repost_comment WHERE C_ID = ? AND User_ID = ?";
        }

        boolean alreadyReposted;
        try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
            ps.setInt(1, Integer.parseInt(TID));
            ps.setString(2, TwitterMain.userId);
            try (ResultSet rs = ps.executeQuery()) {
                alreadyReposted = rs.next();
            }
            
            if (alreadyReposted) already_repost=true;
        }
            
		if(already_repost)repost_button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/full_repost.png")));
		else repost_button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/empty_repost.png")));
		
		//if click?
		repost_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iconChange(1);
			}
		});
		repost_button.setBounds(205, 253, 47, 37);
		add(repost_button);
		
		//like button
		like_button = new JButton("");
		
		
		//check already like
		boolean alreadyLike=false;
        if (type.equalsIgnoreCase("P")) {
            sqlStat = "SELECT User_ID FROM Post_like WHERE P_ID = ? AND User_ID = ?";
        } else { //C
            sqlStat = "SELECT User_ID FROM Comment_like WHERE C_ID = ? AND User_ID = ?";
        }

        try (PreparedStatement ps1 = TwitterMain.conn.prepareStatement(sqlStat)) {
            ps1.setInt(1, Integer.parseInt(TID));
            ps1.setString(2, TwitterMain.userId);

            try (ResultSet rs = ps1.executeQuery()) {
            	alreadyLike=rs.next();
            }
		
            if (alreadyLike) already_like=true;
        }
        
		if(already_like)like_button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/full_heart.png")));
		else like_button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/empty_heart.png")));
		
		//if click?
		like_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iconChange(2);
			}
		});
		
		
		like_button.setBounds(375, 253, 47, 37);
		add(like_button);
		
		//comment button
		JButton comment_button = new JButton("");
		comment_button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/comment.png")));
		comment_button.setBounds(48, 253, 47, 37);
		add(comment_button);
		
		//datetime label
		JLabel timestamp_label = new JLabel("New label");
		timestamp_label.setBounds(359, 21, 179, 29);
		timestamp_label.setText(date);
		add(timestamp_label);
		
		//id label
		JLabel id_label = new JLabel("New label");
		id_label.setBounds(86, 21, 112, 29);
		id_label.setText(id);
		add(id_label);
		
		//comment num label
		JLabel comment_label = new JLabel("New label");
		comment_label.setBounds(99, 264, 34, 29);
		comment_label.setText(comment);
		add(comment_label);
		
		//repost num label
		 repost_label = new JLabel("New label");
		repost_label.setBounds(258, 264, 34, 29);
		repost_label.setText(repost);
		add(repost_label);
		
		//like num label
		like_label = new JLabel("New label");
		like_label.setBounds(427, 264, 34, 29);
		like_label.setText(like);
		add(like_label);
		
		//TID label
		JLabel TID_label = new JLabel("");
		TID_label.setBounds(270, 21, 71, 29);
		TID_label.setText(TID);
		add(TID_label);
		
		JLabel textType_label = new JLabel("<dynamic>");
		textType_label.setBounds(182, 21, 71, 29);
		textType_label.setText(type);
		add(textType_label);
		
	
		
		
	}
	//1-> repost, 2->heart
	void iconChange(int sel) {
		
		String sqlStat;
		
	
		if(sel==1) {
			

            //can unrepost
            if (already_repost) {
    

                if (type.equalsIgnoreCase("P")) {
                    sqlStat = "DELETE FROM Repost_post WHERE P_ID = ? AND User_ID = ?";
                } else {
                    sqlStat = "DELETE FROM Repost_comment WHERE C_ID = ? AND User_ID = ?";
                }

                try (PreparedStatement del = TwitterMain.conn.prepareStatement(sqlStat)) {
                    del.setInt(1, Integer.parseInt(TID));
                    del.setString(2, TwitterMain.userId);
                    del.executeUpdate();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

                this.already_repost=false;
				repost_button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/empty_repost.png")));
				repost_label.setText(""+(Integer.parseInt(repost_label.getText())-1));
                JOptionPane.showMessageDialog(null,"Unrepost Success!", "Unrepost", JOptionPane.INFORMATION_MESSAGE);
            }

            //can repost
            else {
 

                if (type.equalsIgnoreCase("P")) {
                    sqlStat = "INSERT INTO Repost_post (RT_ID, P_ID, User_ID) VALUES (NULL, ?, ?)";
                } else {
                    sqlStat = "INSERT INTO Repost_comment (RT_ID, C_ID, User_ID) VALUES (NULL, ?, ?)";
                }

                try (PreparedStatement ins = TwitterMain.conn.prepareStatement(sqlStat)) {
                    ins.setInt(1,Integer.parseInt(TID));
                    ins.setString(2, TwitterMain.userId);
                    ins.executeUpdate();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                this.already_repost=true;
				repost_button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/full_repost.png")));
				repost_label.setText(""+(Integer.parseInt(repost_label.getText())+1));
                JOptionPane.showMessageDialog(null,"Repost Success!", "Repost", JOptionPane.INFORMATION_MESSAGE);
            }
                
            }
            
		
		else {

	        
	               
					// can unlike
	                if (already_like) {
	                    
	                    if (type.equalsIgnoreCase("P")) {
	                        sqlStat = "DELETE FROM Post_like WHERE P_ID = ? AND User_ID = ?";
	                    } else {
	                        sqlStat = "DELETE FROM Comment_like WHERE C_ID = ? AND User_ID = ?";
	                    }

	                    try (PreparedStatement del = TwitterMain.conn.prepareStatement(sqlStat)) {
	                        del.setInt(1, Integer.parseInt(TID));
	                        del.setString(2, TwitterMain.userId);
	                        del.executeUpdate();
	                    }
	                    catch (SQLException e) {
	        	            e.printStackTrace();
	        	        }
	                    this.already_like=false;
	    				like_button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/empty_heart.png")));
	    				like_label.setText(""+(Integer.parseInt(like_label.getText())-1));
	                    JOptionPane.showMessageDialog(null,"Unlike Success!", "Unlike", JOptionPane.INFORMATION_MESSAGE);
	                    
	                }

	                // can like
	                else {
	                 

	                    if (type.equalsIgnoreCase("P")) {
	                        sqlStat = "INSERT INTO Post_like (PL_ID, P_ID, User_ID) VALUES (NULL, ?, ?)";
	                    } else {
	                        sqlStat = "INSERT INTO Comment_like (CL_ID, C_ID, User_ID) VALUES (NULL, ?, ?)";
	                    }

	                    try (PreparedStatement ins = TwitterMain.conn.prepareStatement(sqlStat)) {
	                        ins.setInt(1, Integer.parseInt(TID));
	                        ins.setString(2, TwitterMain.userId);
	                        ins.executeUpdate();
	                    }
	                    catch (SQLException e) {
	        	            e.printStackTrace();
	        	        }
	                    this.already_like=true;
	    				like_button.setIcon(new ImageIcon(TextFormat.class.getResource("/twitterGUI/full_heart.png")));
	    				like_label.setText(""+(Integer.parseInt(like_label.getText())+1));
	                    JOptionPane.showMessageDialog(null,"Like Success!", "Like", JOptionPane.INFORMATION_MESSAGE);
	                }
	            }
		
		
	}
	
}
	
