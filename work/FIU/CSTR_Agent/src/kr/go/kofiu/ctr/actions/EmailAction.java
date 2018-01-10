package kr.go.kofiu.ctr.actions;

import javax.mail.MessagingException;

import kr.go.kofiu.common.agent.CTRAgent;
import kr.go.kofiu.ctr.conf.CtrAgentEnvInfo;
import kr.go.kofiu.ctr.conf.Configure;
import kr.go.kofiu.ctr.conf.MailInfo;
import kr.go.kofiu.ctr.util.SendMailWrapper;

import org.apache.log4j.Logger;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 오류 발생 시 해당 내용을 관리자에게 메일 전송하는 Action.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class EmailAction implements Action {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(EmailAction.class);

	/**
	 * 전자 메일의 제목. 
	 */
	String title;
	
	/**
	 * 메일 본문 내용 
	 */
	private String content;

	/**
	 * 전자 메일의 내용에 들어갈 오류 객체 
	 */
	Throwable t;

	/**
	 * 생성자
	 * @param title 이메일 제목 
	 */
	public EmailAction(String title){
		this(title, null);
	}
	
	/**
	 * 생성자
	 * @param title 이메일 제목 
	 * @param t	Throwable 객체. 해당 내용은 이메일의 본문에 들어간다.
	 */
	public EmailAction(String title, Throwable t){
		this.title = title;
		this.t = t;
	}

	/**
	 * 생성자
	 * @param title 이메일 제목 
	 * @param t	Throwable 객체. 해당 내용은 이메일의 본문에 들어간다.
	 */
	public EmailAction(String title, String content, Throwable t){
		this.title = title;
		this.content = content;
		this.t = t;
	}
	
	/**
	 * 메일 전송
	 */
	public void doAct() {
		MailInfo mailInfo = Configure.getInstance().getAgentInfo().getMailInfo();
		if ( !mailInfo.isEnabled())
			return;
		
		try {
			SendMailWrapper mail = new SendMailWrapper(mailInfo.getHost()
							, mailInfo.getId(), mailInfo.getPassword());
			mail.setFrom(CtrAgentEnvInfo.FIUCTRAgentMailFrom);
			mail.setTo(mailInfo.getTo());
			mail.setSubject("[FATAL : CTR 보고모듈] - " + title);
			
			if ( content == null )
				content = "CTR 보고 모듈이 아래와 같은 이유로 작동을 정지하였습니다.\n빠른 조치 바랍니다.\n";
			
			if ( t != null){
				content += t.getMessage();
				StackTraceElement[] elem = t.getStackTrace();
				for ( int i = 0 ; i < elem.length ; i++) {
					content += elem[i].toString() + "\n";
				}
			}
			mail.setContent(content);
			mail.send();
		} catch (MessagingException e) {
			logger.fatal(e);
			logger.fatal("Fatal Fail to Send Mail!");
			CTRAgent.getInstance().shutdown();
		}
	}

}
