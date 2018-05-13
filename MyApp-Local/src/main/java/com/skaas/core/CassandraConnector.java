package com.skaas.core;

import com.skaas.core.AppConfig;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.ResultSet;

public class CassandraConnector {
 
    private Cluster cluster;
 
    private Session session;
    
    
	public CassandraConnector() {
		
		cluster = Cluster.builder().addContactPoint(AppConfig.dbEndpoint).build();
        session = cluster.connect();
        session.execute("USE myapp");
	}
	
	public ResultSet execute(String query) {
		return session.execute(query);
	}
 
    public void close() {
        session.close();
        cluster.close();
    }
}