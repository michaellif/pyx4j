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
 * Created on Dec 18, 2009
 * @author vlads
 */
package com.pyx4j.serialization.client;

import com.google.gwt.user.client.rpc.impl.Serializer;

/**
 * 
 * You need to declare new interface to use in application with your service.
 * RemoteServiceTarget annotation is required.
 * 
 * Example:
 * 
 * <pre>
 * &#064;RemoteServiceTarget(MyRemoteService.class)
 * public interface MyRPCSerializer extends RemoteServiceSerializer {
 * }
 * </pre>
 */
public abstract interface RemoteServiceSerializer {

    public Serializer getSerializer();

}
