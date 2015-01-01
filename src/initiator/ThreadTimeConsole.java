package initiator;

/**
 * 用于设置系统内所有线程的轮询时间（TimeUnit.MILLSECOND.sleep(time)）
 * 设置的时间已毫秒为单位
 * @author lods
 *
 */
public enum ThreadTimeConsole {
	
	Thread_MsgPool(1), Thread_XMLSystem(1000),
	Thread_Instance(2), Thread_ListenAction(3), Thread_PublishAction(100),
	Thread_SelfAction(100), Thread_GUI(10);
	
	private int time;
	
	ThreadTimeConsole(int t){
		this.time = t;
	}
	
	public int getTime(){
		return time;
	}

}


