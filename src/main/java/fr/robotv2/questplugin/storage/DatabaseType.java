package fr.robotv2.questplugin.storage;

public enum DatabaseType {

    JSON,
    ;

    public static DatabaseType getByLiteral(String name) {

        for (DatabaseType type : DatabaseType.values()) {
            if(type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
