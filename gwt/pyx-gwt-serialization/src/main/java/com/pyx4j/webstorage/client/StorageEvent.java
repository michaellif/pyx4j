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
 * Created on Sep 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.webstorage.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;

public class StorageEvent extends GwtEvent<StorageEventHandler> {

    static Type<StorageEventHandler> TYPE = new Type<StorageEventHandler>();

    private final NativeStorageEvent nativeEvent;

    static class NativeStorageEvent extends JavaScriptObject {

        protected NativeStorageEvent() {
        }

        /**
         * Returns the key being changed.
         * 
         * @return the key being changed
         * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-key">W3C Web
         *      Storage - StorageEvent.key</a>
         */
        public final native String getKey() /*-{
            return this.key;
        }-*/;

        /**
         * Returns the old value of the key being changed.
         * 
         * @return the old value of the key being changed
         * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-oldvalue">W3C
         *      Web Storage - StorageEvent.oldValue</a>
         */
        public final native String getOldValue() /*-{
            return this.oldValue;
        }-*/;

        /**
         * Returns the new value of the key being changed.
         * 
         * @return the new value of the key being changed
         * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-newvalue">W3C
         *      Web Storage - StorageEvent.newValue</a>
         */
        public final native String getNewValue() /*-{
            return this.newValue;
        }-*/;

        /**
         * Returns the address of the document whose key changed.
         * 
         * @return the address of the document whose key changed
         * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-url">W3C Web
         *      Storage - StorageEvent.url</a>
         */
        public final native String getUrl() /*-{
            return this.url;
        }-*/;

        /**
         * Returns the {@link Storage} object that was affected.
         * 
         * @return the {@link Storage} object that was affected
         * @see <a
         *      href="http://www.w3.org/TR/webstorage/#dom-storageevent-storagearea">W3C
         *      Web Storage - StorageEvent.storageArea</a>
         */
        public final native HTML5Storage getStorageArea() /*-{
            return this.storageArea;
        }-*/;
    }

    StorageEvent(NativeStorageEvent nativeEvent) {
        this.nativeEvent = nativeEvent;
    }

    /**
     * Returns the key being changed.
     * 
     * @return the key being changed
     * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-key">W3C Web
     *      Storage - StorageEvent.key</a>
     */
    public String getKey() {
        return this.nativeEvent.getKey();
    };

    /**
     * Returns the old value of the key being changed.
     * 
     * @return the old value of the key being changed
     * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-oldvalue">W3C Web
     *      Storage - StorageEvent.oldValue</a>
     */
    public String getOldValue() {
        return this.nativeEvent.getOldValue();
    };

    /**
     * Returns the new value of the key being changed.
     * 
     * @return the new value of the key being changed
     * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-newvalue">W3C Web
     *      Storage - StorageEvent.newValue</a>
     */
    public String getNewValue() {
        return this.nativeEvent.getNewValue();
    };

    /**
     * Returns the address of the document whose key changed.
     * 
     * @return the address of the document whose key changed
     * @see <a href="http://www.w3.org/TR/webstorage/#dom-storageevent-url">W3C Web
     *      Storage - StorageEvent.url</a>
     */
    public String getUrl() {
        return this.nativeEvent.getUrl();
    };

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StorageEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StorageEventHandler handler) {
        handler.onStorageChange(this);
    }

}
