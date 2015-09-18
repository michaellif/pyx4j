/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Sep 7, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.sessionstorage;

import com.pyx4j.config.server.FacadeFactory;
import com.pyx4j.config.server.ServerSideConfiguration;

public class SessionBlobStorageFacadeFactory implements FacadeFactory<SessionBlobStorageFacade> {

    //TODO Misha?? Maybe we should change ServerSideFactory to do this?
    private static Class<? extends SessionBlobStorageFacade> implCalss;

    public static void register(Class<? extends SessionBlobStorageFacade> implCalss) {
        SessionBlobStorageFacadeFactory.implCalss = implCalss;
    }

    static {
        switch (ServerSideConfiguration.instance().getEnvironmentType()) {
        case GAEDevelopment:
        case GAESandbox:
            register(SessionBlobStorageFacadeInMemoryImpl.class);
        default:
            break;
        }
    }

    @Override
    public SessionBlobStorageFacade getFacade() {
        if (implCalss != null) {
            try {
                return implCalss.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new Error(e);
            }
        } else {
            return new SessionBlobStorageFacadeDefaultImpl();
        }
    }

}
