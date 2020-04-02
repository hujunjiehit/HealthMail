package com.coinbene.manbiwang.service.contract;

/**
 * Created by june
 * on 2019-11-11
 */
public interface ContractChangePopWindow<T> {

	void showBelowAnchor();

	void setOnItemClickContrctListener(OnItemClickContractListener<T> onItemClickContrckListener);

	void dismiss();

	interface OnItemClickContractListener<T> {
		void onItemClickContract(T data);
	}
}
