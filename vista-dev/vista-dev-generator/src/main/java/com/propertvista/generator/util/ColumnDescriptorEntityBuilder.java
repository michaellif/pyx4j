/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertvista.generator.util;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;

import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;

public class ColumnDescriptorEntityBuilder {

    private final ColumnDescriptorEntity descriptor;

    public static ColumnDescriptorEntityBuilder column(IObject<?> property, String title) {
        return new ColumnDescriptorEntityBuilder(property, title);
    }

    public static ColumnDescriptorEntityBuilder column(IObject<?> property) {
        return new ColumnDescriptorEntityBuilder(property, null);
    }

    public ColumnDescriptorEntityBuilder(IObject<?> property) {
        this(property, null);
    }

    public ColumnDescriptorEntityBuilder(IObject<?> property, String title) {

        descriptor = EntityFactory.create(ColumnDescriptorEntity.class);
        descriptor.propertyPath().setValue(property.getPath().toString());
        descriptor.isSortable().setValue(true);

        if (title != null) {
            descriptor.title().setValue(title);
        } else {
            descriptor.title().setValue(property.getMeta().getCaption());
        }

        descriptor.isSortable().setValue(true);
        descriptor.width().setValue(null);
        descriptor.wrapWords().setValue(true);
        descriptor.isVisible().setValue(true);
    }

    public ColumnDescriptorEntityBuilder title(String columnTitle) {
        descriptor.title().setValue(columnTitle);
        return this;
    }

    public ColumnDescriptorEntityBuilder sortable(boolean sortable) {
        descriptor.isSortable().setValue(sortable);
        return this;
    }

    public ColumnDescriptorEntityBuilder width(String width) {
        descriptor.width().setValue(width);
        return this;
    }

    public ColumnDescriptorEntityBuilder wordWrap(boolean wordWrap) {
        descriptor.wrapWords().setValue(wordWrap);
        return this;
    }

    public ColumnDescriptorEntityBuilder visible(boolean visible) {
        descriptor.isVisible().setValue(visible);
        return this;
    }

    public ColumnDescriptorEntity build() {
        return descriptor;
    }

}
