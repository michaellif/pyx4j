/*
 * My Easy Force
 * Copyright (C) 2009-2010 myeasyforce.com.
 *
 * Created on Jan 14, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * See the GWTCacheFilter, it will print how long it took to start hosted mode.
 */
public class NotifyClientStarted {

    public static void notifyServer() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, "/client-started");
        try {
            builder.sendRequest("start", new RequestCallback() {
                @Override
                public void onError(Request request, Throwable exception) {
                }

                @Override
                public void onResponseReceived(Request request, Response response) {
                }
            });
        } catch (RequestException ignore) {
        }
    }

}
