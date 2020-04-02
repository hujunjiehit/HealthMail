package com.coinbene.common.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class OtcPayTypeTable {

    @Id
    public long id;
    public String payTypeName;
    public int payTypeId;
    public int payId;
    public String bankName;

    public OtcPayTypeTable() {
    }

    public OtcPayTypeTable(int payTypeId, String payTypeName) {
        this.payTypeName = payTypeName;
        this.payTypeId = payTypeId;
    }
}
