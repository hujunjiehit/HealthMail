package com.coinbene.common.network.okgo;

import android.net.ParseException;
import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.NetUtil;
import com.google.gson.JsonParseException;
import com.lzy.okgo.exception.HttpException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLPeerUnverifiedException;


public class OkException {

	private static final int UNAUTHORIZED = 401;
	private static final int FORBIDDEN = 403;
	public static final int NOT_FOUND = 404;
	public static final int REQUEST_TIMEOUT = 408;
	private static final int INTERNAL_SERVER_ERROR = 500;
	private static final int BAD_GATEWAY = 502;
	private static final int SERVICE_UNAVAILABLE = 503;
	private static final int GATEWAY_TIMEOUT = 504;
	private static final int ACCESS_DENIED = 302;
	private static final int HANDEL_ERRROR = 417;

	public static AppThrowable handleException(okhttp3.Response response, Throwable e) {
		int responseCode = 0;
		if (response != null) {
			responseCode = response.code();
		}
		boolean isChinese = LanguageHelper.isChinese(CBRepository.getContext());
		String detail = "";
		if (e.getCause() != null) {
			detail = e.getCause().getMessage();
		}
		AppThrowable ex;

		if (e instanceof HttpException) {
			HttpException httpException = (HttpException) e;
			if (responseCode == 0) {
				responseCode = httpException.code();
			}
			ex = new AppThrowable(e, responseCode, isChinese);
			switch (ex.getCode()) {

				case UNAUTHORIZED:
					ex.setMessage("未授权的请求", "Unauthorized request");
					break;
				case FORBIDDEN:
					ex.setMessage("禁止访问", "Forbidden");
					break;
				case NOT_FOUND:
					ex.setMessage("服务器地址未找到", "Server address not found");
					break;
				case REQUEST_TIMEOUT:
					ex.setMessage("请求超时", "Request timed out");
					break;
				case GATEWAY_TIMEOUT:
					ex.setMessage("网关响应超时", "Gateway response timed out");
					break;
				case INTERNAL_SERVER_ERROR:
					ex.setMessage("服务器出错", "Server error");
					break;
				case BAD_GATEWAY:
					ex.setMessage("无效的请求", "Invalid request");
					break;
				case SERVICE_UNAVAILABLE:
					ex.setMessage("服务器不可用", "Server not available");
					break;
				case ACCESS_DENIED:
					ex.setMessage("网络错误", "Network error");
					break;
				case HANDEL_ERRROR:
					ex.setMessage("接口处理失败", "Interface processing failed");
					break;

				default:
					if (TextUtils.isEmpty(ex.getMessage())) {
						ex.setCode(ERROR.UNKNOWN);
						ex.setMessage("未知错误", "Unknown error");
						break;
					}

			}
		}
//        else if (e instanceof ServerException) {
//            ServerException resultException = (ServerException) e;
//            ex = new Throwable(resultException, resultException.code,isChinese);
//            HashMap<String, String> errorConfigs = ConfigLoader.getErrorConfig();
//            if (errorConfigs != null && errorConfigs.containsKey(String.valueOf(resultException.code))) {
//                ex.setMessage(errorConfigs.get(String.valueOf(resultException.code)),isChinese);
//                return ex;
//            }
//            ex.setMessage(resultException.getMessage());
//            return ex;
//        }
		else if (e instanceof JsonParseException
				|| e instanceof JSONException
				|| e instanceof ParseException) {
			ex = new AppThrowable(e, ERROR.PARSE_ERROR, isChinese);
			ex.setMessage("", "Parse error");//解析错误
		} else if (e instanceof ConnectException) {
			ex = new AppThrowable(e, ERROR.NETWORD_ERROR, isChinese);
			ex.setMessage("", "Connection failed");//连接失败
		} else if (e instanceof javax.net.ssl.SSLHandshakeException) {
			ex = new AppThrowable(e, ERROR.SSL_ERROR, isChinese);
			ex.setMessage("", "Certificate verification failed");//证书验证失败
		} else if (e instanceof java.security.cert.CertPathValidatorException) {
			ex = new AppThrowable(e, ERROR.SSL_NOT_FOUND, isChinese);
			ex.setMessage("", "Certificate path not found");//证书路径没找到

		} else if (e instanceof SSLPeerUnverifiedException) {
			ex = new AppThrowable(e, ERROR.SSL_NOT_FOUND, isChinese);
			ex.setMessage("", "Certificate path not found");//无有效的SSL证书

		} else if (e instanceof ConnectTimeoutException) {
			ex = new AppThrowable(e, ERROR.TIMEOUT_ERROR, isChinese);
			ex.setMessage("", "Connection timed out");//连接超时
		} else if (e instanceof java.net.SocketTimeoutException) {
			ex = new AppThrowable(e, ERROR.TIMEOUT_ERROR, isChinese);
			ex.setMessage("", "Connection timed out");//连接超时
			return ex;
		} else if (e instanceof ClassCastException) {
			ex = new AppThrowable(e, ERROR.FORMAT_ERROR, isChinese);
			ex.setMessage("", "Format error");//类型转换出错
		} else if (e instanceof NullPointerException) {
			ex = new AppThrowable(e, ERROR.NULL, isChinese);
			ex.setMessage("", "Empty data");//Empty data
//        }
//        else if (e instanceof FormatException) {
//            FormatException resultException = (FormatException) e;
//            ex = new Throwable(e, resultException.code,isChinese);
//            ex.setMessage(resultException.message);
//            return ex;
		} else if (e instanceof UnknownHostException) {
			ex = new AppThrowable(e, NOT_FOUND, isChinese);
			if (NetUtil.isNetworkAvailable())
				ex.setMessage("服务器地址未找到,请检查网络或Url", "Server address not found");
			else {
				ex.setMessage("网络错误", "Network error");
			}
		} else {
			ex = new AppThrowable(e, ERROR.UNKNOWN, isChinese);
			ex.setMessage(e.getMessage(), "Unknown error");

		}
//		ex.setUrl(response.request().url().toString());



		return ex;
	}


	/**
	 * 约定异常
	 */
	public class ERROR {
		/**
		 * 未知错误
		 */
		public static final int UNKNOWN = 1000;
		/**
		 * 解析错误
		 */
		public static final int PARSE_ERROR = 1001;
		/**
		 * 网络错误
		 */
		public static final int NETWORD_ERROR = 1002;
		/**
		 * 协议出错
		 */
		public static final int HTTP_ERROR = 1003;

		/**
		 * 证书出错
		 */
		public static final int SSL_ERROR = 1005;

		/**
		 * 连接超时
		 */
		public static final int TIMEOUT_ERROR = 1006;

		/**
		 * 证书未找到
		 */
		public static final int SSL_NOT_FOUND = 1007;

		/**
		 * 出现空值
		 */
		public static final int NULL = -100;

		/**
		 * 格式错误
		 */
		public static final int FORMAT_ERROR = 1008;
	}

}

