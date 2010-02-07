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
 * Created on Feb 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamReader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RecoverableAsyncCallback;
import com.pyx4j.serialization.client.SymmetricClientSerializationStreamWriter;
import com.pyx4j.site.rpc.SiteRequest;
import com.pyx4j.site.rpc.SiteServices;
import com.pyx4j.site.shared.domain.Site;
import com.pyx4j.webstorage.client.HTML5LocalStorage;

/**
 * Use HTML 5 local storage as Cache for site information.
 */
public class SiteCache {

    private static Logger log = LoggerFactory.getLogger(SiteCache.class);

    static {
        RPCManager.ensureInitialization();
    }

    private static RPCSerializer rpcSerializer = GWT.create(RPCSerializer.class);

    public static void obtain(final String siteId, final AsyncCallback<Site> callback) {
        final SiteRequest siteRequest = new SiteRequest();
        siteRequest.setSiteId(siteId);

        final String dataKey = "pyx4j.site." + siteId;
        final Site siteLocal;
        // Read the local site data then check for site update.
        if (HTML5LocalStorage.isSupported()) {
            siteLocal = EntityFactory.create(Site.class);
            HTML5LocalStorage storage = HTML5LocalStorage.getLocalStorage();
            String siteDataSerial = storage.getItem(dataKey);
            if (siteDataSerial != null) {
                try {
                    ClientSerializationStreamReader r = new ClientSerializationStreamReader(rpcSerializer.getSerializer());
                    r.prepareToRead(siteDataSerial);
                    Object o = r.readObject();
                    if (o instanceof Site) {
                        siteLocal.set((Site) o);
                        siteRequest.setModificationTime(siteLocal.updateTimestamp().getValue());
                        log.debug("local site {} payload processed", siteRequest.getModificationTime());
                    }
                } catch (Throwable t) {
                    log.error("Unable to read local site", t);
                }
                if (siteLocal.isNull()) {
                    storage.removeItem(dataKey);
                }
            }
        } else {
            siteLocal = null;
        }

        final AsyncCallback<Site> rpcCallback = new RecoverableAsyncCallback<Site>() {

            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            public void onSuccess(Site result) {
                // Old browser, no HTML5 storage or empty DB
                if ((siteLocal == null) || (result == null)) {
                    callback.onSuccess(result);
                } else {
                    if (result.isNull()) {
                        log.debug("Site is not updated, local site version used");
                        callback.onSuccess(siteLocal);
                    } else {
                        log.debug("Site updated {}, will store localy for future use", result.updateTimestamp().getValue());
                        callback.onSuccess(result);
                        storeSiteLocaly(dataKey, result);
                    }
                }
            }
        };

        RPCManager.execute(SiteServices.Retrieve.class, siteRequest, rpcCallback);
    }

    private static void storeSiteLocaly(String dataKey, Site site) {
        try {
            HTML5LocalStorage storage = HTML5LocalStorage.getLocalStorage();
            SymmetricClientSerializationStreamWriter w = new SymmetricClientSerializationStreamWriter(rpcSerializer.getSerializer());
            w.prepareToWrite();
            w.writeObject(site);
            storage.setItem(dataKey, w.toString());
        } catch (Throwable t) {
            log.error("Unable to store local site", t);
        }
    }
}
