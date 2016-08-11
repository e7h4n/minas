package com.jinyufeili.minas.account.helper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

/**
 * @author pw
 */
public class SmsHelper {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private String key;

    public SmsHelper(String key) {
        this.key = "key-" + key;
    }

    private void send(String phone, String verifyCode) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", key));
        WebResource webResource = client.resource("http://sms-api.luosimao.com/v1/send.json");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("mobile", phone);
        formData.add("message", String.format("验证码: %s, 请在 10 分钟内输入【翡丽社区】", verifyCode));

        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
                post(ClientResponse.class, formData);
        String textEntity = response.getEntity(String.class);

        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(textEntity);
        } catch (JSONException ex) {
            LOG.error("json exception", ex);
            throw new RuntimeException("验证码发送失败");
        }

        int error_code = jsonObj.getInt("error");
        String error_msg = jsonObj.getString("msg");
        if (error_code == 0) {
            return;
        }

        LOG.error("Send message failed,code is {}, msg is {}.", error_code, error_msg);
        throw new RuntimeException("验证码发送失败");

    }
}