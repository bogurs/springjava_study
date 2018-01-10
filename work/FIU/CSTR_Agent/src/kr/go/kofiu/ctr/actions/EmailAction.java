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
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : ���� �߻� �� �ش� ������ �����ڿ��� ���� �����ϴ� Action.
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class EmailAction implements Action {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(EmailAction.class);

	/**
	 * ���� ������ ����. 
	 */
	String title;
	
	/**
	 * ���� ���� ���� 
	 */
	private String content;

	/**
	 * ���� ������ ���뿡 �� ���� ��ü 
	 */
	Throwable t;

	/**
	 * ������
	 * @param title �̸��� ���� 
	 */
	public EmailAction(String title){
		this(title, null);
	}
	
	/**
	 * ������
	 * @param title �̸��� ���� 
	 * @param t	Throwable ��ü. �ش� ������ �̸����� ������ ����.
	 */
	public EmailAction(String title, Throwable t){
		this.title = title;
		this.t = t;
	}

	/**
	 * ������
	 * @param title �̸��� ���� 
	 * @param t	Throwable ��ü. �ش� ������ �̸����� ������ ����.
	 */
	public EmailAction(String title, String content, Throwable t){
		this.title = title;
		this.content = content;
		this.t = t;
	}
	
	/**
	 * ���� ����
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
			mail.setSubject("[FATAL : CTR ������] - " + title);
			
			if ( content == null )
				content = "CTR ���� ����� �Ʒ��� ���� ������ �۵��� �����Ͽ����ϴ�.\n���� ��ġ �ٶ��ϴ�.\n";
			
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
