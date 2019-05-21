package cn.rf.hz.web.common;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
/**
 * fuck  BasicDataSource
 * @author feixinag
 * 
 * 主要解决tomcat7 提示内存泄露问题，强制关闭
 *
 */
public class XBasicDataSource extends BasicDataSource {
    @Override
    public synchronized void close() throws SQLException {
        DriverManager.deregisterDriver(DriverManager.getDriver(url));
        super.close();
    }
}