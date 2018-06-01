package com.ktds.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;

public interface BBSDaoService {
	
	public int getArticleCount() throws SQLException, IOException;
	public ArrayList<BoardVO> getArticles(int startRow, int endRow) throws SQLException, IOException;
	public void insertArticle(BoardVO article) throws SQLException, IOException;
	public BoardVO getArticle(int article_num) throws SQLException, IOException;	
	public int login_check(String id, String pass) throws ServletException, IOException, SQLException;
	public void reply(BoardVO article) throws ServletException, IOException, SQLException;
	public BoardVO getUpdateArticle(int article_num) throws ServletException, IOException, SQLException;
	public void UpdateArticle(int article_num, String title, String content) throws ServletException, IOException, SQLException;
	public void deleteArticle(int article_num) throws SQLException, IOException;
	
	public int getCommentNum(int article_num) throws SQLException, IOException;
	public ArrayList<CommentVO> insertCommnet(int article_num,String comment_content, String id) throws SQLException, IOException;
	public ArrayList<CommentVO> getAllCommnet(int article_num) throws SQLException, IOException;

}
