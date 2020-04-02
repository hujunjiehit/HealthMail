package com.coinbene.manbiwang.service.record;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;


/**
 * Created by june
 * on 2019-11-09
 */
public interface RecordService extends IProvider{

	/**
	 * 跳转到记录页面
	 * @param context
	 * @param recordType
	 */
	void gotoRecord(Context context, RecordType recordType);

	/**
	 * 获取合约历史委托fragment
	 * @param contractType
	 * @return
	 */
	 Fragment getContractHistoryOrderFragment(int contractType);


	/**
	 * 获取合约当前委托
	 */

	Fragment getContractCurrentOrderFragment(int contractType);
}
