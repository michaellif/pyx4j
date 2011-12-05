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
    public static <E extends IEntity> ColumnDescriptor<E> columnDescriptorFromEntity(Class<E> entityClass, ColumnDescriptorEntity cdEntity) {
        IEntity proto = EntityFactory.getEntityPrototype(entityClass);
        IObject<?> member = proto.getMember(new Path(cdEntity.propertyPath().getValue()));

        ColumnDescriptor<E> columnDescriptor = new MemberColumnDescriptor.Builder(member).title(cdEntity.title().getValue())
                .ascendingSort(cdEntity.sortAscending().getValue()).sortable(cdEntity.sortable().getValue()).width(cdEntity.width().getValue())
                .wordWrap(cdEntity.wordWrap().getValue()).visible(cdEntity.visiblily().getValue()).build();

        return columnDescriptor;
    }

    public static <E extends IEntity> ColumnDescriptorEntity columnDescriptorToEntity(ColumnDescriptor<E> columnDescriptor, ColumnDescriptorEntity entity) {
        assert entity != null;

        entity.propertyPath().setValue(columnDescriptor.getColumnName());
        entity.sortAscending().setValue(columnDescriptor.isSortAscending());
        entity.sortable().setValue(columnDescriptor.isSortable());
        entity.title().setValue(columnDescriptor.getColumnTitle());
        entity.width().setValue(columnDescriptor.getWidth());
        entity.wordWrap().setValue(columnDescriptor.isWordWrap());
        entity.visiblily().setValue(columnDescriptor.isVisible());
        return entity;
    }
}
