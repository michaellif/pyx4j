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
                    .sortable(columnDescriptorEntity.sortable().getValue()).width(columnDescriptorEntity.width().getValue())
                    .wordWrap(columnDescriptorEntity.wordWrap().getValue()).visible(columnDescriptorEntity.visiblily().getValue()).build();

            return columnDescriptor;
        } else {
            return null;
        }
    }

    public static <E extends IEntity> ColumnDescriptorEntity columnDescriptorToEntity(ColumnDescriptor columnDescriptor, ColumnDescriptorEntity entity) {
        assert entity != null;

        entity.propertyPath().setValue(columnDescriptor.getColumnName());
        entity.sortable().setValue(columnDescriptor.isSortable());
        entity.title().setValue(columnDescriptor.getColumnTitle());
        entity.width().setValue(columnDescriptor.getWidth());
        entity.wordWrap().setValue(columnDescriptor.isWordWrap());
        entity.visiblily().setValue(columnDescriptor.isVisible());
        return entity;
    }
}
