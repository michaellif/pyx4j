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
 * Created on Jan 29, 2012
 * @author vlads
 */
package com.pyx4j.gwt.server.deferred;

import java.io.Serializable;

import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;

final class DeferredProcessInfo implements Serializable {

    private static final long serialVersionUID = -1334460222092134555L;

    IDeferredProcess process;

    DeferredProcessProgressResponse status;

    DeferredProcessInfo(IDeferredProcess process) {
        this.process = process;
    }

    void setProcessErrorWithStatusMessage(String message) {
        DeferredProcessProgressResponse status = new DeferredProcessProgressResponse();
        status.setErrorStatusMessage(message);
        this.status = status;
        this.process = null;
    }

}