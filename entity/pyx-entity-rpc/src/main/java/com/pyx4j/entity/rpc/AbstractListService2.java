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
 * Created on Apr 13, 2015
 * @author vlads
 */
package com.pyx4j.entity.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.query.IQueryFilterList;
import com.pyx4j.rpc.shared.IService;

public interface AbstractListService2<E extends IEntity> extends IService {

    public void obtainCriteriaMeta(AsyncCallback<IQueryFilterList<E>> callback);

    public void obtainCriteria(AsyncCallback<IQueryFilterList<E>> callback, String saveName);

    public void list(AsyncCallback<EntitySearchResult<E>> callback, IQueryFilterList<E> criteria);

}
