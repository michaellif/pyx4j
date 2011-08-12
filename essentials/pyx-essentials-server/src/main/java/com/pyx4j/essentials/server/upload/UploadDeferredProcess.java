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
 * Created on Aug 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.upload;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;

@SuppressWarnings("serial")
class UploadDeferredProcess implements IDeferredProcess {

    private final DeferredProcessProgressResponse status;

    public UploadDeferredProcess() {
        status = new DeferredProcessProgressResponse();
    }

    @Override
    public void execute() {
    }

    @Override
    public void cancel() {
        status.setCanceled();
    }

    @Override
    public DeferredProcessProgressResponse status() {
        return status;
    }

}
