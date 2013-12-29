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
package com.propertyvista.server.common.gadgets.defaultsettings;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;

import com.propertyvista.domain.dashboard.gadgets.common.ColumnDescriptorEntity;

public class ColumnDescriptorEntityBuilder {

    private final ColumnDescriptorEntity descriptor;

    public static ColumnDescriptorEntityBuilder column(IObject<?> property, String title) {
        return new ColumnDescriptorEntityBuilder(property, title);
    }

    public static ColumnDescriptorEntityBuilder defColumn(IObject<?> property) {
        return new ColumnDescriptorEntityBuilder(property, null);
    }

    public ColumnDescriptorEntityBuilder(IObject<?> property) {
        this(property, null);
    }

    public ColumnDescriptorEntityBuilder(IObject<?> property, String title) {

        descriptor = EntityFactory.create(ColumnDescriptorEntity.class);
        descriptor.propertyPath().setValue(property.getPath().toString());
        descriptor.isSortable().setValue(true);

        descriptor.title().setValue(title); // if null the client that converts this entity to column descriptor should use Caption property metadata

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
