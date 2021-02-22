// MainView.java : Java Client 의 핵심부분
// read keyboard --> write to network (Thread 로 처리)
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
   //클라이언트 변수
   private JPanel contentPane;
   private JTextField chat_field; // 입력창, 보낼 메세지 쓰는곳
   private String id;
   private String ip;
   private int port;
   private Canvas canvas;
   JButton transmission; // 전송버튼
   private Socket socket; // 연결소켓
   private InputStream is;
   private OutputStream os;
   private DataInputStream dis;
   private DataOutputStream dos;
   
   //UI변수
   JPanel gui_panel, paint_panel,user1_panel, user2_panel,user3_panel,user4_panel,text_panel,time_panel, round_panel, chat_panel; 
   JPanel em_1, em_2, em_3, em_4;
    //  GUI구성 패널, 그려지는 패널, 유저 패널, 닉네임,제시어,시간, 채팅 패널
    JButton pencil_bt, eraser_bt; // 연필,지우개 도구를 선택하는 버튼
    JButton colorSelect_bt; // 색선택 버튼
    JLabel thicknessInfo_label; // 도구굵기 라벨
    JTextField thicknessControl_tf; // 도구굵기가 정해질 텍스트필드
    Color selectedColor; 
    // 현 변수에 컬러가 저장되어 추후에 펜색상을 정해주는 변수의 매개변수로 사용된다.
    ImageIcon garlic;
    ImageIcon onion;
    ImageIcon mushroom;
    ImageIcon celery;
    JTextArea chat_window; // 채팅창, 수신된 메세지를 나타낼 변수
    private JLabel textLabel;

    Graphics graphics; // Graphics2D 클래스의 사용을 위해 선언
    Graphics2D g;
    int thickness = 10; // 현 변수는 그려지는 선의 굵기를 변경할때 변경값이 저장되는 변수
    int startX; // 마우스클릭시작의 X좌표값이 저장될 변수
    int startY; // 마우스클릭시작의 Y좌표값이 저장될 변수
    int endX; // 마우스클릭종료의 X좌표값이 저장될 변수
    int endY; // 마우스클릭종료의 Y좌표값이 저장될 변수
    boolean tf = false;
    /* 변 boolean 변수는 처음에 연필로 그리고 지우개로 지운다음 다시 연필로 그릴때
     기본색인 검은색으로 구분시키고 만약 프로그램 시작시 색선택후 그 선택된 색이
     지우개로 지우고 다시 연필로 그릴때 미리 정해진 색상으로 구분하는 변수*/
    
    // 서버에서 받는 정보 저장 변수
    private int access[] = {0,0,0,0}; // 유저 접속 확인용 정수형 배열
    private int [] point_int = new int [4]; // 좌표 int 배열
    private String [] point_st = new String [4]; // 좌표 string 배열
    private int thicknessAll; // 모든 클라이언트 펜 굵기
    private String[] colorAll_st = new String[3]; // 모든 클라이언트 펜 색상 string 배열
    private int [] colorAll_int = new int[3]; // 펜 색상 int 배열

    //문제
    private String rcv_word;
    private String rcv_examiver;
    
   // 액션
    private PaintDraw pd = new PaintDraw();
    private ToolActionListener ta = new ToolActionListener();
    private  MyMouseListener mm = new MyMouseListener();
    private ColorActionListener ca = new ColorActionListener();
    private Myaction action = new Myaction();
    
    // 시간
    private String time;
    private JLabel timeLabel; // 시간 표시
    
    // 라운드
    private String round;
    private JLabel roundLabel;
    private JLabel roundNumber; // 라운드 표시
    
    // panel에 표시 할 이미지
    private ImageIcon timeover;
    private JLabel timeover_lb;
    private ImageIcon correct;
    private JLabel correct_lb;
    private ImageIcon gameover;
    private JLabel gameover_lb;

    // 레디
    private JButton ready_bt;
    private String imready = "준비아님";
    
    //게임 오버 시 점수
    private String gameover_score = "";
    private String gameover_temp;
    
    private String my_ch;
    
    //버튼 이미지
    private ImageIcon pink_bt;
    private ImageIcon mint_bt;
    private ImageIcon pencil_im;
    private ImageIcon eraser_im;
    private ImageIcon color_im;
    private ImageIcon trans_im;
    
   public MainView(String id, String ip, int port, String my_ch)// 생성자
   {
      this.id = id;
      this.ip = ip;
      this.my_ch = my_ch;
      this.port = port;
      init(); // 화면 구성
      start();
      network(); // 통신 쓰레드 실행
   }

   public void network() {
      // 서버에 접속
      try {
         socket = new Socket(ip, port); // socket, connect까지 완료
         if (socket != null) // socket이 null값이 아닐때 즉! 연결되었을때
         {
            Connection(); // 연결 메소드를 호출
         }
      } catch (UnknownHostException e) {

      } catch (IOException e) {
         chat_window.append("소켓 접속 에러!!\n");
      }
   }

   public void Connection() { // 실직 적인 메소드 연결부분
      try { // 스트림 설정
         is = socket.getInputStream();
         dis = new DataInputStream(is);
         os = socket.getOutputStream();
         dos = new DataOutputStream(os);
      } catch (IOException e) {
         chat_window.append("스트림 설정 에러!!\n");
      }

      id.trim();
      imready.trim();
      String m = String.format("%s %s %s", id, imready,my_ch);
      send_Message(m); // 정상적으로 연결되면 나의 id를 전송
      
      Thread th = new Thread(new Runnable() { // 스레드를 돌려서 서버로부터 메세지를 수신
         @SuppressWarnings("null")
         @Override
         public void run() {
            while (true) {
               try {
                  byte[] b = new byte[29];
                  dis.read(b); // 서버 메시지 무한 대기 쓰레드.
                  String msg = new String(b);
                  msg = msg.trim();
                //문제
                  if(msg.startsWith("new_")) {
                      String remove = msg.replace("new_ ", ""); 
                       remove = remove.trim(); // 공백 제거
                       try{
                         //캐릭터
                          if(remove.equals("garlic")) {
                             garlic= new ImageIcon("./src/garlic.png"); // 캐릭터 (마늘 쿵야)
                               JLabel garlic_lb = new JLabel(garlic);
                               garlic_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
                               em_1.add(garlic_lb);
                               em_1.setBackground(Color.yellow);
                          }
                          else if(remove.equals("onion")) {
                             onion= new ImageIcon("./src/onion.png"); // 캐릭터 (양파 쿵야)
                                JLabel onion_lb = new JLabel(onion);
                              onion_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
                               em_2.add(onion_lb); 
                              em_2.setBackground(Color.green);
                          }
                          else if(remove.equals("mush")) {
                             mushroom= new ImageIcon("./src/mushroom.png"); // 캐릭터 (버섯 쿵야)
                                JLabel mushroom_lb = new JLabel(mushroom);
                              mushroom_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
                               em_3.add(mushroom_lb); 
                              em_3.setBackground(Color.CYAN);
                          }
                          else if(remove.equals("celery")) {
                             celery= new ImageIcon("./src/celery.png"); // 캐릭터 (샐러리 쿵야)
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
                       remove = remove.trim(); // 공백 제거
                       try{
                         //캐릭터
                          if(remove.equals("garlic")) {
                             garlic= new ImageIcon("./src/garlic.png"); // 캐릭터 (마늘 쿵야)
                               JLabel garlic_lb = new JLabel(garlic);
                               garlic_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
                               em_1.add(garlic_lb);
                               em_1.setBackground(Color.yellow);
                          }
                          else if(remove.equals("onion")) {
                             onion= new ImageIcon("./src/onion.png"); // 캐릭터 (양파 쿵야)
                                JLabel onion_lb = new JLabel(onion);
                              onion_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
                               em_2.add(onion_lb); 
                              em_2.setBackground(Color.green); 
                            }
                          else if (remove.equals("mush")) {
                             mushroom= new ImageIcon("./src/mushroom.png"); // 캐릭터 (샐러리쿵야)
                                JLabel mushroom_lb = new JLabel(mushroom);
                              mushroom_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
                               em_3.add(mushroom_lb); 
                              em_3.setBackground(Color.CYAN);
                          }
                          else if (remove.equals("celery")) {
                             celery= new ImageIcon("./src/celery.png"); // 캐릭터 (샐러리쿵야)
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
                      remove = remove.trim(); // 공백 제거
                      try{
                        rcv_examiver = remove;
                      } catch (NumberFormatException e) { }
                   }
                  else if(msg.startsWith("THICKNESS")) { // 펜 굵기
                	 String remove = msg.replace("THICKNESS ", ""); // string에서 'THICKNESS' 제거
                	 remove = remove.trim(); // 공백 제거
                	 try{
                		 thicknessAll = Integer.parseInt(remove); // string을 int로 펜 굵기 설정 ( 다른 클라이언트도 )
                	 } catch (NumberFormatException e) { }
                 }
                  // 시간
                  else if(msg.startsWith("TIMER")) {
                	  String remove = msg.replace("TIMER ", "");
                	  remove = remove.trim();
                	  time = remove;
                	  timeLabel.setText(time);
                  }
                  // 라운드
                  else if(msg.startsWith("ROUND")) {
                	  String remove = msg.replace("ROUND ", "");
                	  remove = remove.trim();
                	  try{
                    	  round = Integer.toString(Integer.parseInt(remove) + 1);
                 	 } catch (NumberFormatException e) { }
                	  roundNumber.setText(round);
                  }
                  // 시간 초과
                  else if(msg.startsWith("TIME")) {
                	  timeover_lb.setVisible(true); // 시간 초과 이미지
                	  Thread.sleep(300);
                	  timeover_lb.setVisible(false);
                  }
                  else if(msg.startsWith("CORRECT")) {
                	  correct_lb.setVisible(true); // 정답 이미지
                	  Thread.sleep(300);
                	  correct_lb.setVisible(false);
                  }
               //문제
                 else if(msg.startsWith("WORD")) {
                	 gameover_lb.setVisible(false);
                     ready_bt.setEnabled(false);// ready버튼 비활성화
                     ready_bt.setText("게임 실행중"); // 게임 실행중임
                     String remove = msg.replace("WORD ", ""); 
                     remove = remove.trim(); // 공백 제거
                     try{
                        rcv_word = remove;
                        // paint_panel에 마우스 모션리스너 추가
                 	// 그리는 함수
                        if(id.equals(rcv_examiver)) {
                        	 paint_panel.addMouseListener(mm); // 좌표 이벤트
                             pencil_bt.addActionListener(ta); // 연필버튼 액션처리
                             eraser_bt.addActionListener(ta); // 지우개버튼 액션처리
                             colorSelect_bt.addActionListener(ca); // 색 지정 이벤트
                             paint_panel.addMouseMotionListener(pd); // 패널 그리기 
                             transmission.removeActionListener(action); // 채팅 이벤트 제거
                             chat_field.removeActionListener(action);
                             textLabel.setText(rcv_word);} // 제시어 텍스트를 바꾼다
                        else {
                        	// 이벤트 처리 제거
                       	   paint_panel.removeMouseListener(mm); // 좌표 이벤트 제거
                        	pencil_bt.removeActionListener(ta); // 연필버튼 액션 제거
                            eraser_bt.removeActionListener(ta); // 지우개버튼 액션 제거
                            colorSelect_bt.removeActionListener(ca); // 색 지정 이벤트 제거
                            paint_panel.removeMouseMotionListener(pd); // 패널 그리기 제거
                            transmission.addActionListener(action); // 채팅 이벤트 추가
                            chat_field.addActionListener(action);
                        textLabel.setText("?");
                        }
                        thicknessControl_tf.setText("10");
                        thickness = 10;
                        thicknessAll = 10;
                        g.setColor(Color.black);
                        paint_panel.repaint(); // 그림 초기화
                     } catch (NumberFormatException e) { }
                  }
                 else if(msg.startsWith("Color")) { // 그리기 색
                	 String remove = msg.replace("Color",  ""); //string에서 'java.awt.Color' 제거
                   	 remove = remove.replace("[r=", "");
                	 remove = remove.replace(",g", "");
                	 remove = remove.replace(",b", "");
                	 remove = remove.replace("]", ""); // =와 숫자만 남기고 제거
                	 remove = remove.trim(); // 공백제거
                	 colorAll_st = remove.split("\\="); // colorAll_st에 r, g, b 저장
                	 for (int i=0; i<3; i++) {
                		 try {
                    		 colorAll_int[i] = Integer.parseInt(colorAll_st[i]);
                		 } catch (NumberFormatException e) { }
                	}
                	 g.setColor(new Color(colorAll_int[0],colorAll_int[1],colorAll_int[2])); // 다른 클라이언트도 색 지정
                 }
                 else if(msg.startsWith("POINT")) { // 그림 그리기
                     String remove = msg.replace("POINT ", ""); // string에서 'POINT' 제거
                     point_st  = remove.split(" "); // poinst_st에 string type으로 좌표 4개 저장
                        for(int i=0; i<4; i++) {
                           try{
                              point_int[i] = Integer.parseInt(point_st[i]); // string type을 int type으로 좌표 4개 저장
                           }catch (NumberFormatException e) { }
                        }
                        Draw(point_int[0],point_int[1],point_int[2],point_int[3]); // 다른 클라이언트에게도 그림
                  }
                 else if (msg.startsWith("ERASER")) { // 지우개 설정
                		 g.setColor(Color.WHITE);
                 }
                 else if(msg.startsWith("SN")) { // 닉네임
                	 msg = msg.replace("SN ", "");
                	 msg = msg.trim();
                	gameover_temp = msg;
                 }
                 else if(msg.startsWith("SS")) { // 점수
                	 msg = msg.replace("SS ", "");
                	 msg = msg.trim();
                	gameover_temp = gameover_temp + " : " + msg + "\n";
                	gameover_score = gameover_score + gameover_temp;
                 }
                 else if(msg.startsWith("GAME")) { // 게임 오버
                	 gameover_lb.setVisible(true);
                 	  paint_panel.removeMouseListener(mm); // 좌표 이벤트 제거
                 	  pencil_bt.removeActionListener(ta); // 연필버튼 액션 제거
                      eraser_bt.removeActionListener(ta); // 지우개버튼 액션 제거
                      colorSelect_bt.removeActionListener(ca); // 색 지정 이벤트 제거
                      paint_panel.removeMouseMotionListener(pd); // 패널 그리기 제거
                      transmission.removeActionListener(action); // 채팅 이벤트 제거
                      chat_field.removeActionListener(action);
                      ready_bt.setEnabled(true); // 레디 버튼 활성화
                      ready_bt.setText(""); 
                      ready_bt.setIcon(pink_bt);
                      imready = "준비아님";
                      JOptionPane.showMessageDialog(null, gameover_score);               
                 }
                 else { // 채팅
                	 msg = msg.trim();
                    chat_window.append(msg + "\n");
                    chat_window.setCaretPosition(chat_window.getText().length());   
                 }
               } catch (IOException e) {
                  chat_window.append("메세지 수신 에러!!\n");
                  // 서버와 소켓 통신에 문제가 생겼을 경우 소켓을 닫는다
                  try {
                     os.close();
                     is.close();
                     dos.close();
                     dis.close();
                     socket.close();
                     break; // 에러 발생하면 while문 종료
                  } catch (IOException e1) {
                  }
               } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            } // while문 끝
         }// run메소드 끝
      });
      th.start();
   }

   public void send_Message(String str) { // 서버로 메세지를 보내는 메소드
      try {
         byte[] bb = new byte[29];
         String s = String.format("%-29s", str);
         bb = s.getBytes();
         dos.write(bb);
      } catch (IOException e) {
         chat_window.append("메세지 송신 에러!!\n");
      }
   }

   public void init() { // 화면구성 메소드
      setLayout(null); // 기본 프레임의 레이아웃을 초기화 시켜 패널을 개발자가 직접 다룰수 있게 됨
        setTitle("캐치마인드"); // 프레임 타이틀 지정
        setSize(1520,1000); // 프레임 사이즈 지정
        setLocationRelativeTo(null); // 프로그램 실행시 화면 중앙에 출력
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        // 프레임 우측상단에 X버튼을 눌렀을떄의 기능 정의
        
        //gui_panel
        gui_panel = new JPanel(); // 프레임 상단에 버튼, 텍스트필드, 라벨등이 UI가 들어갈 패널
        gui_panel.setBackground(new Color(195,195,195)); // 패널의 배경색을 회색으로 지정
        gui_panel.setLayout(null); 
        // gui_panel의 레이아웃을 null지정하여 컴포넌트들의 위치를 직접 지정

        pencil_im = new ImageIcon("./src/pencil.png");
        pencil_bt = new JButton("",pencil_im); // 연필 버튼 생성
        eraser_im = new ImageIcon("./src/eraser.png");
        eraser_bt = new JButton("",eraser_im); // 지우개 버튼 생성
        color_im = new ImageIcon("./src/color.png");
        colorSelect_bt = new JButton("",color_im); // 선색상 버튼 생성
        
        thicknessInfo_label = new JLabel("도구굵기"); 
        // 도구굵기 라벨 지정 / 밑에서 나올 텍스트필드의 역할을 설명
        thicknessInfo_label.setFont(new Font("함초롬돋움", Font.BOLD, 20));
        // 도구굵기 라벨 폰트및 글씨 크기 지정
        
        thicknessControl_tf = new JTextField("10", 5); // 도구굵기 입력 텍스트필드 생성
        thicknessControl_tf.setHorizontalAlignment(JTextField.CENTER); 
          // 텍스트필드 라인에 띄어지는 텍스트 중앙 정렬
        thicknessControl_tf.setFont(new Font("궁서체", Font.PLAIN, 25)); 
          // 텍스트필드 X길이 및 폰트 지정
        
        pencil_bt.setBounds(10,10,90,55); // 연필 버튼 위치 지정
        eraser_bt.setBounds(105,10,109,55); // 지우개 버튼 위치 지정
        colorSelect_bt.setBounds(785,10,90,55); // 선색상 버튼 위치 지정
        thicknessInfo_label.setBounds(640,10,100,55); // 도구굵기 라벨 위치 지정
        thicknessControl_tf.setBounds(720,22,50,35); // 도구굵기 텍스트필드 위치 지정
        
        gui_panel.add(pencil_bt); // gui_panel에 연필 버튼 추가
        gui_panel.add(eraser_bt); // gui_panel에 지우개 버튼 추가
        gui_panel.add(colorSelect_bt); // gui_panel에 선색상 버튼 추가
        gui_panel.add(thicknessInfo_label); // gui_panel에 도구굵기 라벨 추가
        gui_panel.add(thicknessControl_tf); // gui_panel에 도구굵기 텍스트필드 추가
        
        gui_panel.setBounds(300,100,900,75); // gui_panel이 프레임에 배치될 위치 지정
        
        //paint_panel
        paint_panel = new JPanel(); // 그림이 그려질 패널 생성
        paint_panel.setBackground(Color.WHITE); // 패널의 배경색 하얀색
        paint_panel.setLayout(null); 
        // paint_panel의 레이아웃을 null해줘 패널 자체를 setBounds로 위치를 조정할수 있다.
        
        paint_panel.setBounds(300,190,900,460); // paint_panel의 위치 조정
        
        // paint_panel의 이미지 추가 
        // time over
        timeover = new ImageIcon("./src/timeover.png");
        timeover_lb = new JLabel(timeover);
        timeover_lb.setBounds(0, 0, 900, 460);
        paint_panel.add(timeover_lb);
        timeover_lb.setVisible(false);
        // 정답을 맞췄을 경우
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
        //user1_panel (마늘 쿵야)
        user1_panel = new JPanel();
        user1_panel.setLayout(null);
        user1_panel.setBackground(Color.YELLOW);
        garlic = new ImageIcon("./src/garlic.png"); // 캐릭터 (마늘쿵야)
        JLabel garlic_lb = new JLabel(garlic);
        garlic_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
        user1_panel.add(garlic_lb);
        JLabel garlic_nickname = new JLabel("Garlic");
        garlic_nickname.setFont(new Font("함초롬돋움", Font.BOLD, 20));
        garlic_nickname.setBounds(65, 70, 200, 200);
        user1_panel.add(garlic_nickname);
        user1_panel.setBounds(50,150,200,200);
        
        //user2_panel (양파 쿵야)
        user2_panel = new JPanel();
        user2_panel.setLayout(null);
        user2_panel.setBackground(Color.GREEN);
        onion = new ImageIcon("./src/onion.png"); // 캐릭터 (양파쿵야)
        JLabel onion_lb = new JLabel(onion);
        onion_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
        user2_panel.add(onion_lb);
        JLabel onion_nickname = new JLabel("Onion");
        onion_nickname.setFont(new Font("함초롬돋움", Font.BOLD, 20));
        onion_nickname.setBounds(70, 80, 200, 200);
        user2_panel.add(onion_nickname);
        user2_panel.setBounds(1250,450,200,200);
        user2_panel.setBounds(50,450,200,200);
        
      //user3_panel (버섯 쿵야)
        user3_panel = new JPanel();
        user3_panel.setLayout(null);
        user3_panel.setBackground(Color.CYAN);
        mushroom = new ImageIcon("./src/mushroom.png"); // 캐릭터 (버섯쿵야)
        JLabel mushroom_lb = new JLabel(mushroom);
        mushroom_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
        user3_panel.add(mushroom_lb);
        JLabel mushroom_nickname = new JLabel("Mushroom");
        mushroom_nickname.setFont(new Font("함초롬돋움", Font.BOLD, 20));
        mushroom_nickname.setBounds(45, 70, 200, 200);
        user3_panel.add(mushroom_nickname);
        user3_panel.setBounds(1250,150,200,200);
        
      //user4_panel (샐러리 쿵야)
        user4_panel = new JPanel();
        user4_panel.setLayout(null);
        user4_panel.setBackground(Color.PINK);
        celery= new ImageIcon("./src/celery.png"); // 캐릭터 (샐러리쿵야)
        JLabel celery_lb = new JLabel(celery);
        celery_lb.setBounds(0, 0, 200, 200); // 크기 200x200 픽셀
        user4_panel.add(celery_lb);
        JLabel celery_nickname = new JLabel("Celery");
        celery_nickname.setFont(new Font("함초롬돋움", Font.BOLD, 20));
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
        textLabel = new JLabel("제시어");
        textLabel.setFont(new Font("함초롬돋움", Font.BOLD, 60));
        text_panel.add(textLabel);
        text_panel.setBounds(600,0,300,200);
        
        //time_panel
        time_panel = new JPanel();
        timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("함초롬돋움", Font.BOLD, 60));
        time_panel.add(timeLabel);
        time_panel.setBounds(0,750,300,100);
        
        //round_panel
        round_panel = new JPanel();
        roundLabel = new JLabel("ROUND");
        roundNumber = new JLabel("10");
        roundLabel.setFont(new Font("함초롬돋움",Font.BOLD,40));
        round_panel.add(roundLabel);
        round_panel.setBounds(1200,820,300,100);
        roundNumber.setFont(new Font("함초롬돋움",Font.BOLD,40));
        round_panel.add(roundNumber);
        roundNumber.setBounds(0, 100, 300, 100);
        
        //chat_panel
        JScrollPane js = new JScrollPane();
        chat_panel = new JPanel();
        chat_panel.setLayout(null);
        chat_window = new JTextArea();
        chat_window.setEditable(false); // 채팅창 입력 막음
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
        
        // 버튼
        pink_bt = new ImageIcon("./src/pink_bt.png");
        ready_bt = new JButton("",pink_bt); // 준비 (핑크)
        ready_bt.setBounds(1260,740,200,50);
        add(ready_bt);
        
        add(gui_panel); // 메인프레임에 gui패널 추가 - 위치는 위에서 다 정해줌
        add(paint_panel); // 메인프레임에 paint패널 추가 - 위치는 위에서 다 정해줌
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
        
        setVisible(true); // 메인프레임을 보이게 한다.
        
        graphics = getGraphics(); // 그래픽초기화
        g = (Graphics2D)graphics; 
        // 기존의 graphics변수를 Graphics2D로 변환후 Graphics2D에 초기화
        // 일반적인 Graphics가 아닌 Graphics2D를 사용한 이유는 펜의 굵기와 관련된 기능을
        //수행하기 위하여 Graphics2D 클래스를 객체화함
        g.setColor(selectedColor); 
        // 그려질 선(=선도 그래픽)의 색상을 selectedColor의 값으로 설정
        
        /////////////////////////////////////////////////// ↓ 액션 처리부분

   }

   public void start() { // 액션이벤트 지정 메소드
      transmission.addActionListener(action);
      chat_field.addActionListener(action);
      Ready_Action ready_action = new Ready_Action();
      ready_bt.addActionListener(ready_action);
   }
   
   class Ready_Action implements ActionListener{
	      @Override
	      public void actionPerformed(ActionEvent arg0) {
	         if(imready.equals("준비아님")) { // 준비를 하지 않은 상태라면 ...
	            imready = "준비함";
	            String msg = null;
	               msg = String.format("READY %s %s\n", id,imready );// 서버에게 사용자와 사용자의 상태를 보낸다. 준비한 상태임을 알린다
	               send_Message(msg);
	               ready_bt.setText(""); // 민트 (준비 취소)
	         }
	         else if(imready.equals("준비함")) {// 이미 준비한 상태라면
	            imready = "준비아님"; // 준비 상태를 푼다
	            String msg = null;
	               msg = String.format("READY %s %s\n", id,imready );// 서버에게 사용자와 사용자의 상태를 보낸다. 준비상태를 풀어준다
	               send_Message(msg);
	               ready_bt.setText(""); // 핑크 (준비)
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
        startX = e.getX(); // 마우스가 눌렸을때 그때의 X좌표값으로 초기화
        startY = e.getY(); // 마우스가 눌렸을때 그때의 Y좌표값으로 초기화
	}
	@Override
	public void mouseReleased(MouseEvent e) { }
	
   }

   class Myaction implements ActionListener // 내부클래스로 액션 이벤트 처리 클래스
   {
      @Override
      public void actionPerformed(ActionEvent e) {
         // 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
         if (e.getSource() == transmission || e.getSource() == chat_field) 
         {
            String msg = null;
            msg = String.format("[%s] %s \n", id, chat_field.getText());
            send_Message(msg);
            chat_field.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
            chat_field.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다            
         }
      }
   }
   
   public void Draw(int x1, int y1, int x2, int y2) { // 모든 클라이언트 그리기 용
       g.setStroke(new BasicStroke(thicknessAll, BasicStroke.CAP_ROUND,0)); //선굵기
       g.drawLine(x1, y1, x2, y2); // 라인이 그려지게 되는부분
   }
   
   public class PaintDraw implements MouseMotionListener {
        // 위에서 paint_panel에 MouseMotionListener액션 처리가 될때 현 클래스로 넘어와서 밑 문장을 실행

        @Override
        public void mouseDragged(MouseEvent e) { 
            // paint_panel에서 마우스 드래그 액션이처리될 때 밑 메소드 실행
        		// 펜 굵기 서버에 보냄
                String th = null;
                th = String.format("THICKNESS %s", thicknessControl_tf.getText());
                send_Message(th);
                
                thickness = Integer.parseInt(thicknessControl_tf.getText());
                // 텍스트필드부분에서 값을 값고와 thickness변수에 대입
                
                    endX = e.getX(); 
                    // 드래그 되는 시점에서 X좌표가 저장 - 밑에서 시작좌표와 끝좌표를 연결 해주어 선이 그어지게된다.
                    
                    if (endX <= 0+thickness)
                       endX = 0+thickness;
                    if (endX >= 900-thickness)
                       endX = 900-thickness;

                    endY = e.getY(); 
                   // 드래그 되는 시점에서 Y좌표가 저장 - 밑에서 시작좌표와 끝좌표를 연결 해주어 선이 그어지게된다.
                    
                   if(endY <= 0+thickness)
                      endY = 0+thickness;
                    if (endY >= 452-thickness)
                       endY = 452-thickness;
     
                    g.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND,0)); //선굵기
                    g.drawLine(startX+310, startY+238, endX+310, endY+238); // 라인이 그려지게 되는부분
                    // 좌표 서버에게 보내줌
                    
                    String point = null;
                    point = String.format("POINT %05d %05d %05d %05d", startX+310, startY+238, endX+310, endY+238);
                    send_Message(point);
                    
                    startX = endX; 
                            // 시작부분이 마지막에 드래그된 X좌표로 찍혀야 다음에 이어 그려질수 있다.
                    startY = endY;
                            // 시작부분이 마지막에 드래그된 Y좌표로 찍혀야 다음에 이어 그려질수 있다.
        }
        @Override
        public void mouseMoved(MouseEvent e) {}
    }
   
   public class ColorActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			tf = true;
            JColorChooser chooser = new JColorChooser(); // JColorChooser 클래스객체화
            selectedColor = chooser.showDialog(null, "Color", Color.ORANGE); 
            // selectedColor에 선택된색으로 초기화
            g.setColor(selectedColor); // 그려지는 펜의 색상을 selectedColor를 매개변수로 하여 지정
            
            String color = null;
            color = String.format("%s", selectedColor);
            color = color.replace("java.awt.", "");
            send_Message(color);
		}
	   
   }
    
    public class ToolActionListener implements ActionListener {
        // 연필,지우개 버튼의 액션처리시 실행되는 클래스
        public void actionPerformed(ActionEvent e ) {
            // 오버라이딩된 actionPerformed메소드 실행
            if(e.getSource() == pencil_bt) { // 연필버튼이 눌렸을떄
            	// 서버로 지우개 상태 OFF 보냄
                if(tf == false) g.setColor(Color.BLACK); // 그려지는 색상을 검은색 지정
                else g.setColor(selectedColor);  // 그려지는 색상을 selectedColor변수의 값으로 지정
            } else if(e.getSource() == eraser_bt) { // 지우개버튼이 눌렸을 때
            	// 서버로 지우개 상태 ON 보냄
            	 String eraser = null;
                 eraser = "ERASER";
                 send_Message(eraser);
                 g.setColor(Color.WHITE);
            }
        }
    }
}