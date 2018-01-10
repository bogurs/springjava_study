package kr.go.kofiu.common.agent;

import kr.go.kofiu.ctr.actions.Action;
import kr.go.kofiu.ctr.actions.ActionRegistry;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : Agent ���� �� Exception�� �߻��� �� AgentException 
 * 		Throw �Ѵ�. 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentException extends Exception {

	/**
	 * Action�� ���. ���� Action �� �߰��� �� initialize�� �Ѵ�. Lazy initialize 
	 */
	ActionRegistry registry;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1413301835939426234L;

	/**
	 * ������  
	 * @param msg ���� �޼���
	 */
	public AgentException(String msg) {
		super(msg);
	}

	/**
	 * ������
	 * @param msg ���� �޼���
	 * @param t Caused Exception
	 */
	public AgentException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * ������
	 * @param t Caused Exception
	 */
	public AgentException(Throwable t) {
		super(t);
	}

	/**
	 * ������
	 * @param registry ActionRegistry
	 * @param t Caused Exception
	 */
	public AgentException(ActionRegistry registry, Throwable t) {
		super(t);
		this.registry = registry;
	}

	/**
	 * Action �� �߰��Ѵ�. �߰��� Action�� Action�� ���������� 
	 * Catch �ϰ� �Ǵ� �������� ����ȴ�. 
	 * ���� ���� �� ���� ����Ʈ�� ScheduleJob Ŭ�����̴�. 
	 * @param action �߰� ��� Action 
	 */
	public void addAction(Action action){
		if ( registry == null)
			registry = new ActionRegistry();
		
		registry.addAction(action);
	}

	/**
	 * ActionRegistry�� Add�Ѵ�. 
	 * @param registry
	 */
	public void addActionRegistry(ActionRegistry registry){
		if ( registry == null)
			registry = new ActionRegistry();
		
		registry.addActionRegistry(registry);
	}
	
	/**
	 * Action �� �����Ѵ�. 
	 *
	 */
	public void fireAction(){
		if ( registry != null ) 
			registry.fireAction();	
		
		if ( getCause() != null && (getCause() instanceof AgentException) ){
			AgentException ex = (AgentException)getCause();
			ex.fireAction();
		}
	}

	/**
	 * get ActionRegistry
	 * @return
	 */
	public ActionRegistry getRegistry() {
		return registry;
	}
}
