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
 * Created on Feb 9, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import com.google.gwt.core.client.EntryPoint;

import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.site.client.SiteDispatcher;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;

public abstract class AbstractSiteDispatcher extends SiteDispatcher implements EntryPoint {

    public void onModuleLoad() {
        ClientEntityFactory.ensureIEntityImplementations();
        ClientLogger.addAppender(new RPCAppender(Level.WARN));
        ClientLogger.setDebugOn(true);
        UnrecoverableErrorHandlerDialog.register();
    }
}
