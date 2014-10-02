package structure;
import java.util.List;

public class Relation {
	private List<RelationMessage> messageList; // message组成的集合

	public Relation(List<RelationMessage> messageList) {
		this.messageList = messageList;
	}

	public List<RelationMessage> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<RelationMessage> messageList) {
		this.messageList = messageList;
	}
	
	/**查找message和instance是否关联，如果关联则返回该instance的action值，否则返回null
	 * @param 消息名
	 * @param 实体名
	 * @return 该实体名的动作名
	 */
	public String detectRelation(String messageName, String instanceName) {
		
		String mname = null;
		for (int i=0; i<messageList.size(); i++) {
			mname = messageList.get(i).getName();
			if (mname.equals(messageName)) {
				return messageList.get(i).relateInstance(instanceName);
			}	
		}
		return null;
	}

}
