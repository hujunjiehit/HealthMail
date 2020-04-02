package com.coinbene.manbiwang.model.http;

public class BottomSelectModel {

    String typeName;
    int type;

    public BottomSelectModel(String typeName, int type) {
        this.typeName = typeName;
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
