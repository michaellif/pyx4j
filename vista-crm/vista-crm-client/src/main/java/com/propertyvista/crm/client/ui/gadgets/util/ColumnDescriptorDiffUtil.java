/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.util;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor.Builder;

import com.propertyvista.domain.dashboard.gadgets.util.ColumnUserSettings;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;

public class ColumnDescriptorDiffUtil {

    public static List<ColumnUserSettings> getDescriptorsDiff(List<ColumnDescriptor> defaultColumnDescriptors, List<ColumnDescriptor> columnDescriptors) {
        List<ColumnUserSettings> diff = new ArrayList<ColumnUserSettings>();
        for (ColumnDescriptor defaultColumnDescriptor : defaultColumnDescriptors) {
            for (ColumnDescriptor columnDescriptor : columnDescriptors) {
                if (columnDescriptor.getColumnName().equals(defaultColumnDescriptor.getColumnName()) & hasDiff(defaultColumnDescriptor, columnDescriptor)) {
                    diff.add(ColumnDescriptorConverter.getUserSettings(columnDescriptor));
                }
            }
        }
        return diff;
    }

    /** makes a new column descriptors list applying differences from ListDescriptor */
    public static <E extends IEntity> List<ColumnDescriptor> applyDiff(Class<E> describedEntityClass, List<ColumnDescriptor> defaultColumnDescriptors,
            ListerUserSettings listDescriptor) {
        List<ColumnDescriptor> patchedColumnDescriptors = new ArrayList<ColumnDescriptor>();
        for (ColumnDescriptor defaultColumnDescriptor : defaultColumnDescriptors) {
            ColumnUserSettings overriddingColumnDescriptor = getOverridingDescriptor(listDescriptor, defaultColumnDescriptor);
            E proto = EntityFactory.getEntityPrototype(describedEntityClass);

            Builder builder = new MemberColumnDescriptor.Builder(proto.getMember(new Path(defaultColumnDescriptor.getColumnName())));
            builder.columnTitle(defaultColumnDescriptor.getColumnTitle());
            builder.visible(overriddingColumnDescriptor != null ? overriddingColumnDescriptor.isVisible().getValue() : defaultColumnDescriptor.isVisible());
            builder.searchable(defaultColumnDescriptor.isSearchable());
            builder.sortable(defaultColumnDescriptor.isSortable());
            builder.wordWrap(defaultColumnDescriptor.isWordWrap());
            builder.width(defaultColumnDescriptor.getWidth());
            if (defaultColumnDescriptor.isSearchableOnly()) {
                builder.searchableOnly();
            }
            patchedColumnDescriptors.add(builder.build());

        }
        return patchedColumnDescriptors;
    }

    static boolean hasDiff(ColumnDescriptor defaultColumnDescriptor, ColumnDescriptor columnDescriptor) {//@formatter:off        
        return (defaultColumnDescriptor.isVisible() != columnDescriptor.isVisible());
    }//@formatter:on

    static ColumnUserSettings getOverridingDescriptor(ListerUserSettings listDescriptor, ColumnDescriptor defaultColumnDescriptor) {
        ColumnUserSettings overridingDescriptor = null;
        String targetPropertyPath = defaultColumnDescriptor.getColumnName();
        for (ColumnUserSettings descriptorEntity : listDescriptor.overriddenColumns()) {
            if (targetPropertyPath.equals(descriptorEntity.property().getValue())) {
                overridingDescriptor = descriptorEntity;
                break;
            }
        }
        return overridingDescriptor;
    }
}
