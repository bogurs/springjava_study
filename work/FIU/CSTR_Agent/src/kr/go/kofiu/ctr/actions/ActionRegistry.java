package kr.go.kofiu.ctr.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class ActionRegistry {
	/**
	 * Action을 저장하는 Collection 객체 
	 */
	Collection actions;
	
	/**
	 * 생성자
	 *
	 */
	public ActionRegistry(){
		actions = new ArrayList();
	}
	
	/**
	 * Action을 Registry 에 등록한다.
	 * @param action
	 */
	public void addAction(Action action){
		actions.add(action);
	}
	
	/**
	 * 등록된 모든 Action 객체를 얻어온다.
	 * @return 등록된 모든 Action이 담긴 Collection
	 */
	public Collection getActions() {
		return actions;
	}

	/**
	 * 등록된 모든 Action을 수행한다.
	 *
	 */
	public void fireAction(){
		Iterator iter = actions.iterator();
		while( iter.hasNext() ) {
			Action action = (Action)iter.next();
			//logger.debug("FIRE Action : " + action.getClass().getName());
			action.doAct();
		}
	}

	/**
	 * 변수로 받은 ActionRegistry에 등록된 모든 Action을 더한다.
	 * @param registry
	 */
	public void addActionRegistry(ActionRegistry registry) {
		actions.addAll(registry.getActions());
	}

}
