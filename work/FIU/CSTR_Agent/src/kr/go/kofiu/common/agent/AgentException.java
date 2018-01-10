package kr.go.kofiu.common.agent;

import kr.go.kofiu.ctr.actions.Action;
import kr.go.kofiu.ctr.actions.ActionRegistry;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Agent 수행 중 Exception이 발생할 때 AgentException 
 * 		Throw 한다. 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class AgentException extends Exception {

	/**
	 * Action을 등록. 실제 Action 이 추가될 때 initialize를 한다. Lazy initialize 
	 */
	ActionRegistry registry;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1413301835939426234L;

	/**
	 * 생성자  
	 * @param msg 오류 메세지
	 */
	public AgentException(String msg) {
		super(msg);
	}

	/**
	 * 생성자
	 * @param msg 오류 메세지
	 * @param t Caused Exception
	 */
	public AgentException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * 생성자
	 * @param t Caused Exception
	 */
	public AgentException(Throwable t) {
		super(t);
	}

	/**
	 * 생성자
	 * @param registry ActionRegistry
	 * @param t Caused Exception
	 */
	public AgentException(ActionRegistry registry, Throwable t) {
		super(t);
		this.registry = registry;
	}

	/**
	 * Action 을 추가한다. 추가된 Action은 Action을 최종적으로 
	 * Catch 하게 되는 지점에서 수행된다. 
	 * 현재 정의 된 최종 포인트는 ScheduleJob 클래스이다. 
	 * @param action 추가 대상 Action 
	 */
	public void addAction(Action action){
		if ( registry == null)
			registry = new ActionRegistry();
		
		registry.addAction(action);
	}

	/**
	 * ActionRegistry를 Add한다. 
	 * @param registry
	 */
	public void addActionRegistry(ActionRegistry registry){
		if ( registry == null)
			registry = new ActionRegistry();
		
		registry.addActionRegistry(registry);
	}
	
	/**
	 * Action 을 실행한다. 
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
