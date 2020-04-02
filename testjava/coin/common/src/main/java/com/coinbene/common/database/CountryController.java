package com.coinbene.common.database;

import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

/**
 * Created by mxd on 2018/6/1.
 * 对国家数据库的操作，初始化，搜索
 */

public class CountryController {

    private String fileName = "country.txt";
    private static CountryController countryManager;

    private CountryController() {
        Box<CountryTable> tableBox = CBRepository.boxFor(CountryTable.class);
        List<CountryTable> countryTableList = tableBox.query().build().find();
        if (countryTableList == null || countryTableList.size() == 0) {
            initCountryData();
        }
    }

    public static CountryController getInstance() {
        if (countryManager == null) {
            synchronized (CountryController.class) {
                if (countryManager == null) {
                    countryManager = new CountryController();
                }
            }
        }
        return countryManager;
    }

    public List<CountryTable> queryAllCountry() {
        Box<CountryTable> tableBox = CBRepository.boxFor(CountryTable.class);
        List<CountryTable> countryTableList = tableBox.query().build().find();
        if (countryTableList == null || countryTableList.size() == 0) {
            boolean isSuccess = initCountryData();
            if (!isSuccess) {
                ToastUtil.show("init country's data failed");
                return null;
            }
            return tableBox.query().build().find();
        }
        return countryTableList;
    }

    private boolean initCountryData() {
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        boolean result = false;
        List<CountryTable> countryTables = new ArrayList<>();
        try {
            inputReader = new InputStreamReader(CBRepository.getContext().getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                String[] countryInfo = line.split(",");
                if (countryInfo == null || countryInfo.length == 0) {
                    continue;
                }
                //如果长度不等于9，表示数据出现了问题
                if (countryInfo.length != 9) {
                    continue;
                }
                CountryTable countryTable = new CountryTable();
                countryTable.iso = countryInfo[1];
                countryTable.iso3 = countryInfo[2];
                countryTable.name = countryInfo[3];
                countryTable.name_zh = countryInfo[4];
                countryTable.name_english = countryInfo[5];
                countryTable.numcode = countryInfo[6];
                countryTable.phonecode = countryInfo[7];
                countryTable.name_pinyin = StringUtils.changeToTonePinYin(countryInfo[4]);//汉字转化成 拼音全拼
                countryTable.name_pinyin_short = StringUtils.changeToGetShortPinYin(countryInfo[4]);

                countryTable.name_hanyu = TextUtils.isEmpty(countryInfo[8]) ? "" : countryInfo[8];
                countryTables.add(countryTable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (countryTables.size() > 0) {
            Box<CountryTable> tableBox = CBRepository.boxFor(CountryTable.class);
            tableBox.removeAll();
            tableBox.put(countryTables);
            result = true;
        }
        return result;
    }

    public void clearCountryData() {
        Box<CountryTable> tableBox = CBRepository.boxFor(CountryTable.class);
        tableBox.removeAll();
    }

    public List<CountryTable> queryCountryLike(boolean isCN, String searchStr) {
        Box<CountryTable> tableBox = CBRepository.boxFor(CountryTable.class);
        if (isCN) {
            return tableBox.query().contains(CountryTable_.name_zh, searchStr).or().contains(CountryTable_.name_pinyin, searchStr.toLowerCase()).or().contains(CountryTable_.name_pinyin_short, searchStr.toLowerCase()).build().find();
        } else {
            //添加韩语支持
            return tableBox.query().contains(CountryTable_.name_english, searchStr).or().contains(CountryTable_.name_hanyu, searchStr).or().contains(CountryTable_.iso3, searchStr).build().find();
        }
    }

    public CountryTable queryCountryByISO(String country_iso) {
        if (TextUtils.isEmpty(country_iso)) {
            return null;
        }
        country_iso = country_iso.trim();
        Box<CountryTable> tableBox = CBRepository.boxFor(CountryTable.class);
        return tableBox.query().equal(CountryTable_.iso, country_iso).build().findFirst();
    }
}
