package com.coinbene.manbiwang.service.kline;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by june
 * on 2020-03-25
 */
public interface KlineService extends IProvider {
	String getUpsAndDowns(String close, String open);
}
