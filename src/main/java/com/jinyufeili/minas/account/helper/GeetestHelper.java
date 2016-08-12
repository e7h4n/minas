package com.jinyufeili.minas.account.helper;

import com.jinyufeili.minas.account.data.GeetestConfig;
import me.chanjar.weixin.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Java SDK
 */
public class GeetestHelper {

    /**
     * 极验验证二次验证表单数据 chllenge
     */
    public static final String FN_GEETEST_CHALLENGE = "geetest_challenge";

    /**
     * 极验验证二次验证表单数据 validate
     */
    public static final String FN_GEETEST_VALIDATE = "geetest_validate";

    /**
     * 极验验证二次验证表单数据 seccode
     */
    public static final String FN_GEETEST_SECCODE = "geetest_seccode";

    public static final String EMPTY_STRING = "";

    /**
     * 极验验证API服务状态Session Key
     */
    public static String GT_SERVER_STATUS_SESSION_KEY = "gt_server_status";

    protected final String verName = "3.2.0";// SDK版本编号

    protected final String sdkLang = "java";// SD的语言类型

    protected final String apiUrl = "http://api.geetest.com"; //极验验证API URL

    protected final String baseUrl = "api.geetest.com";

    protected final String registerUrl = "/register.php"; //register url

    protected final String validateUrl = "/validate.php"; //validate url

    /**
     * 公钥
     */
    private final String captchaId;

    /**
     * 私钥
     */
    private final String privateKey;

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    public GeetestHelper(String captchaId, String privateKey) {
        this.captchaId = captchaId;
        this.privateKey = privateKey;
    }

    public boolean validateRequest(String challenge, String validate, String securityCode) {
        if (!requestIsLegal(challenge, validate, securityCode)) {
            return false;
        }
        LOG.trace("request legitimate");

        String host = baseUrl;
        String path = validateUrl;
        int port = 80;
        String query = String.format("seccode=%s&sdk=%s", securityCode, (this.sdkLang + "_" + this.verName));
        String response = "";

        LOG.trace("query: ", query);
        try {
            if (validate.length() <= 0) {
                return false;
            }

            if (!checkResultByPrivate(challenge, validate)) {
                return false;
            }
            LOG.trace("checkResultByPrivate");
            response = postValidate(host, path, query, port);

            LOG.trace("response: ", response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOG.trace("md5: ", md5Encode(securityCode));

        if (response.equals(md5Encode(securityCode))) {
            return true;
        } else {
            return false;
        }
    }

    public String registerChallenge() {
        try {
            String GET_URL = apiUrl + registerUrl + "?gt=" + this.captchaId;
            LOG.trace("GET_URL:", GET_URL);
            String result_str = readContentFromGet(GET_URL);
            LOG.trace("register_result:", result_str);

            if (32 == result_str.length()) {
                return this.md5Encode(result_str + this.privateKey);
            } else {
                LOG.warn("gtServer register challenge failed");
                return EMPTY_STRING;
            }
        } catch (Exception e) {
            LOG.warn("register challenge failed", e);
        }

        return EMPTY_STRING;
    }

    public String getCaptchaId() {
        return captchaId;
    }

    /**
     * 判断一个表单对象值是否为空
     *
     * @param gtObj
     * @return
     */
    protected boolean objIsEmpty(Object gtObj) {
        if (gtObj == null) {
            return true;
        }

        if (gtObj.toString().trim().length() == 0) {
            return true;
        }

        return false;
    }

    protected boolean checkResultByPrivate(String challenge, String validate) {
        String encodeStr = md5Encode(privateKey + "geetest" + challenge);
        return validate.equals(encodeStr);
    }

    /**
     * 貌似不是Post方式，后面重构时修改名字
     *
     * @param host
     * @param path
     * @param data
     * @param port
     * @return
     * @throws Exception
     */
    protected String postValidate(String host, String path, String data, int port) throws Exception {
        String response = "error";

        InetAddress addr = InetAddress.getByName(host);
        Socket socket = new Socket(addr, port);
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
        wr.write("POST " + path + " HTTP/1.0\r\n");
        wr.write("Host: " + host + "\r\n");
        wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
        wr.write("Content-Length: " + data.length() + "\r\n");
        wr.write("\r\n"); // 以空行作为分割

        // 发送数据
        wr.write(data);
        wr.flush();

        // 读取返回信息
        BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        String line;
        while ((line = rd.readLine()) != null) {
            response = line;
        }
        wr.close();
        rd.close();
        socket.close();
        return response;
    }

    /**
     * 发送请求，获取服务器返回结果
     *
     * @param getURL
     * @return 服务器返回结果
     * @throws IOException
     */
    private String readContentFromGet(String getURL) throws IOException {

        URL getUrl = new URL(getURL);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();

        connection.setConnectTimeout(2000);// 设置连接主机超时（单位：毫秒）
        connection.setReadTimeout(2000);// 设置从主机读取数据超时（单位：毫秒）

        // 建立与服务器的连接，并未发送数据
        connection.connect();

        // 发送数据到服务器并使用Reader读取返回的数据
        StringBuffer sBuffer = new StringBuffer();

        InputStream inStream = null;
        byte[] buf = new byte[1024];
        inStream = connection.getInputStream();
        for (int n; (n = inStream.read(buf)) != -1; ) {
            sBuffer.append(new String(buf, 0, n, "UTF-8"));
        }
        inStream.close();
        connection.disconnect();// 断开连接

        return sBuffer.toString();
    }

    /**
     * 检查客户端的请求是否合法,三个只要有一个为空，则判断不合法
     */
    private boolean requestIsLegal(String challenge, String validate, String seccode) {
        if (objIsEmpty(challenge)) {
            return false;
        }

        if (objIsEmpty(validate)) {
            return false;
        }

        if (objIsEmpty(seccode)) {
            return false;
        }

        return true;
    }

    private String md5Encode(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }
}