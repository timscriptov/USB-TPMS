package com.lidroid.xutils;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.entity.BodyParamsEntity;
import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.ContentBody;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.InputStreamBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;
import com.lidroid.xutils.util.LogUtils;

/**
 * 网络请求参数-拓展实现
 * @version v0.1 king 2014-12-4
 */
public class ExpandRequestParams extends RequestParams {

    private HttpEntity bodyEntity;
    private List<NameValuePair> bodyParams;
    private List<ContentBodyParam> fileParams;

    public ExpandRequestParams() {
        super();
    }

    public ExpandRequestParams(String charset) {
        super(charset);
    }

    
    
    
    
    

    /**
     * 自己实现存储上传参数
     * @param nameValuePairs 参数
     */
    private void addBodyParameterValue(List<NameValuePair> nameValuePairs) {
        if (bodyParams == null) {
            bodyParams = new ArrayList<NameValuePair>();
        }
        
        if (nameValuePairs != null && nameValuePairs.size() > 0) {
            for (NameValuePair pair : nameValuePairs) {
                bodyParams.add(pair);
            }
        }
    }
    /**
     * 自己实现存储上传参数
     * @param nameValuePairs 参数
     */
    private void addBodyParameterValue(NameValuePair... nameValuePairs) {
        addBodyParameterValue(Arrays.asList(nameValuePairs));
    }
    
    public void addBodyParameter(String name, String value) {
        addBodyParameterValue(new BasicNameValuePair(name, value));
    }
    public void addBodyParameter(NameValuePair nameValuePair) {
        addBodyParameterValue(nameValuePair);
    }
    public void addBodyParameter(List<NameValuePair> nameValuePairs) {
        addBodyParameterValue(nameValuePairs);
    }
    
    
    
    
    
    
    /**
     * 自己实现存储上传文件
     * @param key 参数名
     * @param contentBody 内容主体
     */
    private void addBodyParameter(String key, ContentBody contentBody) {
        if (fileParams == null) {
            fileParams = new ArrayList<ContentBodyParam>();
        }
        fileParams.add(new ContentBodyParam(key, contentBody));
    }
    
    public void addBodyParameter(String key, File file) {
        addBodyParameter(key, new FileBody(file));
    }
    public void addBodyParameter(String key, File file, String mimeType) {
        addBodyParameter(key, new FileBody(file, mimeType));
    }
    public void addBodyParameter(String key, File file, String mimeType, String charset) {
        addBodyParameter(key, new FileBody(file, mimeType, charset));
    }
    public void addBodyParameter(String key, File file, String fileName, String mimeType, String charset) {
        addBodyParameter(key, new FileBody(file, fileName, mimeType, charset));
    }
    public void addBodyParameter(String key, InputStream stream, long length) {
        addBodyParameter(key, new InputStreamBody(stream, length));
    }
    public void addBodyParameter(String key, InputStream stream, long length, String fileName) {
        addBodyParameter(key, new InputStreamBody(stream, length, fileName));
    }
    public void addBodyParameter(String key, InputStream stream, long length, String fileName, String mimeType) {
        addBodyParameter(key, new InputStreamBody(stream, length, fileName, mimeType));
    }
    
    
    
    
    
    
    
    /**
     * 重写设置内容主体方法
     */
    public void setBodyEntity(HttpEntity bodyEntity) {
        //super.setBodyEntity(bodyEntity);
        
        this.bodyEntity = bodyEntity;
        if (bodyParams != null) {
            bodyParams.clear();
            bodyParams = null;
        }
        if (fileParams != null) {
            fileParams.clear();
            fileParams = null;
        }
    }

    /**
     * 重写获取内容主体方法
     * Returns an HttpEntity containing all request parameters
     */
    public HttpEntity getEntity() {
        if (bodyEntity != null) {
            return bodyEntity;
        }

        HttpEntity result = null;

        if (fileParams != null && !fileParams.isEmpty()) {

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT, null, Charset.forName(getCharset()));

            if (bodyParams != null && !bodyParams.isEmpty()) {
                for (NameValuePair param : bodyParams) {
                    try {
                        multipartEntity.addPart(param.getName(), new StringBody(param.getValue()));
                    } catch (UnsupportedEncodingException e) {
                        LogUtils.e(e.getMessage(), e);
                    }
                }
            }

            for (ContentBodyParam entry : fileParams) {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }

            result = multipartEntity;
        } else if (bodyParams != null && !bodyParams.isEmpty()) {
            result = new BodyParamsEntity(bodyParams, getCharset());
        }

        return result;
    }
    
    
    
    
    /**
     * 自定义KEY-VALUE实体类
     * @version v0.1 king 2014-12-4
     */
    public class ContentBodyParam {
        public final String key;
        public final ContentBody value;

        public ContentBodyParam(String key, ContentBody value) {
            this.key = key;
            this.value = value;
        }
        
        public String getKey() {
            return this.key;
        }
        public ContentBody getValue() {
            return value;
        }
    }
}