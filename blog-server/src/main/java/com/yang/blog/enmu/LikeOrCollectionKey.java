package com.yang.blog.enmu;

public enum LikeOrCollectionKey {
    LIKE("like", "article:likes:", "article:unlikes:", "article:likesCount:", "user:like:lock:"),
    COLLECTION("collection", "article:collections:", "article:unCollections:", "article:collectionsCount:", "user:collections:lock:"),
    FOOD_COL("foodCol", "article:foodCols:", "article:unfoodCols:", "article:foodColCount:", "user:foodCol:lock:"),;

    private final String type;
    private final String keyPrefix;
    private final String unKeyPrefix;
    private final String countKeyPrefix;
    private final String lockKeyPrefix;

    LikeOrCollectionKey(String type, String keyPrefix, String unKeyPrefix, String countKeyPrefix, String lockKeyPrefix) {
        this.type = type;
        this.keyPrefix = keyPrefix;
        this.unKeyPrefix = unKeyPrefix;
        this.countKeyPrefix = countKeyPrefix;
        this.lockKeyPrefix = lockKeyPrefix;
    }

    // 根据类型字符串获取枚举实例
    public static LikeOrCollectionKey fromType(String type) {
        for (LikeOrCollectionKey op : values()) {
            if (op.type.equals(type)) {
                return op;
            }
        }
        throw new IllegalArgumentException("未知的操作类型: " + type);
    }

    // Getter 方法
    public String getKeyPrefix() { return keyPrefix; }
    public String getUnKeyPrefix() { return unKeyPrefix; }
    public String getCountKeyPrefix() { return countKeyPrefix; }
    public String getLockKeyPrefix() { return lockKeyPrefix; }
}
