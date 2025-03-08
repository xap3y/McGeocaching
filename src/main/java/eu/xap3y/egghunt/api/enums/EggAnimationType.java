package eu.xap3y.egghunt.api.enums;

public enum EggAnimationType {
    SPIRAL,
    REVEAL;

    public static EggAnimationType fromString(String str) {
        for (EggAnimationType type : EggAnimationType.values()) {
            if (type.name().equalsIgnoreCase(str)) {
                return type;
            }
        }
        return null;
    }
}
