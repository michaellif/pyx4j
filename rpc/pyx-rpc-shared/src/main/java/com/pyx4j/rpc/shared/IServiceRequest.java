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
 */
package com.pyx4j.rpc.shared;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.IHaveServiceCallMarker;

public class IServiceRequest implements Serializable, IHaveServiceCallMarker {

    private static final long serialVersionUID = 6090572631432990996L;

    private String serviceClassId;

    private String serviceMethodId;

    private int serviceMethodSignature;

    private Vector<Serializable> args;

    private int rpcCallNumber;

    IServiceRequest() {

    }

    public IServiceRequest(String serviceClassId, String serviceMethodId, int serviceMethodSignature, Serializable[] args, int rpcCallNumber) {
        super();
        this.serviceClassId = serviceClassId;
        this.serviceMethodId = serviceMethodId;
        this.serviceMethodSignature = serviceMethodSignature;
        this.args = new Vector<Serializable>();
        this.args.addAll(Arrays.asList(args));
        this.rpcCallNumber = rpcCallNumber;
    }

    public String getServiceClassId() {
        return serviceClassId;
    }

    public String getServiceMethodId() {
        return serviceMethodId;
    }

    public int getServiceMethodSignature() {
        return serviceMethodSignature;
    }

    public List<Serializable> getArgs() {
        return args;
    }

    public int getRpcCallNumber() {
        return rpcCallNumber;
    }

    @Override
    public String getServiceCallMarker() {
        String className = getServiceClassId();
        return className.substring(className.lastIndexOf(".") + 1) + "." + getServiceMethodId();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(serviceClassId).append('.').append(serviceMethodId).append('(');
        b.append(args);
        b.append(')');
        return b.toString();
    }

}
