package twitterGUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;



public class LoginSignup extends JFrame {

	
	//setting
    Statement stmt=null;
    PreparedStatement pstm=null;
    String sqlStat=null;
    String op=null;
	
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;


	/**
	 * Create the frame.
	 */
	public LoginSignup() {
		
		//window title
		super("Welcome to Twitter");
		
		//setting window size& close operation
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 583, 704);
		
		//panel setting
		contentPane = new JPanel();
		contentPane.setBackground(new Color(210, 218, 234));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		//~~~login button~~~
		JButton btnLogin = new JButton("Log-in");
		btnLogin.setBounds(177, 231, 222, 69);
		//button effect
		LineBorder Li=new LineBorder(new Color(69, 100, 152),3,true);
		btnLogin.setBorder(Li);
		
		//if click -> new Window(Login)
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Login();
				setVisible(false);
				
			}
		});
		btnLogin.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 30));
		btnLogin.setForeground(new Color(69, 100, 152));
		contentPane.add(btnLogin);
		
		
		//~~~signup button~~~
		JButton btnSignup = new JButton("Sign-up");
		btnSignup.setBounds(177, 310, 222, 69);
		//button effect
		LineBorder Su=new LineBorder(new Color(69, 100, 152),3,true);
		btnSignup.setBorder(Su);
				
		//if click -> new Window(Signup)
		btnSignup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Signup();
				setVisible(false);
			}
		});
		btnSignup.setForeground(new Color(69, 100, 152));
		btnSignup.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 30));
		contentPane.add(btnSignup);
		
		//~~~change PW button~~~
		JButton btnChangePW = new JButton("Change password");
		//if click -> new popup(Change password)
		btnChangePW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//get ID&PW
				String inputID=JOptionPane.showInputDialog(null,"Enter ID", "Change password", JOptionPane.QUESTION_MESSAGE);
				String existPW=JOptionPane.showInputDialog(null,"Enter Existing Password", "Change password", JOptionPane.QUESTION_MESSAGE);
				
				 sqlStat = "SELECT Password FROM User WHERE User_ID = ?";
				 
				  try (PreparedStatement ps =  TwitterMain.conn.prepareStatement(sqlStat)) {
                      ps.setString(1, inputID);//bind user Id
                      try (ResultSet rs = ps.executeQuery()) {

                          if (rs.next()) {
                              String dbPw=rs.getString(1);
                              if (existPW.equals(dbPw)) {
                                  //if match, ask for new password
                                  String newPW=JOptionPane.showInputDialog(null,"Enter New Password(max 15)", "Change password", JOptionPane.QUESTION_MESSAGE);
                                  
                                  // check password length
                                  if (newPW == null) {
                                      return;
                                  }
                                  if (newPW.length() > 15) {
                                      JOptionPane.showMessageDialog(null, "New Password is too wrong", "ERROR", JOptionPane.ERROR_MESSAGE);
                                      return;
                                  }

                                  //update password
                                  String updateSql="UPDATE User SET Password = ? WHERE User_ID = ?";
                                  try (PreparedStatement ups= TwitterMain.conn.prepareStatement(updateSql)) {
                                      ups.setString(1, newPW);
                                      ups.setString(2, inputID);
                                      ups.executeUpdate();
                                  }
                                  JOptionPane.showMessageDialog(null,"Change password Success!", "Change password", JOptionPane.INFORMATION_MESSAGE);
                              } else {
                            	  JOptionPane.showMessageDialog(null,"Existing Password is wrong", "ERROR", JOptionPane.ERROR_MESSAGE);
                              }
                          } else {
                        	  JOptionPane.showMessageDialog(null,"This ID does not exist", "ERROR", JOptionPane.ERROR_MESSAGE);
                          }
                      }
                  } catch (SQLException e1) {
                      e1.printStackTrace();
                  }
				
				
				
			}
		});
		btnChangePW.setForeground(new Color(69, 100, 152));
		btnChangePW.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
		btnChangePW.setBounds(156, 389, 260, 33);
		//button effect
				LineBorder cp=new LineBorder(new Color(69, 100, 152),3,true);
				btnChangePW.setBorder(cp);
		contentPane.add(btnChangePW);
		
		JLabel info_label = new JLabel("Team_10");
		info_label.setHorizontalAlignment(SwingConstants.RIGHT);
		info_label.setFont(new Font("굴림", Font.BOLD, 20));
		info_label.setForeground(new Color(255, 255, 255));
		info_label.setBounds(435, 633, 122, 24);
		contentPane.add(info_label);
		
		JLabel lblNewLabel_1 = new JLabel("TWITTER");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setForeground(new Color(69, 100, 152));
		lblNewLabel_1.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 55));
		lblNewLabel_1.setBounds(76, 60, 419, 110);
		contentPane.add(lblNewLabel_1);
		
		//visible window
		setVisible(true);

	}
}
