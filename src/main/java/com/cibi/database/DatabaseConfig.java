package com.cibi.database;

public final class DatabaseConfig {

    public static final String URL = 
            readConfig(
                    "cibi.db.url",
                    "CIBI_DB_URL",
                    "jdbc:mysql://localhost:3306/cibi");

    public static final String USER = 
            readConfig(
                    "cibi.db.user",
                    "CIBI_DB_USER", 
                    "root");
    
    public static final String PASSWORD = 
            readConfig(
                    "cibi.db.password", 
                    "CIBI_DB_PASSWORD", 
                    "bc487180");

    private DatabaseConfig() {
        // impede instanciação
    }

    private static String readConfig(String systemProperty, String environmentVariable, String defaultValue) {
        String propertyValue = System.getProperty(systemProperty);
        if (propertyValue != null) {
            return propertyValue;
        }

        String environmentValue = System.getenv(environmentVariable);
        return environmentValue != null ? environmentValue : defaultValue;
    }
}
