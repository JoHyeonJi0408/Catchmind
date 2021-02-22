// Java Server �ڵ�

import java.awt.BasicStroke;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame {
   private JPanel contentPane;
   private JButton Start; // ������ �����Ų ��ư
   JTextArea textArea; // Ŭ���̾�Ʈ �� ���� �޽��� ���

   private ServerSocket socket; //��������
   private Socket soc; // ������� 
   private int Port; // ��Ʈ��ȣ
   private Vector vc = new Vector(); // ����� ����ڸ� ������ ����
   
   // ����
   private String[] Word = {"�����", "������","��ǻ��","���","����","�޴���","����","Ŀ�Ǹӽ�","��Ʈ��","ȭ��"};
   private int k=0, n=0; // for���� �������� �̰ɷ�
   private int now_ex=0, now_word = 0;
   
   // �ð�
   private int time_int = 30;
   private String time_st= "30";
   private Timer timer;
   private static TimerTask task; // ������
   
   // ���� round
	private int round = 0; // game round ��
	private String round_st = "10";
	
	// ����
	private String rcv_str[];
	private int all_ready = 0; // 0:�غ�ȵ� 1:����غ� 2:���ӽ����� 3: ��������
	private int user_count = 0;
	private ArrayList<String> ready = new ArrayList<>();
	private ArrayList<String> Examiner = new ArrayList<>();
	
	// ����
	private HashMap<String , Integer> score = new HashMap<String , Integer>();
   
	private int first_start = 1;
	private ArrayList<String> my_ch = new ArrayList<>();
	
   public static void main(String[] args)
   {   
         Server frame = new Server();
         frame.setVisible(true);         
   }

   public Server() {
      init();
   }

   private void init() { // GUI�� �����ϴ� �޼ҵ�      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 280, 400);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);

      JScrollPane js = new JScrollPane();            

      textArea = new JTextArea();
      textArea.setColumns(20);
      textArea.setRows(5);
      js.setBounds(0, 0, 264, 254);
      contentPane.add(js);
      js.setViewportView(textArea);
      Start = new JButton("���� ����");
      
      Myaction action = new Myaction(); // enter key
      Start.addActionListener(action); // ����Ŭ������ �׼� �����ʸ� ��ӹ��� Ŭ������
      Start.setBounds(0, 290, 264, 37);
      contentPane.add(Start);
      textArea.setEditable(false); // textArea�� ����ڰ� ���� ���ϰԲ� ���´�.   
   }
   
   class Myaction implements ActionListener // ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
   {
      @Override
      public void actionPerformed(ActionEvent e) {
         Port = 30000; // ��Ʈ ��ȣ ����
         server_start();
      }
   }
   
   private void server_start() {
      try {
         socket = new ServerSocket(Port); // socket, bind, listen ������ ��Ʈ ���ºκ�
         Start.setText("����������");
         Start.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
         
         if(socket!=null) // socket �� ���������� ��������
         {
            Connection();
         }
         
      } catch (IOException e) {
         textArea.append("������ �̹� ������Դϴ�...\n");

      }

   }

   private void Connection() {
      Thread th = new Thread(new Runnable() { // ����� ������ ���� ������
         @Override
         public void run() {
            while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
               try {
                  textArea.append("����� ���� �����...\n");
                  soc = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
                  textArea.append("����� ����!!\n");
                  UserInfo user = new UserInfo(soc, vc); // ����� ���� ������ �ݹ� ������Ƿ�, user Ŭ���� ���·� ��ü ����
                                   // �Ű������� ���� ����� ���ϰ�, ���͸� ��Ƶд�
                  vc.add(user); // �ش� ���Ϳ� ����� ��ü�� �߰�
                  user.start(); // ���� ��ü�� ������ ����
               } catch (IOException e) {
                  textArea.append("!!!! accept ���� �߻�... !!!!\n");
               } 
            }
         }
      });
      th.start();
   }

   class UserInfo extends Thread {
      private InputStream is;
      private OutputStream os;
      private DataInputStream dis;
      private DataOutputStream dos;
      private Socket user_socket;
      private Vector user_vc;
      private String Nickname = "";

      public UserInfo(Socket soc, Vector vc) // �����ڸ޼ҵ�
      {
         // �Ű������� �Ѿ�� �ڷ� ����
         this.user_socket = soc; // accept���� �Ѱ��� socket ���
         this.user_vc = vc;
         User_network();
      }
      
      public void User_network() {
         try {
            is = user_socket.getInputStream();
            dis = new DataInputStream(is);
            os = user_socket.getOutputStream();
            dos = new DataOutputStream(os);
            byte[] b=new byte[29];
            dis.read(b); // ������ ������� client ID ����, ���� ���
           String str = new String(b);
           rcv_str = str.split(" ");
          textArea.append("ID " + rcv_str[0]+ " ����\n");
          textArea.setCaretPosition(textArea.getText().length());
          String  entrance = rcv_str[0] + "���� �����ϼ̽��ϴ�.";
          entrance = entrance.trim();
          send_Message(entrance); // ����� ����ڿ��� ���������� �˸�
          InMessage(entrance); // �ٸ� ����ڿ��Ե� ���������� �˸�
          rcv_str[0].trim();//���̵�
          rcv_str[1].trim();//�غ�
          rcv_str[2].trim();//ĳ����
          score.put(rcv_str[0], new Integer(0)); // ����� ���� ��� (0��)
          //Examiner.add(rcv_str[0]); // ����ڰ� �߰��� ������ ArrayList�� �߰�.
          //ready.add(rcv_str[1]); // ���̵� ���� ����Ʈ�� �ִ´�
          first_start =1 ;
         } catch (Exception e) {
            textArea.append("��Ʈ�� ���� ����\n");
            textArea.setCaretPosition(textArea.getText().length());
         }
      }

      // Ŭ���̾�Ʈ �޼����� ȭ�鿡 ����ϰ�, ��� Ŭ���̾�Ʈ���� ����Ѵ�.
      public void InMessage(String str) {
      textArea.append(str + "\n"); // server�� ���� ��
      textArea.setCaretPosition(textArea.getText().length());
      // ����� �޼��� ó��
      str = str.trim();
      broad_cast(str);
      }

      // ��� �Լ�
      public void broad_cast(String str) {
    	  str = str.trim();
         for (int i = 0; i < user_vc.size(); i++) {
            UserInfo imsi = (UserInfo) user_vc.elementAt(i);
            imsi.send_Message(str);
         }
      }

      public void send_Message(String str) { // client��
         try {
            byte[] bb = new byte[29];
            String s = String.format("%-29s", str);
            bb = s.getBytes();
            dos.write(bb); 
         } 
         catch (IOException e) {
            textArea.append("�޽��� �۽� ���� �߻�\n");   
            textArea.setCaretPosition(textArea.getText().length());
         }
      }
      
    //����
      public void send_word(String str) {
         String word_msg = String.format("WORD %s", str);
         InMessage(word_msg);
      }
      public void Set_Examiner(String str) {
         String word_msg = String.format("EXAMINER %s", str);
         InMessage(word_msg);
 
      }
      
      // TimerTask ������ �Լ�
      public TimerTask timerTaskMarker() {
    	  TimerTask tempTask = new TimerTask() {
       			@Override
       			public void run() {
       				if(time_int>0) {
       					time_int -= 1;
       					 try{
                      		 time_st = Integer.toString(time_int); 
                      	 } catch (NumberFormatException e) { }
       					InMessage("TIMER " + time_st);
       				}
       				else {
       					 round += 1;
       					 String round_msg = String.format("ROUND %d", 10-round);
       					 InMessage(round_msg);
	      					if(n == Word.length) n = 0;
	     	                now_word = n;
	     	                if(k == Examiner.size()) k=0;
	     	                now_ex = k;
	     	                Set_Examiner(Examiner.get(k++));
	      	                send_word(Word[n++]);
	                       String sd_msg = "TIME OVER!";
	       	                InMessage(sd_msg);
	       	                sd_msg = "������ " + Word[round-2];
	       	                InMessage(sd_msg);
	       	                sd_msg = String.format("%s�� ������", Examiner.get(now_ex));
	       	                sd_msg=sd_msg.trim();
	       	                InMessage(sd_msg);
	         			    time_int = 30;
	         			   if (round >10) {
	       	                	timer.cancel();
	       	            	   Set<String> keys = score.keySet();
	       	            	   Iterator<String> it = keys.iterator();
	       	            	   while(it.hasNext()) {
	       	            		   String key = it.next();
	       	            		   try {
	       	            			   String value = Integer.toString(score.get(key));
	       	            			   String score_msg = null;
	       	                		   score_msg = String.format("SN %s",key);
	       	                		   InMessage(score_msg);
	       	                		   score_msg = String.format("SS %s", value);
	       	                		   InMessage(score_msg);
	       	            		   } catch (NumberFormatException e) { }
	       	            	   }
	       	            	   String over = "GAME OVER";
	       	            	   InMessage(over);
	       	            	   all_ready = 0;
	       	            	   user_count=-1;
	       	            	   round= 0;
	       	                 }
       				}
       			}
       	    };
    	  return tempTask;
      }

      public void run() // ������ ����
      {

         while (true) {
            try {
               // ����ڿ��� �޴� �޼���
               byte[] b = new byte[29];
               // read one
               dis.read(b); // UserInfo ��ü�� Ŭ���̾�Ʈ�� �ϳ��� ���� ���
               String msg = new String(b);
               msg = msg.trim();
               // write to screen and write all
               InMessage(msg);
             //����
               if(msg.startsWith("READY")) {
                   String remove = msg.replace("READY ", ""); 
                   remove = remove.trim(); // ���� ����
                   String rcv[] = remove.split(" ");
                   try{
                      int i = Examiner.indexOf(rcv[0]);
                      ready.set(i, rcv[1]);
                   } catch (NumberFormatException e) { }
                }
               if(first_start == 1) {
                   
                   String new_ch = String.format("new_ %s", rcv_str[2]);

                   InMessage(new_ch);//���ο� ����� �����Ѱ�
                   
                   
                       for(int i=0; i<Examiner.size();i++) {
                         String aready = String.format("al_ %s", my_ch.get(i));
                         aready.trim(); 
                         InMessage(aready);// �̹��������ִ°͵�. ���� ������ Ŭ���̾�Ʈ���Ը�
                        //textArea.append("already " +  my_ch.get(i)+ "ĳ����\n");
                       }
                    
                   
                   Examiner.add(rcv_str[0]); // ����ڰ� �߰��� ������ ArrayList�� �߰�.
                   ready.add(rcv_str[1]); // ���̵� ���� ����Ʈ�� �ִ´�
                   my_ch.add(rcv_str[2]);
                   first_start = 2;
               }
               //start��ư�� ���� ��� ����ǵ��� �ٲ����
               if(all_ready == 1) {
                   all_ready = 2;
                   round += 1;
 	                 if (round > 10)
 	                	   all_ready = 3;
                   String round_msg = String.format("ROUND %d", 10-round);
 				  InMessage(round_msg);
                  if(n == Word.length) n = 0;
                  	now_word = n;
                  if(k == Examiner.size()) k=0;
                  	now_ex = k;
                  Set_Examiner(Examiner.get(k++));
                  send_word(Word[n++]);
                  InMessage("TIMER " + time_st);
                  timer = new Timer();
             	 task = timerTaskMarker();
                  timer.schedule(task, 1000,1000); // Ÿ�̸� ����
                }
               else if(all_ready == 0){
                    for(int i=0; i<ready.size();i++) {
                       if(ready.get(i).equals("�غ���")) {
                          user_count ++;
                       }
                    if(user_count == Examiner.size()) all_ready = 1;
                    }
                }
               else if(all_ready == 3) {
            	   timer.cancel();
            	   Set<String> keys = score.keySet();
            	   Iterator<String> it = keys.iterator();
            	   while(it.hasNext()) {
            		   String key = it.next();
            		   try {
            			   String value = Integer.toString(score.get(key));
            			   String score_msg = null;
                		   score_msg = String.format("SN %s",key);
                		   InMessage(score_msg);
                		   score_msg = String.format("SS %s", value);
                		   InMessage(score_msg);
            		   } catch (NumberFormatException e) { }
            	   }
            	   String over = "GAME OVER";
            	   InMessage(over);
            	   all_ready = 0;
            	   user_count=-1;
            	   round= 0;
               }
   
               
               //msg�� ������ ���ԵǾ� ���� ��� ������ �ٲ��(������ ������ ���� ��� ���� ���ڰ� �ٸ�������� �����Ǿ����)
               if(msg.indexOf(Word[now_word]) != -1) {
            	   round += 1;
            	   String round_msg = String.format("ROUND %d", 10-round);
            	   InMessage(round_msg);
                   if (round >10)
                	   all_ready = 3;
                  if(n == Word.length) n = 0;
                  now_word = n;
                  if(k == Examiner.size()) k=0;
                  now_ex = k;
                  Set_Examiner(Examiner.get(k++));
                  send_word(Word[n++]);
                  msg = msg.trim();
                  String [] msg_st = msg.split(" ");
                  msg_st[0] = msg_st[0].replaceAll("\\[", "");
                  msg_st[0] = msg_st[0].replaceAll("\\]", "");
                  if(score.containsKey(msg_st[0])) { // ���� ��� ���� �߰�
                	  score.put(msg_st[0], score.get(msg_st[0])+1);
                  }
                  String sd_msg = String.format("CORRECT %s", msg);
                  InMessage(sd_msg);
                  sd_msg = String.format("%s ����! ", msg);
                  InMessage(sd_msg);
                  sd_msg = String.format("%s�� ������", Examiner.get(now_ex));
                  InMessage(sd_msg);
           	      time_int = 30; // Ÿ�̸� �ʱ�ȭ
                }
            }
            catch (IOException e) 
            {
               
               try {
                  dos.close();
                  dis.close();
                  user_socket.close();
                  vc.removeElement( this ); // �������� ���� ��ü�� ���Ϳ��� �����
                  textArea.append(vc.size() +" : ���� ���Ϳ� ����� ����� ��\n");
                  textArea.append("����� ���� ������ �ڿ� �ݳ�\n");
                  // �ƹ��� ������ Ÿ�̸� ����
                 if (vc.size() == 0)
                	 timer.cancel();
                  textArea.setCaretPosition(textArea.getText().length());
                //���� �Ƹ��� ����
                  textArea.append(rcv_str[0] +"���� ������ �����߽��ϴ�.\n");
                  
                  break;
               
               } catch (Exception ee) {
               
               }// catch�� ��
            }// �ٱ� catch����

         }
         
         
         
      }// run�޼ҵ� ��

   } // ���� userinfoŬ������

}