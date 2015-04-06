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
 * Created on Jan 22, 2010
 * @author vlads
 */
package com.pyx4j.rpc.client;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link RPCStatusChangeEvent} events.
 */
public interface RPCStatusChangeHandler extends EventHandler {

    /**
     * Fired before RPC service started and after it's AsyncCallback has been completed.
     * 
     * @param event
     *            the event
     */
    void onRPCStatusChange(RPCStatusChangeEvent event);

}
