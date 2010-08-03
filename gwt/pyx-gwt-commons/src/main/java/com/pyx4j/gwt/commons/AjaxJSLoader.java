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
 * Created on 2010-08-03
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class AjaxJSLoader {

    private static Logger log = LoggerFactory.getLogger(AjaxJSLoader.class);

    public static interface IsJSLoaded {

        public boolean isLoaded();

    }

    private static class ApiInstance {

        int timeout = 30;

        Timer loadTimer;

        boolean loaded = false;

        Vector<Runnable> queuedLoads = new Vector<Runnable>();

        void load(final String apiUrl, final IsJSLoaded isJSLoaded) {
            injectJS(apiUrl);
            loadTimer = new Timer() {
                @Override
                public void run() {
                    if (isJSLoaded.isLoaded()) {
                        loaded = true;
                        loadTimer.cancel();
                        loadTimer = null;
                        log.debug("loaded {}", apiUrl);
                        fireQueuedActions();
                    } else {
                        timeout--;
                        if (timeout == 0) {
                            loadTimer.cancel();
                            loadTimer = null;
                            // probably working OffLine 
                            log.error("load timeout for {}", apiUrl);
                        }
                    }
                }
            };
            loadTimer.scheduleRepeating(500);
        }

        private void fireQueuedActions() {
            for (Runnable action : queuedLoads) {
                action.run();
            }
            queuedLoads.clear();
        }

        private void injectJS(String apiUrl) {
            String url;
            int prefixIdx = apiUrl.indexOf(')');
            if (prefixIdx == -1) {
                url = Window.Location.getProtocol() + "//" + apiUrl;
            } else {
                int protocolSeparatorIdx = apiUrl.indexOf('|');
                if (Window.Location.getProtocol().equals("https:")) {
                    url = "https://" + apiUrl.substring(1, protocolSeparatorIdx) + apiUrl.substring(prefixIdx + 1);
                } else {
                    url = "http://" + apiUrl.substring(protocolSeparatorIdx + 1, prefixIdx) + apiUrl.substring(prefixIdx + 1);
                }
            }
            Document doc = Document.get();
            ScriptElement script = doc.createScriptElement();
            script.setSrc(url);
            script.setType("text/javascript");
            doc.getBody().appendChild(script);
        }
    }

    static Map<String, ApiInstance> apis = new HashMap<String, ApiInstance>();

    public static synchronized void load(final String apiUrl, IsJSLoaded isJSLoaded, Runnable onLoad) {
        ApiInstance instance = apis.get(apiUrl);
        if (instance == null) {
            instance = new ApiInstance();
            apis.put(apiUrl, instance);
            instance.queuedLoads.add(onLoad);
            instance.load(apiUrl, isJSLoaded);
        } else if (instance.loaded) {
            onLoad.run();
        } else if (instance.timeout != 0) {
            instance.queuedLoads.add(onLoad);
        }
    }
}
