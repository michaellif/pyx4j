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
 * Created on Mar 4, 2015
 * @author vlads
 */
package com.pyx4j.config.server.events;

import com.google.common.eventbus.Subscribe;

/**
 * Generic configuration change event
 */
public class ServerConfigurationChangeEvent implements ServerEvent {

    public interface Handler extends ServerEvent.Handler {

        @Subscribe
        void onConfigurationChanged(ServerConfigurationChangeEvent event);

    }

    public ServerConfigurationChangeEvent() {
        super();
    }

    @Override
    public String toString() {
        return "ServerConfigurationChanged";
    }

}