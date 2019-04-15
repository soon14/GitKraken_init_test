package cn.rf.hz.web.mybatis;
  
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
  
/** 
 * @author  
 * java中的boolean和jdbc中的char之间转换;true-1;false-0 
 */  
public class BooleanTypeHandler implements TypeHandler {  
  
    private static final int FALSE_NUM = 0;
	private static final int TRUE_NUM = 1;

	@Override  
    public Object getResult(ResultSet arg0, String arg1) throws SQLException {  
        Integer num = arg0.getInt(arg1);  
        Boolean rt = Boolean.FALSE;  
        if (num==0){  
            rt = Boolean.TRUE;  
        }  
        return rt;   
    }  
  
    @Override  
    public Object getResult(CallableStatement arg0, int arg1) throws SQLException {  
        Boolean b = arg0.getBoolean(arg1);  
        return b == true ? TRUE_NUM : FALSE_NUM;  
    }  
  
    @Override  
    public void setParameter(PreparedStatement arg0, int arg1, Object arg2,  
            JdbcType arg3) throws SQLException {  
        Boolean b = (Boolean) arg2;  
        Integer value = (Boolean) b == Boolean.TRUE ? TRUE_NUM : FALSE_NUM;  
        arg0.setInt(arg1, value);  
    }

	@Override
	public Object getResult(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}  
} 