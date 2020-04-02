package com.coinbene.common.database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Created by june
 * on 2020-02-19
 */
@Entity
public class TagsTable {
	@Id(assignable = true)
	public long id;

	public ToOne<TradePairInfoTable> tradePairInfoTable;

	public String iconUrl;
}
