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
 * Created on Jan 2, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityOperationsMeta {

    private final List<MemberOperationsMeta> members = new Vector<MemberOperationsMeta>();

    private final List<MemberMeta> cascadePersistMembers = new Vector<MemberMeta>();

    private final List<MemberMeta> cascadeRetrieveMembers = new Vector<MemberMeta>();

    EntityOperationsMeta(Dialect dialect, EntityMeta entityMeta) {
        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            if (memberMeta.isEmbedded()) {
                addEmbededMemebers(dialect, new Vector<String>(), memberName, EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getObjectClass()));
            } else {
                members.add(new MemberOperationsMeta(dialect.sqlName(memberName), memberMeta));

                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    //TODO
                } else if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    if (memberMeta.isOwnedRelationships() && !memberMeta.isEmbedded()) {
                        cascadePersistMembers.add(memberMeta);
                    }
                    if (!memberMeta.isDetached()) {
                        cascadeRetrieveMembers.add(memberMeta);
                    }
                }
            }
        }
    }

    private void addEmbededMemebers(Dialect dialect, List<String> path, String embeddedMemberName, EntityMeta entityMeta) {
        List<String> thisPath = new Vector<String>();
        thisPath.addAll(path);
        thisPath.add(embeddedMemberName);

        StringBuilder sqlPrefix = new StringBuilder();
        for (String pathPart : thisPath) {
            sqlPrefix.append(dialect.sqlName(pathPart));
            sqlPrefix.append('_');
        }

        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            if (memberMeta.isEmbedded()) {
                addEmbededMemebers(dialect, thisPath, memberName, EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getObjectClass()));
            } else {
                members.add(new MemberEmbeddedOperationsMeta(dialect.sqlName(sqlPrefix.toString() + memberName), thisPath, memberMeta));

                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    //TODO
                } else if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    if (memberMeta.isOwnedRelationships() && !memberMeta.isEmbedded()) {
                        //TODO
                        //cascadePersistMembers.add(memberMeta);
                    }
                    if (!memberMeta.isDetached()) {
                        //TODO
                        //cascadeRetrieveMembers.add(memberMeta);
                    }
                }
            }
        }
    }

    public List<MemberOperationsMeta> getMembers() {
        return members;
    }

    public List<MemberMeta> getCascadePersistMembers() {
        return cascadePersistMembers;
    }

    public List<MemberMeta> getCascadeRetrieveMembers() {
        return cascadeRetrieveMembers;
    }

}
