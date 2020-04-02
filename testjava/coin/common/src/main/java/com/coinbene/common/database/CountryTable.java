package com.coinbene.common.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by mengxd on 2018/6/1.
 */
@Entity
public class CountryTable {
    @Id
    public long id;
    public String iso;
    public String iso3;
    public String name;
    public String name_zh;
    public String name_english;
    public String numcode;
    public String phonecode;

    public String name_pinyin;
    public String name_pinyin_short;
    public String name_hanyu;//韩语
}
