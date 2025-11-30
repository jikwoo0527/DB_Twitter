package twitterGUI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Signup extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField PW_textField;
	private JTextField email_textField;
	private JButton signup_button;
	private JTextField pn_textField;
	private JTextField name_textField;
	private JTextField ID_textField;
	private JButton back2Main_button;

	
	  //setting
    Statement stmt=null;
    PreparedStatement pstm=null;
    String sqlStat=null;
    String op=null;

	
	/**
	 * Create the frame.
	 */
	public Signup() {
	
			
			//window title
			super("Signup");
					
			//setting window size& close operation
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 583, 704);
					
			//panel setting
			contentPane = new JPanel();
			contentPane.setBackground(new Color(210, 218, 234));
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			
			
			
			//Signup label
			JLabel lblNewLabel = new JLabel("Sign-up");
			lblNewLabel.setBounds(78, 60, 419, 110);
			lblNewLabel.setForeground(new Color(69, 100, 152));
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 55));
			contentPane.add(lblNewLabel);

			
			//id label
			JLabel ID_label = new JLabel("ID");
			ID_label.setHorizontalAlignment(SwingConstants.RIGHT);
			ID_label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
			ID_label.setBounds(148, 219, 37, 38);
			ID_label.setForeground(new Color(69, 100, 152));
			contentPane.add(ID_label);
			
			
			//password label
			JLabel PW_label = new JLabel("Password");
			PW_label.setHorizontalAlignment(SwingConstants.RIGHT);
			PW_label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
			PW_label.setBounds(78, 275, 107, 38);
			PW_label.setForeground(new Color(69, 100, 152));
			contentPane.add(PW_label);
			
			//email label
			JLabel email_label = new JLabel("Email");
			email_label.setHorizontalAlignment(SwingConstants.RIGHT);
			email_label.setForeground(new Color(69, 100, 152));
			email_label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
			email_label.setBounds(78, 328, 107, 38);
			contentPane.add(email_label);
			
			//phone number label
			JLabel pn_label = new JLabel("Phone number");
			pn_label.setHorizontalAlignment(SwingConstants.RIGHT);
			pn_label.setForeground(new Color(69, 100, 152));
			pn_label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
			pn_label.setBounds(36, 376, 149, 38);
			contentPane.add(pn_label);
			
			//name label
			JLabel name_label = new JLabel("Name");
			name_label.setHorizontalAlignment(SwingConstants.RIGHT);
			name_label.setForeground(new Color(69, 100, 152));
			name_label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
			name_label.setBounds(78, 427, 107, 38);
			contentPane.add(name_label);
						
			
			
			//id textfield
			ID_textField = new JTextField();
			ID_textField.setBounds(197, 221, 214, 41);
			contentPane.add(ID_textField);
			ID_textField.setColumns(10);
			//textField effect
			LineBorder idlb=new LineBorder(new Color(69, 100, 152),2,true);
			ID_textField.setBorder(idlb);
			
			
			//pw textfield
			PW_textField = new JTextField();
			PW_textField.setBounds(197, 272, 214, 41);
			contentPane.add(PW_textField);
			PW_textField.setColumns(10);
			//textField effect
			LineBorder pwlb=new LineBorder(new Color(69, 100, 152),2,true);
			PW_textField.setBorder(pwlb);
			
			
			//email textfield
			email_textField = new JTextField();
			email_textField.setColumns(10);
			email_textField.setBounds(197, 325, 214, 41);
			contentPane.add(email_textField);
			//textField effect
			LineBorder emlb=new LineBorder(new Color(69, 100, 152),2,true);
			email_textField.setBorder(emlb);
			
			
			//phone number textfield
			pn_textField = new JTextField();
			pn_textField.setColumns(10);
			pn_textField.setBounds(197, 376, 214, 41);
			contentPane.add(pn_textField);
			//textField effect
			LineBorder pnlb=new LineBorder(new Color(69, 100, 152),2,true);
			pn_textField.setBorder(pnlb);
			
			//name textfield
			name_textField = new JTextField();
			name_textField.setColumns(10);
			name_textField.setBounds(197, 427, 214, 41);
			contentPane.add(name_textField);
			//textField effect
			LineBorder nalb=new LineBorder(new Color(69, 100, 152),2,true);
			name_textField.setBorder(nalb);
			
			
			
			//signup button
			signup_button = new JButton("Sign-up");
			signup_button.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
			signup_button.setForeground(new Color(69, 100, 152));
			//button effect
					LineBorder llb=new LineBorder(new Color(69, 100, 152),2,true);
					signup_button.setBorder(llb);
			
			//get text -> signup process
			signup_button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					int empty=0;
					
					//get new info
					String newId=ID_textField.getText();  if(newId.equals("")) empty++;
                    String newPw=PW_textField.getText(); if(newPw.equals("")) empty++;
                    String newEmail=email_textField.getText(); if(newEmail.equals("")) empty++;
                    String newPhoneNum=pn_textField.getText(); if(newPhoneNum.equals("")) empty++;
                    String newName=name_textField.getText(); if(newName.equals("")) empty++;
                  
                    
                    
					
					//signup process
                    if(empty==0) {
					try {
						signupProcess(newId,newPw,newEmail,newPhoneNum,newName);
					} catch (Exception e1) {
					}
					
                    }
                    //empty format?
                    else  JOptionPane.showMessageDialog(null,"Input entire required information!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    
					//go to main
					new LoginSignup();
					setVisible(false);
				}
			});
			signup_button.setBounds(239, 478, 128, 41);
			contentPane.add(signup_button);
			
			
			//back to main button
			back2Main_button = new JButton("Back to main");
			//back to main..
			back2Main_button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new LoginSignup();
					setVisible(false);
				}
			});
			back2Main_button.setBounds(12, 10, 140, 35);
			//button effect
			LineBorder btmlb=new LineBorder(new Color(69, 100, 152),1,true);
			back2Main_button.setBorder(btmlb);
			contentPane.add(back2Main_button);
				
			
			
			
			
			//visible window
			setVisible(true);
		
	}
	
	
	
	//signup process  accessSLC()'s variation
	void signupProcess(String newId,String newPw,String newEmail,String newPhoneNum,String newName)throws Exception {
		
		
		  //prepare sql
        sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
        try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
            ps.setString(1,newId);//bind user Id
            try (ResultSet rs = ps.executeQuery()) {
                // match check
                if (rs.next()) {
                	JOptionPane.showMessageDialog(null,"This ID already exist", "ERROR", JOptionPane.ERROR_MESSAGE);
                   //can sign up~
                    } else {
                    	
                    	//prepare sql
                        stmt=TwitterMain.conn.createStatement();
                        sqlStat="INSERT INTO User(User_ID , Password, Email , Phone_number, Name) VALUES(?,?,?,?,?)";
                        pstm=TwitterMain.conn.prepareStatement(sqlStat);
                        
                        //setting prepared sql
                        pstm.setString(1,newId);
                        pstm.setString(2,newPw);
                        pstm.setString(3,newEmail);
                        pstm.setString(4,newPhoneNum);
                        pstm.setString(5,newName);

                        //execute DML~
                        pstm.executeUpdate();

                        JOptionPane.showMessageDialog(null,"Sign up Success!", "Signup", JOptionPane.INFORMATION_MESSAGE);
                    	
                    }
            } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}
	
	}

}
