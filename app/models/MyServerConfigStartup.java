package models;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.event.ServerConfigStartup;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MyServerConfigStartup implements ServerConfigStartup {


    @Override
    public void onStart(ServerConfig serverConfig) {
        //TODO Edit db connection
        /*
        DataSourceConfig dsconfig = serverConfig.getDataSourceConfig();

        Properties pp=new Properties();
        pp.put("proxy_host", "rproxy.virtual.uniandes.edu.co");
        pp.put("proxy_port", "22");
        //info.put("proxy_type", "[proxy port]");

        Map<String, String> info=new HashMap<String,String>();
        info.put("proxy_host", "rproxy.virtual.uniandes.edu.co");
        info.put("proxy_port", "22");
        dsconfig.setCustomProperties(info);

        try {
            Connection con = serverConfig.getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
        //dsconfig.setCustomProperties();
        //serverConfig.setDatabaseSequenceBatchSize(1);
    }
}