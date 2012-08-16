/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-08-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.deferred;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;

public abstract class AbstractDeferredProcess implements IDeferredProcess {

    private static final long serialVersionUID = -7013680464201337453L;

    protected boolean complete = false;

    protected volatile boolean canceled;

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
        if (complete) {
            r.setCompleted();
        } else if (canceled) {
            r.setCanceled();
        }
        return r;

    }
}
