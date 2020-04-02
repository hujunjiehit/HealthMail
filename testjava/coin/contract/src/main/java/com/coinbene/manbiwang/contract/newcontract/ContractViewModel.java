package com.coinbene.manbiwang.contract.newcontract;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.model.contract.PriceParamsModel;
import com.coinbene.manbiwang.model.contract.UnitChangeMode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by june
 * on 2020-03-09
 */
public class ContractViewModel extends AndroidViewModel {

	private String buyPriceOne;
	private String sellPriceOne;
	private String lastPrice;
	private String markPrice;
	private String priceSymbol;
	private PriceParamsModel model;

	@Override
	protected void onCleared() {
		super.onCleared();
		EventBus.getDefault().unregister(this);
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onContractUnitChange(UnitChangeMode unitChange){
		getUnitType().postValue(unitChange.getUnit());
	}


	private MutableLiveData<PriceParamsModel> priceParamsModel = new MutableLiveData<>();
	private MutableLiveData<String> clickPrice = new MutableLiveData<>();
	private MutableLiveData<Integer> unitType = new MutableLiveData<>();
	private MutableLiveData<String> symbol = new MutableLiveData<>();
	private MutableLiveData<Boolean> showContractPlan = new MutableLiveData<>();
	private MutableLiveData<Boolean> showContractHighLever = new MutableLiveData<>();
	private MutableLiveData<Boolean> drawerOpenStatus = new MutableLiveData<>();


	public ContractViewModel(@NonNull Application application) {
		super(application);
		EventBus.getDefault().register(this);
		unitType.setValue(SpUtil.getContractUsdtUnitSwitch());
		symbol.setValue(SpUtil.getContractCionNew());
		showContractPlan.setValue(SpUtil.getShowContractPlan());
		showContractHighLever.setValue(false);
		drawerOpenStatus.setValue(false);
	}


	public void setBuyPriceOne(String buyPriceOne) {
		this.buyPriceOne = buyPriceOne;
	}

	public void setSellPriceOne(String sellPriceOne) {
		this.sellPriceOne = sellPriceOne;
	}

	public void setLastPrice(String lastPrice) {
		this.lastPrice = lastPrice;
	}

	public void setMarkPrice(String markPrice) {
		this.markPrice = markPrice;
	}


	public MutableLiveData<String> getClickPrice() {
		return clickPrice;
	}

	public MutableLiveData<PriceParamsModel> getPriceParamsModel() {
		return priceParamsModel;
	}

	public MutableLiveData<Boolean> getShowContractPlan() {
		return showContractPlan;
	}


	public MutableLiveData<Boolean> getShowContractHighLever() {
		return showContractHighLever;
	}

	public void postPriceModel() {
		if (model == null) {
			model = new PriceParamsModel();
		}
		model.setBuyOnePrice(buyPriceOne);
		model.setSellOnePrice(sellPriceOne);
		model.setLastPrice(lastPrice);
		model.setMarkPrice(markPrice);
		model.setSymbol(priceSymbol);
		priceParamsModel.postValue(model);
	}

	public MutableLiveData<Integer> getUnitType() {
		return unitType;
	}

	public MutableLiveData<String> getSymbol() {
		return symbol;
	}

	public void setPriceSymbol(String priceSymbol) {
		this.priceSymbol = priceSymbol;
	}

	public MutableLiveData<Boolean> getDrawerOpenStatus() {
		return drawerOpenStatus;
	}
}
