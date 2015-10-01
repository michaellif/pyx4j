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
 * Created on Feb 12, 2010
 * @author vlads
 */
package com.pyx4j.essentials.server;

import java.util.ArrayList;
import java.util.Collection;

import com.pyx4j.config.server.LifecycleListener;
import com.pyx4j.config.server.LocaleResolver;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceContextLifecycleListener;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.essentials.rpc.SystemState;
import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.essentials.server.dev.bugs.MemoryLeakContextLifecycleListener;
import com.pyx4j.i18n.server.CookieLocaleResolver;
import com.pyx4j.server.contexts.ServerContext;
import com.pyx4j.server.contexts.Visit;

public class EssentialsServerSideConfiguration extends ServerSideConfiguration {

    @Override
    public LocaleResolver getLocaleResolver() {
        return new CookieLocaleResolver();
    }

    @Override
    public Collection<LifecycleListener> getLifecycleListeners() {
        Collection<LifecycleListener> rc = new ArrayList<LifecycleListener>(super.getLifecycleListeners());

        rc.add(new PersistenceContextLifecycleListener() {
            @Override
            public void onRequestBegin() {
                super.onRequestBegin();
                Visit visit = ServerContext.getVisit();
                if ((visit != null) && (visit.isUserLoggedIn())) {
                    Persistence.service().setTransactionUserKey(visit.getUserVisit().getPrincipalPrimaryKey());
                }
            }
        });

        if (ApplicationMode.isDevelopment()) {
            rc.add(new MemoryLeakContextLifecycleListener());
        }

        return rc;
    }

    public DataPreloaderCollection getDataPreloaders() {
        return new DataPreloaderCollection();
    }

    public AbstractAntiBot getAntiBot() {
        return new ReCaptchaAntiBot();
    }

    public int getReCaptchaVersion() {
        return 2;
    }

    public String getReCaptchaPrivateKey() {
        return null;
    }

    public String getReCaptchaPublicKey() {
        return null;
    }

    @Override
    public boolean datastoreReadOnly() {
        return SystemMaintenance.getState() != SystemState.Online;
    }

    public Class<? extends SystemMaintenanceState> getSystemMaintenanceStateClass() {
        return SystemMaintenanceState.class;
    }

    @Override
    public String getApplicationMaintenanceMessage() {
        return SystemMaintenance.getApplicationMaintenanceMessage();
    }

}
