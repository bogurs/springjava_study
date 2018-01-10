package kr.go.kofiu.ctr.conf;

/*******************************************************
 * <pre>
 * ����   �׷��  : CTR �ý���
 * ����   ������  : CTR DB �ý���
 * ��        ��  : 
 * ��   ��   ��  : ����ȣ 
 * ��   ��   ��  : 2005. 9. 23
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
	 * CorePoolSize �� get
	 * @return CorePoolSize �� 
	 */
	public int getCorePoolSize() {
		return CorePoolSize;
	}
	
	/**
	 * CorePoolSize �� set
	 * @param corePoolSize CorePoolSize �� 
	 */
	public void setCorePoolSize(int corePoolSize) {
		CorePoolSize = corePoolSize;
	}
	
	/**
	 * KeepAliveTime �� get
	 * @return KeepAliveTime �� 
	 */
	public long getKeepAliveTime() {
		return KeepAliveTime;
	}
	
	/**
	 * KeepAliveTime �� set
	 * @param keepAliveTime KeepAliveTime �� 
	 */
	public void setKeepAliveTime(long keepAliveTime) {
		KeepAliveTime = keepAliveTime;
	}
	
	/**
	 * MaximumPoolSize �� get
	 * @return MaximumPoolSize ��
	 */
	public int getMaximumPoolSize() {
		return MaximumPoolSize;
	}
	
	/**
	 * MaximumPoolSize �� set
	 * @param maximumPoolSize MaximumPoolSize ��
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
