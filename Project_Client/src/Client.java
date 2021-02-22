/* Client.Java
   ó�� ȭ�� 
   ĳ���� ����â*/

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Client extends JFrame {
   private JPanel contentPane;
   private JPanel select_char1, select_char2, select_char3, select_char4; // ĳ���� ���� â
   private ImageIcon garlic; // ���� ���� ĳ����
    private ImageIcon onion; // ���� ���� ĳ����
    private ImageIcon mushroom; // ���� ���� ĳ����
    private ImageIcon celery; // ������ ���� ĳ����
    private JRadioButton bt_garlic, bt_onion, bt_mushroom, bt_celery;
    private ButtonGroup  buttongroup;
    private String _id; // ĳ���� ���ý� �������� ���� �г���

    private int garlics = 0;
    private int onions = 0;
    private int celeries = 0;
    private int mushrooms = 0;
    
    // ĳ���� ��������
    private String my_ch;
    
    // �̹���
    private ImageIcon pink_bt;
    private ImageIcon mint_bt;

   public Client(String id) // ������
   {
     _id = id;
      init();
   }

   public void init() // ȭ�� ����
   {
	   setTitle("ĳ���� ����â");
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
      btnNewButton.addActionListener(action); // ��ư Ŭ���� �Է��� ID�� ����   
      
      bt_garlic = new JRadioButton();
       bt_onion = new JRadioButton();
       bt_mushroom = new JRadioButton();
       bt_celery = new JRadioButton();  
       buttongroup = new ButtonGroup();
       
       // ���� ���� ĳ����
       select_char1 = new JPanel();
       select_char1.setLayout(null);
       select_char1.setBackground(Color.YELLOW);
       garlic = new ImageIcon("./src/garlic.png"); // ĳ���� (��������)
       JLabel garlic_lb = new JLabel(garlic);
       garlic_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
       select_char1.add(garlic_lb);
       JLabel garlic_nickname = new JLabel("Garlic");
       garlic_nickname.setFont(new Font("���ʷҵ���", Font.BOLD, 20));
       garlic_nickname.setBounds(65, 70, 200, 200);
       select_char1.add(garlic_nickname);
       select_char1.setBounds(50,100,200,200);
       add(select_char1);
       bt_garlic.setBounds(135,310,30, 30);
       contentPane.add(bt_garlic);
       buttongroup.add(bt_garlic);
       
       // ���� ���� ĳ����
        select_char2 = new JPanel();
        select_char2.setLayout(null);
        select_char2.setBackground(Color.GREEN);
        onion = new ImageIcon("./src/onion.png"); // ĳ���� (��������)
        JLabel onion_lb = new JLabel(onion);
        onion_lb.setBounds(0, 0, 200, 200);
        select_char2.add(onion_lb);
        JLabel onion_nickname = new JLabel("Onion");
        onion_nickname.setFont(new Font("���ʷҵ���", Font.BOLD, 20));
        onion_nickname.setBounds(65, 70, 200, 200);
        select_char2.add(onion_nickname);
        select_char2.setBounds(300,100,200,200);
        add(select_char2);
        bt_onion.setBounds(385,310,30, 30);
        contentPane.add(bt_onion);
        buttongroup.add(bt_onion);
        
        // ���� ���� ĳ����
        select_char3 = new JPanel();
        select_char3.setLayout(null);
        select_char3.setBackground(Color.CYAN);
        mushroom = new ImageIcon("./src/mushroom.png"); // ĳ���� (��������)
        JLabel mushroom_lb = new JLabel(mushroom);
        mushroom_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
        select_char3.add(mushroom_lb);
        JLabel mushroom_nickname = new JLabel("Mush");
        mushroom_nickname.setFont(new Font("���ʷҵ���", Font.BOLD, 20));
        mushroom_nickname.setBounds(70, 70, 200, 200);
        select_char3.add(mushroom_nickname);
        select_char3.setBounds(550,100,200,200);
        add(select_char3);
        bt_mushroom.setBounds(635,310,30, 30);
        contentPane.add(bt_mushroom);
        buttongroup.add(bt_mushroom);
        
        // ������ ���� ĳ����
        select_char4 = new JPanel();
        select_char4.setLayout(null);
        select_char4.setBackground(Color.PINK);
        celery = new ImageIcon("./src/celery.png"); // ĳ���� (����������)
        JLabel celery_lb = new JLabel(celery);
        celery_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
        select_char4.add(celery_lb);
        JLabel celery_nickname = new JLabel("Celery");
        celery_nickname.setFont(new Font("���ʷҵ���", Font.BOLD, 20));
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