package function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import structure.Instance;
import structure.Message;
import structure.Variable;

/**
 * 寻找同名函数并执行 该类主要根据xml的function名字，查找函数库里的同名函数并执行；
 * 
 * @author Administrator
 */
public class FunctionLauncher {

	// 函数库的名称
	public static final String FUNCTION_BASE_NAME = "function.Functionbase";
	private static Class<?> c = null;
	private static Object functionbase = null;
	private static Set<String> systemFunction = new HashSet<String>();

	static {
		try {
			c = Class.forName(FUNCTION_BASE_NAME);
			functionbase = c.newInstance();
		} catch (ClassNotFoundException e) {
			new RuntimeException(FUNCTION_BASE_NAME + "没有找到！");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		systemFunction.add("END");
		systemFunction.add("NEW");
	}

	private static boolean isSysFunction(String function) {

		return systemFunction.contains(function);
	}

	/**
	 * 根据function的名字调用同名函数
	 * 
	 * @param function
	 *            function的名字
	 * @return 返回Variable类型
	 */
	public static Variable launch(String function) {

		if (isSysFunction(function)) {
			new RuntimeException("STOP###############################STOP");
		}
		Method m;
		Object results = null;

		try {
			m = c.getMethod(function);
			results = m.invoke(functionbase);
			Variable res = (Variable) results;
			return res;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			new RuntimeException("找不到" + function + "方法，请检查"
					+ FUNCTION_BASE_NAME + "是否有该方法！");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 根据function的名字调用同名函数
	 * 
	 * @param function
	 *            function的名字
	 * @param variable
	 *            调用function函数所需要的参数
	 * @return 返回Variable类型
	 */
	public static Variable launch(String function, Variable variable) {

		if (isSysFunction(function)) {
			return null;
		}
		Method m;
		Object results = null;
		Variable v = variable.clone();
		try {
			// 如果variable未定义
			if (v == null) {
				m = c.getMethod(function);
				results = m.invoke(functionbase);
			} else {
				m = c.getMethod(function, Variable.class);
				results = m.invoke(functionbase, v);
			}
			Variable res = (Variable) results;
			return res;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			new RuntimeException("找不到" + function + "方法，请检查"
					+ FUNCTION_BASE_NAME + "是否有该方法！");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean launch(String function, Map<String, Variable> map) {

		if (isSysFunction(function)) {
			return false;
		}
		Method m;
		Object results = null;
		try {
			// 如果variable未定义
			if (map == null) {
				m = c.getMethod(function);
				results = m.invoke(functionbase);
			} else {
				m = c.getMethod(function, Map.class);
				results = m.invoke(functionbase, map);
			}
			Boolean res = (Boolean) results;
			return res;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			new RuntimeException("找不到" + function + "方法，请检查"
					+ FUNCTION_BASE_NAME + "是否有该方法！");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 根据function的名字调用同名函数
	 * 
	 * @param function
	 *            function的名字
	 * @param variables
	 *            调用function函数所需要的参数
	 * @return 返回Variable类型
	 */
	public static Variable launch(String function, List<Variable> variableList) {

		if (isSysFunction(function)) {
			return null;
		}
		Method m;
		List<Variable> variables = new ArrayList<Variable>(
				Arrays.asList(new Variable[variableList.size()]));
		Collections.copy(variables, variableList);
		Object results = null;
		try {

			if (variables == null || variables.size() == 0) {
				m = c.getMethod(function);
				results = m.invoke(functionbase);
			} else {
				m = c.getMethod(function, List.class);
				results = m.invoke(functionbase, variables);
			}
			Variable res = (Variable) results;
			return res;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			new RuntimeException("找不到" + function + "方法，请检查"
					+ FUNCTION_BASE_NAME + "是否有该方法！");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<Variable> launchList(String function,
			List<Variable> variableList) {

		if (isSysFunction(function)) {
			return null;
		}
		// 复制一份
		List<Variable> variables = new ArrayList<Variable>(
				Arrays.asList(new Variable[variableList.size()]));
		Collections.copy(variables, variableList);
		Method m;
		Object results = null;
		try {

			if (variables == null || variables.size() == 0) {
				m = c.getMethod(function);
				results = m.invoke(functionbase);
			} else {
				m = c.getMethod(function, Variable.class);
				results = m.invoke(functionbase, variables);
			}
			ArrayList<Variable> res = (ArrayList<Variable>) results;
			return res;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			new RuntimeException("找不到" + function + "方法，请检查"
					+ FUNCTION_BASE_NAME + "是否有该方法！");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<Variable> launchList(String function, Variable v) {

		if (isSysFunction(function)) {
			return null;
		}

		Method m;
		Variable variable = v.clone();
		Object results = null;
		try {

			if (variable == null) {
				m = c.getMethod(function);
				results = m.invoke(functionbase);
			} else {
				m = c.getMethod(function, Variable.class);
				results = m.invoke(functionbase, variable);
			}

			ArrayList<Variable> res = (ArrayList<Variable>) results;
			return res;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			new RuntimeException("找不到" + function + "方法，请检查"
					+ FUNCTION_BASE_NAME + "是否有该方法！");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void launch(String function, Instance instance,
			Message message) {
		Method m;
		try {

			if (message == null) {
				m = c.getMethod(function, Instance.class);
				m.invoke(functionbase, instance);
			} else {
				m = c.getMethod(function, Instance.class, Message.class);
				m.invoke(functionbase, instance, message);
			}

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			new RuntimeException("找不到" + function + "方法，请检查"
					+ FUNCTION_BASE_NAME + "是否有该方法！");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public static void launch(String function, Instance instance) {
		Method m;
		try {

			m = c.getMethod(function);
			m.invoke(functionbase, instance);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			new RuntimeException("找不到" + function + "方法，请检查"
					+ FUNCTION_BASE_NAME + "是否有该方法！");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}
}
