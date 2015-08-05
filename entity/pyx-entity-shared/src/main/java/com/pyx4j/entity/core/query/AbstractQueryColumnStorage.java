/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 27, 2015
 * @author vlads
 */
package com.pyx4j.entity.core.query;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

/**
 * This class acts as check constraint for ICriterion.columnId() in QueryCriteriaStorage
 *
 * Instance of the class should be defined in application, just like AbstractOutgoingMailQueue.
 *
 * see PersistableQueryFacade#registerColumnStorageClass(Class persistableEntityClass)
 *
 */
@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AbstractQueryColumnStorage extends IEntity {

    @Indexed
    @MemberColumn(notNull = true)
    IPrimitive<String> queryClass();

    //TODO use serialized path, Today this is limited to one memberName!
    @MemberColumn(notNull = true)
    IPrimitive<String> columnPath();

    @MemberColumn(notNull = true)
    IPrimitive<String> criterionType();

}
