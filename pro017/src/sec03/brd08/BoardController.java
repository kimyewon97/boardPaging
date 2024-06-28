package sec03.brd08;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
/**
 * Servlet implementation class BoardController
 */
@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image"; //이미지 저장 위치를 상수로 저장
	BoardService boardService;
	ArticleVO articleVO;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doHandle(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		String nextPage = "";
		HttpSession session;
		
		//1. 요청받은 정보 추출
		String action = request.getPathInfo(); //요청 url을 action에 담음
		System.out.println("action:" + action);
		
		try {
			
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			
			if (action == null) { //최초요청시 
				
				//파라미터로 가져옴
				String _section=request.getParameter("section");
				String _pageNum=request.getParameter("pageNum");
				
				//값이 없다면 기본값 1로 지정
				int section = Integer.parseInt(((_section==null)? "1":_section) ); 
				int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));  
				
				System.out.println("section: " + section);
				System.out.println("pageNum: " + pageNum);
				
				Map<String, Integer> pagingMap = new HashMap<String, Integer>();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				
				//2. 게시글 목록 DB 연동 처리
				Map articlesMap=boardService.listArticles(pagingMap);
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				
				articlesMap.get("totArticles");
				
				request.setAttribute("articlesMap", articlesMap);
				nextPage = "/board07/listArticles.jsp"; //다음 페이지 /board07/listArticles.jsp 이동
				
			
			} else if (action.equals("/listArticles.do")) { //회원 목록리스트 
				String _section=request.getParameter("section");
				String _pageNum=request.getParameter("pageNum");
				
				//값이 없다면 각각 1로 초기화
				int section = Integer.parseInt(((_section==null)? "1":_section) );
				int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));
				
				System.out.println("section: " + section);
				System.out.println("pageNum: " + pageNum);
				
				Map pagingMap=new HashMap();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
			
				//2. 게시글 목록 DB 연동 처리
				Map articlesMap=boardService.listArticles(pagingMap); //해당 섹션과 페이지에 해당되는 글 목록을 조회
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				
				request.setAttribute("articlesMap", articlesMap);
				nextPage = "/board07/listArticles.jsp"; // /board07/listArticles.jsp 이동
	
				
			} else if (action.equals("/articleForm.do")) { //글쓰기 창
				nextPage = "/board07/articleForm.jsp"; ///board07/articleForm.jsp으로 이동
				
			}  else if (action.equals("/addArticle.do")) { //새 글 추가 요청, /addArticle.do로 들어온다면 
				int articleNO=0;
				
				Map<String, String> articleMap = upload(request, response); //업로드
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName"); //저장된 글 정보 가져옴

				articleVO.setParentNO(0); //새 글의 부모 글 번호를 0으로 설정
				articleVO.setId("hong"); //새 글 작성자 hong으로 설정
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				
				articleNO= boardService.addArticle(articleVO);
				//테이블에 새글을 추가한 후 새글에 대한 글 번호 가져옴
				
			 if(imageFileName!=null && imageFileName.length()!=0) { //파일을 첨부 했다면
			    File srcFile = new 	File(ARTICLE_IMAGE_REPO +"\\"+"temp"+"\\"+imageFileName); //게시글 번호로 디렉토리 만들어줌 
				File destDir = new File(ARTICLE_IMAGE_REPO +"\\"+articleNO);
				destDir.mkdirs();
				
				FileUtils.moveFileToDirectory(srcFile, destDir, true); //폴더 생성
			}
			PrintWriter pw = response.getWriter();
			pw.print("<script>" 
			         +"  alert('새글을 추가했습니다.');" 
					 +" location.href='"+request.getContextPath()+"/board/listArticles.do';"
			         +"</script>");

			return;
			} else if(action.equals("/viewArticle.do")){
				String articleNO = request.getParameter("articleNO");
				
				//2. 게시글 상세 정보 DB 연동 처리
				articleVO=boardService.viewArticle(Integer.parseInt(articleNO));
				
				request.setAttribute("article",articleVO);
				nextPage = "/board07/viewArticle.jsp";
			
			}else if (action.equals("/modArticle.do")) {
				Map<String, String> articleMap = upload(request, response);
				int articleNO = Integer.parseInt(articleMap.get("articleNO"));
				articleVO.setArticleNO(articleNO);
				
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				
				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				
				//2. 게시글 상세 정보 수정 DB 연동 처리
				boardService.modArticle(articleVO);
				
				if (imageFileName != null && imageFileName.length() != 0) { //파일이 존재하면 
					String originalFileName = articleMap.get("originalFileName"); //원래 파일을 가지고 있다가
					
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO); 
					destDir.mkdirs(); //새로 생성
					
					FileUtils.moveFileToDirectory(srcFile, destDir, true); //이동
					
					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
					oldFile.delete();
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('글을 수정했습니다.');" + " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO=" + articleNO + "';" + "</script>");
				return;
			
			}else if (action.equals("/removeArticle.do")) {
				int articleNO = Integer.parseInt(request.getParameter("articleNO"));
				
				List<Integer> articleNOList = boardService.removeArticle(articleNO);
				
				for (int _articleNO : articleNOList) {
					File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
					if (imgDir.exists()) {
						FileUtils.deleteDirectory(imgDir);
					}
				}

				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('글을 삭제했습니다.');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");
				return;
			
			}else if (action.equals("/replyForm.do")) {
				int parentNO = Integer.parseInt(request.getParameter("parentNO"));
				System.out.println("parentNO: " + parentNO);
				
				session = request.getSession();
				session.setAttribute("parentNO", parentNO); //답글창 요청시 부모글을 세션에 미리 저장
				
				nextPage = "/board07/replyForm.jsp";
			} else if (action.equals("/addReply.do")) {
				session = request.getSession();
				
				int parentNO = (Integer) session.getAttribute("parentNO");
				session.removeAttribute("parentNO");
				Map<String, String> articleMap = upload(request, response);
				
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				
				articleVO.setParentNO(parentNO); //부모글 설정
				articleVO.setId("lee"); //작성자 id lee로 지정
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				
				int articleNO = boardService.addReply(articleVO);
				System.out.println("articleNO: " + articleNO);
				
				if (imageFileName != null && imageFileName.length() != 0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = response.getWriter();
				
				pw.print("<script>" + "  alert('답글을 추가했습니다.');" + " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO="+articleNO+"';" + "</script>");
				return;
			
			}else {
				//글 목록을 명시적으로 페이지 번호를 눌러서 요펑한 경우 section과 pageNum 값을 가져옴
				String _section = request.getParameter("section");
				String _pageNum = request.getParameter("pageNum");
				
				int section = Integer.parseInt(((_section==null)? "1":_section));
				int pageNum = Integer.parseInt(((_pageNum==null)? "1":_pageNum));
				
				System.out.println("section: " + section);
				System.out.println("pageNum: " + pageNum);

				Map<String, Integer> pagingMap = new HashMap<String, Integer>();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				
				//2. 게시글 목록 DB 연동 처리
				Map articlesMap=boardService.listArticles(pagingMap);
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				
				request.setAttribute("articlesMap", articlesMap);
				nextPage = "/board07/listArticles.jsp";
				
			}
			
			//3. 화면 이동
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding = "utf-8";
		
		File currentDirPath = new File(ARTICLE_IMAGE_REPO); //글 이미지 저장 폴더에 대해 파일 객체를 생성
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024 * 1024);
		
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try {
			List items = upload.parseRequest(request);
			
			for (int i = 0; i < items.size(); i++) {
				FileItem fileItem = (FileItem) items.get(i);
				
				if (fileItem.isFormField()) {
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
				} else {
					System.out.println("파라미터명:" + fileItem.getFieldName());
					//System.out.println("파일명:" + fileItem.getName());
					System.out.println("파일크기:" + fileItem.getSize() + "bytes");
					//articleMap.put(fileItem.getFieldName(), fileItem.getName());
					
					if (fileItem.getSize() > 0) {
						int idx = fileItem.getName().lastIndexOf("\\");
						
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}

						String fileName = fileItem.getName().substring(idx + 1);
						System.out.println("파일명:" + fileName);
						
						articleMap.put(fileItem.getFieldName(), fileName);  //익스플로러에서 업로드 파일의 경로 제거 후 map에 파일명 저장
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						
						fileItem.write(uploadFile);

					} // end if
				} // end if
			} // end for
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;
	}

}
