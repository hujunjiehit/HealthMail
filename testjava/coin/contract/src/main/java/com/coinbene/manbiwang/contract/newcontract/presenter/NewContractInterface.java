package com.coinbene.manbiwang.contract.newcontract.presenter;

import android.widget.TextView;

import com.coinbene.common.base.BasePresenter;
import com.coinbene.common.base.BaseView;
import com.coinbene.common.widget.input.PlusSubInputView;
import com.coinbene.manbiwang.model.contract.CalAvlPositionModel;
import com.coinbene.manbiwang.model.contract.ContractPlaceOrderParmsModel;
import com.coinbene.manbiwang.model.contract.PriceParamsModel;
import com.coinbene.manbiwang.model.http.ContractAccountInfoModel;
import com.coinbene.manbiwang.model.http.ContractLeverModel;

public class NewContractInterface {


	public interface View extends BaseView<NewContractInterface.Presenter> {
		void setCurLeverData(int curLeverage);

		void setMarginMode(ContractLeverModel.DataBean data);

		void setContractAccountInfo(ContractAccountInfoModel.DataBean data);

		void setCurrentOrderData( int total);

		void setAvlOpenAccout(String[] avlOpenAccount);

		void setPisitionNumber(int size);

		void placeOrderSucces();

		void updateModeSuccess(String mode);

		void setBond(String[] strings);
	}


	public interface Presenter extends BasePresenter {

		int getPricePresition();

		String getMinPriceChange();

		void setSymbol(String symbol);

		String getQouteAsset();

		String getContractUnit();

		String getContractMinQuantityChange();

		void calculationAvlOpen(PriceParamsModel priceParamsModel, CalAvlPositionModel calAvlPositionModel);

		void calculationSingerAvlOpen(PriceParamsModel priceParamsModel, CalAvlPositionModel calAvlPositionModel);


		void getCurLeverage();

		void getAccountInfo();

		void getCurrentOrderList();

		int getCecheLever();

		void updateContractLever(int leverage);

		void getAllData();

		void userConfigCreate(String usdtContract_protocol, String s);

		String[] getEstimatedValue(String lastPrice, PlusSubInputView text);

		void getPositionlist();

		void subAll();
		void unSubAll();


		String getContractUsdtUnitValue(String qutatity);

		boolean checkProtocalStatus(android.view.View v);

		void doTransFer(android.view.View v);

		void doPlaceOrder(ContractPlaceOrderParmsModel orderModel);

		void cancelAll();

		void updateMarginMode(String mode);

		String getPlaceOrderQuantity(String text);

		void setQuantityPresition(PlusSubInputView editQuantity);

		void calBond(PriceParamsModel priceParamsModel,CalAvlPositionModel calAvlPositionModel,String quantity);
		void calSingerBond(PriceParamsModel priceParamsModel,CalAvlPositionModel calAvlPositionModel,String quantity);
	}
}
