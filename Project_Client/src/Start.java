import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class Start extends JFrame {

   private JPanel contentPane;
   private JTextField tf_ID; // ID�� �Է¹�����
   
   // �̹���
   private ImageIcon catchmind;
   private ImageIcon pink_bt;
   private ImageIcon mint_bt;
   private JLabel catchmind_lb;

   public Start() // ������
   {
      init();
      start();
   }

   public void init() // ȭ�� ����
   {
	   setTitle("ĳġ���ε�");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(0, 0, 800, 572);
      contentPane = new JPanel();
      
      // �̹���
      catchmind = new ImageIcon("./src/catchmind.png");
      catchmind_lb = new JLabel(catchmind);
      catchmind_lb.setBounds(0,0,800,572);
      contentPane.add(catchmind_lb);
      
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);

      pink_bt = new ImageIcon("./src/pink_bt.png");
      mint_bt = new ImageIcon("./src/mint_bt.png");
      JButton btnNewButton = new JButton("", pink_bt);
      btnNewButton.setBounds(300, 450, 200, 50);
      contentPane.add(btnNewButton);
      btnNewButton.setRolloverIcon(mint_bt);
      
      ConnectAction action = new ConnectAction();
      btnNewButton.addActionListener(action);
      //tf_PORT.addActionListener(action);
         
   }
   class ConnectAction implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent arg0) {      
         String _id = "";
         //String _ip= tf_IP.getText().trim(); // ������ ������ �𸣹Ƿ� ��������
         String _ip = "127.0.0.1";
         //int _port=Integer.parseInt(tf_PORT.getText().trim()); // ������ ������ �� int������ ��ȯ          
         int _port = 30000;
         Client view = new Client(_id);
         view.setVisible(true);
         setVisible(false);      
      }
   }
   
   public void start() // �̺�Ʈ ó��
   {

   }

}