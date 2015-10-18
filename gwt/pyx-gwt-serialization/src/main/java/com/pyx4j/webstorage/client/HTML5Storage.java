/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Dec 17, 2009
 * @author vlads
 */
package com.pyx4j.webstorage.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.webstorage.client.StorageEvent.NativeStorageEvent;

/**
 *
 * @see <a href="http://www.w3.org/TR/webstorage/#storage-0">W3C Web Storage - Storage</a>
 */
public final class HTML5Storage extends JavaScriptObject {

    private static EventBus eventBus;

    private static boolean disabled = false;

    private static boolean verified = false;

    private static JavaScriptObject dispatchStorageEvent;

    protected HTML5Storage() {
    }

    public static final boolean isSupported() {
        if (BrowserType.isIE() && !GWT.isScript()) {
            return false;
        } else {
            return isSupportedNative() && verifyOnce();
        }
    }

    // We will get the QuotaExceededError in Safari Private Browser Mode on both iOS and OS X
    private static boolean verifyOnce() {
        if (!verified) {
            try {
                getLocalStorage().setItem("localStorageTest", "1");
                getLocalStorage().removeItem("localStorageTest");
            } catch (Throwable t) {
                disabled = true;
            }
            verified = true;
        }
        return !disabled;

    }

    private static final native boolean isSupportedNative() /*-{
                                                            return typeof $wnd.localStorage != "undefined";
                                                            }-*/;

    public static final native HTML5Storage getLocalStorage() /*-{
                                                              return $wnd.localStorage;
                                                              }-*/;

    public static final native HTML5Storage getSessionStorage() /*-{
                                                                return $wnd.sessionStorage;
                                                                }-*/;

    public final native int getLength() /*-{
                                        return this.length;
                                        }-*/;

    /**
     * The getItem(key) method return a structured clone of the current value associated
     * with the given key. If the given key does not exist in the list associated with the
     * object then this method must return null.
     *
     * @param key
     * @return current value or null if key does not exist.
     */
    public final native String getItem(String key) /*-{
                                                   return this.getItem(key);
                                                   }-*/;

    public final native String key(int index) /*-{
                                              return this.key(index);
                                              }-*/;

    /**
     * New key/value pair added to the list, with the given key and with its value set to
     * the newly obtained clone of value.
     *
     * If the given key does exist in the list, then its value updated to the newly
     * obtained clone of value.
     *
     * @param key
     * @param data
     */
    public final native void setItem(String key, String data) /*-{
                                                              this.setItem(key, data);
                                                              }-*/;

    public final native void removeItem(String key) /*-{
                                                    this.removeItem(key);
                                                    }-*/;

    public final native void clear() /*-{
                                     this.clear();
                                     }-*/;

    private static native void initEventDispatcher() /*-{
                                                     @com.pyx4j.webstorage.client.HTML5Storage::dispatchStorageEvent = $entry(function(e) {
                                                     if (!e) { e = $wnd.event; }
                                                     @com.pyx4j.webstorage.client.HTML5Storage::fireEvent(Lcom/pyx4j/webstorage/client/StorageEvent$NativeStorageEvent;) (e);
                                                     });
                                                     if ($wnd.addEventListener) {
                                                     $wnd.addEventListener("storage", @com.pyx4j.webstorage.client.HTML5Storage::dispatchStorageEvent, true);
                                                     } else {
                                                     $doc.attachEvent("onstorage", @com.pyx4j.webstorage.client.HTML5Storage::dispatchStorageEvent);
                                                     }
                                                     }-*/;

    private final static void fireEvent(NativeStorageEvent nativeEvent) {
        if (eventBus != null) {
            eventBus.fireEvent(new StorageEvent(nativeEvent));
        }
    }

    /**
     * There are no difference now, where you attach Handler LocalStorage or
     * SessionStorage! We can't distinguish events.
     */
    public static HandlerRegistration addStorageEventHandler(StorageEventHandler handler) {
        if (dispatchStorageEvent == null) {
            initEventDispatcher();
        }
        if (eventBus == null) {
            eventBus = new SimpleEventBus();
        }
        return eventBus.addHandler(StorageEvent.TYPE, handler);
    }

}
