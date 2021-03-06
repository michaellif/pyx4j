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
 * Created on Dec 29, 2009
 * @author vlads
 */
package com.pyx4j.rpc.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Single service call definition.
 * 
 * Use RPCManager.execute(...) in client code;
 */
@RemoteServiceRelativePath("srv")
public interface RemoteService extends com.google.gwt.user.client.rpc.RemoteService {

    public static final String SESSION_TOKEN_HEADER = "X-XSRF-SessionToken";

    public static final String SESSION_ACL_TIMESTAMP_HEADER = "X-SessionTS";

    public Serializable execute(String serviceInterfaceClassName, Serializable serviceRequest, String userVisitHashCode) throws RuntimeException;

}
