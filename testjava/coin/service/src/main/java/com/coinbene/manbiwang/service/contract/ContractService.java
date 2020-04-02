package com.coinbene.manbiwang.service.contract;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by june
 * on 2019-11-11
 */
public interface ContractService extends IProvider {

	ContractChangePopWindow getBtcContractChangePopWindow(View anchor, boolean useKline);

	ContractChangePopWindow getUsdtContractChangePopWindow(View anchor, boolean useKline);

	Fragment getContractFragment();

//	void registerTotalListener(Fragment currentOrderUsdtFragment, BaseContractTotalListener baseContractTotalListener);


	void registerTotalListener(Fragment fragment, ContractTotalListener listener);

	void totalListenerUpdateUnit();

	void totalListenerUpdateSymbol(Fragment fragment);

	void totalListenerFragmentHide();

	void totalListenerFragmentShow();

	void clearListener();
}
