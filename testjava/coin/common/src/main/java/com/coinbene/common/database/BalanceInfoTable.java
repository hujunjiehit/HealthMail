package com.coinbene.common.database;

import java.io.Serializable;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

/**
 * Created by mengxiangdong on 2017/12/13.
 */
@Entity
public class BalanceInfoTable implements Serializable {
    @Id(assignable = true)
    public long id;
    public String asset;//资产id ,
    public String chain;//资产id ,
    public String bank;//银行 ,
    public boolean deposit;//能否充值 ,
    public String frozenBalance;//冻结资产 ,
    public String precision;//币种精度 ,
    public String totalBalance;//总资产 ,
    public boolean transfer;//能否转账 ,
    public String transferPrecision;//转账精度 ,
    public boolean withdraw;//能够提现 ,
    public String withdrawPrecision;//提现精度
    public String localPreestimate;//预估资产  法币
    public String preestimateBTC;//预估资产 BTC
    public String accountType;//1现货2期货 ,
    public String depositMinConfirmations;//充值区块链最小确认数 ,
    public String depositPrecision;//充值精度 ,
    public String englishAssetName;//英文资产名称
    //    public String iconUrl;//图标地址 ,
    public String localAssetName;//资产名称 ,
    public String minDeposit;//最小充值入账额度 ,
    public String minWithdraw;//最小提币额度 ,
    //    public String smallAmountThreshold;// 小额资产线 ,
    public int sort;//排序 ,
    public boolean useTag;//是否使用tag ,
    public String withdrawFee;//提币手续费 ,
    public String withdrawMinConfirmations;//提币最小区块确认数
    public String depositHints;//充值提示语 ,
    public String tagLabel;//tag标签名称  ,
    public String withdrawHints;//提现提示语 , ,
    public boolean withdrawTag;//提现tag是否必填 ,

    public String banDepositReason;//禁止充币 原因
    public String banWithdrawReason;//禁止 提币 原因
    public String banTransferReason;//禁止转账 原因

    @Backlink
    public ToMany<BalanceChainTable> balanceChains;//多链


    public String availableBalance;//可用资产 ,单个获取资产接口返回这个字段. 这个字段在全部资产中不返回.
    //不想被持久化
    @Transient
    public int from;//1,recharge,2,withdraw
    @Transient
    public boolean isSelected;//选择币种页面用
    @Transient
    public boolean needReturn;//返回值
    @Transient
    public String url;//coin img

}
