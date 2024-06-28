package sec03.brd08;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {
	
	private DataSource dataFactory;
	Connection conn;
	PreparedStatement pstmt;

	public BoardDAO() {
		try {
			//db에 접근
			Context ctx = new InitialContext();
			Context envContext = (Context) ctx.lookup("java:/comp/env");
			dataFactory = (DataSource) envContext.lookup("jdbc/oracle"); //톰켓에 미리 연결한 DataSource 받아옴
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List selectAllArticles() {//전체 글 목록 조회
		List articlesList = new ArrayList();
		try {
			conn = dataFactory.getConnection(); //데이터베이스 연결
			
			String query = "SELECT LEVEL,articleNO,parentNO,title,content,id,writeDate" 
			             + " from t_board"
					     + " START WITH  parentNO=0" + " CONNECT BY PRIOR articleNO=parentNO"
					     + " ORDER SIBLINGS BY articleNO DESC";
			
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery(); //select
			
			while (rs.next()) {
				int level = rs.getInt("level");
				int articleNO = rs.getInt("articleNO");
				int parentNO = rs.getInt("parentNO");
				
				String title = rs.getString("title");
				String content = rs.getString("content");
				String id = rs.getString("id");
				Date writeDate = rs.getDate("writeDate");
				
				ArticleVO article = new ArticleVO(); //글 정보 객체 속성에 저장
				article.setLevel(level);
				article.setArticleNO(articleNO);
				article.setParentNO(parentNO);
				article.setTitle(title);
				article.setContent(content);
				article.setId(id);
				article.setWriteDate(writeDate);
				
				articlesList.add(article); //리스트에 글 정보 바인딩
			}
			rs.close();
			pstmt.close();
			conn.close(); //연결 해제 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articlesList;
	}
	
	private int getNewArticleNO() {//새 글에 글 번호
		int result = 0;	// +강사님 추가
		try {	
			conn = dataFactory.getConnection();
			
			String query = "SELECT  max(articleNO) from t_board "; //마지막 글 번호
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery(query);
			
			if (rs.next())
				result = (rs.getInt(1) + 1); //+강사님 추가
				//첫번째 컬럼 값 + 1

			rs.close();
			pstmt.close();
			conn.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result; //+강사님 추가
	}
	
	public int insertNewArticle(ArticleVO article) {
		int articleNO = getNewArticleNO(); //새로운 글 번호 가져옴
		try {
			conn = dataFactory.getConnection();//연결
			 
			int parentNO = article.getParentNO();
			String title = article.getTitle();
			String content = article.getContent();
			String id = article.getId();
			String imageFileName = article.getImageFileName();
			
			String query = "INSERT INTO t_board (articleNO, parentNO, title, content, imageFileName, id)"
					+ " VALUES (?, ? ,?, ?, ?, ?)";
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			pstmt.setInt(2, parentNO);
			pstmt.setString(3, title);
			pstmt.setString(4, content);
			pstmt.setString(5, imageFileName);
			pstmt.setString(6, id);
			pstmt.executeUpdate();
			
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleNO;
	}
	public ArticleVO selectArticle(int articleNO){ //게시판 상세 글 조회 창 
		ArticleVO article=new ArticleVO();
		try{
		conn = dataFactory.getConnection();
		
		String query ="select articleNO,parentNO,title,content,  NVL(imageFileName, 'null') as imageFileName, id, writeDate"
			                     +" from t_board" 
			                     +" where articleNO=?";
		System.out.println(query);
		
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, articleNO);
		
		ResultSet rs =pstmt.executeQuery();
		rs.next();
		
		int _articleNO =rs.getInt("articleNO");
		int parentNO=rs.getInt("parentNO");
		String title = rs.getString("title");
		String content =rs.getString("content");
		String imageFileName = URLEncoder.encode(rs.getString("imageFileName"), "UTF-8"); 
		//파일이름에 특수문자가 있을 경우 방지하고자 인코딩합니다.
		
		if(imageFileName.equals("null")) { //만약에 첨부파일이 없을 경우
			imageFileName = null; //null값으로 지정 
		}
		
		String id = rs.getString("id");
		Date writeDate = rs.getDate("writeDate");

		article.setArticleNO(_articleNO);
		article.setParentNO (parentNO);
		article.setTitle(title);
		article.setContent(content);
		article.setImageFileName(imageFileName);
		article.setId(id);
		article.setWriteDate(writeDate); //객체에 담음
		
		rs.close();
		pstmt.close();
		conn.close(); //연결 종료 
		
		}catch(Exception e){
		e.printStackTrace();	
		}
		return article;
		}
	
	public void updateArticle(ArticleVO article) { //수정
		int articleNO = article.getArticleNO();
		String title = article.getTitle();
		String content = article.getContent();
		String imageFileName = article.getImageFileName();
		
		try {
			conn = dataFactory.getConnection();
			String query = "update t_board  set title=?,content=?";
			
			if (imageFileName != null && imageFileName.length() != 0) {
				//수정된 이미지가 있다면 
				query += ",imageFileName=?"; //imageFileName을 SQL문에 추가 
			}
			
			query += " where articleNO=?";
			
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			
			if (imageFileName != null && imageFileName.length() != 0) {
				//이미지 파일을 수정하면 
				pstmt.setString(3, imageFileName);
				pstmt.setInt(4, articleNO);
			} else { //그렇지 않으면 
				pstmt.setInt(3, articleNO);
			}
			
			pstmt.executeUpdate(); //실행 
			
			pstmt.close();
			conn.close(); //종료 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Integer> selectRemovedArticles(int  articleNO) { 
		List<Integer> articleNOList = new ArrayList<Integer>();
		try {
			conn = dataFactory.getConnection();
			String query = "SELECT articleNO FROM  t_board  ";
			query += " START WITH articleNO = ?";
			query += " CONNECT BY PRIOR  articleNO = parentNO";
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				articleNO = rs.getInt("articleNO");
				articleNOList.add(articleNO);
			}
			
			pstmt.close();
			conn.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleNOList;
	}
	
	public void deleteArticle(int  articleNO) { //삭제
		try {
			conn = dataFactory.getConnection();
			
			String query = "DELETE FROM t_board ";
			query += " WHERE articleNO in (";
			query += "  SELECT articleNO FROM  t_board ";
			query += " START WITH articleNO = ?";
			query += " CONNECT BY PRIOR  articleNO = parentNO )"; //삭제 글과 관련된 자식글까지 모두 삭제 
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			pstmt.executeUpdate();
			
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List selectAllArticles(Map pagingMap){
		List articlesList = new ArrayList();
		
		int section = (Integer)pagingMap.get("section");
		int pageNum=(Integer)pagingMap.get("pageNum"); //전송된 두 값을 가져옴 
		
		try{
		   conn = dataFactory.getConnection();
		   
		   String query ="SELECT * FROM ( "
						+ "select ROWNUM  as recNum,"+"LVL,"
							+"articleNO,"
							+"parentNO,"
							+"title,"
							+"id,"
							+"writeDate"
				                  +" from (select LEVEL as LVL, "
								+"articleNO,"
								+"parentNO,"
								+"title,"
								+"id,"
								 +"writeDate"
							   +" from t_board" 
							   +" START WITH  parentNO=0"
							   +" CONNECT BY PRIOR articleNO = parentNO"
							  +"  ORDER SIBLINGS BY articleNO DESC)"
					+") "                        
					+" where recNum between(?-1)*100+(?-1)*10+1 and (?-1)*100+?*10";                
		   System.out.println(query);
		   
		   pstmt= conn.prepareStatement(query);
		   pstmt.setInt(1, section);
		   pstmt.setInt(2, pageNum);
		   pstmt.setInt(3, section);
		   pstmt.setInt(4, pageNum);
		   ResultSet rs =pstmt.executeQuery();
		   
		   while(rs.next()){
		      int level = rs.getInt("lvl");
		      int articleNO = rs.getInt("articleNO");
		      int parentNO = rs.getInt("parentNO");
		      String title = rs.getString("title");
		      String id = rs.getString("id");
		      Date writeDate= rs.getDate("writeDate");
		      
		      ArticleVO article = new ArticleVO();//하나의 게시글 불러옴
		      article.setLevel(level);
		      article.setArticleNO(articleNO);
		      article.setParentNO(parentNO);
		      article.setTitle(title);
		      article.setId(id);
		      article.setWriteDate(writeDate);
		      
		      articlesList.add(article);	
		   } //end while
		  
		   rs.close();
		   pstmt.close();
		   conn.close();
		   
	  }catch(Exception e){
	     e.printStackTrace();	
	  }
	  return articlesList;
    } 
	
	public int selectTotArticles() {
		int result = 0;
		
		try {
			conn = dataFactory.getConnection();
			
			String query = "select count(articleNO) from t_board "; //전체 글 수 조회
			System.out.println(query);
			
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				result = rs.getInt(1);
			
			rs.close();
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
