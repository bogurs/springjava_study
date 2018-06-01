package com.ktds.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class bbs_Controller {
	//컨트롤러에 모든 메소드를 넣으면 너무 복잡해진다(길어짐)
	
	//@Autowired, @Resource, @Inject(모두 DI를 하는 방법)
	//쓰려면 servlet.xml에 context:annotation-config/를 등록해야 한다.
	BBSDaoService bds;
	public bbs_Controller(BBSDaoService bds){
		this.bds=bds;
	}
	
	int count;
	ArrayList<BoardVO> articleList=null;
	BoardVO article;
	int commentNum;
	
	@RequestMapping("/list.ktds")
	public ModelAndView list(HttpServletRequest req){
		Page page;
		
		ModelAndView mav = new ModelAndView();
		
		String pageCode;
		//Page page;		 
			
		String pageNum = req.getParameter("pageNum");//페이지 번호O
		if (pageNum == null) {
            pageNum = "1";
        }
		
        try{    
	        count = bds.getArticleCount();//전체 글의 수 
        }catch(Exception e){
        	
        }
        
        int pageSize = 10;//한 페이지의 글의 갯수
        int pageBlock=10;  //한블럭에 보여줄 페이지 갯수        
        
        page = new Page(Integer.parseInt(pageNum), 
        		count, pageSize, pageBlock);
 		pageCode = page.getSb().toString();
         
        if (count > 0) { 
        	try{ 
            articleList = bds.getArticles(page.getStartRow(), page.getEndRow());//현재 페이지에 해당하는 글 목록
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        } else {
            articleList =null;
        }
        //해당 뷰에서 사용할 속성 //request 객체에 심는 과정 //심지 않고는 class에서 jsp로 값을 넘겨줄 수 없다.
        mav.addObject("count", count); //총 글의 개수
        mav.addObject("pageNum", pageNum); 
        mav.addObject("pageCode", pageCode);  
        mav.addObject("articleList", articleList); //arraylist내부에 들어있는 값을 출력해야 하는데 for문을 쓰지 않고 jstl을 이용해 쉽게 출력한다. 
        mav.setViewName("list");
		
		return mav;
	}
	
	@RequestMapping("/login.ktds")
	public String login(HttpServletRequest req) throws ServletException, IOException, SQLException {		
		
		String id=req.getParameter("id"); //ktds(id)값이 넘어옴
		String pass=req.getParameter("pass"); //1234(pass)값이 넘어옴
//		BBSOracleDao bbs_Oracle_Dao=BBSOracleDao.getInstance(); 
		//BBSOracleDao으로부터 bbs_Oracle_Dao객체를 넘겨 받아 쓸 수 있음
		int status=bds.login_check(id, pass); 
		//id와 pass를 넘겨서 BBSOracleDao에서 메소드로 처리한다
		if(status==1){//그냥 세션을 부르면 불러오지 못한다(jsp내에서만 불러낼 수 있다)
			//servlet context(application내장객체의 타입)
			HttpSession hs=req.getSession();
			hs.setAttribute("id", id);
			//세션에서 id값을 받아옴
			if(hs.getAttribute("logined")==null){
				//세션에 로그인 정보(ok)가 없으면(이 경우는 list화면에서 로그인 한 것이다)
				return "redirect:/list.ktds";
			}else{
				return "redirect:/writeForm.ktds";
				//세션에 로그인 정보가 있으면(이 경우는 write를 명령한 상태로 로그인 한 것이다)
			}
		}else if(status==2){
			System.out.println("즐");
			return null;
		}else{
			System.out.println("바이");
			return null;
		}
	}
	
	@RequestMapping("/logout.ktds")
	public String logout(HttpServletRequest req){
		
		try{
        	HttpSession hs=req.getSession();
        	//세션의 값을 받아온다(로그인이 되어 있으므로 이 경우에는 id가 할당되어 있다)
    		hs.invalidate();//세션 비활성화
        }catch(Exception e){
        	e.printStackTrace();
        }
        
		return "redirect:/list.ktds";
	}
	
	@RequestMapping("/content.ktds")
	public ModelAndView content(HttpServletRequest req){
		ModelAndView mav = new ModelAndView();
		
		try{
			article=bds.getArticle(Integer.parseInt(req.getParameter("article_num")));
			//글 내용에 보여줄 값들(제목, 내용, 파일)을 가져온다
			commentNum=bds.getCommentNum(Integer.parseInt(req.getParameter("article_num")));
			//글 내용 아래 댓글의 수를 표시하기 위한 메서드
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		mav.addObject("article", article);
		mav.addObject("pageNum", req.getParameter("pageNum"));
		mav.addObject("commentNum", commentNum);
		//request객체에 article객체(컨텐트 정보를 담고 있다), 페이지숫자(항상 달고다닌다), 댓글의 수를 심는다.
		mav.setViewName("content");
		return mav;
	}
	
	@RequestMapping("/writeForm.ktds")
	public ModelAndView writeForm(HttpServletRequest req){
		ModelAndView mav = new ModelAndView();
		
		HttpSession hs=req.getSession();
		//getSession으로 세션에 할당된 id값을 가져올 때 사용
		if(hs.getAttribute("id")!=null){
			//세션에 id값이 있는 경우
			mav.setViewName("write");
			return mav;
			//바로 글쓰기 양식으로 간다
		}else{
			hs.setAttribute("logined", "ok");
			mav.setViewName("login");
			return mav;
			//세션에 id가 없는 경우 logined문자열을 세션에 할당하고 login jsp에 넘긴다
		}
	}
	
	@RequestMapping("/write.ktds")
	public String write(HttpServletRequest req) throws IOException {
		
		req.setCharacterEncoding("utf-8"); //한글 입력 허용 코드
		
//		Collection<Part> cp=req.getParts();
//		Iterator<Part> it=cp.iterator();
//		while(it.hasNext()){
//			Part pa=it.next();
//			System.out.println(pa.getName()+"입니다");
//			Collection<String> co=pa.getHeaderNames();
//			Iterator<String> itt=co.iterator();
//			while(itt.hasNext()){
//				String name=itt.next();
//				System.out.println("헤더이름"+name);
//				System.out.println("헤더 값"+pa.getHeader(name));
//			}
//		}		
//		BBSOracleDao bbs_Oracle_Dao=null; 

		BoardVO article=new BoardVO(); //title, id, content, file(입력값들)을 저장하기 위한 선언
		article.setId((String)req.getSession().getAttribute("id"));//세션에서 id값 얻음
		//원래 서블릿 3.0규약에 따라서 한글 값을 읽어올 수 없는데 패치가 되었는지 잘 읽어온다.
		article.setTitle(req.getParameter("title")); //입력받은 title을 받음(jsp로 부터)
		article.setContent(req.getParameter("content")); //입력받은 content를 받음(jsp로 부터)
		article.setFname(req.getParameter("fname")); //입력받은 content를 받음(jsp로 부터)		
//		Part filePart=req.getPart("fname");
//		String fileName = getFileName(filePart);
//		article.setFname(fileName);
		
//		if(fileName!=null && !fileName.equals("")){
//			FileSaveHelper.save(fileName,filePart.getInputStream());
//		}
		
		try{
//			bbs_Oracle_Dao=BBSOracleDao.getInstance(); //DB에 접근을 위한 객체를 받아옴
			bds.insertArticle(article); //할당한 값들을 boardVO 객체를 이용해 넘겨줌
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return "redirect:/list.ktds";
	}
	
	@RequestMapping("/updateForm.ktds")
	public ModelAndView updateForm(HttpServletRequest req){
		ModelAndView mav = new ModelAndView();
		
		try{//제목과 본문내용만 가져올 것(전부 가져오지 말고)
//			bbs_Oracle_Dao=BBSOracleDao.getInstance();
			article=bds.getUpdateArticle(Integer.parseInt(req.getParameter("article_num")));
		}catch(Exception e){
			e.printStackTrace();
		}	
		
		mav.addObject("article", article);
		mav.addObject("article_num", req.getParameter("article_num"));
		mav.addObject("pageNum", req.getParameter("pageNum"));
		
		mav.setViewName("update");
		return mav;
		
	}
	
	@RequestMapping("/update.ktds")
	public String update(HttpServletRequest req) throws IOException{
		
		req.setCharacterEncoding("utf-8"); //한글 입력 허용 코드
		
		int article_num=Integer.parseInt(req.getParameter("article_num"));
		String pageNum=req.getParameter("pageNum");
		String title=req.getParameter("title");
		String content=req.getParameter("content");
		
		try{
//			bbs_Oracle_Dao=BBSOracleDao.getInstance();
			bds.UpdateArticle(article_num, title, content); 
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "redirect:/content.ktds?pageNum="+pageNum+"&article_num="+article_num;
	}
	
	@RequestMapping("/delete.ktds")
	public String delete(HttpServletRequest req) throws SQLException, IOException {
		int article_num=Integer.parseInt(req.getParameter("article_num"));
		String pageNum=req.getParameter("pageNum");
		
//		BBSOracleDao bbs_Oracle_dao= BBSOracleDao.getInstance();
		bds.deleteArticle(article_num);
		return "redirect:/list.ktds?pageNum="+pageNum;	
	}
	
	@RequestMapping("/replyForm.ktds")
	public ModelAndView replyForm(HttpServletRequest req) {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("depth", req.getParameter("depth"));
		mav.addObject("pos", req.getParameter("pos"));
		mav.addObject("pageNum", req.getParameter("pageNum"));
		mav.addObject("group_id", req.getParameter("group_id"));
		
		mav.setViewName("replyForm");
		return mav;
	}
	
	@RequestMapping("/reply.ktds")
	public String reply(HttpServletRequest req) throws IOException {
		
		req.setCharacterEncoding("utf-8");
		
//		Collection<Part> cp=req.getParts();
//		Iterator<Part> it=cp.iterator();
//		while(it.hasNext()){
//			Part pa=it.next();
//			System.out.println(pa.getName()+"입니다");
//			Collection<String> co=pa.getHeaderNames();
//			Iterator<String> itt=co.iterator();
//			while(itt.hasNext()){
//				String name=itt.next();
//				System.out.println("헤더이름"+name);
//				System.out.println("헤더 값"+pa.getHeader(name));
//			}
//		}
		
		article=new BoardVO();
		article.setTitle(req.getParameter("title"));
		article.setId((String)req.getSession().getAttribute("id"));
		article.setContent(req.getParameter("content"));
		article.setPos(Integer.parseInt(req.getParameter("pos")));
		article.setGroup_id(Integer.parseInt(req.getParameter("group_id")));
		article.setDepth(Integer.parseInt(req.getParameter("depth")));
		article.setFname(req.getParameter("fname"));
//		Part filePart=req.getPart("fname");
//		String fileName=getFileName(filePart); //파일 이름을 읽어오기 위한 정의 메서드
//		article.setFname(fileName);
		
//		if(fileName!=null && !fileName.equals("")){//파일을 저장하기 위한 메서드 호출
//			FileSaveHelper.save(fileName,filePart.getInputStream());
//		}
		
		try{
//			bbs_Oracle_Dao=BBSOracleDao.getInstance();	
			bds.reply(article); //답글을 입력하기 위한 메서드 호출
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "redirect:/list.ktds?pageNum="+req.getParameter("pageNum");
		
	}
	
	@RequestMapping("/commentRead.ktds")
	public void commentRead(HttpServletRequest req, HttpServletResponse res)
		throws SQLException, IOException{
		
		res.setCharacterEncoding("utf-8");
		int article_num=Integer.parseInt(req.getParameter("article_num"));		
//		BBSOracleDao bbs_oracle_dao=BBSOracleDao.getInstance();
		ArrayList<CommentVO> commentList=bds.getAllCommnet(article_num);
		JSONArray ja= new JSONArray(commentList);
		PrintWriter pw=res.getWriter();
		pw.println(ja);
		pw.close();
	}
	
	@RequestMapping("/commentWrite.ktds")
	public void commentWrite(HttpServletRequest req, HttpServletResponse res)
		throws SQLException, IOException{
		
		res.setCharacterEncoding("utf-8"); 
		int article_num=Integer.parseInt(req.getParameter("article_num"));
		String comment_content=req.getParameter("comment_content");
		String id=(String)req.getSession().getAttribute("id");
//		BBSOracleDao bbs_oracle_dao=BBSOracleDao.getInstance();
		ArrayList<CommentVO> commentList=
				bds.insertCommnet(article_num, comment_content, id);
		JSONArray ja= new JSONArray(commentList);
		PrintWriter pw=res.getWriter();
		pw.println(ja);
		pw.close();		
		
	}
	
	@RequestMapping("/commentDelete.ktds")
	public void commentDelete(HttpServletRequest req, HttpServletResponse res)
		throws SQLException, IOException{
		
		int article_num=Integer.parseInt(req.getParameter("article_num"));
		
	}
}
