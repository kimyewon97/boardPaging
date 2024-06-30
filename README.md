# developer

[수정하기 구현 2024.05.22]

○ part1. 수정하기 버튼을 누르면 id를 통해 회원을 조회 후 회원 정보 수정창 띄우기
1) listMembers.jsp에 브라우저 화면에 수정하기 보이도록 구현 
* listMembers.jsp 경로: [src\main\webapp\WEB-INF\views\member\listMembers.jsp] → *25, 37 line*
2) MemberController와 MemberControllerImpl에서 modMemberForm.do가 호출이 되면 접근하도록 modMemberForm 메서드 틀 잡기 
* MemberController 경로 : [src\main\java\com\myspring\pro27\member\controller\MemberController.java] → *18 line*
* MemberControllerImpl 경로 : [src\main\java\com\myspring\pro27\member\controller\MemberControllerImpl.java] → *131 -146 line* 
3) MemberService와 MemberServiceImpl 클래스에 findMember(조회) 메서드 추가 / DAO연결
* MemberService 경로: [src\main\java\com\myspring\pro27\member\service\MemberService.java] → *16 line*
* MemberServiceImpl 경로: [src\main\java\com\myspring\pro27\member\service\MemberServiceImpl.java] → *45 - 49 line*
4) MemberDAO와 MemberDAOImpl 클래스에 findMember(조회) 메서드 추가 / 데이터 연결
* MemberDAO 경로 : [src\main\java\com\myspring\pro27\member\dao\MemberDAO.java] → *6 line*
* MemberDAOImpl 경로: [src\main\java\com\myspring\pro27\member\dao\MemberDAOImpl.java] → *44 - 48 line*
5) member.xml 클래스에 회원 정보 조회(findMember) sql문 작성
* member.xml 경로: [src\main\resources\mybatis\mappers\member.xml] → *56 - 62 line*
6) MemberControllerImpl 데이터 접근, 회원 정보 추가 나머지 코드 작성
* MemberControllerImpl 경로 : [src\main\java\com\myspring\pro27\member\controller\MemberControllerImpl.java] → *141 - 142 line*

○ part2. 회원 정보를 수정한 후 수정하기 버튼을 클릭하면 수정 반영이 되고 리스트로 이동하기
1) MemberController와 MemberControllerImpl에서 modMember.do가 호출이 되면 접근하도록 modMember(수정) 메서드 생성 후 작성
* MemberController 경로 : [src\main\java\com\myspring\pro27\member\controller\MemberController.java] → *20 line*
* MemberControllerImpl 경로 : [src\main\java\com\myspring\pro27\member\controller\MemberControllerImpl.java] → *149 - 160 line* 
2) MemberService와 MemberServiceImpl 클래스에 modMember(수정) 메서드 추가 / DAO연결
* MemberService 경로: [src\main\java\com\myspring\pro27\member\service\MemberService.java] → *19 line*
* MemberServiceImpl 경로: [src\main\java\com\myspring\pro27\member\service\MemberServiceImpl.java] → *51-54 line*
3) MemberDAO와 MemberDAOImpl 클래스에 modMember(수정) 메서드 추가 / 데이터 연결
* MemberDAO 경로 : [src\main\java\com\myspring\pro27\member\dao\MemberDAO.java] → *17 line*
* MemberDAOImpl 경로: [src\main\java\com\myspring\pro27\member\dao\MemberDAOImpl.java] → *51 - 55 line*
4)  member.xml 클래스에 회원 정보 수정 후 업데이트(modMember) sql문 작성
* member.xml 경로: [src\main\resources\mybatis\mappers\member.xml] → *65 - 72 line*
