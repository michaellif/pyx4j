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
package com.pyx4j.entity.core.query;

import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * When using this entity as member use @Detached and @Owned(cascade = {}) for Storage.
 *
 * You should not Extend this entity.
 *
 * To get IQuery use ServerSideFactory.create(PersistableQueryFacade.class).retriveCriteria();
 * To save IQuery defined in this object use ServerSideFactory.create(PersistableQueryFacade.class).saveCriteria();
 */
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface QueryStorage extends IEntity {

    @Owned
    @OrderBy(ICondition.DisplayOrderId.class)
    IList<ICondition> conditions();

    //Set to false during version update/DB migration
    IPrimitive<Boolean> valid();

    IPrimitive<String> stringQuery();

}
