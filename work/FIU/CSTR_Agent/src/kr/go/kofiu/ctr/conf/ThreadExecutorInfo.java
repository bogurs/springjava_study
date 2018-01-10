package kr.go.kofiu.ctr.conf;

/*******************************************************
 * <pre>
 * 업무   그룹명  : CTR 시스템
 * 서브   업무명  : CTR DB 시스템
 * 설        명  : 
 * 작   성   자  : 최중호 
 * 작   성   일  : 2005. 9. 23
 * copyright @ SK C&C. All Right Reserved
 * <pre>
 *******************************************************/
public class ThreadExecutorInfo {
	
	/**
	 * the number of threads to keep in the pool, even if they are idle.
	 */
	private int CorePoolSize;
	
	/**
	 * the maximum number of threads to allow in the pool.
	 */
	private int MaximumPoolSize;
	
	/**
	 *  when the number of threads is greater than the core, 
	 *  this is the maximum time that excess idle threads will wait for new tasks before terminating.
	 */
	private long KeepAliveTime;

	/**
	 * CorePoolSize 값 get
	 * @return CorePoolSize 값 
	 */
	public int getCorePoolSize() {
		return CorePoolSize;
	}
	
	/**
	 * CorePoolSize 값 set
	 * @param corePoolSize CorePoolSize 값 
	 */
	public void setCorePoolSize(int corePoolSize) {
		CorePoolSize = corePoolSize;
	}
	
	/**
	 * KeepAliveTime 값 get
	 * @return KeepAliveTime 값 
	 */
	public long getKeepAliveTime() {
		return KeepAliveTime;
	}
	
	/**
	 * KeepAliveTime 값 set
	 * @param keepAliveTime KeepAliveTime 값 
	 */
	public void setKeepAliveTime(long keepAliveTime) {
		KeepAliveTime = keepAliveTime;
	}
	
	/**
	 * MaximumPoolSize 값 get
	 * @return MaximumPoolSize 값
	 */
	public int getMaximumPoolSize() {
		return MaximumPoolSize;
	}
	
	/**
	 * MaximumPoolSize 값 set
	 * @param maximumPoolSize MaximumPoolSize 값
	 */
	public void setMaximumPoolSize(int maximumPoolSize) {
		MaximumPoolSize = maximumPoolSize;
	}

	/**
	 * 
	 * @param threadExecutorInfo
	 */
	public void accept(ThreadExecutorInfo threadExecutorInfo) {
		setCorePoolSize(threadExecutorInfo.getCorePoolSize());
		setKeepAliveTime(threadExecutorInfo.getKeepAliveTime());
		setMaximumPoolSize(threadExecutorInfo.getMaximumPoolSize());
	}

	
	public String toString() {
		String xmlText = "\t<ThreadExecutor>\n";
		xmlText += "\t\t<corePoolSize>" + this.getCorePoolSize() + "</corePoolSize>\n" ;
		xmlText += "\t\t<maximumPoolSize>" + this.getMaximumPoolSize() + "</maximumPoolSize>\n" ;
		xmlText += "\t\t<keepAliveTime>" + this.getKeepAliveTime() + "</keepAliveTime>\n" ;
		xmlText += "\t</ThreadExecutor>\n";
		return xmlText;
	}

}
