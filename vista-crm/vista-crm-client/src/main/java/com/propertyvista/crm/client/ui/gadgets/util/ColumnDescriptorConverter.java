/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.util;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.gadgets.common.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.util.ColumnUserSettings;

public class ColumnDescriptorConverter {

    private static final I18n i18n = I18n.get(ColumnDescriptorConverter.class);

    public static <E extends IEntity> ColumnDescriptor columnDescriptorFromEntity(Class<E> describedEntityClass, ColumnDescriptorEntity columnDescriptorEntity) {
        if (columnDescriptorEntity != null && !columnDescriptorEntity.isNull()) {
            IEntity proto = EntityFactory.getEntityPrototype(describedEntityClass);
            IObject<?> member = proto.getMember(new Path(columnDescriptorEntity.propertyPath().getValue()));

            MemberColumnDescriptor.Builder columnDescriptorBuilder = new MemberColumnDescriptor.Builder(member);
            if (!columnDescriptorEntity.title().isNull()) {
                columnDescriptorBuilder.title(i18n.translate("", columnDescriptorEntity.title().getValue()));
            }
            //@formatter:off
            return columnDescriptorBuilder
                .sortable(columnDescriptorEntity.isSortable().getValue())
                .width(columnDescriptorEntity.width().getValue())
                .wordWrap(columnDescriptorEntity.wrapWords().getValue())
                .visible(columnDescriptorEntity.isVisible().getValue())
                .build();
            //@formatter:on
        } else {
            return null;
        }
    }

    public static <E extends IEntity> ColumnDescriptorEntity saveColumnDescriptorToEntity(Class<E> describedEntityClass, ColumnDescriptor columnDescriptor,
            ColumnDescriptorEntity entity) {
        entity.propertyPath().setValue(columnDescriptor.getColumnName());
        entity.isSortable().setValue(columnDescriptor.isSortable());
        if (!EntityFactory.getEntityPrototype(describedEntityClass).getMember(new Path(entity.propertyPath().getValue())).getMeta().getCaption()
                .equals(columnDescriptor.getColumnTitle())) {
            entity.title().setValue(columnDescriptor.getColumnTitle());
        }

        entity.width().setValue(columnDescriptor.getWidth());
        entity.wrapWords().setValue(columnDescriptor.isWordWrap());
        entity.isVisible().setValue(columnDescriptor.isVisible());
        return entity;
    }

    public static <E extends IEntity> List<ColumnDescriptorEntity> asColumnDesciptorEntityList(Class<E> describedEntityClass,
            List<ColumnDescriptor> columnDescriptorList) {
        List<ColumnDescriptorEntity> columnDescriptorEntityList = new ArrayList<ColumnDescriptorEntity>();
        for (ColumnDescriptor columnDescriptor : columnDescriptorList) {
            ColumnDescriptorEntity columnDescriptorEntity = EntityFactory.create(ColumnDescriptorEntity.class);
            saveColumnDescriptorToEntity(describedEntityClass, columnDescriptor, columnDescriptorEntity);
            columnDescriptorEntityList.add(columnDescriptorEntity);
        }
        return columnDescriptorEntityList;
    }

    public static <E extends IEntity> List<ColumnDescriptor> asColumnDescriptorist(Class<E> describedEntityClass,
            List<ColumnDescriptorEntity> columnDescriptorEntities) {
        List<ColumnDescriptor> descriptors = new ArrayList<ColumnDescriptor>();
        for (ColumnDescriptorEntity entity : columnDescriptorEntities) {
            descriptors.add(columnDescriptorFromEntity(describedEntityClass, entity));
        }
        return descriptors;
    }

    public static <E extends IEntity> ColumnUserSettings getUserSettings(ColumnDescriptor columnDescriptor) {
        ColumnUserSettings entity = EntityFactory.create(ColumnUserSettings.class);
        entity.property().setValue(columnDescriptor.getColumnName());
        entity.isVisible().setValue(columnDescriptor.isVisible());
        return entity;
    }

}
