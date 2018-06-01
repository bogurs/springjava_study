package com.ktds.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class testController {
	//각각의 메소드에서 보내줄 뷰이름을 지정해서 만들면 앞서 j2ee에서 만들었던 jsp파일을 그대로 호출하여 사용할 수 있다.
	ParentMaster m;
//	public testController(ParentMaster m){
//		this.m=m;
//	}//생성자 DI로 하는 방법 //싱글톤으로 올라감
	
	@RequestMapping("/test.ktds")
	public ModelAndView test(){
		ModelAndView mav = new ModelAndView();
		mav.addObject("test", m.test()); //req.setAttribute와 같은 역할을 갖는다
		mav.setViewName("home"); //view 이름을 지정해줄 수 있다(현재는 설정되어 있는 home의 값을 주었음)
		return mav;		
	}
	
	public void setMaster(ParentMaster m){
		this.m=m;
	}//properties를 이용한 방법
}
