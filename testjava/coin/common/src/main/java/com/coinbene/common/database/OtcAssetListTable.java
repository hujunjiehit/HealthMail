package com.coinbene.common.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class OtcAssetListTable {

    @Id
    public long id;
    public String asset;
}
