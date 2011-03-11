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
 * Created on 2011-03-11
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.shared;

import java.io.Serializable;

public class IServiceRequest implements Serializable {

    private static final long serialVersionUID = 6090572631432990996L;

    private String serviceClassId;

    private String serviceMethodId;

    private Serializable[] args;

    IServiceRequest() {

    }

    public IServiceRequest(String serviceClassId, String serviceMethodId, Serializable[] args) {
        super();
        this.serviceClassId = serviceClassId;
        this.serviceMethodId = serviceMethodId;
        this.args = args;
    }

    public String getServiceClassId() {
        return serviceClassId;
    }

    public String getServiceMethodId() {
        return serviceMethodId;
    }

    public Serializable[] getArgs() {
        return args;
    }
}
