package sec03.brd08;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardService { //하나의 서비스에서 여러번의 DAO를 실행할 수 있다
	
	BoardDAO boardDAO; //has-a관계, 생성자
	
	public BoardService() { 
		boardDAO = new BoardDAO(); //서비스 객체가 만들어질때 DAO 사용
	}

	public List<ArticleVO> listArticles() {//해당된는 메소드 사용
		List<ArticleVO> articlesList = boardDAO.selectAllArticles();
		return articlesList;
	}

	public int addArticle(ArticleVO article) {
		return boardDAO.insertNewArticle(article);
	}

	public ArticleVO viewArticle(int articleNO) {
		ArticleVO article = null;
		article = boardDAO.selectArticle(articleNO);
		//실제 게시판의 경우 조회수를 하나 증가시켜주는 SQL 문을 작성해야 합니다.
		return article;
	}
	
	public void modArticle(ArticleVO article) {
		boardDAO.updateArticle(article);
	}

	public List<Integer> removeArticle(int  articleNO) {
		List<Integer> articleNOList = boardDAO.selectRemovedArticles(articleNO);
		boardDAO.deleteArticle(articleNO);
		return articleNOList;
	}
	
	public int addReply(ArticleVO article) {
		return boardDAO.insertNewArticle(article);
	}

	public Map listArticles(Map<String, Integer> pagingMap) {
		Map articlesMap = new HashMap();
		
		List<ArticleVO> articlesList = boardDAO.selectAllArticles(pagingMap); //글 목록 조회
		int totArticles = boardDAO.selectTotArticles(); //전체 글 수 조회 
		
		articlesMap.put("articlesList", articlesList);
		//articlesMap.put("totArticles", totArticles);
		articlesMap.put("totArticles", 402); 
		return articlesMap;
	}

}
