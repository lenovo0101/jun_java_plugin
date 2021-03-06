package com.jun.plugin.httphelper.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.jun.plugin.httphelper.WSHttpHelperConfig;
import com.jun.plugin.httphelper.WSHttpHelperConstant;
import com.jun.plugin.httphelper.WSHttpHelperXmlConfig;
import com.jun.plugin.httphelper.annotation.WSRequest;
import com.jun.plugin.httphelper.exception.WSException;
import com.jun.plugin.httphelper.model.ResponseResult;
import com.jun.plugin.httphelper.model.WSRequestContext;
import com.jun.plugin.httphelper.model.config.HttpClientConfig;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 15-12-28.
 */
public abstract class WSHttpAbstractClient {
    protected static Log log = LogFactory.getLog(WSHttpClient.class);

    protected WSRequestContext context;
    protected HttpClient httpClient = null;
    protected HttpRequestBase httpRequest = null;

    public WSHttpAbstractClient(WSRequestContext context) {
        this.context = context;
    }

    protected void buildHttpRequest() throws WSException {
        List<NameValuePair> nvps = context.getNameValuePairList();
        String url = context.getUrl();
        if (context.getMethod() == WSRequest.MethodType.GET) {
            this.httpRequest = new HttpGet(url);
            String str = null;
            try {
                if (nvps != null) {
                    str = EntityUtils.toString(new UrlEncodedFormEntity(nvps, context.getCharset()));
                    String doc = "?";
                    if (context.getUrl().indexOf("?") != -1) {
                        doc = "&";
                    }
                    url += doc + str;
                    //context.setUrl(url);
                    this.httpRequest.setURI(new URI(url));
                }
            } catch (Exception e) {
                throw new WSException(e);
            }
        } else if (context.getMethod() == WSRequest.MethodType.POST) {
            HttpPost post = new HttpPost(url);
            post.setHeader("ContentType", "application/x-www-form-urlencoded");

            Map<String, Object> multipartDataMap = context.getMultipartDataMap();
            try {
                // ????????????
                if (multipartDataMap != null && !multipartDataMap.isEmpty()) {
                    log.debug("????????????");
                    Set<String> keySet = multipartDataMap.keySet();
                    MultipartEntity entity = new MultipartEntity();
                    for (String key : keySet) {
                        Object obj = multipartDataMap.get(key);
                        if (obj instanceof File) {
                            FileBody fileBody = new FileBody((File) obj);
                            entity.addPart(key, fileBody);
                        } else if (obj instanceof String) {
                            StringBody stringBody = new StringBody(String.valueOf(obj));
                            entity.addPart(key, stringBody);
                        }
                    }
                    if (nvps != null && !nvps.isEmpty()) {
                        for (NameValuePair nvp : nvps) {
                            StringBody stringBody = new StringBody(nvp.getValue());
                            entity.addPart(nvp.getName(), stringBody);
                        }
                    }
                    post.setEntity(entity);
                }
                // ????????????
                else {
                    UrlEncodedFormEntity entry = new UrlEncodedFormEntity(nvps, context.getCharset());
                    entry.setContentType("application/x-www-form-urlencoded;charset=" + context.getCharset());
                    post.setEntity(entry);
                }
                this.httpRequest = post;
            } catch (UnsupportedEncodingException e) {
                throw new WSException(e);
            }
        } else if (context.getMethod() == WSRequest.MethodType.DELETE) {
            this.httpRequest = new HttpDelete(url);
            String str = null;
            try {
                if (nvps != null) {
                    str = EntityUtils.toString(new UrlEncodedFormEntity(nvps, context.getCharset()));
                    String doc = "?";
                    if (context.getUrl().indexOf("?") != -1) {
                        doc = "&";
                    }
                    url += doc + str;
                    log.debug(url);
                    //context.setUrl(url);
                    this.httpRequest.setURI(new URI(url));
                }
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        // ??????head
        Map<String, String> headerMap = context.getHeaderMap();
        if (!headerMap.isEmpty()) {
            Set<String> keySet = headerMap.keySet();
            for (String key : keySet) {
                this.httpRequest.addHeader(key, headerMap.get(key));
            }
        }
        // ??????cookie
        Map<String, String> cookieMap = context.getCookieMap();
        if (!cookieMap.isEmpty()) {
            Set<String> keySet = cookieMap.keySet();
            StringBuffer cookies = new StringBuffer();
            for (String key : keySet) {
                cookies.append(key).append("=").append(cookieMap.get(key)).append(";");
            }
            this.httpRequest.addHeader("Cookie", cookies.toString());
        }
    }

    public ResponseResult doRequest() throws WSException {
        if (context == null) {
            throw new WSException("????????????context???");
        }
        httpClient = new DefaultHttpClient();
        if (context.getUrl().startsWith("https")) {
            wrapClient();
        }
        HttpClientConfig httpClientConfig = WSHttpHelperXmlConfig.getInstance().getHttpClientConfig();
        this.httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, httpClientConfig.getConnectionTimeout());
        this.httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, httpClientConfig.getSocketTimeout());
        // ?????????
        buildHttpRequest();
        log.debug(context);
        ResponseResult result = new ResponseResult();
        // ????????????
        long beginTime = System.currentTimeMillis();
        try {
            HttpResponse response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            // ????????????
            result.setStatus(response.getStatusLine().getStatusCode());
            // ????????????
            result.setWasteTime(System.currentTimeMillis() - beginTime);
            // ?????????gzip??????
            boolean isGzip = false;
            if (entity.getContentEncoding() != null) {
                String gzip = entity.getContentEncoding().getValue();
                isGzip = "gzip".equals(gzip.toLowerCase());
            }
            // ?????????byte[]
            if (context.getResponseType() == WSRequest.ResponseType.BYTE_ARRAY) {
                InputStream in = entity.getContent();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    byte b[] = new byte[1024 * 8];
                    int j = 0;
                    while ((j = in.read(b)) != -1) {
                        out.write(b, 0, j);
                    }
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    throw ex;
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        throw e;
                    }
                }
                result.setBody(out.toByteArray());
            }
            // gzip????????????
            else if (isGzip) {
                result.setBody(EntityUtils.toString(new GzipDecompressingEntity(entity), context.getCharset()));
            } else {
                result.setBody(EntityUtils.toString(entity, context.getCharset()));
            }
            // ??????cookie
            Header[] cookies = response.getHeaders("Cookie");
            if (cookies != null && cookies.length > 0) {
                for (Header cookie : cookies) {
                    context.addCookie(cookie.getName(), cookie.getValue());
                }
            }
            log.debug("???????????????" + result.getWasteTime());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new WSException(e);
        } finally {
            httpClient.getConnectionManager().closeExpiredConnections();
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

    private void wrapClient() throws WSException {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs,
                                               String string) {
                }

                public void checkServerTrusted(X509Certificate[] xcs,
                                               String string) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            httpClient = new DefaultHttpClient(ccm, httpClient.getParams());
        } catch (Exception ex) {
            throw new WSException(ex);
        }
    }
}
