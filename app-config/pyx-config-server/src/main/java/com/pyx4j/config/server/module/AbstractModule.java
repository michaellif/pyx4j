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
 * Created on Jun 10, 2015
 * @author vlads
 */
package com.pyx4j.config.server.module;

import com.pyx4j.config.server.events.HandlerRegistrations;
import com.pyx4j.config.server.events.ServerEvent;

public abstract class AbstractModule implements IModule {

    protected final HandlerRegistrations handlers = new HandlerRegistrations();

    protected void handles(ServerEvent.Handler handler) {
        handlers.register(handler);
    }

    @Override
    public void shutdown() {
        handlers.unregister();
    }

}
