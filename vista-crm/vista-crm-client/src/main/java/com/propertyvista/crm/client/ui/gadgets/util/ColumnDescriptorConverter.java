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

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;

import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;

public class ColumnDescriptorConverter {

    public static <E extends IEntity> ColumnDescriptor columnDescriptorFromEntity(Class<E> describedEntityClass, ColumnDescriptorEntity columnDescriptorEntity) {
        if (columnDescriptorEntity != null && !columnDescriptorEntity.isNull()) {
            IEntity proto = EntityFactory.getEntityPrototype(describedEntityClass);
            IObject<?> member = proto.getMember(new Path(columnDescriptorEntity.propertyPath().getValue()));

            ColumnDescriptor columnDescriptor = new MemberColumnDescriptor.Builder(member).title(columnDescriptorEntity.title().getValue())
                    .sortable(columnDescriptorEntity.isSortable().getValue()).width(columnDescriptorEntity.width().getValue())
                    .wordWrap(columnDescriptorEntity.wrapWords().getValue()).visible(columnDescriptorEntity.isVisible().getValue()).build();

            return columnDescriptor;
        } else {
            return null;
        }
    }

    public static ColumnDescriptorEntity saveColumnDescriptorToEntity(ColumnDescriptor columnDescriptor, ColumnDescriptorEntity entity) {
        entity.propertyPath().setValue(columnDescriptor.getColumnName());
        entity.isSortable().setValue(columnDescriptor.isSortable());
        entity.title().setValue(columnDescriptor.getColumnTitle());
        entity.width().setValue(columnDescriptor.getWidth());
        entity.wrapWords().setValue(columnDescriptor.isWordWrap());
        entity.isVisible().setValue(columnDescriptor.isVisible());
        return entity;
    }

    public static List<ColumnDescriptorEntity> asColumnDesciptorEntityList(List<ColumnDescriptor> columnDescriptorList) {
        List<ColumnDescriptorEntity> columnDescriptorEntityList = new ArrayList<ColumnDescriptorEntity>();
        for (ColumnDescriptor columnDescriptor : columnDescriptorList) {
            ColumnDescriptorEntity columnDescriptorEntity = EntityFactory.create(ColumnDescriptorEntity.class);
            saveColumnDescriptorToEntity(columnDescriptor, columnDescriptorEntity);
            columnDescriptorEntityList.add(columnDescriptorEntity);
        }
        return columnDescriptorEntityList;
    }
}
