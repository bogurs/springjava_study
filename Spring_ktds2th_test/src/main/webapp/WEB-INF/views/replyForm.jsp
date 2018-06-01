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
	<h1>답글달기</h1>
		<form action="/test/reply.ktds" method="post">
		<!-- 확인버튼을 누르면 reply.ktds 명령어 호출, 또한 multipart/form-data속성은 파일업로드시 파일 명을 알아내기 위한 속성이다 -->
			<input type="hidden" name="pageNum" value="${pageNum }">
			<input type="hidden" name="depth" value="${depth }">
			<input type="hidden" name="pos" value="${pos }">
			<input type="hidden" name="group_id" value="${group_id }">
			<table>
				<tr>
					<th> ID | </th>
					<td> ${id } </td>
				</tr>
				
				<tr>
					<th>  </th>
					<td> <input type="text" name="title" size="100" value="[RE]:"> </td>
				</tr>
				
				<tr>
					<th> 내용 | </th>
					<td> <textarea name="content" rows="25" cols="87"></textarea> </td>
				</tr>
				
				<tr>
					<th> 파일첨부 | </th>
					<td> <input type="file" name="fname"> </td>
			</table>
			<input type="submit" value="확인">
			<input type="reset" value="취소">
		</form>
</center>
</body>
</html>