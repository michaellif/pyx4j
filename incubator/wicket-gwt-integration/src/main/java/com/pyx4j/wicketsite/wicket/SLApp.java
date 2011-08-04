package com.pyx4j.wicketsite.wicket;

import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * 
 * @see com.starlight.Start#main(String[])
 */

//http://www.google.com/codesearch#zTyFOgmdQBs/webclient/trunk/src/main/webapp/WEB-INF/web.xml&type=cs
public class SLApp extends WebApplication {

    public SLApp() {
    }

    @Override
    protected void init() {
        mountBookmarkablePage("home", Home.class);
        mountBookmarkablePage("services", Services.class);
        mountBookmarkablePage("about_us", AboutUs.class);
        mountBookmarkablePage("contact", Contact.class);
    }

    @Override
    public Class<Home> getHomePage() {
        return Home.class;
    }

}
