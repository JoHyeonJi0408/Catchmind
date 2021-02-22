// Java Server 코드

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
   private JButton Start; // 서버를 실행시킨 버튼
   JTextArea textArea; // 클라이언트 및 서버 메시지 출력

   private ServerSocket socket; //서버소켓
   private Socket soc; // 연결소켓 
   private int Port; // 포트번호
   private Vector vc = new Vector(); // 연결된 사용자를 저장할 벡터
   
   // 문제
   private String[] Word = {"고양이", "강아지","컴퓨터","사과","가방","휴대폰","공원","커피머신","노트북","화분"};
   private int k=0, n=0; // for문을 돌릴것임 이걸로
   private int now_ex=0, now_word = 0;
   
   // 시간
   private int time_int = 30;
   private String time_st= "30";
   private Timer timer;
   private static TimerTask task; // 재정의
   
   // 남은 round
	private int round = 0; // game round 수
	private String round_st = "10";
	
	// 시작
	private String rcv_str[];
	private int all_ready = 0; // 0:준비안됨 1:모두준비 2:게임시작중 3: 게임종료
	private int user_count = 0;
	private ArrayList<String> ready = new ArrayList<>();
	private ArrayList<String> Examiner = new ArrayList<>();
	
	// 점수
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

   private void init() { // GUI를 구성하는 메소드      
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
      Start = new JButton("서버 실행");
      
      Myaction action = new Myaction(); // enter key
      Start.addActionListener(action); // 내부클래스로 액션 리스너를 상속받은 클래스로
      Start.setBounds(0, 290, 264, 37);
      contentPane.add(Start);
      textArea.setEditable(false); // textArea를 사용자가 수정 못하게끔 막는다.   
   }
   
   class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
   {
      @Override
      public void actionPerformed(ActionEvent e) {
         Port = 30000; // 포트 번호 고정
         server_start();
      }
   }
   
   private void server_start() {
      try {
         socket = new ServerSocket(Port); // socket, bind, listen 서버가 포트 여는부분
         Start.setText("서버실행중");
         Start.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
         
         if(socket!=null) // socket 이 정상적으로 열렸을때
         {
            Connection();
         }
         
      } catch (IOException e) {
         textArea.append("소켓이 이미 사용중입니다...\n");

      }

   }

   private void Connection() {
      Thread th = new Thread(new Runnable() { // 사용자 접속을 받을 스레드
         @Override
         public void run() {
            while (true) { // 사용자 접속을 계속해서 받기 위해 while문
               try {
                  textArea.append("사용자 접속 대기중...\n");
                  soc = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
                  textArea.append("사용자 접속!!\n");
                  UserInfo user = new UserInfo(soc, vc); // 연결된 소켓 정보는 금방 사라지므로, user 클래스 형태로 객체 생성
                                   // 매개변수로 현재 연결된 소켓과, 벡터를 담아둔다
                  vc.add(user); // 해당 벡터에 사용자 객체를 추가
                  user.start(); // 만든 객체의 스레드 실행
               } catch (IOException e) {
                  textArea.append("!!!! accept 에러 발생... !!!!\n");
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

      public UserInfo(Socket soc, Vector vc) // 생성자메소드
      {
         // 매개변수로 넘어온 자료 저장
         this.user_socket = soc; // accept에서 넘겨준 socket 통로
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
            dis.read(b); // 각각의 쓰레드는 client ID 수신, 무한 대기
           String str = new String(b);
           rcv_str = str.split(" ");
          textArea.append("ID " + rcv_str[0]+ " 접속\n");
          textArea.setCaretPosition(textArea.getText().length());
          String  entrance = rcv_str[0] + "님이 입장하셨습니다.";
          entrance = entrance.trim();
          send_Message(entrance); // 연결된 사용자에게 정상접속을 알림
          InMessage(entrance); // 다른 사용자에게도 정상접속을 알림
          rcv_str[0].trim();//아이디
          rcv_str[1].trim();//준비
          rcv_str[2].trim();//캐릭터
          score.put(rcv_str[0], new Integer(0)); // 사용자 점수 등록 (0점)
          //Examiner.add(rcv_str[0]); // 사용자가 추가될 때마다 ArrayList에 추가.
          //ready.add(rcv_str[1]); // 아이디 따로 리스트에 넣는다
          first_start =1 ;
         } catch (Exception e) {
            textArea.append("스트림 셋팅 에러\n");
            textArea.setCaretPosition(textArea.getText().length());
         }
      }

      // 클라이언트 메세지를 화면에 출력하고, 모든 클라이언트에게 방송한다.
      public void InMessage(String str) {
      textArea.append(str + "\n"); // server에 쓰는 것
      textArea.setCaretPosition(textArea.getText().length());
      // 사용자 메세지 처리
      str = str.trim();
      broad_cast(str);
      }

      // 방송 함수
      public void broad_cast(String str) {
    	  str = str.trim();
         for (int i = 0; i < user_vc.size(); i++) {
            UserInfo imsi = (UserInfo) user_vc.elementAt(i);
            imsi.send_Message(str);
         }
      }

      public void send_Message(String str) { // client로
         try {
            byte[] bb = new byte[29];
            String s = String.format("%-29s", str);
            bb = s.getBytes();
            dos.write(bb); 
         } 
         catch (IOException e) {
            textArea.append("메시지 송신 에러 발생\n");   
            textArea.setCaretPosition(textArea.getText().length());
         }
      }
      
    //문제
      public void send_word(String str) {
         String word_msg = String.format("WORD %s", str);
         InMessage(word_msg);
      }
      public void Set_Examiner(String str) {
         String word_msg = String.format("EXAMINER %s", str);
         InMessage(word_msg);
 
      }
      
      // TimerTask 재정의 함수
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
	       	                sd_msg = "정답은 " + Word[round-2];
	       	                InMessage(sd_msg);
	       	                sd_msg = String.format("%s가 출제자", Examiner.get(now_ex));
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

      public void run() // 스레드 정의
      {

         while (true) {
            try {
               // 사용자에게 받는 메세지
               byte[] b = new byte[29];
               // read one
               dis.read(b); // UserInfo 객체는 클라이언트당 하나씩 무한 대기
               String msg = new String(b);
               msg = msg.trim();
               // write to screen and write all
               InMessage(msg);
             //문제
               if(msg.startsWith("READY")) {
                   String remove = msg.replace("READY ", ""); 
                   remove = remove.trim(); // 공백 제거
                   String rcv[] = remove.split(" ");
                   try{
                      int i = Examiner.indexOf(rcv[0]);
                      ready.set(i, rcv[1]);
                   } catch (NumberFormatException e) { }
                }
               if(first_start == 1) {
                   
                   String new_ch = String.format("new_ %s", rcv_str[2]);

                   InMessage(new_ch);//새로운 사용자 접속한것
                   
                   
                       for(int i=0; i<Examiner.size();i++) {
                         String aready = String.format("al_ %s", my_ch.get(i));
                         aready.trim(); 
                         InMessage(aready);// 이미접속해있는것들. 지금 접속한 클라이언트에게만
                        //textArea.append("already " +  my_ch.get(i)+ "캐릭터\n");
                       }
                    
                   
                   Examiner.add(rcv_str[0]); // 사용자가 추가될 때마다 ArrayList에 추가.
                   ready.add(rcv_str[1]); // 아이디 따로 리스트에 넣는다
                   my_ch.add(rcv_str[2]);
                   first_start = 2;
               }
               //start버튼을 누를 경우 실행되도록 바꿔야함
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
                  timer.schedule(task, 1000,1000); // 타이머 시작
                }
               else if(all_ready == 0){
                    for(int i=0; i<ready.size();i++) {
                       if(ready.get(i).equals("준비함")) {
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
   
               
               //msg에 정답이 포함되어 있을 경우 문제가 바뀐다(누군가 정답을 맞춘 경우 문제 출자가 다른사람으로 지정되어야함)
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
                  if(score.containsKey(msg_st[0])) { // 맞춘 사람 점수 추가
                	  score.put(msg_st[0], score.get(msg_st[0])+1);
                  }
                  String sd_msg = String.format("CORRECT %s", msg);
                  InMessage(sd_msg);
                  sd_msg = String.format("%s 정답! ", msg);
                  InMessage(sd_msg);
                  sd_msg = String.format("%s가 출제자", Examiner.get(now_ex));
                  InMessage(sd_msg);
           	      time_int = 30; // 타이머 초기화
                }
            }
            catch (IOException e) 
            {
               
               try {
                  dos.close();
                  dis.close();
                  user_socket.close();
                  vc.removeElement( this ); // 에러가난 현재 객체를 벡터에서 지운다
                  textArea.append(vc.size() +" : 현재 벡터에 담겨진 사용자 수\n");
                  textArea.append("사용자 접속 끊어짐 자원 반납\n");
                  // 아무도 없으면 타이머 종료
                 if (vc.size() == 0)
                	 timer.cancel();
                  textArea.setCaretPosition(textArea.getText().length());
                //여기 아마도 오류
                  textArea.append(rcv_str[0] +"님이 게임을 종료했습니다.\n");
                  
                  break;
               
               } catch (Exception ee) {
               
               }// catch문 끝
            }// 바깥 catch문끝

         }
         
         
         
      }// run메소드 끝

   } // 내부 userinfo클래스끝

}