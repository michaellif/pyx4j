/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.util;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;

import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.ListerGadgetBaseMetadata;

/**
 * This is not a JUnit test, just a file that has to be manually run to see the results
 * 
 * @author ArtyomB
 */
public class ReportTableTemplateBuilderTest {

    public static void main(String argv[]) {
        ReportTableTemplateBuilderTestMockupEntity mockupProto = EntityFactory.getEntityPrototype(ReportTableTemplateBuilderTestMockupEntity.class);

        ListerGadgetBaseMetadata metadata = EntityFactory.create(ListerGadgetBaseMetadata.class);
        metadata.columnDescriptors().add(columnDescriptor(mockupProto.name(), "the name", true));
        metadata.columnDescriptors().add(columnDescriptor(mockupProto.money(), null, true));
        metadata.columnDescriptors().add(columnDescriptor(mockupProto.date(), null, false));

        ReportTableTemplateBuilder builder = new ReportTableTemplateBuilder(mockupProto, metadata);
        System.out.println(builder.generateReportTemplate());
    }

    private static ColumnDescriptorEntity columnDescriptor(IObject<?> property, String title, Boolean isVisible) {
        ColumnDescriptorEntity descriptor = EntityFactory.create(ColumnDescriptorEntity.class);
        descriptor.propertyPath().setValue(property.getPath().toString());
        if (title == null) {
            descriptor.title().setValue(property.getMeta().getCaption());
        } else {
            descriptor.title().setValue(title);
        }
        descriptor.isVisible().setValue(isVisible);

        return descriptor;

    }
}
