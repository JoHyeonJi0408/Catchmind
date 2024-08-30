## 📌 프로젝트 소개  
학부생 시절 ‘네트워크 프로그래밍’ 과목의 기말 프로젝트입니다.  
소켓 프로그래밍을 활용하여 넷마블 ‘캐치마인드’ 게임을 모작하였습니다.

## 📌 프로토콜 흐름
### 1 . 게임 방 생성 프로토콜 흐름
가장 먼저 들어온 Client1이 방장이 됩니다. 인원은 최대 4명까지 설정 가능합니다.  
- Client1 접속 : Client1 -> Server (CONN Client1)  
- Client2 접속 : Client2 -> Server (CONN Client2)  
- Server -> Client1, Client2 (CREAT ROOM 1 CONN Client1 CONN Client2)  

1 ) Client1이 방 인원을 2명으로 설정했을 경우  
- Client3 접속 : Client3 -> Server (CONN Client3)  
- Client4 접속 : Client4 -> Server (CONN Client4)  
- Server -> Client3, Client4 (CREAT ROOM 2 CONN Client3 CONN Client4)  

2 ) Client1이 방 인원을 4명으로 설정했을 경우  
- Client3 접속 : Client3 -> Server (CONN Client3)  
- Server -> Client1, Client2, Client3 (CONN Client3)  
- Client4 접속 : Client4 -> Server (CONN Client4)  
- Server -> Client1, Client2, Client3, Client4 (CONN Client4)  

### 2. 게임 진행 프로토콜 흐름
방장은 처음 방을 만든 Client로 자동 지정됩니다.  
방장을 제외한 모든 참가자가 Ready일 경우에 게임 Start 가능합니다.  
- Client2, Client3, Client4 -> Server (READY Client2, Client3, Client4)  
- Client1 -> Server (START Client1)  
- Server -> All Client (GAME START)  

방장이 처음 PAINTER가 되고 채팅 중에 정답을 맞추면 그 사람이 PAINTER 됩니다.  
PAINTER일 경우에는 채팅을 할 수 없습니다.  

1 ) 정답이 아닐 경우 (채팅으로 처리)
- Client2 -> Server (MESSAGE Client2)
- Server -> All Client (MESSAGE Client2)

2 ) 정답일 경우 (PAINTER 교체)
- Client3 -> Server (MESSAGE Client3)
- Server -> All Client (MESSAGE Client3 CORRECT, SCORE Client3 , PAINTER Client3)

### 3. 게임 오버 프로토콜 흐름
주어진 문제가 모두 끝났을 경우 최종 승리자는 방장이 됩니다.  
퇴장하고 싶은 사람은 퇴장이 가능합니다.
- Server -> ALL Client (GAME END, WIN Client3)  
- Client1 -> Server (EXIT Client1)  
- Server -> All Client (EXIT Client1)
 
모두 퇴장했을 경우  
- Server -> All Client (EXIT All Client)
- Server (ROOM1 CLOSE)


## 📌 시스템 구성도
![image](https://github.com/user-attachments/assets/31e33c6e-752d-45c7-b38e-3fb6c8fedbdf)

## 📌 플레이 화면
![image](https://github.com/user-attachments/assets/e64501f7-4ac8-4a0f-a915-64d834ec8844)
