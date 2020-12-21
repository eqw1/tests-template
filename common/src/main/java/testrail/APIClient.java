package testrail;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class APIClient {

    private String m_user;
    private String m_password;
    private String m_url;

    public APIClient(String base_url) {
        if (!base_url.endsWith("/")) base_url+="/";
        this.m_url = base_url + "index.php?/api/v2";
    }

    /**
     * Get/Set User
     */
    public String getUser() {return this.m_user;}
    public void setUser(String user) {this.m_user = user;}

    /**
     * Get/Set password
     */
    public String getPassword() {return this.m_password;}
    public void setPassword(String password) {this.m_password = password;}

    /**
     * Requests
     */
    public Object sendRequest(String method, String uri, Object data)
            throws MalformedURLException, IOException, APIException
    {
        URL url = new URL(this.m_url + uri);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("Content-Type", "application/json");

        String auth = getAuthorization(this.m_user, this.m_password);
        conn.addRequestProperty("Authorization", "Basic " + auth);

        if (method == "POST") {
            if (data != null) {
                byte[] block = JSONValue.toJSONString(data).getBytes(StandardCharsets.UTF_8);
                conn.setDoOutput(true);
                OutputStream ostream = conn.getOutputStream();
                ostream.write(block);
                ostream.flush();
            }
        }

        int status = conn.getResponseCode();
        InputStream istream;
        if (status != 200) {
            istream = conn.getErrorStream();
            if (istream == null) {
                throw new APIException("TestRail API return HTTP " + status + " (No additional error message received");
            }
        }
        else {
            istream = conn.getInputStream();
        }

        String text = "";
        if (istream != null) {
            BufferedReader reader = new BufferedReader( new InputStreamReader(istream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                text += line;
                text += System.getProperty("line.separator");
            }
            reader.close();
        }

        Object result;
        if (text != "") {
            result = JSONValue.parse(text);
        }
        else {
            result = new JSONObject();
        }

        if (status != 200) {
            String error = "No additional error message received";
            if (result != null && result instanceof JSONObject) {
                JSONObject obj = (JSONObject) result;
                if (obj.containsKey("error")) {
                    error = '"' + (String) obj.get("error") + '"';
                }
            }

            throw new APIException("TestRail API returned HTTP " + status + "(" + error + ")");
        }

        return result;
    }

    public Object sendGet(String uri)
            throws MalformedURLException, IOException, APIException
    {
        return this.sendRequest("GET", uri, null);
    }

    public Object sendPost(String uri, Object data)
            throws MalformedURLException, IOException, APIException
    {
        return this.sendRequest("POST", uri, data);
    }

    public static String getAuthorization(String user, String password)
    {
        return getBase64((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    private static String getBase64(byte[] buffer) {
        final char[] map = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                             'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                             'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
                             'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                             'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                             'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
                             '8', '9', '+', '/'
        };
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buffer.length; i++) {
            byte b0 = buffer[i++], b1 = 0, b2 = 0;
            int bytes = 3;
            if (i < buffer.length) {
                b1 = buffer[i++];
                if (i < buffer.length) b2 = buffer[i];
                else bytes = 2;
            }
            else {
                bytes = 1;
            }
            int total = (b0 << 16) | (b1 << 8) | b2;
            switch (bytes)
            {
                case 3:
                    sb.append(map[(total >> 18) & 0x3f]);
                    sb.append(map[(total >> 12) & 0x3f]);
                    sb.append(map[(total >> 6) & 0x3f]);
                    sb.append(map[total & 0x3f]);
                    break;
                case 4:
                    sb.append(map[(total >> 18) & 0x3f]);
                    sb.append(map[(total >> 12) & 0x3f]);
                    sb.append(map[(total >> 6) & 0x3f]);
                    sb.append("=");
                    break;
                case 1:
                    sb.append(map[(total >> 18) & 0x3f]);
                    sb.append(map[(total >> 12) & 0x3f]);
                    sb.append("=");
                    sb.append("=");
                    break;
            }
        }
        return  sb.toString();
    }
}
