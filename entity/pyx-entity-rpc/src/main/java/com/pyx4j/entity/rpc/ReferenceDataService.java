/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Sep 21, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.ServiceExecution;

public interface ReferenceDataService extends IService {

    public void query(AsyncCallback<EntitySearchResult<? extends IEntity>> callback, EntityQueryCriteria<? extends IEntity> criteria);

    /**
     * This is the same service implementation only different by UI appearance
     */
    @ServiceExecution(operationType = ServiceExecution.OperationType.NonBlocking)
    public void queryNonBlocking(AsyncCallback<EntitySearchResult<? extends IEntity>> callback, EntityQueryCriteria<? extends IEntity> criteria);

}