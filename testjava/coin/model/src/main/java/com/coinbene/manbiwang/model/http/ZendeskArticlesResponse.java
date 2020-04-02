package com.coinbene.manbiwang.model.http;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created by june
 * on 2019-08-05
 */
public class ZendeskArticlesResponse {

	/**
	 * count : 65
	 * next_page : https://coinbenevip.zendesk.com/api/v2/help_center/en-us/categories/360002111153/articles.json?page=2&per_page=30
	 * page : 1
	 * page_count : 3
	 * per_page : 30
	 * previous_page : null
	 * articles : []
	 * sort_by : position
	 * sort_order : asc
	 */

	private int count;
	private String next_page;
	private int page;
	private int page_count;
	private int per_page;
	private int previous_page;
	private String sort_by;
	private String sort_order;
	private List<ArticlesBean> articles;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getNext_page() {
		return next_page;
	}

	public void setNext_page(String next_page) {
		this.next_page = next_page;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage_count() {
		return page_count;
	}

	public void setPage_count(int page_count) {
		this.page_count = page_count;
	}

	public int getPer_page() {
		return per_page;
	}

	public void setPer_page(int per_page) {
		this.per_page = per_page;
	}

	public int getPrevious_page() {
		return previous_page;
	}

	public void setPrevious_page(int previous_page) {
		this.previous_page = previous_page;
	}

	public String getSort_by() {
		return sort_by;
	}

	public void setSort_by(String sort_by) {
		this.sort_by = sort_by;
	}

	public String getSort_order() {
		return sort_order;
	}

	public void setSort_order(String sort_order) {
		this.sort_order = sort_order;
	}

	public List<ArticlesBean> getArticles() {
		return articles;
	}

	public void setArticles(List<ArticlesBean> articles) {
		this.articles = articles;
	}

	public static class ArticlesBean {

		@NonNull
		@Override
		public String toString() {
			return getHtml_url()+getTitle();
		}

		/**
		 * id : 360033770233
		 * url : https://coinbenevip.zendesk.com/api/v2/help_center/en-us/articles/360033770233--New-Token-BOSAGORA-BOA-Will-be-Listed-on-CoinBene.json
		 * html_url : https://coinbenevip.zendesk.com/hc/en-us/articles/360033770233--New-Token-BOSAGORA-BOA-Will-be-Listed-on-CoinBene
		 * author_id : 385864683493
		 * comments_disabled : false
		 * draft : false
		 * promoted : false
		 * position : 0
		 * vote_sum : 0
		 * vote_count : 0
		 * section_id : 360005018454
		 * created_at : 2019-07-30T11:29:45Z
		 * updated_at : 2019-07-30T11:29:45Z
		 * name : [New Token] BOSAGORA（BOA） Will be Listed on CoinBene
		 * title : [New Token] BOSAGORA（BOA） Will be Listed on CoinBene
		 * source_locale : zh-cn
		 * locale : en-us
		 * outdated : false
		 * outdated_locales : []
		 * edited_at : 2019-07-30T11:29:45Z
		 * user_segment_id : null
		 * permission_group_id : 1136733
		 * label_names : []
		 * body : <p>BOA will be listed on CoinBene, with BOA/USDT trading support, starting from 29 July 2019.<br></p><p><br>BOA Available for Deposit: Available<br>BOA Available for Trading: 30 July 2019 16:00 (GMT+8)<br>BOA Available for Withdrawal: 31 July 2019 16:00 (GMT+8)<br></p><p><br>BOSAGORA
		 focuses on establishing a decentralized blockchain platform which can
		 implement democratic decision-making process based on Trust
		 Contracts and Congress Network that are described in the existing
		 BOScoin White Paper. <br><br>Building on top of the Stellar Consensus
		 Protocol, BOSAGORA adds a proof of stake protocol to make the network
		 open so that any user with 40,000 BOA can participate in consensus. <br>On
		 top of this, we plan to implement Trust Contracts to provide
		 programmatic features expected in a modern Blockchain project. Based on
		 this stack, we intend to build a transparent and clear decision-making
		 body called Congress Network. We also seek to establish an ecosystem by
		 adding functions to the decision-making process that has not been
		 clarified in BOScoin White Paper. <br></p><p><br>Token Volume: 542,130,130.196<br></p><p><br>BOA Official Site :<a href="https://www.bosagora.io" target="_blank">https://www.bosagora.io</a><br>BOA White Paper：<a href="https://bosagora.io/pdf/BOScoinWhitePaper.pdf" target="_blank">https://bosagora.io/pdf/BOScoinWhitePaper.pdf</a><br></p><p><br>CoinBene Social Media<br>Telegram: <a href="https://t.me/coinbene" target="_blank">https://t.me/coinbene</a><br>Twitter: <a href="https://twitter.com/coinbene" target="_blank">https://twitter.com/coinbene  </a><br></p><p><br>Risk Disclosure<br>Purchasing
		 cryptocurrencies and digital assets comes with a number of risks,
		 including but not limited to volatile market price swings, flash
		 crashes, fraud, market manipulation, regulatory changes, and
		 cybersecurity risks. Investors should conduct extensive research into
		 the legitimacy of each individual cryptocurrency, token, or any other
		 form of digital asset, including its platform, before investing. <br>CoinBene
		 strives to protect our users and customers from foreseeable risks such
		 as but not limited to financial fraud, cybersecurity risks, and market
		 manipulation. However, CoinBene is not responsible nor liable for any
		 form of compensation of financial losses resulting from investment in
		 cryptocurrencies and digital assets on the CoinBene platform.<br></p><p><br>CoinBene Team<br>29 July 2019<br><br><br><br></p><p><br></p>
		 */

		private long id;
		private String url;
		private String html_url;
		private long author_id;
		private boolean comments_disabled;
		private boolean draft;
		private boolean promoted;
		private int position;
		private int vote_sum;
		private int vote_count;
		private long section_id;
		private String created_at;
		private String updated_at;
		private String name;
		private String title;
		private String source_locale;
		private String locale;
		private boolean outdated;
		private String edited_at;
		private Object user_segment_id;
		private int permission_group_id;
		private String body;
		private List<?> outdated_locales;
		private List<?> label_names;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getHtml_url() {
			return html_url;
		}

		public void setHtml_url(String html_url) {
			this.html_url = html_url;
		}

		public long getAuthor_id() {
			return author_id;
		}

		public void setAuthor_id(long author_id) {
			this.author_id = author_id;
		}

		public boolean isComments_disabled() {
			return comments_disabled;
		}

		public void setComments_disabled(boolean comments_disabled) {
			this.comments_disabled = comments_disabled;
		}

		public boolean isDraft() {
			return draft;
		}

		public void setDraft(boolean draft) {
			this.draft = draft;
		}

		public boolean isPromoted() {
			return promoted;
		}

		public void setPromoted(boolean promoted) {
			this.promoted = promoted;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public int getVote_sum() {
			return vote_sum;
		}

		public void setVote_sum(int vote_sum) {
			this.vote_sum = vote_sum;
		}

		public int getVote_count() {
			return vote_count;
		}

		public void setVote_count(int vote_count) {
			this.vote_count = vote_count;
		}

		public long getSection_id() {
			return section_id;
		}

		public void setSection_id(long section_id) {
			this.section_id = section_id;
		}

		public String getCreated_at() {
			return created_at;
		}

		public void setCreated_at(String created_at) {
			this.created_at = created_at;
		}

		public String getUpdated_at() {
			return updated_at;
		}

		public void setUpdated_at(String updated_at) {
			this.updated_at = updated_at;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getSource_locale() {
			return source_locale;
		}

		public void setSource_locale(String source_locale) {
			this.source_locale = source_locale;
		}

		public String getLocale() {
			return locale;
		}

		public void setLocale(String locale) {
			this.locale = locale;
		}

		public boolean isOutdated() {
			return outdated;
		}

		public void setOutdated(boolean outdated) {
			this.outdated = outdated;
		}

		public String getEdited_at() {
			return edited_at;
		}

		public void setEdited_at(String edited_at) {
			this.edited_at = edited_at;
		}

		public Object getUser_segment_id() {
			return user_segment_id;
		}

		public void setUser_segment_id(Object user_segment_id) {
			this.user_segment_id = user_segment_id;
		}

		public int getPermission_group_id() {
			return permission_group_id;
		}

		public void setPermission_group_id(int permission_group_id) {
			this.permission_group_id = permission_group_id;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public List<?> getOutdated_locales() {
			return outdated_locales;
		}

		public void setOutdated_locales(List<?> outdated_locales) {
			this.outdated_locales = outdated_locales;
		}

		public List<?> getLabel_names() {
			return label_names;
		}

		public void setLabel_names(List<?> label_names) {
			this.label_names = label_names;
		}
	}
}
