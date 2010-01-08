/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 17, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.webstorage.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @see <a href="http://www.w3.org/TR/webstorage/#storage-0">W3C Web Storage - Storage</a>
 */
public class HTML5LocalStorage extends JavaScriptObject {

    protected HTML5LocalStorage() {
    }

    public static final native boolean isSupported()
    /*-{
        return typeof $wnd.localStorage != "undefined";
    }-*/;

    public static final native HTML5LocalStorage getLocalStorage()
    /*-{
        return $wnd.localStorage;
    }-*/;

    public final native HTML5LocalStorage getSessionStorage()
    /*-{
        return $wnd.sessionStorage;
    }-*/;

    public final native int getLength()
    /*-{
        return this.length;
    }-*/;

    public final native String getItem(String key)
    /*-{
        return this.getItem(key);
    }-*/;

    public final native String key(int index)
    /*-{
        return this.key(index);
    }-*/;

    public final native void setItem(String key, String data)
    /*-{
        this.setItem(key, data);
    }-*/;

    public final native void removeItem(String key)
    /*-{
        this.removeItem(key);
    }-*/;

    public final native void clear()
    /*-{
        this.clear();
    }-*/;
}
