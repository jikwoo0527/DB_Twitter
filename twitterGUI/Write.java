package twitterGUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Write extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static String text="";

    
	/**
	 * Create the frame.
	 */
	public Write() {
		
		
		super("Write Text");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 649, 303);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(41, 46, 560, 172);
		contentPane.add(textArea);
		
		//send button
		JButton sendText_Button = new JButton("Send");
		sendText_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				text=textArea.getText();
				
				JOptionPane.showMessageDialog(null,"Write Success!", "Write Text", JOptionPane.INFORMATION_MESSAGE);
				
				
				
				setVisible(false);
				
			}
		});
		sendText_Button.setFont(new Font("굴림", Font.PLAIN, 20));
		sendText_Button.setBounds(489, 228, 123, 28);
		contentPane.add(sendText_Button);

		
		setVisible(true);
	}
	
	

}
