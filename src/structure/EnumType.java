package structure;

//此文件定义程序中要用到的枚举类型

public enum EnumType {

	APPETIZER(XMLType.Priority.class), MAINCOURSE(XMLType.MessageType.class);
	private XMLType[] Msgvalues;

	private EnumType(Class<? extends XMLType> kind) {
		Msgvalues = kind.getEnumConstants();
	}

	public interface XMLType {

		enum Priority implements XMLType {
			LOWEST, LOW, NORMAL, HIGH, HIGHEST;
		}

		enum MessageType implements XMLType {
			SELF, UNICAST, MULTICAST, BROADCAST;
		}

		enum ActionType {
			SELF, LISTEN, PUBLISH, SUBSCRIPTION, NOTIFICATION;
		}

		enum VariableType {
			BOOLEAN, DATE, DOUBLE, INSTANCE, INT, STRING;
		}
	}
}
