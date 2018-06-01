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
	<h1>글수정</h1>
		<form action="/test/update.ktds" method="post">
			<input type="hidden" name="pageNum" value="${pageNum }">
			<input type="hidden" name="article_num" value="${article_num }">
			
			<table> <!-- 확인 버튼을 누르면 list로 돌아가면서 한 줄 추가(쿼리 실행) -->
				<tr>
					<th> 제목 | </th>
					<td> <input type="text" name="title" size="100" value=${article.title }> </td>
				</tr>
				
				<tr>
					<th> ID | </th>
					<td> ${id } </td>
				</tr>
				
				<tr>
					<th> 내용 | </th>
					<td> <textarea name="content" rows="25" cols="87">${article.content }</textarea> </td>
				</tr>
				
				<tr>
					<th> 파일첨부 | </th>
					<td> <input type="file" name="fname" value=${article.fname }> </td>
				</tr>
			</table>
			<input type="submit" value="수정">
			<input type="reset" value="수정취소">
		</form>
	</center>
</body>
</html>