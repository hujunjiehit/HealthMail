package com.coinbene.common.database;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class BalanceChainTable implements Serializable {
    @Id(assignable = true)
    public long id;
    public String chain;
    public String asset;
    public String protocolName;
    public String bank;
    public String mainChain;
    public String withdrawPrecision;
    public String transferPrecision;
    public String depositPrecision;
    public boolean deposit;
    public boolean transfer;
    public boolean withdraw;
    public boolean useTag;
    public String minDeposit;
    public String minWithdraw;
    public String withdrawFee;
    public int withdrawMinConfirmations;
    public String depositMinConfirmations;
    public int assetType;
    public String banDepositReason;
    public String banWithdrawReason;
    public String banTransferReason;

    public String depositHints;
    public String withdrawHints;

    public ToOne<BalanceInfoTable> balanceInfoTable;
    public String tagLabel;
}
