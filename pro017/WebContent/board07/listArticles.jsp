<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<c:set  var="articlesList"  value="${articlesMap.articlesList}" />
<c:set  var="totArticles"  value="${articlesMap.totArticles}" />
<c:set  var="section"  value="${articlesMap.section}" />
<c:set  var="pageNum"  value="${articlesMap.pageNum}" />

<%
  request.setCharacterEncoding("UTF-8");
%>  
<!DOCTYPE html>
<html>
<head>
<style>
.no-uline {text-decoration:none;}
.sel-page{text-decoration:none;color:red;} 
.cls1 {text-decoration:none;}
.cls2{text-align:center; font-size:30px;}
</style>
<meta charset="UTF-8">
<title>글목록창</title>
</head>
<body>
<table align="center" border="1"  width="80%"  >
  <tr height="10" align="center"  bgcolor="lightgreen">
     <td >글번호</td>
     <td >작성자</td>              
     <td >제목</td>
     <td >작성일</td>
  </tr>
<c:choose>
  <c:when test="${empty articlesList}" > <!-- 리스트가 비어있다면 -->
    <tr  height="10">
      <td colspan="4">
         <p align="center">
            <b><span style="font-size:9pt;">등록된 글이 없습니다.</span></b>
        </p>
      </td>  
    </tr>
  </c:when>
  <c:when test="${!empty articlesList}" >
    <c:forEach  var="article" items="${articlesList }" varStatus="articleNum" > <!-- 포워딩 된 글목록 표시 -->
     <tr align="center">
	<td width="5%">${articleNum.count}</td> <!-- count 속성을 이용해 글 번호를 1부터 자동으로 표시 -->
	<td width="10%">${article.id }</td>
	<td align='left'  width="35%">
	    <span style="padding-right:30px"></span>    
	   <c:choose>
	      <c:when test='${article.level > 1 }'>  <!-- 자식글 -->
	         <c:forEach begin="1" end="${article.level }" step="1">
	             <span style="padding-left:10px"></span> 
	         </c:forEach> <!-- 부모글 기준으로 level 값 만큼 왼쪽으로 공백(들여쓰기) -->
	         <span style="font-size:12px;">[답변]</span>
                   <a class='cls1' href="${contextPath}/board/viewArticle.do?articleNO=${article.articleNO}">${article.title}</a>
	          </c:when>
	          <c:otherwise>
	            <a class='cls1' href="${contextPath}/board/viewArticle.do?articleNO=${article.articleNO}">${article.title }</a>
	          </c:otherwise>
	        </c:choose>
	  </td>
	  <td  width="10%"><fmt:formatDate value="${article.writeDate}" /></td> 
	</tr>
    </c:forEach>
     </c:when>
    </c:choose>
</table>

	<!-- 페이지 추가 구현 -->
<div class="cls2">
	<c:set var="totalPage" value="${Math.ceil((totArticles * 1.0)/10 )}"/> <!-- 24 -->
	<c:set var="endPage" value="${Math.ceil(pageNum / 10.0) * 10}"/> <!-- 30 --> <!-- 현재 페이지 번호가 24라면 끝 페이지 번호는 30 -->
	<c:set var="startPage" value="${ endPage - (10 - 1) }"/> <!-- 21 --> <!-- 끝 페이지 번호가 30이면 시작 페이지 번호는 21 -->
	<c:set var="hasNext" value="${totalPage > endPage}"/> <!-- 다음 페이지가 있는지 여부를 확인 true -->	
	
	<c:if test="${endPage > totalPage }"> <!-- 30 > 24 -->
		<c:set var="endPage" value="${totalPage }"/> <!-- 24 -->
	</c:if>
	
	<c:if test="${totArticles != null }">
		<!-- 시작 페이지부터 끝 페이지까지 반복하여 페이지 생성 -->
		<c:forEach var="page" begin="${ startPage }" end="${ endPage > totalPage ? totalPage : endPage }" step="1">
	
			<!-- Pre -->
			<c:if test="${section > 1 && page == startPage}"> <!-- 첫번째 한번만 나옴 -->
				<a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section-1}&pageNum=${Math.round(startPage) - 1}">&nbsp; pre </a> <!-- 이전 섹션과 이전 페이지가 있으면 생성 -->
			</c:if>
	
			<!-- page -->
			<a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section}&pageNum=${page}">${page} </a>
	
			<!-- next -->
			<c:if test="${endPage == page && hasNext}"> <!-- c:if 태그는 현재 페이지가 끝 페이지이고, 다음 페이지 그룹이 있을 경우에 next 생성 -->
				<a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section+1}&pageNum=${Math.round(endPage + 1)}">&nbsp; next</a> <!-- 다음 페이지 그룹으로 이동 -->
			</c:if>
	
		</c:forEach>
	</c:if>
	
	<br/>
	
<%-- 
	<!-- 수업 페이지 구현 -->
	<c:if test="${totArticles != null }" >
      <c:choose>
        <c:when test="${totArticles > 100 }">  <!-- 글 개수가 100 초과인경우 -->
	     <c:if test="${section == 1}">
	      <c:forEach   var="page" begin="1" end="10" step="1" >
	         <c:if test="${section >1 && page==1 }">
	          <a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section-1}&pageNum=${(section-1)*10 +1 }">&nbsp; pre </a>
	         </c:if>
	          <a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section}&pageNum=${page}">${(section-1)*10 +page } </a>
	         <c:if test="${section == 1 && page == 10 }">
	          <a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section+1}&pageNum=${section*10+1}">&nbsp; next</a>
	         </c:if>
	         </c:forEach>
	      </c:if>
	       <c:if test="${section == 2}">
	      <c:forEach   var="page" begin="1" end="10" step="1" >
	         <c:if test="${section >1 && page==1 }">
	          <a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section-1}&pageNum=${(section-1)*10 +1 }">&nbsp; pre </a>
	         </c:if>
	          <a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section}&pageNum=${page}">${(section-1)*10 +page } </a>
	         <c:if test="${section == 2 && page == 10 }">
	          <a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section+1}&pageNum=${section*10+1}">&nbsp; next</a>
	         </c:if>
	      </c:forEach>
	      </c:if>
	      <c:if test="${section == 3}">
		      <c:forEach   var="page" begin="1" end="${(totArticles%100)/10+1}" step="1" >
		         <c:if test="${section >1 && page==1 }">
		          <a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section-1}&pageNum=${(section-1)*10 +1 }">&nbsp; pre </a>
		         </c:if>
		          <a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section}&pageNum=${page}">${(section-1)*10 +page } </a>
		         <c:if test="${section == 3 && page == 10 }">
		          <a class="no-uline" href="${contextPath }/board/listArticles.do?section=${section+1}&pageNum=${section*10+1}">&nbsp; next</a>
		         </c:if>
		      </c:forEach>
	      </c:if>
	      
	     
        </c:when>
        
        <c:when test="${totArticles == 100 }" >  <!--등록된 글 개수가 100개인경우  -->
	      <c:forEach   var="page" begin="1" end="10" step="1" >
	        <a class="no-uline"  href="#">${page } </a>
	      </c:forEach>
        </c:when>
        
        <c:when test="${totArticles < 100 }" >   <!--등록된 글 개수가 100개 미만인 경우  -->
         <c:out value="${totArticles}"></c:out> 
	      <c:forEach   var="page" begin="1" end="${totArticles/10 +1}" step="1" >
	         <c:choose>
	           <c:when test="${page==pageNum }">
	            <a class="sel-page"  href="${contextPath }/board/listArticles.do?section=${section}&pageNum=${page}">${page } </a>
	           </c:when>
	          <c:otherwise>
	            <a class="no-uline"  href="${contextPath }/board/listArticles.do?section=${section}&pageNum=${page}">${page } </a>
	          </c:otherwise>
	        </c:choose>
	      </c:forEach>
       </c:when>
       
      </c:choose>
 </c:if>  --%>
</div>    
<br><br>
<a  class="cls1"  href="${contextPath}/board/articleForm.do"><p class="cls2">글쓰기</p></a>
</body>
</html>