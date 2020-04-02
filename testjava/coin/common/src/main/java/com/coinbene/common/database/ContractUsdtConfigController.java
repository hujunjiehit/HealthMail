package com.coinbene.common.database;

import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.ContractListModel;

import io.objectbox.Box;

public class ContractUsdtConfigController {

    private static ContractUsdtConfigController contractConfigController;

    private ContractUsdtConfigController() {

    }

    public static ContractUsdtConfigController getInstance() {
        if (contractConfigController == null) {
            synchronized (ContractUsdtConfigController.class) {
                if (contractConfigController == null) {
                    contractConfigController = new ContractUsdtConfigController();
                }
            }
        }
        return contractConfigController;
    }


    /**
     * @param contractConfig
     * @return
     */
    public synchronized void addInToDatabase(ContractListModel.DataBean.ConfigBean contractConfig) {
        if (contractConfig == null) {
            return;
        }
        Box<ContractUsdtConfigTable> tableBox = CBRepository.boxFor(ContractUsdtConfigTable.class);
        if (tableBox == null) {
            return;
        }
        ContractUsdtConfigTable contractConfigTable = new ContractUsdtConfigTable();
        contractConfigTable.makerFeeRate = contractConfig.getMakerFeeRate();
        contractConfigTable.takerFeeRate = contractConfig.getTakerFeeRate();
        contractConfigTable.showPrecision = contractConfig.getShowPrecision();
        contractConfigTable.maintainMarginRate = contractConfig.getMaintainMarginRate();
        contractConfigTable.leverages = contractConfig.getLeverages();
        contractConfigTable.marketOrderFloatRate = contractConfig.getMarketOrderFloatRate();
        contractConfigTable.maxOrder = contractConfig.getMaxOrder();
        CBRepository.getBoxStore().runInTx(() -> {
            tableBox.removeAll();
            tableBox.put(contractConfigTable);
        });
    }

    /**
     * 查询合约配置信息
     *
     * @return
     */
    public ContractUsdtConfigTable queryContrackConfig() {
        Box<ContractUsdtConfigTable> tableBox = CBRepository.boxFor(ContractUsdtConfigTable.class);
        return tableBox.query().build().findFirst();
    }



}
