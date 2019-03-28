package ru.hse.spb.interpreter.model;

public enum EntityType {
    SIMPLE_PART(true), PART_IN_PRIME(false), PART_IN_DOUBLE_PRIME(true);

    private final boolean needReplace;

    EntityType(boolean needReplace) {
        this.needReplace = needReplace;
    }

    public boolean isNeedReplace() {
        return needReplace;
    }
}
