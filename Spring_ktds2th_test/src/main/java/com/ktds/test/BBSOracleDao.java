package com.ktds.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;

public class BBSOracleDao implements BBSDaoService {
	//부모 메서드의 메서드를 오버라이드한 자식 클래스
		
		Connection con;
		PreparedStatement pstmt;
		ResultSet rs;
		ArrayList<BoardVO> articleList;
		BoardVO article;
		ArrayList<CommentVO> commentList;
		CommentVO comment;
		
		
		public Connection getConnection(){
			//DB접근
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver");
				String url="jdbc:oracle:thin:@127.0.0.1:1521:XE";
				con=DriverManager.getConnection(url, "hr", "1234");
						
			}catch(Exception e){
				e.printStackTrace();
			}
			return con;
		}

		@Override
		public int getArticleCount() throws SQLException, IOException{
			//글의 개수가 얼마나 있는지 넘겨줌
			int count=0;	
			con =this.getConnection();
			pstmt= con.prepareStatement("select count(*) from bbs");
			rs=pstmt.executeQuery();			
			if(rs.next()){
				count =rs.getInt(1);
			} 
			
			return count;
		}

		@Override
		public ArrayList<BoardVO> getArticles(int startRow, int endRow) throws SQLException, IOException {
			//리스트에 보여줄 모든 항목들을 넘겨주는 메서드(ArrayList를 넘겨준다)
			articleList= new ArrayList<>();//jdk7 지원
			//제네릭이 에러가 나면 프로젝트명 우클릭 후 Project pacets에서 jdk버젼을 1.7로 설정하면 된다.
			con=this.getConnection();
			StringBuffer query= new StringBuffer();
			query.append("select * "); //인라인 뷰로 미리 할당
			query.append("from (select rownum rm,human.* from " +
					          "(select * from bbs order by group_id desc, pos) human) ");		
			query.append("where rm between ? and ?");
			pstmt=con.prepareStatement(query.toString());
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			rs=pstmt.executeQuery();
			
			while(rs.next()){
				article = new BoardVO();
				article.setArticle_num(rs.getInt("article_num"));
				article.setId(rs.getString("id"));
				article.setTitle(rs.getString("title"));
				article.setContent(rs.getString("content"));
				article.setDepth(rs.getInt("depth"));
				article.setHit(rs.getInt("hit"));
				article.setGroup_id(rs.getInt("group_id"));
				article.setPos(rs.getInt("pos"));
				article.setWrite_date(rs.getTimestamp("write_date"));
				article.setFname(rs.getString("fname"));
				articleList.add(article);
			}		
			return articleList;

		}

		@Override
		public void insertArticle(BoardVO article)
				throws SQLException, IOException {
			//데이터 삽입 메서드
			con=this.getConnection();
			StringBuffer query= new StringBuffer();
			query.append("insert into bbs values ");
			query.append("(bbs_seq.nextval,?,?,?,0,0,bbs_seq.currval,0,sysdate,?)");
			pstmt=con.prepareStatement(query.toString());
			//object클래스내 toString메소드를 쓰면 append에 입력된 쿼리 문장이 Oracle에 할당된다.
			//query StringBuffer객체에 쿼리문 할당>object toString메소드로 쿼리문 읽어오기>connection으로 DB에 연결
			pstmt.setString(1, article.getId());
			pstmt.setString(2, article.getTitle());
			pstmt.setString(3, article.getContent());
			pstmt.setString(4, article.getFname());
			pstmt.executeUpdate();
			//입력 후 DB가 최신화가 되야하므로 Update를 사용한다
			
			con.close();
			pstmt.close();
		}

		@Override
		public BoardVO getArticle(int article_num) throws SQLException, IOException {
			//글번호에 맞는 글 내용을 가져오는 메서드
			con=this.getConnection();
			StringBuffer query= new StringBuffer();
			query.append("select * from bbs where article_num=?");
			pstmt=con.prepareStatement(query.toString());
			pstmt.setInt(1, article_num);
			rs=pstmt.executeQuery();
			if(rs.next()){
				article=new BoardVO();
				article.setArticle_num(rs.getInt("article_num"));
				article.setId(rs.getString("id"));
				article.setTitle(rs.getString("title"));
				article.setContent(rs.getString("content"));
				article.setDepth(rs.getInt("depth"));
				article.setHit(rs.getInt("hit"));
				article.setGroup_id(rs.getInt("group_id"));
				article.setPos(rs.getInt("pos"));
				article.setWrite_date(rs.getTimestamp("write_date"));
				article.setFname(rs.getString("fname"));
			}
			
			con.close();
			pstmt.close();
			rs.close();
			
			return article;
		}
		
		public int login_check(String id, String pass) throws ServletException, IOException, SQLException{
			//로그인 관리 메서드
			 con=this.getConnection(); //내부 메소드 connection에 접근 (DB에 접근하기 위한 값을 받아옴)
		      String sql="select pass from login where id=?"; 
		      pstmt=con.prepareStatement(sql); //prepareStatement(DB로 SQL상태를 전송한다)
		      pstmt.setString(1, id);
		      rs=pstmt.executeQuery();
		      int status=0;
		      if(rs.next()){
		         if(pass.equals(rs.getString(1))){
		            status=1;
		         }else{
		            status=2;
		         }
		      }else{
		         status=3;
		      }
		      con.close();
		      pstmt.close();
		      rs.close();
		      
		      return status;
		   }

		@Override
		public void reply(BoardVO article) throws ServletException, IOException,
				SQLException {
			//답글을 달았을 때 답글이 달리는 글의 position값 1 증가 및 재정렬
			con=this.getConnection(); //내부 메소드 connection에 접근 (DB에 접근하기 위한 값을 받아옴)
		    String sql="update bbs set pos=pos+1 where group_id=? and pos>?"; 
		    pstmt=con.prepareStatement(sql); //prepareStatement(DB로 SQL상태를 전송한다)
		    pstmt.setInt(1, article.getGroup_id());
		    pstmt.setInt(2, article.getPos());
		    pstmt.executeUpdate();
		    
		    //댓글도 글을 넣는 것과 같은 형태(depth와 pos는 1씩 증가된다)
		    String sql1="insert into bbs values (bbs_seq.nextval,?,?,?,?,0,?,?,sysdate,?)"; 
		    pstmt=con.prepareStatement(sql1); //prepareStatement(DB로 SQL상태를 전송한다)
		    pstmt.setString(1, article.getId());
		    pstmt.setString(2, article.getTitle());
		    pstmt.setString(3, article.getContent());
		    pstmt.setInt(4, article.getDepth()+1);
		    pstmt.setInt(5, article.getGroup_id());
		    pstmt.setInt(6, article.getPos()+1);
		    pstmt.setString(7, article.getFname());
		    pstmt.executeUpdate();
		    
		    con.close();
		    pstmt.close();
			
		}

		@Override
		public BoardVO getUpdateArticle(int article_num) throws ServletException,
				IOException, SQLException {
			//글 수정을 위한 메서드
			con =this.getConnection();
			StringBuffer query= new StringBuffer();
			query.append("select title, content, fname from bbs where article_num=?");
			//쿼리문에서 전체적인 내용을 불러오게 되면 쓸데없는 컬럼값도 가져오게 되므로 불러와야 하는 값들만 가져온다
			pstmt=con.prepareStatement(query.toString());
			pstmt.setInt(1, article_num);
			rs=pstmt.executeQuery();
			
			if(rs.next()){
				article=new BoardVO();
				article.setTitle(rs.getString("title"));
				article.setContent(rs.getString("content"));
				article.setFname(rs.getString("fname"));
			}
					
			rs.close();
			con.close();
			pstmt.close();
			
			return article;
		
		}

		@Override
		public void UpdateArticle(int article_num, String title, String content)
				throws ServletException, IOException, SQLException {
			//글수정을 한 뒤 해당 레코드를 갱신해주는 메서드
			con=this.getConnection(); //내부 메소드 connection에 접근 (DB에 접근하기 위한 값을 받아옴)
		    String sql="update bbs set title=?, content=? where article_num=?"; 
		    pstmt=con.prepareStatement(sql); //prepareStatement(DB로 SQL상태를 전송한다)
		    pstmt.setString(1, title);
		    pstmt.setString(2, content);
		    pstmt.setInt(3, article_num);
		    pstmt.executeUpdate();
			
		    con.close();
			pstmt.close();
		}

		@Override
		public void deleteArticle(int article_num) throws SQLException, IOException {
			//글 삭제를 위한 메서드(게시판 테이블과 게시판을 위한 댓글 테이블에 해당 글번호를 갖는 모든 레코드를 삭제한다)
			con =this.getConnection();		
			String sql="delete from bbs where article_num=?";	
			pstmt=con.prepareStatement(sql);	
			pstmt.setInt(1, article_num);
			pstmt.executeUpdate();
			
			String sql1="delete from comment_bbs where article_num=?";
			pstmt=con.prepareStatement(sql1);	
			pstmt.setInt(1, article_num);	
			pstmt.executeUpdate();
			
			con.close();
			pstmt.close();	
		}
		
		@Override
		public int getCommentNum(int article_num) throws SQLException, IOException {
			//댓글 개수를 가져오는 메서드
			int count=0;	
			con =this.getConnection();
			pstmt= con.prepareStatement("select count(*) from comment_bbs where article_num=?");
			pstmt.setInt(1, article_num);
			rs=pstmt.executeQuery();
			if(rs.next()){
				count =rs.getInt(1);
			} 
			
			con.close();
			pstmt.close();
			rs.close();
			
			return count;
		}

		@Override
		public ArrayList<CommentVO> insertCommnet(int article_num,
			String comment_content, String id) throws SQLException, IOException {
			//댓글을 입력할 때 댓글 관리 테이블에 값을 입력하는 메서드
			con =this.getConnection();		
			String query="insert into comment_bbs values(comment_seq.nextval,?,?,sysdate,?)";			
			pstmt=con.prepareStatement(query);									
			pstmt.setString(1, id);						
			pstmt.setString(2, comment_content);						
			pstmt.setInt(3, article_num);						
			pstmt.executeUpdate();			
		
			return this.getAllCommnet(article_num);
		}

		@Override
		public ArrayList<CommentVO> getAllCommnet(int article_num)
				throws SQLException, IOException {
			//모든 댓글을 자바스크립트로 보여주는 메서드
			con =this.getConnection();				
			String query1="select * from comment_bbs where article_num=? order by comment_num ";			
			pstmt=con.prepareStatement(query1);							
			pstmt.setInt(1, article_num);					
			rs=pstmt.executeQuery();
			
			commentList= new ArrayList<CommentVO>();
			while(rs.next()){
				comment = new CommentVO();
				comment.setComment_num(rs.getInt("comment_num"));
				comment.setId(rs.getString("id"));
				comment.setComment_content(rs.getString("comment_content"));
				comment.setComment_date(rs.getTimestamp("comment_date"));
				comment.setArticle_num(rs.getInt("article_num"));
				commentList.add(comment);
			}			
		
			return commentList;
			
		}
		

}
