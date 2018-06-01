<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- jstl을 사용해주겠다는 의미(c라는 이름으로 사용하겠다) -->

<html>  
<head>
<title>게시판</title>
</head>

<body>
 <c:if test="${id!=null }"> <!-- 세션에 할당되어 입력 된 id에 값이 있으면 -->
  <%@include file="login_ok.jsp" %> <!-- 1.로그인 -->
  <form action="/test/logout.ktds" method="post">
 	<input type="submit" value="로그아웃">
 </form><!-- 로그아웃 실행을 위한 폼 --> <!-- 2.로그아웃 -->
 </c:if>
 <c:if test="${id==null }">
  <%@include file="login.jsp" %>
 </c:if>
<center><b>글목록(전체 글:${count})</b> <!-- HTML설계는 디자이너가 하기 때문에 신경안써도 되지만, 읽을줄은 알아야 한다. -->
<table width="700" > <!-- EL문이 차례로 application>session>request순으로 검사한다 -->
  <tr>
    <td align="right" >
       <a href="/test/writeForm.ktds">글쓰기</a>
    </td><!-- 글쓰기 클래스 실행을 위한 폼 --> <!-- 3.글쓰기 -->
  </tr>
</table>

<c:if test="${count == 0}"> <!-- jstl은 태그 내부에서 직접 연산이 가능하다 -->
<table width="700" border="1" cellpadding="0" cellspacing="0">
  <tr>
    <td align="center">
      게시판에 저장된 글이 없습니다.
    </td>
  </tr>
</table>
</c:if>

<table border="1" width="700" cellpadding="2" cellspacing="2" align="center"> 
    <tr height="30" > 
      <td align="center"  width="50"  >번 호</td> 
      <td align="center"  width="250" >제   목</td> 
      <td align="center"  width="100" >작성자</td>
      <td align="center"  width="150" >작성일</td> 
      <td align="center"  width="50" >조 회</td>
    </tr>

	<!-- arraylist는 순서가 있고 hashmap은 순서가 없다 -->
   <c:forEach var="article" items="${articleList}"> <!-- jstl을 이용(for문 대신) -->
   <tr height="30"> <!-- items에는 배열값을 넣을수 있고, var은 article로 이름을 설정해 값을 참조할 수 있다 -->
    <td align="center"  width="50" >
	  <c:out value="${article.article_num}"/> <!-- article.(get이 생략되어있다) doget으로 간다 -->
	</td>
    <td  width="250" >
      <c:if test="${article.depth > 0}">
	  	<img src="images/level.gif" width="${10 * article.depth}"  height="16">
	    <img src="images/re.gif">
	  </c:if>
	  <c:if test="${article.depth == 0}">
	    <img src="images/level.gif" width="0"  height="16">
	  </c:if>         <!-- 4.글내용 -->
      <a href="/test/content.ktds?article_num=${article.article_num}&pageNum=${pageNum}">
          ${article.title}</a> <!-- a 태그를 통해 get방식으로 제목을 클릭했을 때의 동작을 선언한다 -->
          <c:if test="${article.hit >= 20}">
            <img src="images/hot.gif" border="0" height="16">
		  </c:if>
	</td>
    <td align="center"  width="100">${article.id}</td>
    <td align="center"  width="150">${article.write_date}</td>
    <td align="center"  width="50">${article.hit}</td>
  </tr>
  </c:forEach>
  <tr>
	  
           <td colspan="5" align="center" height="40">	 
	  ${pageCode} <!-- page 링크들을 보여주는 것(ex 1~10) -->
	  </td>

  </tr>
</table>
</center>
</body>
</html>