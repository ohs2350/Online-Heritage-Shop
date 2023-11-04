package com.ohsproject.ohs.global.constant;

public enum OrderValidTime {
    COMMON("일반 결제", 15L),
    DEPOSITOR("무통장 입금", 60L);

    private final String type;
    private final Long minutes;

    OrderValidTime(String type, Long minutes) {
        this.type = type;
        this.minutes = minutes;
    }

    public String getType() {
        return this.type;
    }

    public Long getMinutes() {
        return this.minutes;
    }

    public int getSeconds() {
        return (int) (this.minutes * 60);
    }
}
