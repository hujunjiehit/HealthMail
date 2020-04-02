package com.coinbene.common.database;

import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.model.http.ContractListModel;

import io.objectbox.Box;

public class ContractConfigController {

    private static ContractConfigController contractConfigController;

    private ContractConfigController() {

    }

    public static ContractConfigController getInstance() {
        if (contractConfigController == null) {
            synchronized (ContractConfigController.class) {
                if (contractConfigController == null) {
                    contractConfigController = new ContractConfigController();
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
        Box<ContractConfigTable> tableBox = CBRepository.boxFor(ContractConfigTable.class);
        if (tableBox == null) {
            return;
        }
        ContractConfigTable contractConfigTable = new ContractConfigTable();
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
    public ContractConfigTable queryContrackConfig() {
        Box<ContractConfigTable> tableBox = CBRepository.boxFor(ContractConfigTable.class);
        return tableBox.query().build().findFirst();
    }



}
