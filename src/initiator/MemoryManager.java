package initiator;

import java.sql.*;
import java.util.ArrayList;

import structure.Instance;

public class MemoryManager {

	String driver = "oracle.jdbc.OracleDriver";// 驱动字符串
	String url = "jdbc:oracle:thin:@localhost:1521:lods";// 链接字符串
	String user = "scott";// 用户名
	String password = "tiger";// 密码

	void store() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection conn = null;
		Statement statt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			statt = conn.createStatement();

//			String createInstancesTableSql = "CREATE TABLE sun ("
//					+ "no NUMBER(5), weight NUMBER(38), "
//					+ "position_x NUMBER(15), position_y NUMBER(15), "
//					+ "velocity_x NUMBER(15), velocity_y NUMBER(15))";
//			statt.executeUpdate(createInstancesTableSql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			rs = null;

			if (statt != null) {
				try {
					statt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			statt = null;

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn = null;
		}

	}

	public static void main(String[] args) {
		MemoryManager mMgr = new MemoryManager();
		mMgr.store();
	}

	public void saveInstance(Instance instance) {
		
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection conn = null;
		Statement statt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			statt = conn.createStatement();

			int no = instance.getId();
			ArrayList<String> pValues = instance.toPropertyValueList();
			
			String insertInstance = "INSERT INTO " + instance.getName() + " values (" + no;
			for (int i=0; i<pValues.size(); i++) {
				insertInstance += ", " + pValues.get(i);
			}
			insertInstance += ")";
			
//			System.out.println("insertInstance = " + insertInstance);
			statt.executeUpdate(insertInstance);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			rs = null;

			if (statt != null) {
				try {
					statt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			statt = null;

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn = null;
		}
	
		
	}

	public void createTable(Instance instance) {

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection conn = null;
		Statement statt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			statt = conn.createStatement();

			ArrayList<String> propertiesName = instance.toPropertyNameList();
			
			String createInstancesTableSql = "CREATE TABLE " + instance.getName() + " ("
					+ "no NUMBER(5)";
			for (int i=0; i<propertiesName.size(); i++) {
				createInstancesTableSql += ", " + propertiesName.get(i) + " VARCHAR2(50)";
			}
			createInstancesTableSql += ")";
			
			statt.executeUpdate(createInstancesTableSql);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			rs = null;

			if (statt != null) {
				try {
					statt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			statt = null;

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			conn = null;
		}
	}

}
