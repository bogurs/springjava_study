package kr.go.kofiu.ctr.actions;



/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Command pattern을 이용한 Action 정의. Action Registry에 
 * 				복수개의 Action을 등록하여 한꺼번에 Action을 취할 수 있다.
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public interface Action {

	/**
	 * 수행할 행위를 이 메소드에 구현한다.
	 *
	 */
	public void doAct();
}
