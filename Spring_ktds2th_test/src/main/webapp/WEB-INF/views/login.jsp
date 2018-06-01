<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form action="/test/login.ktds" method="post">
	<!-- 로그인 관리 클래스에 연결한다 -->
	  <input type="hidden" value="${logined}"><!-- 세션 내의 logined값(ok) 저장 -->
		I  D : <input type="text" name="id"> <br>
		PASS : <input type="text" name="pass">
		<input type="submit" value="로그인">
		<input type="reset" value="취소">
		<input type="button" value="회원가입">	
	</form>
</body>
</html>