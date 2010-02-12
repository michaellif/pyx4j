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
 * Created on Feb 10, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RPCStatusChangeEvent;
import com.pyx4j.rpc.client.RPCStatusChangeHandler;
import com.pyx4j.rpc.client.RPCStatusChangeEvent.When;
import com.pyx4j.widgets.client.GlassPanel;

public class ApplicationCommon {

    public static void initRpcGlassPanel() {
        RPCManager.addRPCStatusChangeHandler(new RPCStatusChangeHandler() {

            @Override
            public void onRPCStatusChange(RPCStatusChangeEvent event) {
                if (!event.isExecuteBackground()) {
                    if (event.getWhen() == When.START) {
                        GlassPanel.show();
                    } else {
                        GlassPanel.hide();
                    }
                }
            }
        });
    }

}
