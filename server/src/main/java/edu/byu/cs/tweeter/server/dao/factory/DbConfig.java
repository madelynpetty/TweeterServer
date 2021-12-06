package edu.byu.cs.tweeter.server.dao.factory;

public class DbConfig {
    public DbConfig() {
        DAOFactory.setInstance(new DynamoDbFactory());
    }
}
