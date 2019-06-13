package com.heshun.dsm.common.http;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * HttpClient请求工具类
 *
 * @author: xu.jin
 * @time: 2014-7-9 0009 下午 15:03
 */
public class HttpUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	public static final String CHARSET = "UTF-8";
	private static final CloseableHttpClient httpClient;

	public static CloseableHttpClient getClient() {
		return httpClient;
	}

	/**
	 * 执行GET请求
	 *
	 * @param url
	 *            GET请求URL字符串
	 * @return 响应字符串
	 * @throws HttpException
	 *             传入参数或返回状态码非200时抛出运行时错误
	 */
	public static String doGet(String url) throws IOException, IllegalArgumentException {
		if (null == url || url.trim().length() == 0) {
			throw new HttpException("URL地址错误");
		}
		String result = null;
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug(url);
			response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
				release(response, httpGet);
				if (logger.isDebugEnabled())
					logger.debug("HttpGet error,status code:" + statusCode);
				throw new HttpException("HttpGet error,status code:" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
		} finally {
			release(response, httpGet);
		}
		return result;
	}

	/**
	 * POST请求StringEntity数据
	 *
	 * @param url
	 *            请求URL地址
	 * @param data
	 *            请求JSON数据
	 * @return String 响应JSON字符串
	 * @throws IOException
	 */
	public static String post(String url, JSONObject data) {
		String result = null;
		HttpEntity httpEntity;
		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		try {
			httpEntity = new StringEntity(data.toJSONString(), ContentType.APPLICATION_JSON);
			httpPost = new HttpPost(url);
			httpPost.setEntity(httpEntity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK != statusCode && logger.isDebugEnabled()) {
				logger.debug("HttpPost error,status code:" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			release(response, httpPost);
		}
		return result;
	}

	/**
	 * 模拟StringEntity表单提交
	 *
	 * @return 返回结果
	 */
	public static Result doGAUrlPost(String url, JSONObject data) throws IOException {
		if (null == url || url.trim().length() == 0) {
			throw new HttpException("URL地址错误");
		}
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			HttpEntity httpEntity = new StringEntity(data.toJSONString(), ContentType.APPLICATION_JSON);
			httpPost.setEntity(httpEntity);
			response = httpClient.execute(httpPost);
			httpEntity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK != statusCode) {
				if (logger.isDebugEnabled())
					logger.debug("HttpPost error,status code:" + statusCode);
				return new Result(Result.FAILURE, statusCode, "请求失败");
			}
			if (null != httpEntity) {
				JSONObject obj = JSONObject.parseObject(EntityUtils.toString(httpEntity));
				Result rt = new Result("True".equalsIgnoreCase(obj.getString("succ")), obj.getString("msg"));
				EntityUtils.consume(httpEntity);
				return rt;
			} else {
				return new Result(Result.FAILURE, "返回值为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Result.FAILURE, "提交失败");
		} finally {
			release(response, httpPost);
		}
	}

	public static String doPostString(String url, Object data) {
		try {
			RequestConfig config = RequestConfig.custom().setConnectTimeout(10000).build();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity(JSON.toJSONString(data), ContentType.APPLICATION_JSON));
			httpPost.setConfig(config);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					return EntityUtils.toString(entity);
				}
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				release(response, httpPost);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static CloseableHttpResponse getHttpResponse(String url, Map<String, String> params) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			url = processGetParams(url, params, CHARSET);
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * HTTP Post 获取内容
	 *
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public static String doPost(String url, Map<String, String> params, String charset) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			List<NameValuePair> pairs = null;
			if (params != null && !params.isEmpty()) {
				pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
			}
			HttpPost httpPost = new HttpPost(url);
			if (pairs != null && pairs.size() > 0) {
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
			}
			CloseableHttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 处理GET参数
	 *
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求参数
	 * @param charset
	 *            请求参数字符集
	 * @return String
	 * @throws IOException
	 */
	private static String processGetParams(String url, Map<String, String> params, String charset) throws IOException {
		if (params != null && !params.isEmpty()) {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if (value != null) {
					pairs.add(new BasicNameValuePair(entry.getKey(), value));
				}
			}
			url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
		}
		return url;
	}

	/**
	 * 释放链接资源
	 *
	 * @param response
	 *            响应流
	 * @param httpMethod
	 *            执行请求方法
	 */
	private static void release(Closeable response, HttpRequestBase httpMethod) {
		if (null != response) {
			try {
				response.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			httpMethod.abort();
			httpMethod.releaseConnection();
		}
	}

	static {
		// PoolingHttpClientConnectionManager pcm = new
		// PoolingHttpClientConnectionManager();
		// pcm.setMaxTotal(200);
		// pcm.setDefaultMaxPerRoute(pcm.getMaxTotal());
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
	}
}
