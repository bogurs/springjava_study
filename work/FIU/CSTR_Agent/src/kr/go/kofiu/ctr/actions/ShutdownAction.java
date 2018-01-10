package kr.go.kofiu.ctr.actions;

import kr.go.kofiu.common.agent.CTRAgent;




/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Agent shutdown Action 구현 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class ShutdownAction implements Action {

	/**
	 * Agent를 Shutdown한다.
	 */
	public void doAct() {
		CTRAgent.getInstance().shutdown();
	}

}
