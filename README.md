# WorkInAndOutApp
my very first kotlin multiplatform mobile application (he he)

회사에 출퇴근 기록 관리를 위한 웹사이트가 있는데 <br>
출퇴근 버튼 누르는 것을 웹사이트에서만 할 수 있는 점이 불편해서 시도한 개인적인 앱 프로젝트<br>
자바 11 스프링 부트 API를 사용한다. (https://github.com/semihumanbeing/WorkInAndOutAPI)<br>

---
기능
- 로그인, 회원가입
  - 회원가입 시 이름, 출퇴근기록관리 사이트의 아이디랑 비번을 넣고 가입한다.
  - 암호화 되어 저장 되나 같은 계정 정보로 원격 로그인을 하기 때문에 복호화 할 수 있음
- 메인 화면
  - 로그인 시 기록관리 웹사이트의 출퇴근 버튼이 눌러져 있는지 조회한다.
  - 이미 출근, 퇴근 되어 있으면 해당 버튼은 비활성화 된다. (스케줄러로 매일 밤 12시 초기화)
- 출근, 퇴근 버튼
  - 자바 서버에서 나의 계정으로 로그인 한 다음 각종 팝업이 있으면 끄고 출근, 퇴근 버튼을 눌러준다.
  - 셀레니움을 사용해서 직접 요소를 클릭해 준다 .. 이는 최대 10초가 걸리므로 비동기 처리된다.
  - 또한 윈도우 서버를 사용하고, 셀레니움으로 항상 화면이 조회되도록 처리해야한다.
 
---
사실 결과적으로는 셀레니움의 호환성이 너무 떨어지고,
랜덤으로 나오는 팝업들을 닫아 주어야 다음 화면으로 넘어가는 부분이 너무 불안정했다.
출퇴근 기록 관리 웹사이트에서 가끔 클릭을 해도 다음날에 보면 출근 퇴근 클릭이 되어 있지 않은 적도 있었다.
더 나은 원격 라이브러리를 찾아야 할 것 같다..
그래도 코틀린을 아무것도 모르는 상태로 어플을 만들어보는 것은 재미있었다 



