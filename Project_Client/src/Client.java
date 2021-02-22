/* Client.Java
   처음 화면 
   캐릭터 선택창*/

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame {
   private JPanel contentPane;
   private JPanel select_char1, select_char2, select_char3, select_char4; // 캐릭터 선택 창
   private ImageIcon garlic; // 마늘 쿵야 캐릭터
    private ImageIcon onion; // 양파 쿵야 캐릭터
    private ImageIcon mushroom; // 버섯 쿵야 캐릭터
    private ImageIcon celery; // 샐러리 쿵야 캐릭터
    private JRadioButton bt_garlic, bt_onion, bt_mushroom, bt_celery;
    private ButtonGroup  buttongroup;
    private String _id; // 캐릭터 선택시 서버에게 보낼 닉네임

    private int garlics = 0;
    private int onions = 0;
    private int celeries = 0;
    private int mushrooms = 0;
    
    // 캐릭터 받을거임
    private String my_ch;
    
    // 이미지
    private ImageIcon pink_bt;
    private ImageIcon mint_bt;

   public Client(String id) // 생성자
   {
     _id = id;
      init();
   }

   public void init() // 화면 구성
   {
	   setTitle("캐릭터 선택창");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1100,500);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);
      
      pink_bt = new ImageIcon("./src/pink_bt.png");
      mint_bt = new ImageIcon("./src/mint_bt.png");
      JButton btnNewButton = new JButton("", pink_bt);
      btnNewButton.setBounds(420, 360, 200, 50);
      contentPane.add(btnNewButton);
      btnNewButton.setRolloverIcon(mint_bt);
      
      ConnectAction action = new ConnectAction();
      btnNewButton.addActionListener(action); // 버튼 클릭시 입력한 ID로 접속   
      
      bt_garlic = new JRadioButton();
       bt_onion = new JRadioButton();
       bt_mushroom = new JRadioButton();
       bt_celery = new JRadioButton();  
       buttongroup = new ButtonGroup();
       
       // 마늘 쿵야 캐릭터
       select_char1 = new JPanel();
       select_char1.setLayout(null);
       select_char1.setBackground(Color.YELLOW);
       garlic = new ImageIcon("./src/garlic.png"); // 캐릭터 (마늘쿵야)
       JLabel garlic_lb = new JLabel(garlic);
       garlic_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
       select_char1.add(garlic_lb);
       JLabel garlic_nickname = new JLabel("Garlic");
       garlic_nickname.setFont(new Font("함초롬돋움", Font.BOLD, 20));
       garlic_nickname.setBounds(65, 70, 200, 200);
       select_char1.add(garlic_nickname);
       select_char1.setBounds(50,100,200,200);
       add(select_char1);
       bt_garlic.setBounds(135,310,30, 30);
       contentPane.add(bt_garlic);
       buttongroup.add(bt_garlic);
       
       // 양파 쿵야 캐릭터
        select_char2 = new JPanel();
        select_char2.setLayout(null);
        select_char2.setBackground(Color.GREEN);
        onion = new ImageIcon("./src/onion.png"); // 캐릭터 (양파쿵야)
        JLabel onion_lb = new JLabel(onion);
        onion_lb.setBounds(0, 0, 200, 200);
        select_char2.add(onion_lb);
        JLabel onion_nickname = new JLabel("Onion");
        onion_nickname.setFont(new Font("함초롬돋움", Font.BOLD, 20));
        onion_nickname.setBounds(65, 70, 200, 200);
        select_char2.add(onion_nickname);
        select_char2.setBounds(300,100,200,200);
        add(select_char2);
        bt_onion.setBounds(385,310,30, 30);
        contentPane.add(bt_onion);
        buttongroup.add(bt_onion);
        
        // 버섯 쿵야 캐릭터
        select_char3 = new JPanel();
        select_char3.setLayout(null);
        select_char3.setBackground(Color.CYAN);
        mushroom = new ImageIcon("./src/mushroom.png"); // 캐릭터 (버섯쿵야)
        JLabel mushroom_lb = new JLabel(mushroom);
        mushroom_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
        select_char3.add(mushroom_lb);
        JLabel mushroom_nickname = new JLabel("Mush");
        mushroom_nickname.setFont(new Font("함초롬돋움", Font.BOLD, 20));
        mushroom_nickname.setBounds(70, 70, 200, 200);
        select_char3.add(mushroom_nickname);
        select_char3.setBounds(550,100,200,200);
        add(select_char3);
        bt_mushroom.setBounds(635,310,30, 30);
        contentPane.add(bt_mushroom);
        buttongroup.add(bt_mushroom);
        
        // 샐러리 쿵야 캐릭터
        select_char4 = new JPanel();
        select_char4.setLayout(null);
        select_char4.setBackground(Color.PINK);
        celery = new ImageIcon("./src/celery.png"); // 캐릭터 (샐러리쿵야)
        JLabel celery_lb = new JLabel(celery);
        celery_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
        select_char4.add(celery_lb);
        JLabel celery_nickname = new JLabel("Celery");
        celery_nickname.setFont(new Font("함초롬돋움", Font.BOLD, 20));
        celery_nickname.setBounds(65, 70, 200, 200);
        select_char4.add(celery_nickname);
        select_char4.setBounds(800,100,200,200);
        add(select_char4);
        bt_celery.setBounds(885,310,30, 30);
        contentPane.add(bt_celery);
        buttongroup.add(bt_celery);
   }
   
   class ConnectAction implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         if (bt_garlic.isSelected()) {
               _id = "garlic";
               my_ch = "garlic";
            } else if (bt_onion.isSelected()) {
               _id = "onion";
               my_ch ="onion";
            } else if (bt_mushroom.isSelected()) {
               _id = "mush";
               my_ch ="mush";
            }else if(bt_celery.isSelected()) {
               _id = "celery";
               my_ch ="celery";
            }
         String _ip= "127.0.0.1"; // ip
         int _port= 30000; // port   
         MainView view = new MainView(_id,_ip,_port,my_ch);
         setVisible(false);
      }
   }
}