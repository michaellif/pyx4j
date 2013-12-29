/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-02-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.report;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.essentials.rpc.report.ReportColumn;

public class EntityReportFormatter<E extends IEntity> {

    private final Class<? extends E> entityClass;

    private List<String> selectedMemberNames;

    private final Set<String> ignoreMembers = new HashSet<String>();

    private final Map<String, String> customMemberCaptions = new HashMap<String, String>();

    public EntityReportFormatter(Class<? extends E> entityClass) {
        this.entityClass = entityClass;
    }

    public void selectMemebers() {
        selectedMemberNames = new Vector<String>();
        EntityMeta em = EntityFactory.getEntityMeta(entityClass);
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (em.getMemberMeta(memberName).isRpcTransient()) {
                continue;
            }
            ReportColumn reportColumn = memberMeta.getAnnotation(ReportColumn.class);
            if ((reportColumn != null) && reportColumn.ignore()) {
                continue;
            }
            if (!acceptMember(memberName, memberMeta)) {
                continue;
            }
            if (memberMeta.isEntity()) {
                selectedMemberNames.add(memberName);
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                selectedMemberNames.add(memberName);
            }
        }
    }

    protected boolean acceptMember(String memberName, MemberMeta memberMeta) {
        return (!ignoreMembers.contains(memberName));
    }

    public void addIgnoreMember(String memberName) {
        ignoreMembers.add(memberName);
    }

    public void addMemberCaption(String memberName, String caption) {
        customMemberCaptions.put(memberName, caption);
    }

    protected String getMemberCaption(String memberName, MemberMeta memberMeta) {
        String caption = customMemberCaptions.get(memberName);
        if (caption != null) {
            return caption;
        } else {
            return memberMeta.getCaption();
        }
    }

    protected void createHeaderEnds(ReportTableFormatter formatter) {
        formatter.newRow();
    }

    public void createHeader(ReportTableFormatter formatter) {
        if (selectedMemberNames == null) {
            selectMemebers();
        }
        EntityMeta em = EntityFactory.getEntityMeta(entityClass);
        for (String memberName : selectedMemberNames) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isEntity()) {
                formatter.header(getMemberCaption(memberName, memberMeta));
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                formatter.header(getMemberCaption(memberName, memberMeta));
            }
        }
        createHeaderEnds(formatter);
    }

    protected boolean reportMember(E entity, String memberName, MemberMeta memberMeta) {
        return true;
    }

    protected void reportEntityEnds(ReportTableFormatter formatter, E entity) {
        formatter.newRow();
    }

    public void reportAll(ReportTableFormatter formatter, Collection<E> entityCollection) {
        for (E entity : entityCollection) {
            reportEntity(formatter, entity);
        }
    }

    public void reportEntity(ReportTableFormatter formatter, E entity) {
        if (selectedMemberNames == null) {
            selectMemebers();
        }
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : selectedMemberNames) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (!reportMember(entity, memberName, memberMeta)) {
                formatter.cell(null);
                continue;
            }
            if (memberMeta.isEntity()) {
                formatter.cell(((IEntity) entity.getMember(memberName)).getStringView());
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                formatter.cell(entity.getMember(memberName).getValue());
            }
        }
        reportEntityEnds(formatter, entity);
    }
}
