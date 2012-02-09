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
 * Created on Feb 9, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

abstract class JoinInformation {

    Class<? extends IEntity> joinTableClass;

    boolean joinTableSameAsTarget;

    String sqlName;

    String sqlValueName = null;

    String sqlOwnerName = null;

    String sqlOrderColumnName = null;

    ValueAdapter ownerValueAdapter;

    static JoinInformation build(Dialect dialect, NamingConvention namingConvention, EntityMeta rootEntityMeta, EntityMeta entityMeta, MemberMeta memberMeta) {
        Owner owner = memberMeta.getAnnotation(Owner.class);
        JoinTable joinTable = memberMeta.getAnnotation(JoinTable.class);
        if ((joinTable == null) && (owner == null)) {
            return null;
        }
        if (owner != null) {
            if (Owned.TODO) {
                //Disable all new Owned/Owner mapping unitl it is tested
                return null;
            }
            if (joinTable != null) {
                //TODO What to do? Throw error?
            }
            JoinColumn joinColumn = memberMeta.getAnnotation(JoinColumn.class);
            if (joinColumn != null) {
                // @Owned mappedby this Column.
                return null;
            }
            return new JoinOwnerInformation(dialect, namingConvention, rootEntityMeta, entityMeta, memberMeta, owner);
        } else {
            return new JoinTableInformation(dialect, namingConvention, rootEntityMeta, entityMeta, memberMeta, joinTable);
        }
    }
}
