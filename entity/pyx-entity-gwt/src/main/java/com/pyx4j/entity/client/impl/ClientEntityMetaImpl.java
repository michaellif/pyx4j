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
 * Created on Jan 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public abstract class ClientEntityMetaImpl implements EntityMeta {

    private final Class<? extends IEntity> entityClass;

    private final String caption;

    private final String description;

    private final boolean persistenceTransient;

    private final boolean rpcTransient;

    private List<String> memberNames;

    protected final HashMap<String, MemberMeta> membersMeta = new HashMap<String, MemberMeta>();

    private final String toStringFormat;

    private final List<String> toStringMemberNames;

    private final String nullString;

    public ClientEntityMetaImpl(Class<? extends IEntity> entityClass, String caption, String description, boolean persistenceTransient, boolean rpcTransient,
            String toStringFormat, String nullString, String[] memberNamesToString) {
        this.entityClass = entityClass;
        this.caption = caption;
        this.description = description;
        this.persistenceTransient = persistenceTransient;
        this.rpcTransient = rpcTransient;
        this.toStringFormat = toStringFormat;
        this.toStringMemberNames = Arrays.asList(memberNamesToString);
        this.nullString = nullString;
    }

    @Override
    public Class<? extends IEntity> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getPersistenceName() {
        return null;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isTransient() {
        return persistenceTransient;
    }

    @Override
    public boolean isRpcTransient() {
        return rpcTransient;
    }

    @Override
    public List<String> getMemberNames() {
        if (memberNames == null) {
            SharedEntityHandler anInstance = (SharedEntityHandler) EntityFactory.create(getEntityClass());
            memberNames = Collections.unmodifiableList(Arrays.asList(anInstance.getMemebers()));
        }
        return memberNames;
    }

    /**
     * Generated method
     */
    protected abstract MemberMeta createMemberMeta(String memberName);

    @Override
    public MemberMeta getMemberMeta(String memberName) {
        MemberMeta memberMeta = membersMeta.get(memberName);
        if (memberMeta == null) {
            memberMeta = createMemberMeta(memberName);
            membersMeta.put(memberName, memberMeta);
        }
        return memberMeta;
    }

    @SuppressWarnings("unchecked")
    public MemberMeta getMemberMeta(Path path) {
        EntityMeta em = this;
        MemberMeta mm = null;
        for (String memberName : path.getPathMembers()) {
            //TODO ICollection support
            if (mm != null) {
                if (!mm.isEntity()) {
                    throw new RuntimeException("Invalid member in path " + memberName);
                } else {
                    em = EntityFactory.getEntityMeta((Class<? extends IEntity>) mm.getValueClass());
                }
            }
            mm = em.getMemberMeta(memberName);
        }
        return mm;
    }

    @Override
    public String getToStringFormat() {
        return toStringFormat;
    }

    @Override
    public String getNullString() {
        return nullString;
    }

    @Override
    public List<String> getToStringMemberNames() {
        return toStringMemberNames;
    }

    @Override
    public List<String> getBidirectionalReferenceMemberNames() {
        //TODO do we need this ever on client ?
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCreatedTimestampMember() {
        //TODO do we need this ever on client ?
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUpdatedTimestampMember() {
        //TODO do we need this ever on client ?
        throw new UnsupportedOperationException();
    }
}
