/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-01-13
 * @author vlads
 */
package com.pyx4j.security.server;

import com.google.appengine.api.capabilities.CapabilitiesServiceFactory;
import com.google.appengine.api.capabilities.Capability;
import com.google.appengine.api.capabilities.CapabilityState;
import com.google.appengine.api.capabilities.CapabilityStatus;
import com.google.appengine.api.users.UserServiceFactory;

public class AppengineContainerHelper implements IContainerHelper {

    @Override
    public String createLoginURL(String destinationURL) {
        return UserServiceFactory.getUserService().createLoginURL(destinationURL);
    }

    @Override
    public String createLogoutURL(String destinationURL) {
        return UserServiceFactory.getUserService().createLogoutURL(destinationURL);
    }

    /**
     * Detects GAE Maintenance
     */
    @Override
    public boolean isDBReadOnly() {
        CapabilitiesServiceFactory.getCapabilitiesService();
        CapabilityState state = CapabilitiesServiceFactory.getCapabilitiesService().getStatus(Capability.DATASTORE);
        return (state.getStatus() != CapabilityStatus.ENABLED);
    }

}
