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
 * Created on Jan 5, 2010
 * @author vlads
 */
package com.pyx4j.entity.rpc;

import java.util.Vector;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntitySearchCriteria;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.rpc.shared.VoidSerializable;

@Deprecated
public interface EntityServices {

    public interface Save extends Service<IEntity, IEntity> {
    };

    public interface MergeSave extends Save {
    };

    public interface SaveList extends Service<Vector<? extends IEntity>, Vector<? extends IEntity>> {
    };

    public interface Query extends Service<EntityQueryCriteria<?>, Vector<? extends IEntity>> {
    };

    public interface Search extends Service<EntitySearchCriteria<?>, EntitySearchResult<? extends IEntity>> {
    };

    public interface SearchLister extends EntityServices.Search {
    };

    public interface Retrieve extends Service<EntityQueryCriteria<?>, IEntity> {
    };

    public interface Count extends Service<EntityQueryCriteria<?>, Long> {
    };

    public interface Delete extends Service<IEntity, VoidSerializable> {
    };

    public interface MergeDelete extends Delete {
    };

    public interface DeleteQuery extends Service<EntityQueryCriteria<?>, VoidSerializable> {
    };
}
