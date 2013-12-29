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
 * Created on Feb 15, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.core;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface IVersionData<VERSIONED_ENTITY extends IVersionedEntity<?>> extends IEntity {

    @Owner
    @JoinColumn
    @Indexed(group = { "h,1" })
    @MemberColumn(notNull = true)
    VERSIONED_ENTITY holder();

    @OrderColumn
    @Format(value = "#", nil = "Draft")
    IPrimitive<Integer> versionNumber();

    @Format("MM/dd/yyyy, HH:mm:ss")
    @Indexed(group = { "h,2" })
    IPrimitive<Date> fromDate();

    @Format("MM/dd/yyyy, HH:mm:ss")
    @Indexed(group = { "h,3" })
    IPrimitive<Date> toDate();

    IPrimitive<Key> createdByUserKey();

    //TODO make Abstract user part of the framework
    @Transient
    @ReadOnly
    @Editor(type = Editor.EditorType.label)
    IPrimitive<String> createdByUser();

}
