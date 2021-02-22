// MainView.java : Java Client �� �ٽɺκ�
// read keyboard --> write to network (Thread �� ó��)
// read network --> write to textArea

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class MainView extends JFrame {
   //Ŭ���̾�Ʈ ����
   private JPanel contentPane;
   private JTextField chat_field; // �Է�â, ���� �޼��� ���°�
   private String id;
   private String ip;
   private int port;
   private Canvas canvas;
   JButton transmission; // ���۹�ư
   private Socket socket; // �������
   private InputStream is;
   private OutputStream os;
   private DataInputStream dis;
   private DataOutputStream dos;
   
   //UI����
   JPanel gui_panel, paint_panel,user1_panel, user2_panel,user3_panel,user4_panel,text_panel,time_panel, round_panel, chat_panel; 
   JPanel em_1, em_2, em_3, em_4;
    //  GUI���� �г�, �׷����� �г�, ���� �г�, �г���,���þ�,�ð�, ä�� �г�
    JButton pencil_bt, eraser_bt; // ����,���찳 ������ �����ϴ� ��ư
    JButton colorSelect_bt; // ������ ��ư
    JLabel thicknessInfo_label; // �������� ��
    JTextField thicknessControl_tf; // �������Ⱑ ������ �ؽ�Ʈ�ʵ�
    Color selectedColor; 
    // �� ������ �÷��� ����Ǿ� ���Ŀ� ������� �����ִ� ������ �Ű������� ���ȴ�.
    ImageIcon garlic;
    ImageIcon onion;
    ImageIcon mushroom;
    ImageIcon celery;
    JTextArea chat_window; // ä��â, ���ŵ� �޼����� ��Ÿ�� ����
    private JLabel textLabel;

    Graphics graphics; // Graphics2D Ŭ������ ����� ���� ����
    Graphics2D g;
    int thickness = 10; // �� ������ �׷����� ���� ���⸦ �����Ҷ� ���氪�� ����Ǵ� ����
    int startX; // ���콺Ŭ�������� X��ǥ���� ����� ����
    int startY; // ���콺Ŭ�������� Y��ǥ���� ����� ����
    int endX; // ���콺Ŭ�������� X��ǥ���� ����� ����
    int endY; // ���콺Ŭ�������� Y��ǥ���� ����� ����
    boolean tf = false;
    /* �� boolean ������ ó���� ���ʷ� �׸��� ���찳�� ������� �ٽ� ���ʷ� �׸���
     �⺻���� ���������� ���н�Ű�� ���� ���α׷� ���۽� �������� �� ���õ� ����
     ���찳�� ����� �ٽ� ���ʷ� �׸��� �̸� ������ �������� �����ϴ� ����*/
    
    // �������� �޴� ���� ���� ����
    private int access[] = {0,0,0,0}; // ���� ���� Ȯ�ο� ������ �迭
    private int [] point_int = new int [4]; // ��ǥ int �迭
    private String [] point_st = new String [4]; // ��ǥ string �迭
    private int thicknessAll; // ��� Ŭ���̾�Ʈ �� ����
    private String[] colorAll_st = new String[3]; // ��� Ŭ���̾�Ʈ �� ���� string �迭
    private int [] colorAll_int = new int[3]; // �� ���� int �迭

    //����
    private String rcv_word;
    private String rcv_examiver;
    
   // �׼�
    private PaintDraw pd = new PaintDraw();
    private ToolActionListener ta = new ToolActionListener();
    private  MyMouseListener mm = new MyMouseListener();
    private ColorActionListener ca = new ColorActionListener();
    private Myaction action = new Myaction();
    
    // �ð�
    private String time;
    private JLabel timeLabel; // �ð� ǥ��
    
    // ����
    private String round;
    private JLabel roundLabel;
    private JLabel roundNumber; // ���� ǥ��
    
    // panel�� ǥ�� �� �̹���
    private ImageIcon timeover;
    private JLabel timeover_lb;
    private ImageIcon correct;
    private JLabel correct_lb;
    private ImageIcon gameover;
    private JLabel gameover_lb;

    // ����
    private JButton ready_bt;
    private String imready = "�غ�ƴ�";
    
    //���� ���� �� ����
    private String gameover_score = "";
    private String gameover_temp;
    
    private String my_ch;
    
    //��ư �̹���
    private ImageIcon pink_bt;
    private ImageIcon mint_bt;
    private ImageIcon pencil_im;
    private ImageIcon eraser_im;
    private ImageIcon color_im;
    private ImageIcon trans_im;
    
   public MainView(String id, String ip, int port, String my_ch)// ������
   {
      this.id = id;
      this.ip = ip;
      this.my_ch = my_ch;
      this.port = port;
      init(); // ȭ�� ����
      start();
      network(); // ��� ������ ����
   }

   public void network() {
      // ������ ����
      try {
         socket = new Socket(ip, port); // socket, connect���� �Ϸ�
         if (socket != null) // socket�� null���� �ƴҶ� ��! ����Ǿ�����
         {
            Connection(); // ���� �޼ҵ带 ȣ��
         }
      } catch (UnknownHostException e) {

      } catch (IOException e) {
         chat_window.append("���� ���� ����!!\n");
      }
   }

   public void Connection() { // ���� ���� �޼ҵ� ����κ�
      try { // ��Ʈ�� ����
         is = socket.getInputStream();
         dis = new DataInputStream(is);
         os = socket.getOutputStream();
         dos = new DataOutputStream(os);
      } catch (IOException e) {
         chat_window.append("��Ʈ�� ���� ����!!\n");
      }

      id.trim();
      imready.trim();
      String m = String.format("%s %s %s", id, imready,my_ch);
      send_Message(m); // ���������� ����Ǹ� ���� id�� ����
      
      Thread th = new Thread(new Runnable() { // �����带 ������ �����κ��� �޼����� ����
         @SuppressWarnings("null")
         @Override
         public void run() {
            while (true) {
               try {
                  byte[] b = new byte[29];
                  dis.read(b); // ���� �޽��� ���� ��� ������.
                  String msg = new String(b);
                  msg = msg.trim();
                //����
                  if(msg.startsWith("new_")) {
                      String remove = msg.replace("new_ ", ""); 
                       remove = remove.trim(); // ���� ����
                       try{
                         //ĳ����
                          if(remove.equals("garlic")) {
                             garlic= new ImageIcon("./src/garlic.png"); // ĳ���� (���� ����)
                               JLabel garlic_lb = new JLabel(garlic);
                               garlic_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
                               em_1.add(garlic_lb);
                               em_1.setBackground(Color.yellow);
                          }
                          else if(remove.equals("onion")) {
                             onion= new ImageIcon("./src/onion.png"); // ĳ���� (���� ����)
                                JLabel onion_lb = new JLabel(onion);
                              onion_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
                               em_2.add(onion_lb); 
                              em_2.setBackground(Color.green);
                          }
                          else if(remove.equals("mush")) {
                             mushroom= new ImageIcon("./src/mushroom.png"); // ĳ���� (���� ����)
                                JLabel mushroom_lb = new JLabel(mushroom);
                              mushroom_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
                               em_3.add(mushroom_lb); 
                              em_3.setBackground(Color.CYAN);
                          }
                          else if(remove.equals("celery")) {
                             celery= new ImageIcon("./src/celery.png"); // ĳ���� (������ ����)
                               JLabel celery_lb = new JLabel(celery);
                             celery_lb.setBounds(0, 0, 200, 200); 
                             em_4.add(celery_lb);
                             em_4.setBackground(Color.pink);
                          }
                       } catch (NumberFormatException e) { }
                       setVisible(true);
                      }
                   
                   
                   else if(msg.startsWith("al_")) {
                      String remove = msg.replace("al_ ", ""); 
                       remove = remove.trim(); // ���� ����
                       try{
                         //ĳ����
                          if(remove.equals("garlic")) {
                             garlic= new ImageIcon("./src/garlic.png"); // ĳ���� (���� ����)
                               JLabel garlic_lb = new JLabel(garlic);
                               garlic_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
                               em_1.add(garlic_lb);
                               em_1.setBackground(Color.yellow);
                          }
                          else if(remove.equals("onion")) {
                             onion= new ImageIcon("./src/onion.png"); // ĳ���� (���� ����)
                                JLabel onion_lb = new JLabel(onion);
                              onion_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
                               em_2.add(onion_lb); 
                              em_2.setBackground(Color.green); 
                            }
                          else if (remove.equals("mush")) {
                             mushroom= new ImageIcon("./src/mushroom.png"); // ĳ���� (����������)
                                JLabel mushroom_lb = new JLabel(mushroom);
                              mushroom_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
                               em_3.add(mushroom_lb); 
                              em_3.setBackground(Color.CYAN);
                          }
                          else if (remove.equals("celery")) {
                             celery= new ImageIcon("./src/celery.png"); // ĳ���� (����������)
                               JLabel celery_lb = new JLabel(celery);
                             celery_lb.setBounds(0, 0, 200, 200); 
                             em_4.add(celery_lb);
                             em_4.setBackground(Color.pink);
                          }
                            
                       } catch (NumberFormatException e) { }
                       setVisible(true);
                   }
                   else if(msg.startsWith("EXAMINER")) {
                      String remove = msg.replace("EXAMINER ", ""); 
                      remove = remove.trim(); // ���� ����
                      try{
                        rcv_examiver = remove;
                      } catch (NumberFormatException e) { }
                   }
                  else if(msg.startsWith("THICKNESS")) { // �� ����
                	 String remove = msg.replace("THICKNESS ", ""); // string���� 'THICKNESS' ����
                	 remove = remove.trim(); // ���� ����
                	 try{
                		 thicknessAll = Integer.parseInt(remove); // string�� int�� �� ���� ���� ( �ٸ� Ŭ���̾�Ʈ�� )
                	 } catch (NumberFormatException e) { }
                 }
                  // �ð�
                  else if(msg.startsWith("TIMER")) {
                	  String remove = msg.replace("TIMER ", "");
                	  remove = remove.trim();
                	  time = remove;
                	  timeLabel.setText(time);
                  }
                  // ����
                  else if(msg.startsWith("ROUND")) {
                	  String remove = msg.replace("ROUND ", "");
                	  remove = remove.trim();
                	  try{
                    	  round = Integer.toString(Integer.parseInt(remove) + 1);
                 	 } catch (NumberFormatException e) { }
                	  roundNumber.setText(round);
                  }
                  // �ð� �ʰ�
                  else if(msg.startsWith("TIME")) {
                	  timeover_lb.setVisible(true); // �ð� �ʰ� �̹���
                	  Thread.sleep(300);
                	  timeover_lb.setVisible(false);
                  }
                  else if(msg.startsWith("CORRECT")) {
                	  correct_lb.setVisible(true); // ���� �̹���
                	  Thread.sleep(300);
                	  correct_lb.setVisible(false);
                  }
               //����
                 else if(msg.startsWith("WORD")) {
                	 gameover_lb.setVisible(false);
                     ready_bt.setEnabled(false);// ready��ư ��Ȱ��ȭ
                     ready_bt.setText("���� ������"); // ���� ��������
                     String remove = msg.replace("WORD ", ""); 
                     remove = remove.trim(); // ���� ����
                     try{
                        rcv_word = remove;
                        // paint_panel�� ���콺 ��Ǹ����� �߰�
                 	// �׸��� �Լ�
                        if(id.equals(rcv_examiver)) {
                        	 paint_panel.addMouseListener(mm); // ��ǥ �̺�Ʈ
                             pencil_bt.addActionListener(ta); // ���ʹ�ư �׼�ó��
                             eraser_bt.addActionListener(ta); // ���찳��ư �׼�ó��
                             colorSelect_bt.addActionListener(ca); // �� ���� �̺�Ʈ
                             paint_panel.addMouseMotionListener(pd); // �г� �׸��� 
                             transmission.removeActionListener(action); // ä�� �̺�Ʈ ����
                             chat_field.removeActionListener(action);
                             textLabel.setText(rcv_word);} // ���þ� �ؽ�Ʈ�� �ٲ۴�
                        else {
                        	// �̺�Ʈ ó�� ����
                       	   paint_panel.removeMouseListener(mm); // ��ǥ �̺�Ʈ ����
                        	pencil_bt.removeActionListener(ta); // ���ʹ�ư �׼� ����
                            eraser_bt.removeActionListener(ta); // ���찳��ư �׼� ����
                            colorSelect_bt.removeActionListener(ca); // �� ���� �̺�Ʈ ����
                            paint_panel.removeMouseMotionListener(pd); // �г� �׸��� ����
                            transmission.addActionListener(action); // ä�� �̺�Ʈ �߰�
                            chat_field.addActionListener(action);
                        textLabel.setText("?");
                        }
                        thicknessControl_tf.setText("10");
                        thickness = 10;
                        thicknessAll = 10;
                        g.setColor(Color.black);
                        paint_panel.repaint(); // �׸� �ʱ�ȭ
                     } catch (NumberFormatException e) { }
                  }
                 else if(msg.startsWith("Color")) { // �׸��� ��
                	 String remove = msg.replace("Color",  ""); //string���� 'java.awt.Color' ����
                   	 remove = remove.replace("[r=", "");
                	 remove = remove.replace(",g", "");
                	 remove = remove.replace(",b", "");
                	 remove = remove.replace("]", ""); // =�� ���ڸ� ����� ����
                	 remove = remove.trim(); // ��������
                	 colorAll_st = remove.split("\\="); // colorAll_st�� r, g, b ����
                	 for (int i=0; i<3; i++) {
                		 try {
                    		 colorAll_int[i] = Integer.parseInt(colorAll_st[i]);
                		 } catch (NumberFormatException e) { }
                	}
                	 g.setColor(new Color(colorAll_int[0],colorAll_int[1],colorAll_int[2])); // �ٸ� Ŭ���̾�Ʈ�� �� ����
                 }
                 else if(msg.startsWith("POINT")) { // �׸� �׸���
                     String remove = msg.replace("POINT ", ""); // string���� 'POINT' ����
                     point_st  = remove.split(" "); // poinst_st�� string type���� ��ǥ 4�� ����
                        for(int i=0; i<4; i++) {
                           try{
                              point_int[i] = Integer.parseInt(point_st[i]); // string type�� int type���� ��ǥ 4�� ����
                           }catch (NumberFormatException e) { }
                        }
                        Draw(point_int[0],point_int[1],point_int[2],point_int[3]); // �ٸ� Ŭ���̾�Ʈ���Ե� �׸�
                  }
                 else if (msg.startsWith("ERASER")) { // ���찳 ����
                		 g.setColor(Color.WHITE);
                 }
                 else if(msg.startsWith("SN")) { // �г���
                	 msg = msg.replace("SN ", "");
                	 msg = msg.trim();
                	gameover_temp = msg;
                 }
                 else if(msg.startsWith("SS")) { // ����
                	 msg = msg.replace("SS ", "");
                	 msg = msg.trim();
                	gameover_temp = gameover_temp + " : " + msg + "\n";
                	gameover_score = gameover_score + gameover_temp;
                 }
                 else if(msg.startsWith("GAME")) { // ���� ����
                	 gameover_lb.setVisible(true);
                 	  paint_panel.removeMouseListener(mm); // ��ǥ �̺�Ʈ ����
                 	  pencil_bt.removeActionListener(ta); // ���ʹ�ư �׼� ����
                      eraser_bt.removeActionListener(ta); // ���찳��ư �׼� ����
                      colorSelect_bt.removeActionListener(ca); // �� ���� �̺�Ʈ ����
                      paint_panel.removeMouseMotionListener(pd); // �г� �׸��� ����
                      transmission.removeActionListener(action); // ä�� �̺�Ʈ ����
                      chat_field.removeActionListener(action);
                      ready_bt.setEnabled(true); // ���� ��ư Ȱ��ȭ
                      ready_bt.setText(""); 
                      ready_bt.setIcon(pink_bt);
                      imready = "�غ�ƴ�";
                      JOptionPane.showMessageDialog(null, gameover_score);               
                 }
                 else { // ä��
                	 msg = msg.trim();
                    chat_window.append(msg + "\n");
                    chat_window.setCaretPosition(chat_window.getText().length());   
                 }
               } catch (IOException e) {
                  chat_window.append("�޼��� ���� ����!!\n");
                  // ������ ���� ��ſ� ������ ������ ��� ������ �ݴ´�
                  try {
                     os.close();
                     is.close();
                     dos.close();
                     dis.close();
                     socket.close();
                     break; // ���� �߻��ϸ� while�� ����
                  } catch (IOException e1) {
                  }
               } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            } // while�� ��
         }// run�޼ҵ� ��
      });
      th.start();
   }

   public void send_Message(String str) { // ������ �޼����� ������ �޼ҵ�
      try {
         byte[] bb = new byte[29];
         String s = String.format("%-29s", str);
         bb = s.getBytes();
         dos.write(bb);
      } catch (IOException e) {
         chat_window.append("�޼��� �۽� ����!!\n");
      }
   }

   public void init() { // ȭ�鱸�� �޼ҵ�
      setLayout(null); // �⺻ �������� ���̾ƿ��� �ʱ�ȭ ���� �г��� �����ڰ� ���� �ٷ�� �ְ� ��
        setTitle("ĳġ���ε�"); // ������ Ÿ��Ʋ ����
        setSize(1520,1000); // ������ ������ ����
        setLocationRelativeTo(null); // ���α׷� ����� ȭ�� �߾ӿ� ���
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        // ������ ������ܿ� X��ư�� ���������� ��� ����
        
        //gui_panel
        gui_panel = new JPanel(); // ������ ��ܿ� ��ư, �ؽ�Ʈ�ʵ�, �󺧵��� UI�� �� �г�
        gui_panel.setBackground(new Color(195,195,195)); // �г��� ������ ȸ������ ����
        gui_panel.setLayout(null); 
        // gui_panel�� ���̾ƿ��� null�����Ͽ� ������Ʈ���� ��ġ�� ���� ����

        pencil_im = new ImageIcon("./src/pencil.png");
        pencil_bt = new JButton("",pencil_im); // ���� ��ư ����
        eraser_im = new ImageIcon("./src/eraser.png");
        eraser_bt = new JButton("",eraser_im); // ���찳 ��ư ����
        color_im = new ImageIcon("./src/color.png");
        colorSelect_bt = new JButton("",color_im); // ������ ��ư ����
        
        thicknessInfo_label = new JLabel("��������"); 
        // �������� �� ���� / �ؿ��� ���� �ؽ�Ʈ�ʵ��� ������ ����
        thicknessInfo_label.setFont(new Font("���ʷҵ���", Font.BOLD, 20));
        // �������� �� ��Ʈ�� �۾� ũ�� ����
        
        thicknessControl_tf = new JTextField("10", 5); // �������� �Է� �ؽ�Ʈ�ʵ� ����
        thicknessControl_tf.setHorizontalAlignment(JTextField.CENTER); 
          // �ؽ�Ʈ�ʵ� ���ο� ������� �ؽ�Ʈ �߾� ����
        thicknessControl_tf.setFont(new Font("�ü�ü", Font.PLAIN, 25)); 
          // �ؽ�Ʈ�ʵ� X���� �� ��Ʈ ����
        
        pencil_bt.setBounds(10,10,90,55); // ���� ��ư ��ġ ����
        eraser_bt.setBounds(105,10,109,55); // ���찳 ��ư ��ġ ����
        colorSelect_bt.setBounds(785,10,90,55); // ������ ��ư ��ġ ����
        thicknessInfo_label.setBounds(640,10,100,55); // �������� �� ��ġ ����
        thicknessControl_tf.setBounds(720,22,50,35); // �������� �ؽ�Ʈ�ʵ� ��ġ ����
        
        gui_panel.add(pencil_bt); // gui_panel�� ���� ��ư �߰�
        gui_panel.add(eraser_bt); // gui_panel�� ���찳 ��ư �߰�
        gui_panel.add(colorSelect_bt); // gui_panel�� ������ ��ư �߰�
        gui_panel.add(thicknessInfo_label); // gui_panel�� �������� �� �߰�
        gui_panel.add(thicknessControl_tf); // gui_panel�� �������� �ؽ�Ʈ�ʵ� �߰�
        
        gui_panel.setBounds(300,100,900,75); // gui_panel�� �����ӿ� ��ġ�� ��ġ ����
        
        //paint_panel
        paint_panel = new JPanel(); // �׸��� �׷��� �г� ����
        paint_panel.setBackground(Color.WHITE); // �г��� ���� �Ͼ��
        paint_panel.setLayout(null); 
        // paint_panel�� ���̾ƿ��� null���� �г� ��ü�� setBounds�� ��ġ�� �����Ҽ� �ִ�.
        
        paint_panel.setBounds(300,190,900,460); // paint_panel�� ��ġ ����
        
        // paint_panel�� �̹��� �߰� 
        // time over
        timeover = new ImageIcon("./src/timeover.png");
        timeover_lb = new JLabel(timeover);
        timeover_lb.setBounds(0, 0, 900, 460);
        paint_panel.add(timeover_lb);
        timeover_lb.setVisible(false);
        // ������ ������ ���
        correct = new ImageIcon("./src/correct.png");
        correct_lb = new JLabel(correct);
        correct_lb.setBounds(0, 0, 900, 460);
        paint_panel.add(correct_lb);
        correct_lb.setVisible(false);
        // game over
        gameover = new ImageIcon("./src/gameover.png");
        gameover_lb = new JLabel(gameover);
        gameover_lb.setBounds(0, 0, 900, 460);
        paint_panel.add(gameover_lb);
        gameover_lb.setVisible(false);
        
        /*
        //user1_panel (���� ����)
        user1_panel = new JPanel();
        user1_panel.setLayout(null);
        user1_panel.setBackground(Color.YELLOW);
        garlic = new ImageIcon("./src/garlic.png"); // ĳ���� (��������)
        JLabel garlic_lb = new JLabel(garlic);
        garlic_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
        user1_panel.add(garlic_lb);
        JLabel garlic_nickname = new JLabel("Garlic");
        garlic_nickname.setFont(new Font("���ʷҵ���", Font.BOLD, 20));
        garlic_nickname.setBounds(65, 70, 200, 200);
        user1_panel.add(garlic_nickname);
        user1_panel.setBounds(50,150,200,200);
        
        //user2_panel (���� ����)
        user2_panel = new JPanel();
        user2_panel.setLayout(null);
        user2_panel.setBackground(Color.GREEN);
        onion = new ImageIcon("./src/onion.png"); // ĳ���� (��������)
        JLabel onion_lb = new JLabel(onion);
        onion_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
        user2_panel.add(onion_lb);
        JLabel onion_nickname = new JLabel("Onion");
        onion_nickname.setFont(new Font("���ʷҵ���", Font.BOLD, 20));
        onion_nickname.setBounds(70, 80, 200, 200);
        user2_panel.add(onion_nickname);
        user2_panel.setBounds(1250,450,200,200);
        user2_panel.setBounds(50,450,200,200);
        
      //user3_panel (���� ����)
        user3_panel = new JPanel();
        user3_panel.setLayout(null);
        user3_panel.setBackground(Color.CYAN);
        mushroom = new ImageIcon("./src/mushroom.png"); // ĳ���� (��������)
        JLabel mushroom_lb = new JLabel(mushroom);
        mushroom_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
        user3_panel.add(mushroom_lb);
        JLabel mushroom_nickname = new JLabel("Mushroom");
        mushroom_nickname.setFont(new Font("���ʷҵ���", Font.BOLD, 20));
        mushroom_nickname.setBounds(45, 70, 200, 200);
        user3_panel.add(mushroom_nickname);
        user3_panel.setBounds(1250,150,200,200);
        
      //user4_panel (������ ����)
        user4_panel = new JPanel();
        user4_panel.setLayout(null);
        user4_panel.setBackground(Color.PINK);
        celery= new ImageIcon("./src/celery.png"); // ĳ���� (����������)
        JLabel celery_lb = new JLabel(celery);
        celery_lb.setBounds(0, 0, 200, 200); // ũ�� 200x200 �ȼ�
        user4_panel.add(celery_lb);
        JLabel celery_nickname = new JLabel("Celery");
        celery_nickname.setFont(new Font("���ʷҵ���", Font.BOLD, 20));
        celery_nickname.setBounds(65, 70, 200, 200);
        user4_panel.add(celery_nickname);
        user4_panel.setBounds(1250,450,200,200);
        */
        
        em_1 = new JPanel();
        em_2 = new JPanel();
        em_3 = new JPanel();
        em_4 = new JPanel();
        
        em_1.setBackground(Color.LIGHT_GRAY);
        em_2.setBackground(Color.LIGHT_GRAY);
        em_3.setBackground(Color.LIGHT_GRAY);
        em_4.setBackground(Color.LIGHT_GRAY);
        
        em_1.setBounds(50,150,200,200);
        em_2.setBounds(50,450,200,200);
        em_3.setBounds(1250,150,200,200);
        em_4.setBounds(1250,450,200,200);
        
        add(em_1);
        add(em_2);
        add(em_3);
        add(em_4);
        
        //text_panel
        text_panel = new JPanel();
        textLabel = new JLabel("���þ�");
        textLabel.setFont(new Font("���ʷҵ���", Font.BOLD, 60));
        text_panel.add(textLabel);
        text_panel.setBounds(600,0,300,200);
        
        //time_panel
        time_panel = new JPanel();
        timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("���ʷҵ���", Font.BOLD, 60));
        time_panel.add(timeLabel);
        time_panel.setBounds(0,750,300,100);
        
        //round_panel
        round_panel = new JPanel();
        roundLabel = new JLabel("ROUND");
        roundNumber = new JLabel("10");
        roundLabel.setFont(new Font("���ʷҵ���",Font.BOLD,40));
        round_panel.add(roundLabel);
        round_panel.setBounds(1200,820,300,100);
        roundNumber.setFont(new Font("���ʷҵ���",Font.BOLD,40));
        round_panel.add(roundNumber);
        roundNumber.setBounds(0, 100, 300, 100);
        
        //chat_panel
        JScrollPane js = new JScrollPane();
        chat_panel = new JPanel();
        chat_panel.setLayout(null);
        chat_window = new JTextArea();
        chat_window.setEditable(false); // ä��â �Է� ����
        chat_window.setColumns(20);
        chat_window.setRows(5);
        js.setBounds(0,0, 900, 200);
        js.setViewportView(chat_window);
        chat_field = new JTextField();
        chat_field.setBounds(0,210,830,40);
        trans_im = new ImageIcon("./src/trans.png");
        transmission = new JButton("",trans_im);
        transmission.setBounds(830, 210, 70, 40);
        chat_panel.add(js);
        chat_panel.add(chat_field);
        chat_panel.add(transmission);
        chat_panel.setBounds(300,670,900,250);
        
        // ��ư
        pink_bt = new ImageIcon("./src/pink_bt.png");
        ready_bt = new JButton("",pink_bt); // �غ� (��ũ)
        ready_bt.setBounds(1260,740,200,50);
        add(ready_bt);
        
        add(gui_panel); // ���������ӿ� gui�г� �߰� - ��ġ�� ������ �� ������
        add(paint_panel); // ���������ӿ� paint�г� �߰� - ��ġ�� ������ �� ������
        /*if(id.equals("garlic"))
           add(user1_panel);
        if(id.equals("onion"))
           add(user2_panel);
        if(id.equals("mushroom"))
           add(user3_panel);
        if(id.equals("celery"))
         add(user4_panel);*/
        add(text_panel);
        add(time_panel);
        add(round_panel);
        add(chat_panel);
        
        setVisible(true); // ������������ ���̰� �Ѵ�.
        
        graphics = getGraphics(); // �׷����ʱ�ȭ
        g = (Graphics2D)graphics; 
        // ������ graphics������ Graphics2D�� ��ȯ�� Graphics2D�� �ʱ�ȭ
        // �Ϲ����� Graphics�� �ƴ� Graphics2D�� ����� ������ ���� ����� ���õ� �����
        //�����ϱ� ���Ͽ� Graphics2D Ŭ������ ��üȭ��
        g.setColor(selectedColor); 
        // �׷��� ��(=���� �׷���)�� ������ selectedColor�� ������ ����
        
        /////////////////////////////////////////////////// �� �׼� ó���κ�

   }

   public void start() { // �׼��̺�Ʈ ���� �޼ҵ�
      transmission.addActionListener(action);
      chat_field.addActionListener(action);
      Ready_Action ready_action = new Ready_Action();
      ready_bt.addActionListener(ready_action);
   }
   
   class Ready_Action implements ActionListener{
	      @Override
	      public void actionPerformed(ActionEvent arg0) {
	         if(imready.equals("�غ�ƴ�")) { // �غ� ���� ���� ���¶�� ...
	            imready = "�غ���";
	            String msg = null;
	               msg = String.format("READY %s %s\n", id,imready );// �������� ����ڿ� ������� ���¸� ������. �غ��� �������� �˸���
	               send_Message(msg);
	               ready_bt.setText(""); // ��Ʈ (�غ� ���)
	         }
	         else if(imready.equals("�غ���")) {// �̹� �غ��� ���¶��
	            imready = "�غ�ƴ�"; // �غ� ���¸� Ǭ��
	            String msg = null;
	               msg = String.format("READY %s %s\n", id,imready );// �������� ����ڿ� ������� ���¸� ������. �غ���¸� Ǯ���ش�
	               send_Message(msg);
	               ready_bt.setText(""); // ��ũ (�غ�)
	         }
	      }
	      }
   
   class MyMouseListener implements MouseListener{	   
	@Override
	public void mouseClicked(MouseEvent arg0) { }
	@Override
	public void mouseEntered(MouseEvent arg0) { }
	@Override
	public void mouseExited(MouseEvent arg0) { }
	@Override
	public void mousePressed(MouseEvent e) {
        startX = e.getX(); // ���콺�� �������� �׶��� X��ǥ������ �ʱ�ȭ
        startY = e.getY(); // ���콺�� �������� �׶��� Y��ǥ������ �ʱ�ȭ
	}
	@Override
	public void mouseReleased(MouseEvent e) { }
	
   }

   class Myaction implements ActionListener // ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
   {
      @Override
      public void actionPerformed(ActionEvent e) {
         // �׼� �̺�Ʈ�� sendBtn�϶� �Ǵ� textField ���� Enter key ġ��
         if (e.getSource() == transmission || e.getSource() == chat_field) 
         {
            String msg = null;
            msg = String.format("[%s] %s \n", id, chat_field.getText());
            send_Message(msg);
            chat_field.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
            chat_field.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��            
         }
      }
   }
   
   public void Draw(int x1, int y1, int x2, int y2) { // ��� Ŭ���̾�Ʈ �׸��� ��
       g.setStroke(new BasicStroke(thicknessAll, BasicStroke.CAP_ROUND,0)); //������
       g.drawLine(x1, y1, x2, y2); // ������ �׷����� �Ǵºκ�
   }
   
   public class PaintDraw implements MouseMotionListener {
        // ������ paint_panel�� MouseMotionListener�׼� ó���� �ɶ� �� Ŭ������ �Ѿ�ͼ� �� ������ ����

        @Override
        public void mouseDragged(MouseEvent e) { 
            // paint_panel���� ���콺 �巡�� �׼���ó���� �� �� �޼ҵ� ����
        		// �� ���� ������ ����
                String th = null;
                th = String.format("THICKNESS %s", thicknessControl_tf.getText());
                send_Message(th);
                
                thickness = Integer.parseInt(thicknessControl_tf.getText());
                // �ؽ�Ʈ�ʵ�κп��� ���� ����� thickness������ ����
                
                    endX = e.getX(); 
                    // �巡�� �Ǵ� �������� X��ǥ�� ���� - �ؿ��� ������ǥ�� ����ǥ�� ���� ���־� ���� �׾����Եȴ�.
                    
                    if (endX <= 0+thickness)
                       endX = 0+thickness;
                    if (endX >= 900-thickness)
                       endX = 900-thickness;

                    endY = e.getY(); 
                   // �巡�� �Ǵ� �������� Y��ǥ�� ���� - �ؿ��� ������ǥ�� ����ǥ�� ���� ���־� ���� �׾����Եȴ�.
                    
                   if(endY <= 0+thickness)
                      endY = 0+thickness;
                    if (endY >= 452-thickness)
                       endY = 452-thickness;
     
                    g.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND,0)); //������
                    g.drawLine(startX+310, startY+238, endX+310, endY+238); // ������ �׷����� �Ǵºκ�
                    // ��ǥ �������� ������
                    
                    String point = null;
                    point = String.format("POINT %05d %05d %05d %05d", startX+310, startY+238, endX+310, endY+238);
                    send_Message(point);
                    
                    startX = endX; 
                            // ���ۺκ��� �������� �巡�׵� X��ǥ�� ������ ������ �̾� �׷����� �ִ�.
                    startY = endY;
                            // ���ۺκ��� �������� �巡�׵� Y��ǥ�� ������ ������ �̾� �׷����� �ִ�.
        }
        @Override
        public void mouseMoved(MouseEvent e) {}
    }
   
   public class ColorActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			tf = true;
            JColorChooser chooser = new JColorChooser(); // JColorChooser Ŭ������üȭ
            selectedColor = chooser.showDialog(null, "Color", Color.ORANGE); 
            // selectedColor�� ���õȻ����� �ʱ�ȭ
            g.setColor(selectedColor); // �׷����� ���� ������ selectedColor�� �Ű������� �Ͽ� ����
            
            String color = null;
            color = String.format("%s", selectedColor);
            color = color.replace("java.awt.", "");
            send_Message(color);
		}
	   
   }
    
    public class ToolActionListener implements ActionListener {
        // ����,���찳 ��ư�� �׼�ó���� ����Ǵ� Ŭ����
        public void actionPerformed(ActionEvent e ) {
            // �������̵��� actionPerformed�޼ҵ� ����
            if(e.getSource() == pencil_bt) { // ���ʹ�ư�� ��������
            	// ������ ���찳 ���� OFF ����
                if(tf == false) g.setColor(Color.BLACK); // �׷����� ������ ������ ����
                else g.setColor(selectedColor);  // �׷����� ������ selectedColor������ ������ ����
            } else if(e.getSource() == eraser_bt) { // ���찳��ư�� ������ ��
            	// ������ ���찳 ���� ON ����
            	 String eraser = null;
                 eraser = "ERASER";
                 send_Message(eraser);
                 g.setColor(Color.WHITE);
            }
        }
    }
}