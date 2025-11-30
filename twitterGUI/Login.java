package twitterGUI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField ID_textField;
	private JTextField PW_textField;
	private JButton Login_button;
    private JButton back2Main_button;
	  //setting
    Statement stmt=null;
    PreparedStatement pstm=null;
    String sqlStat=null;
    String op=null;



	/**
	 * Create the frame.
	 */
	public Login() {
		
		//window title
		super("Login");
				
		//setting window size& close operation
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 583, 704);
				
		//panel setting
		contentPane = new JPanel();
		contentPane.setBackground(new Color(210, 218, 234));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		//LOGIN label
		JLabel lblNewLabel = new JLabel("Log-in");
		lblNewLabel.setBounds(78, 60, 419, 110);
		lblNewLabel.setForeground(new Color(69, 100, 152));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 55));
		contentPane.add(lblNewLabel);
		
		//login textfield
		ID_textField = new JTextField();
		ID_textField.setBounds(197, 281, 214, 41);
		contentPane.add(ID_textField);
		ID_textField.setColumns(10);
		//textField effect
		LineBorder idlb=new LineBorder(new Color(69, 100, 152),2,true);
		ID_textField.setBorder(idlb);
		
		
		//password textfield
		PW_textField = new JTextField();
		PW_textField.setColumns(10);
		PW_textField.setBounds(197, 341, 214, 41);
		contentPane.add(PW_textField);
		//textField effect
		LineBorder pwlb=new LineBorder(new Color(69, 100, 152),2,true);
		PW_textField.setBorder(pwlb);
		
		//login label
		JLabel ID_label = new JLabel("ID");
		ID_label.setHorizontalAlignment(SwingConstants.CENTER);
		ID_label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
		ID_label.setBounds(148, 284, 37, 38);
		ID_label.setForeground(new Color(69, 100, 152));
		contentPane.add(ID_label);
		
		//password label
		JLabel PW_label = new JLabel("Password");
		PW_label.setHorizontalAlignment(SwingConstants.CENTER);
		PW_label.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
		PW_label.setBounds(78, 339, 107, 38);
		PW_label.setForeground(new Color(69, 100, 152));
		contentPane.add(PW_label);
		
		//login button
		Login_button = new JButton("Log-in");
		Login_button.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
		Login_button.setForeground(new Color(69, 100, 152));
		//button effect
				LineBorder llb=new LineBorder(new Color(69, 100, 152),2,true);
				Login_button.setBorder(llb);
		
		//get text -> login process
		Login_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//get id,pw
				String inputID= ID_textField.getText();
				String inputPW= PW_textField.getText();
				
				//login process
				loginProcess(inputID,inputPW);
			
				
			}
		});
		Login_button.setBounds(241, 404, 128, 41);
		contentPane.add(Login_button);
		
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
	
	
	//login process  accessSLC()'s variation
	void loginProcess(String inputID,String inputPW) {
		
		  //prepare sql
        sqlStat = "SELECT Password FROM User WHERE User_ID = ?";
        try (PreparedStatement ps = TwitterMain.conn.prepareStatement(sqlStat)) {
            ps.setString(1, inputID);//bind user Id
            try (ResultSet rs = ps.executeQuery()) {
                // match check
                if (rs.next()) {
                    String dbPw = rs.getString(1);
                    if (inputPW.equals(dbPw)) {
                        JOptionPane.showMessageDialog(null,"Log-in Success!", "Login", JOptionPane.INFORMATION_MESSAGE);
                        // now we can access various tasks
                        TwitterMain.login = true;
                        TwitterMain.userId = inputID;
                        TwitterMain.userPw = inputPW;
                        //go to timeline window..
                        new timeline();
                        setVisible(false);
                        
                    } else {
                    	 JOptionPane.showMessageDialog(null,"This Password is wrong", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                	JOptionPane.showMessageDialog(null,"This ID does not exist", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}
	
	

}
