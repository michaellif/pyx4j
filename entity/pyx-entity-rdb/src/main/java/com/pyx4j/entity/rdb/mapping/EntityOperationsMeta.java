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

import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityOperationsMeta {

    private final List<MemberOperationsMeta> members = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadePersistMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> cascadeRetrieveMembers = new Vector<MemberOperationsMeta>();

    private final List<MemberOperationsMeta> indexMembers = new Vector<MemberOperationsMeta>();

    EntityOperationsMeta(Dialect dialect, EntityMeta entityMeta) {
        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            if (memberMeta.isEmbedded()) {
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    //TODO
                } else {
                    addEmbededMemebers(dialect, new Vector<String>(), memberName, EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getObjectClass()));
                }
            } else {
                MemberOperationsMeta member = new MemberOperationsMeta(dialect.sqlName(memberName), memberMeta);
                members.add(member);

                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    //TODO
                } else if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    if (memberMeta.isOwnedRelationships()) {
                        cascadePersistMembers.add(member);
                    } else if (memberMeta.getAnnotation(Reference.class) != null) {
                        cascadePersistMembers.add(member);
                    }
                    if (!memberMeta.isDetached()) {
                        cascadeRetrieveMembers.add(member);
                    }
                }

                Indexed index = memberMeta.getAnnotation(Indexed.class);
                if ((index != null) && (index.adapters() != null) && (index.adapters().length > 0)) {
                    for (Class<? extends IndexAdapter<?>> adapterClass : index.adapters()) {
                        IndexAdapter<?> adapter = AdapterFactory.getIndexAdapter(adapterClass);
                        String indexedPropertyName = dialect.sqlName(adapter.getIndexedColumnName(null, member.getMemberMeta()));
                        indexMembers.add(new MemberOperationsMeta(indexedPropertyName, memberMeta, adapterClass, adapter.getIndexValueClass()));
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
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    //TODO
                } else {
                    addEmbededMemebers(dialect, thisPath, memberName, EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getObjectClass()));
                }
            } else {
                MemberOperationsMeta member = new MemberEmbeddedOperationsMeta(dialect.sqlName(sqlPrefix.toString() + memberName), thisPath, memberMeta);
                members.add(member);

                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    //TODO
                } else if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    if (memberMeta.isOwnedRelationships()) {
                        cascadePersistMembers.add(member);
                    } else if (memberMeta.getAnnotation(Reference.class) != null) {
                        cascadePersistMembers.add(member);
                    }
                    if (!memberMeta.isDetached()) {
                        cascadeRetrieveMembers.add(member);
                    }
                }
            }
        }
    }

    public List<MemberOperationsMeta> getMembers() {
        return members;
    }

    public List<MemberOperationsMeta> getCascadePersistMembers() {
        return cascadePersistMembers;
    }

    public List<MemberOperationsMeta> getCascadeRetrieveMembers() {
        return cascadeRetrieveMembers;
    }

    public List<MemberOperationsMeta> getIndexMembers() {
        return indexMembers;
    }

}
