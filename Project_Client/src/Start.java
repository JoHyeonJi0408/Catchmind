import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class Start extends JFrame {

   private JPanel contentPane;
   private JTextField tf_ID; // ID를 입력받을곳
   
   // 이미지
   private ImageIcon catchmind;
   private ImageIcon pink_bt;
   private ImageIcon mint_bt;
   private JLabel catchmind_lb;

   public Start() // 생성자
   {
      init();
      start();
   }

   public void init() // 화면 구성
   {
	   setTitle("캐치마인드");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(0, 0, 800, 572);
      contentPane = new JPanel();
      
      // 이미지
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
         //String _ip= tf_IP.getText().trim(); // 공백이 있을지 모르므로 공백제거
         String _ip = "127.0.0.1";
         //int _port=Integer.parseInt(tf_PORT.getText().trim()); // 공백을 제거한 후 int형으로 변환          
         int _port = 30000;
         Client view = new Client(_id);
         view.setVisible(true);
         setVisible(false);      
      }
   }
   
   public void start() // 이벤트 처리
   {

   }

}