package com.hifleet.thread;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hifleet.bean.LoginBean;
import com.hifleet.bean.loginSession;
import com.hifleet.map.IndexConstants;
import com.hifleet.map.MapActivity;
import com.hifleet.map.OsmandApplication;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by manyships on 2017/1/11.
 */

public class EmailLoginThread extends AsyncTask<String, Void, Void>{

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
         */
        private OsmandApplication app;
    String mEmail;
    String mPassword;
    Activity activity;
    public static String sessionid;
    private List<LoginBean> mPhoneLoginBeans = new ArrayList<LoginBean>();
    public EmailLoginThread(OsmandApplication app, String mEmail, String mPassword, Activity activity){
        this.app=app;
        this.mEmail=mEmail;
        this.mPassword=mPassword;
        this.activity=activity;
    }
        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String httpPost = app.myPreferences.getString("loginserver",
                        null)
                        + IndexConstants.LOGIN_URL
                        + mEmail
                        + "&password=" + mPassword;
                System.out.println("islogin:::"+httpPost);
                URL shipsUrl = new URL(httpPost);
                HttpURLConnection conn = (HttpURLConnection) shipsUrl
                        .openConnection();
                if (sessionid != null) {

                    conn.setRequestProperty("cookie", sessionid);
                    // response.encodeURL(sessionid);
                }
                // String cookieval = conn.getHeaderField("set-cookie");
                String cookieval = null;
                Map<String, List<String>> headers = conn.getHeaderFields();
                for (String s : headers.keySet()) {
                    List<String> headerslist = (headers.get(s));
                    for (String ss : headerslist) {
                        System.out.println("islogin value: " + ss);
                        if (ss.contains("JSESSIONID")) {
                            cookieval = ss;
                        }
                    }
                }
                if (cookieval != null) {
                    sessionid = cookieval.substring(0, cookieval.indexOf(";"));
                    // System.out.println("保存sessionid： "+sessionid);
//                    loginSession.setSessionid(sessionid);
//                    app.mEditor.putString("sessionid", sessionid);
//                    app.mEditor.commit();
                }
                conn.setConnectTimeout(10000);
                InputStream inStream = conn.getInputStream();
                parseEmailLoginXML(inStream);
                inStream.close();
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("未能获取网络数据");
                Toast.makeText(activity,"不能获取网络数据", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//            System.err.println("islogin mPhoneLoginBeans:: " + mPhoneLoginBeans.size());
            for (LoginBean l : mPhoneLoginBeans) {
//                System.err.println("islogin LoginBeans:: " + l.getEmail()+l.getFlag()+l.getMsg()+l.getRole()+l.getTraffic());
                if (l.getFlag().equals("1")) {

                    loginSession.setSessionid(sessionid);
                    app.mEditor.putString("sessionid", sessionid);
                    app.mEditor.commit();

                    app.mEditor.putString("PassWord", mPassword);
                    app.mEditor.putString("User", l.getEmail());
                    app.mEditor.putString("Username", l.getName());
                    app.mEditor.putString("type", l.getType());
                    app.mEditor.putString("role", l.getRole());
                    app.setMyrole(l.getRole());
                    app.setIslogin(true);
                    app.setLoginbean(l);
//					System.err.println("islogin rolerole " + l.getRole());
                    app.mEditor.putBoolean("IsLogin", true);
                    app.mEditor.commit();
                }

//                if(activity==null){
//                    System.err.println("activity==null");
//                }
                if(activity!=null&&activity.equals(app.getLoginActivity())){

                    Toast.makeText(activity, l.getMsg(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(app.getLoginActivity(), MapActivity.class);
                    app.getLoginActivity().startActivity(intent);
                    app.getLoginActivity().finish();
                }else if(activity!=null){

                    Toast.makeText(activity, l.getMsg(), Toast.LENGTH_LONG).show();
                    activity.finish();
                }
            }
        }
    private void parseEmailLoginXML(InputStream inStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inStream);
        Element root = document.getDocumentElement();
//		NodeList childNodes = root.getChildNodes();
        mPhoneLoginBeans.clear();
        if (root.getNodeName().compareTo("result") == 0) {
            LoginBean l=new LoginBean();
            l.setEmail(root.getAttribute("email"));
            l.setType(root.getAttribute("type"));
            l.setFlag(root.getAttribute("flag"));
            l.setRole(root.getAttribute("role"));
            l.setMsg(root.getAttribute("msg"));
            l.setName(root.getAttribute("name"));
            if(l.getRole().equals("vvip")){
                l.setMap(root.getAttribute("map"));
                l.setSatellitemap(root.getAttribute("satellitemap"));
                l.setChinachart(root.getAttribute("chinachart"));
                l.setGchart(root.getAttribute("gchart"));
                l.setGchartupdate(root.getAttribute("gchartupdate"));
                l.setShipdetail(root.getAttribute("shipdetail"));
                l.setWeather(root.getAttribute("weather"));
                l.setSearchship(root.getAttribute("searchship"));
                l.setTraffic(root.getAttribute("traffic"));
                l.setFleets(root.getAttribute("fleets"));
                l.setRegionalert(root.getAttribute("regionalert"));
                l.setPortship(root.getAttribute("portship"));
                l.setRegionship(root.getAttribute("regionship"));
                l.setRoute(root.getAttribute("route"));
                l.setObservatory(root.getAttribute("observatory"));
            }
            mPhoneLoginBeans.add(l);
            System.err.println("islogin xmlparse finish LoginBeans:: " + l.getEmail()+l.getFlag()+l.getMsg()+l.getRole());
//			mPhoneLoginBeans.add(XmlParseUtility.parse(root, LoginBean.class));
//			for(LoginBean l:mPhoneLoginBeans){
//			System.err.println("islogin xmlparse finish LoginBeans:: " + l.getEmail()+l.getFlag()+l.getMsg()+l.getRole());
//			}
        }
    }
}
