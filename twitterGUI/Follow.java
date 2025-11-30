package twitterGUI;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;

public class Follow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static DefaultListModel Following=new DefaultListModel();
	static DefaultListModel Follower=new DefaultListModel();

	/**
	 * Create the frame.
	 */
	public Follow() {
		
		super("Following/Follower");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 553, 386);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JList Following_list = new JList();
		Following_list.setBounds(60, 62, 436, 248);
		JList Follower_list = new JList();
		Follower_list.setBounds(276, 62, 200, 248);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(61, 62, 214, 248);
		scrollPane.setViewportView(Following_list);
		contentPane.add(scrollPane);
		Following_list.setModel(Following);
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(276, 61, 200, 249);
		contentPane.add(scrollPane_1);
		scrollPane_1.setViewportView(Follower_list);
		Follower_list.setModel(Follower);
		
		
		
		JLabel Following_Label = new JLabel("Following");
		Following_Label.setFont(new Font("굴림", Font.PLAIN, 20));
		Following_Label.setBounds(118, 15, 94, 37);
		contentPane.add(Following_Label);
		
		JLabel Follower_Label_1 = new JLabel("Follower");
		Follower_Label_1.setFont(new Font("굴림", Font.PLAIN, 20));
		Follower_Label_1.setBounds(326, 15, 94, 37);
		contentPane.add(Follower_Label_1);

		
		//visible window
		setVisible(true);
		
		
	}
}
