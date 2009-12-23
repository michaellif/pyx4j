package com.pyx4j.site.admin;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface PageServiceAsync {
	void getPageHTML(String name, AsyncCallback<String> callback);
	void setPageHTML(String name, String html, AsyncCallback<Void> callback);

}
