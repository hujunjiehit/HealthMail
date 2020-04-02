package com.coinbene.manbiwang.contract.serviceimpl;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.manbiwang.contract.contractbtc.widget.ContractBtcChangePopWindow;
import com.coinbene.manbiwang.contract.contractusdt.ContractUsdtFragment;
import com.coinbene.manbiwang.contract.contractusdt.widget.ContractUsdtChangePopWindow;
import com.coinbene.manbiwang.contract.newcontract.NewContractFragment;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.contract.ContractChangePopWindow;
import com.coinbene.manbiwang.service.contract.ContractService;
import com.coinbene.manbiwang.service.contract.ContractTotalListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by june
 * on 2019-11-11
 */
@Route(path = RouteHub.Contract.contractService)
public class ContractServiceImpl implements ContractService {

	Fragment contractFragment;

	private List<ContractTotalListener> totalListener = new ArrayList<>();

	@Override
	public ContractChangePopWindow getBtcContractChangePopWindow(View anchor, boolean useKline) {
		return new ContractBtcChangePopWindow(anchor, useKline);
	}

	@Override
	public ContractChangePopWindow getUsdtContractChangePopWindow(View anchor, boolean useKline) {
		return new ContractUsdtChangePopWindow(anchor, useKline);
	}

	@Override
	public Fragment getContractFragment() {
		return new NewContractFragment();
	}
//
//	@Override
//	public void registerTotalListener(Fragment fragment, BaseContractTotalListener listener) {
//		if (fragment.getParentFragment() instanceof ContractUsdtFragment) {
//			((ContractUsdtFragment) fragment.getParentFragment()).registerTotalListener(listener);
//		}
//	}

	/**
	 * 注册合约总监听   暂时放了合约名字
	 *
	 * @param listener
	 */
	@Override
	public void registerTotalListener(Fragment fragment, ContractTotalListener listener) {
		if (totalListener == null)
			totalListener = new ArrayList<>();

		if (!totalListener.contains(listener)) {
			totalListener.add(listener);
			if (fragment instanceof ContractUsdtFragment) {
				ContractUsdtFragment contractUsdtFragment = ((ContractUsdtFragment) fragment);
				listener.updateSymbol(contractUsdtFragment.getCurrentContractName());
			}
			//由于第一次注册的时候 fragment本身还没有注册进来  不会调用parent 的 show方法  所以第一次注册 把show方法传给当前注册的fragment
			listener.parentFragmentShow();
		}
	}

	@Override
	public void totalListenerUpdateUnit() {
		if (totalListener != null)
			for (ContractTotalListener listener : totalListener) {
				listener.updateContractUnit();
			}
	}

	@Override
	public void totalListenerUpdateSymbol(Fragment fragment) {
		if (totalListener != null)
			for (ContractTotalListener listener : totalListener) {
				if (fragment instanceof ContractUsdtFragment) {
					ContractUsdtFragment contractUsdtFragment = ((ContractUsdtFragment) fragment);
					listener.updateSymbol(contractUsdtFragment.getCurrentContractName());
				}
			}
	}

	@Override
	public void totalListenerFragmentHide() {
		if (totalListener != null)
			for (ContractTotalListener listener : totalListener) {
				listener.parentFramentHide();
			}
	}

	@Override
	public void totalListenerFragmentShow() {
		if (totalListener != null)
			for (ContractTotalListener listener : totalListener) {
				listener.parentFragmentShow();
			}
	}

	@Override
	public void clearListener() {
		if (totalListener != null) {
			totalListener.clear();
			totalListener = null;
		}
	}


	@Override
	public void init(Context context) {

	}
}
