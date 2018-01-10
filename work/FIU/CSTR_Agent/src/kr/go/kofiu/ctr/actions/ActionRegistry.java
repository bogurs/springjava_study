package kr.go.kofiu.ctr.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : ���� ��� Agent
 * ��        ��  : 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class ActionRegistry {
	/**
	 * Action�� �����ϴ� Collection ��ü 
	 */
	Collection actions;
	
	/**
	 * ������
	 *
	 */
	public ActionRegistry(){
		actions = new ArrayList();
	}
	
	/**
	 * Action�� Registry �� ����Ѵ�.
	 * @param action
	 */
	public void addAction(Action action){
		actions.add(action);
	}
	
	/**
	 * ��ϵ� ��� Action ��ü�� ���´�.
	 * @return ��ϵ� ��� Action�� ��� Collection
	 */
	public Collection getActions() {
		return actions;
	}

	/**
	 * ��ϵ� ��� Action�� �����Ѵ�.
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
	 * ������ ���� ActionRegistry�� ��ϵ� ��� Action�� ���Ѵ�.
	 * @param registry
	 */
	public void addActionRegistry(ActionRegistry registry) {
		actions.addAll(registry.getActions());
	}

}
