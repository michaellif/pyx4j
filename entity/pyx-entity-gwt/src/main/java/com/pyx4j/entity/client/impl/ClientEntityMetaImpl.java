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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.i18n.shared.I18n;

public abstract class ClientEntityMetaImpl implements EntityMeta {

    //TODO make it private
    protected static final I18n i18n = I18n.get(ClientEntityMetaImpl.class);

    private final Class<? extends IEntity> entityClass;

    private final Class<? extends IEntity> expandedFromClass;

    private final String captionNL;

    private final String caption;

    private final String description;

    private final String watermark;

    private final boolean persistenceTransient;

    private final boolean rpcTransient;

    private List<String> memberNames;

    protected final HashMap<String, MemberMeta> membersMeta = new HashMap<String, MemberMeta>();

    private final String toStringFormat;

    private final String ownerMemberName;

    private final List<String> toStringMemberNames;

    private final List<String> businessEqualMemberNames;

    private final String nullString;

    public ClientEntityMetaImpl(Class<? extends IEntity> entityClass, Class<? extends IEntity> expandedFromClass, String captionNL, String caption,
            String description, String watermark, boolean persistenceTransient, boolean rpcTransient, String toStringFormat, String nullString,
            String ownerMemberName, String[] memberNamesToString, String[] businessEqualMemberNames) {
        this.entityClass = entityClass;
        this.expandedFromClass = expandedFromClass;
        this.captionNL = captionNL;
        this.caption = caption;
        this.description = description;
        this.watermark = watermark;
        this.persistenceTransient = persistenceTransient;
        this.rpcTransient = rpcTransient;
        this.toStringFormat = toStringFormat;
        this.ownerMemberName = ownerMemberName;
        this.toStringMemberNames = Arrays.asList(memberNamesToString);
        this.nullString = nullString;
        this.businessEqualMemberNames = Arrays.asList(businessEqualMemberNames);
    }

    @Override
    public Class<? extends IEntity> getEntityClass() {
        return entityClass;
    }

    @Override
    public Class<? extends IEntity> getDBOClass() {
        return expandedFromClass;
    }

    @Override
    public Class<? extends IEntity> getPersistableSuperClass() {
        return null;
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
    public String getCaptionNL() {
        return captionNL;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getWatermark() {
        return watermark;
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
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getMemberNames() {
        if (memberNames == null) {
            SharedEntityHandler anInstance = (SharedEntityHandler) EntityFactory.create(getEntityClass());
            memberNames = Collections.unmodifiableList(Arrays.asList(anInstance.getMembers()));
        }
        return memberNames;
    }

    @Override
    public List<String> getMemberNamesWithPk() {
        List<String> l = new ArrayList<String>(getMemberNames());
        l.add(IEntity.PRIMARY_KEY);
        return l;
    }

    /**
     * Generated method
     */
    protected abstract MemberMeta createMemberMeta(String memberName);

    private static MemberMeta primaryKeyMeta = new ClientMemberMetaImpl(IEntity.PRIMARY_KEY, "Id", i18n.tr("Id"), "", "", com.pyx4j.commons.Key.class,
            com.pyx4j.entity.shared.IPrimitive.class, ObjectClassType.Primitive, false, false, false, false, AttachLevel.Attached, false, true, false, false,
            false, -1, null, false, "", false);

    @Override
    public MemberMeta getMemberMeta(String memberName) {
        MemberMeta memberMeta = membersMeta.get(memberName);
        if (memberMeta == null) {
            memberMeta = createMemberMeta(memberName);
            if (memberMeta == null) {
                if (IEntity.PRIMARY_KEY.equals(memberName)) {
                    return primaryKeyMeta;
                } else {
                    throw new RuntimeException("Unknown member '" + memberName + "' of " + this.entityClass.getName());
                }
            }
            membersMeta.put(memberName, memberMeta);
        }
        return memberMeta;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MemberMeta getMemberMeta(Path path) {
        EntityMeta em = this;
        MemberMeta mm = null;
        for (String memberName : path.getPathMembers()) {
            //TODO ICollection support
            if (mm != null) {
                if (!mm.isEntity()) {
                    throw new RuntimeException("Invalid member in path '" + memberName + "' of " + this.entityClass.getName());
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
    public List<String> getBusinessEqualMemberNames() {
        return businessEqualMemberNames;
    }

    @Override
    public String getOwnerMemberName() {
        return ownerMemberName;
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
