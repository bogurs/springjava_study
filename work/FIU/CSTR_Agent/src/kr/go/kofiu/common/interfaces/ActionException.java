package kr.go.kofiu.common.interfaces;


/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : Action을 수행 중에 발생하는 Exception
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 9
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class ActionException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4421877515429163482L;

	/**
	 * 생성자 
	 * @param msg 오류 메세지 
	 */
	public ActionException(String msg) {
		super(msg);
	}

	/**
	 * 생성자 
	 * @param msg 오류 메세지 
	 * @param t Caused Exception
	 */
	public ActionException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * 생성자
	 * @param t Caused Exception
	 */
	public ActionException(Throwable t) {
		super(t);
	}

}
