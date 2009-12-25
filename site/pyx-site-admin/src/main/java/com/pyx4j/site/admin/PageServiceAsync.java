package com.pyx4j.site.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface PageServiceAsync {

    void loadPageHtml(String name, AsyncCallback<String> callback);

    void savePageHtml(String name, String html, AsyncCallback<Void> callback);

}
