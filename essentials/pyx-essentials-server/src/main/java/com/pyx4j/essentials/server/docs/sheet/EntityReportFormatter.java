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
 */
package com.pyx4j.essentials.server.docs.sheet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.shared.utils.EntityMetaUtils;
import com.pyx4j.essentials.rpc.report.ReportColumn;

public class EntityReportFormatter<E extends IEntity> implements ReportModelFormatter<E> {

    private final Class<? extends E> entityClass;

    private List<Path> selectedMemberNames;

    private final Set<Path> ignoreMembers = new HashSet<>();

    private final Map<Path, String> customMemberCaptions = new HashMap<>();

    private boolean memberValueUseStringView;

    public EntityReportFormatter(Class<? extends E> entityClass) {
        this.entityClass = entityClass;
    }

    public boolean isMemberValueUseStringView() {
        return memberValueUseStringView;
    }

    public void setMemberValueUseStringView(boolean memberValueUseStringView) {
        this.memberValueUseStringView = memberValueUseStringView;
    }

    protected List<Path> getAllEntityMembers() {
        return EntityMetaUtils.getDirectMembers(entityClass);
    }

    public void selectMemeber(Path memberPath, String caption) {
        if (selectedMemberNames == null) {
            selectedMemberNames = new ArrayList<>();
        }
        selectedMemberNames.add(memberPath);
        addMemberCaption(memberPath, caption);
    }

    public void selectMemebers() {
        selectedMemberNames = new ArrayList<>();
        EntityMeta em = EntityFactory.getEntityMeta(entityClass);
        for (Path memberPath : getAllEntityMembers()) {
            MemberMeta memberMeta = em.getMemberMeta(memberPath);
            if (memberMeta.isRpcTransient()) {
                continue;
            }
            ReportColumn reportColumn = memberMeta.getAnnotation(ReportColumn.class);
            if ((reportColumn != null) && reportColumn.ignore()) {
                continue;
            }
            if (!acceptMember(memberPath, memberMeta)) {
                continue;
            }
            if (memberMeta.isEntity()) {
                selectedMemberNames.add(memberPath);
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                selectedMemberNames.add(memberPath);
            }
        }
    }

    protected boolean acceptMember(Path memberPath, MemberMeta memberMeta) {
        return (!ignoreMembers.contains(memberPath));
    }

    public void addIgnoreMember(Path memberPath) {
        ignoreMembers.add(memberPath);
    }

    public void addMemberCaption(Path memberPath, String caption) {
        customMemberCaptions.put(memberPath, caption);
    }

    public void addMember(IObject<?> member) {
        if (selectedMemberNames == null) {
            selectMemebers();
        }
        selectedMemberNames.add(member.getPath());
    }

    protected String getMemberCaption(Path memberPath, MemberMeta memberMeta) {
        String caption = customMemberCaptions.get(memberPath);
        if (caption != null) {
            return caption;
        } else {
            return memberMeta.getCaption();
        }
    }

    public void sortMembers(Comparator<? super Path> comparator) {
        if (selectedMemberNames == null) {
            selectMemebers();
        }
        // Java 8
        //selectedMemberNames.sort(comparator);
        // Java 7
        Collections.sort(selectedMemberNames, comparator);
    }

    protected void createHeaderEnds(ReportTableFormatter formatter) {
        formatter.newRow();
    }

    @Override
    public void createHeader(ReportTableFormatter formatter) {
        if (selectedMemberNames == null) {
            selectMemebers();
        }
        EntityMeta em = EntityFactory.getEntityMeta(entityClass);
        for (Path memberPath : selectedMemberNames) {
            MemberMeta memberMeta = em.getMemberMeta(memberPath);
            if (memberMeta.isEntity()) {
                formatter.header(getMemberCaption(memberPath, memberMeta));
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                formatter.header(getMemberCaption(memberPath, memberMeta));
            } else if (IPrimitiveSet.class.isAssignableFrom(memberMeta.getObjectClass())) {
                formatter.header(getMemberCaption(memberPath, memberMeta));
            }
        }
        createHeaderEnds(formatter);
    }

    @Override
    public void createFooter(ReportTableFormatter formatter) {

    }

    protected boolean reportMember(E entity, Path memberPath, MemberMeta memberMeta) {
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

    @Override
    public void reportEntity(ReportTableFormatter formatter, E entity) {
        if (selectedMemberNames == null) {
            selectMemebers();
        }
        EntityMeta em = entity.getEntityMeta();
        for (Path memberPath : selectedMemberNames) {
            MemberMeta memberMeta = em.getMemberMeta(memberPath);
            if (!reportMember(entity, memberPath, memberMeta)) {
                formatter.cell(null);
                continue;
            }
            if (memberMeta.isEntity()) {
                formatter.cell(((IEntity) entity.getMember(memberPath)).getStringView());
            } else if (IPrimitive.class.isAssignableFrom(memberMeta.getObjectClass())) {
                if (memberMeta.getEditorType() == EditorType.percentage && (formatter instanceof ReportTableXLSXFormatter)) {
                    ((ReportTableXLSXFormatter) formatter).cellPercentage((BigDecimal) memberValue(entity.getMember(memberPath)));
                } else {
                    formatter.cell(memberValue(entity.getMember(memberPath)));
                }
            } else if (IPrimitiveSet.class.isAssignableFrom(memberMeta.getObjectClass())) {
                formatter.cell(memberValue(entity.getMember(memberPath)));
            }
        }
        reportEntityEnds(formatter, entity);
    }

    public Object memberValue(IObject<?> member) {
        if (memberValueUseStringView) {
            return member.getStringView();
        } else {
            if (member instanceof IPrimitiveSet) {
                @SuppressWarnings("unchecked")
                IPrimitiveSet<Serializable> ps = ((IPrimitiveSet<Serializable>) member);
                return ConverterUtils.convertCollection(ps.getValue(), ", ");
            } else {
                return member.getValue();
            }
        }
    }

}
