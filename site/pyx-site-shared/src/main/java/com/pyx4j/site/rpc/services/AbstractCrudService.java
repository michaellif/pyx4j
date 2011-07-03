/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on Apr 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.IService;

public interface AbstractCrudService<EditableEntity extends IEntity> extends IService {

    public void create(AsyncCallback<EditableEntity> callback, EditableEntity editableEntity);

    public void retrieve(AsyncCallback<EditableEntity> callback, Key entityId);

    public void save(AsyncCallback<EditableEntity> callback, EditableEntity editableEntity);

// TODO - VladL :extract 'list' to ListerService (it'll be used by Listers) and inherit AbstractCrudService from it.  
// VladL: not sure now if we need it, but let's leave it as TODO for further re-thinking...     
    public void list(AsyncCallback<EntitySearchResult<EditableEntity>> callback, EntityListCriteria<EditableEntity> criteria);
}
