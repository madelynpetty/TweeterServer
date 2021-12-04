package edu.byu.cs.tweeter.server.dao;

public class DbConfig {
    public DbConfig() {
        DAOFactory.setInstance(new DynamoDbFactory());
    }
}
