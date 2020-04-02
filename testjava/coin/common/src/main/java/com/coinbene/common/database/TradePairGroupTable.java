package com.coinbene.common.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;

/**
 * Created by mengxiangdong on 2017/11/27.
 */
@Entity
public class TradePairGroupTable {
    @Transient
    public static final String SELF_GROUP="Favorites";

    @Transient
    public static final String CONTRACT_GROUP ="Contrack";

    @Id
    public long id;
    public String groupName;
    @Index
    public int sort;


    public TradePairGroupTable() {
    }

    public TradePairGroupTable(String groupName) {
        this.groupName = groupName;
    }
}
