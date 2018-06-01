<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<center>
	<h1>글쓰기</h1>
		<form action="/test/write.ktds" method="post">
		<input type="hidden" name="pageNum" value="${pageNum }">
		<!-- form의 enctype이 multipart면 req.getPara가 먹지 않는다 -->
		<!-- 이것을 쓰기 위해서 req.getPart로 사용해서 읽어온다. -->
		<!-- 근데 req.getPara로도 읽을 수 있다>하지만, 한글을 인식하지 못한다 -->
			<table> <!-- 확인 버튼을 누르면 list로 돌아가면서 한 줄 추가(쿼리 실행) -->
				<tr>
					<th> 제목 | </th>
					<td> <input type="text" name="title" size="100"> </td>
				</tr>
				
				<tr>
					<th> ID | </th>
					<td> ${id } </td>
				</tr>
				
				<tr>
					<th> 내용 | </th>
					<td> <textarea name="content" rows="25" cols="87"></textarea> </td>
				</tr>
				
				<tr>
					<th> 파일첨부 | </th>
					<td> <input type="file" name="fname"> </td>
				</tr>
			</table>
			<input type="submit" value="확인">
			<input type="button" value="취소" onclick="document.location.href='/test/list.ktds?pageNum=${pageNum}'">
		</form>
	</center>
</body>
</html>