package com.pyx4j.site.admin;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("page")
public interface PageService extends RemoteService {
	
	String getPageHTML(String name);
	void setPageHTML(String name, String html);
	
}
