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
package com.pyx4j.entity.shared;

import java.util.Date;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.ManagedColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.i18n.annotations.I18n;

@AbstractEntity(generateMetadata = false)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface IVersionedEntity<VERSIONED_ITEM extends IVersionData<?>> extends IEntity {

    @Detached(level = AttachLevel.Detached)
    @Owned
    IList<VERSIONED_ITEM> versions();

    /**
     * Finalize: Triggered by Not draft and not empty version() member.
     */
    @ManagedColumn
    IPrimitive<Boolean> draft();

    /**
     * null for draft
     * 
     * On persist (only draft):
     * version() contains Draft
     * 
     * On finalised:
     * version == null, draft from versions -> finalised (from date is set to current date)
     * version != null, only for preloader and with condition that versions() is detached
     */
    @ManagedColumn
    IPrimitive<Date> forDate();

    @ManagedColumn
    @ToString(index = 1)
    VERSIONED_ITEM version();

}
