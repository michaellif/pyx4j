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
package com.pyx4j.entity.core.filter;

import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * When using this entity as member user @Detached and @Owned(cascade = {}) for Storage.
 *
 * To get IQueryFilterList use PersistableQueryManager.retriveCriteria.
 * To save IQueryFilterList in this object use PersistableQueryManager.saveCriteria.
 */
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface QueryFilterStorage extends IEntity {

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<IQueryFilter> filters();

    //Set to false during version update/DB migration
    IPrimitive<Boolean> valid();

    IPrimitive<String> stringQuery();

}
