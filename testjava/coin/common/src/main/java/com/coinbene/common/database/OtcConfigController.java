package com.coinbene.common.database;

import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.OtcAdConfigModel;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class OtcConfigController {

    private static OtcConfigController otcConfigController;

    private OtcConfigController() {

    }

    public static OtcConfigController getInstance() {
        if (otcConfigController == null) {
            synchronized (OtcConfigController.class) {
                if (otcConfigController == null) {
                    otcConfigController = new OtcConfigController();
                }
            }
        }
        return otcConfigController;
    }

    /**
     * @param assetList
     * @return
     */
    public synchronized void addAssetInToDatabase(List<String> assetList) {
        if (assetList == null || assetList.size() == 0) {
            return;
        }
        Box<OtcAssetListTable> tableBox = CBRepository.boxFor(OtcAssetListTable.class);
        if (tableBox != null && tableBox.count() > 0) {
            tableBox.removeAll();
        }
        List<OtcAssetListTable> otcAssetListTables = new ArrayList<>();
        for (int i = 0; i < assetList.size(); i++) {
            OtcAssetListTable otcAssetListTable = new OtcAssetListTable();
            otcAssetListTable.asset = assetList.get(i);
            otcAssetListTables.add(otcAssetListTable);
        }
        tableBox.put(otcAssetListTables);
    }

    /**
     * 查询所有币种
     *
     * @return
     */
    public List<OtcAssetListTable> queryAssetList() {
        Box<OtcAssetListTable> tableBox = CBRepository.boxFor(OtcAssetListTable.class);
        return tableBox.query().build().find();
    }

    public boolean checkAssetExist() {
        Box<OtcAssetListTable> tableBox = CBRepository.boxFor(OtcAssetListTable.class);
        List<OtcAssetListTable> otcAssetListTables = tableBox.query().build().find();
        return otcAssetListTables.size() != 0;
    }


    /**
     * @param currencyList
     * @return
     */
    public synchronized void addCurrencyInToDatabase(List<String> currencyList) {
        if (currencyList == null || currencyList.size() == 0) {
            return;
        }
        Box<OtcCurrencyTable> tableBox = CBRepository.boxFor(OtcCurrencyTable.class);
        if (tableBox != null && tableBox.count() > 0) {
            tableBox.removeAll();
        }
        List<OtcCurrencyTable> otcCurrencyTables = new ArrayList<>();
        for (int i = 0; i < currencyList.size(); i++) {
            OtcCurrencyTable otcCurrencyTable = new OtcCurrencyTable();
            otcCurrencyTable.currency = currencyList.get(i);
            otcCurrencyTables.add(otcCurrencyTable);
        }
        tableBox.put(otcCurrencyTables);
    }


    /**
     * 查询所有法币
     *
     * @return
     */
    public List<OtcCurrencyTable> queryCurrencyList() {
        Box<OtcCurrencyTable> tableBox = CBRepository.boxFor(OtcCurrencyTable.class);
        return tableBox.query().build().find();
    }


    /**
     * @param payType
     * @return
     */
    public synchronized void addPayTypeInToDatabase(List<OtcAdConfigModel.DataBean.PayTypesListBean> payType) {
        if (payType == null || payType.size() == 0) {
            return;
        }
        Box<OtcPayTypeTable> tableBox = CBRepository.boxFor(OtcPayTypeTable.class);
        if (tableBox != null && tableBox.count() > 0) {
            tableBox.removeAll();
        }
        List<OtcPayTypeTable> otcPayTypeTables = new ArrayList<>();
        for (int i = 0; i < payType.size(); i++) {
            OtcPayTypeTable otcCurrencyTable = new OtcPayTypeTable();
            otcCurrencyTable.payTypeName = payType.get(i).getPayTypeName();
            otcCurrencyTable.payId = payType.get(i).getPayId();
            otcCurrencyTable.payTypeId = payType.get(i).getPayTypeId();
            otcPayTypeTables.add(otcCurrencyTable);
        }
        tableBox.put(otcPayTypeTables);
    }

    /**
     * 查询所有支付方式
     *
     * @return
     */
    public List<OtcPayTypeTable> queryPayTypeList() {
        Box<OtcPayTypeTable> tableBox = CBRepository.boxFor(OtcPayTypeTable.class);
        return tableBox.query().build().find();
    }

}
