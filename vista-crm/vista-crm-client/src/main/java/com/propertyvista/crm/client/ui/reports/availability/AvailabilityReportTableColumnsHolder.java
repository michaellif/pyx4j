/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-09
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.reports.availability;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;

public final class AvailabilityReportTableColumnsHolder {

    private static final I18n i18n = I18n.get(AvailabilityReportTableColumnsHolder.class);

    public static ColumnDescriptor[] AVAILABILITY_TABLE_COLUMNS;

    static {
        UnitAvailabilityStatus proto = EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class);

        AVAILABILITY_TABLE_COLUMNS = new ColumnDescriptor[] {//@formatter:off
                
                new ColumnDescriptor.Builder(proto.building().propertyCode()).build(),
                new ColumnDescriptor.Builder(proto.building().externalId()).build(),
                new ColumnDescriptor.Builder(proto.building().info().name()).columnTitle(i18n.tr("Building Name")).build(),
                new ColumnDescriptor.Builder(proto.building().info().address()).build(),
                new ColumnDescriptor.Builder(proto.building().complex().name()).visible(false).columnTitle(i18n.tr("Complex")).build(),
                new ColumnDescriptor.Builder(proto.unit().info().number()).columnTitle(i18n.tr("Unit Name")).build(),
                new ColumnDescriptor.Builder(proto.floorplan().name()).visible(false).columnTitle(i18n.tr("Floorplan Name")).build(),
                new ColumnDescriptor.Builder(proto.floorplan().marketingName()).visible(false).columnTitle(i18n.tr("Floorplan Marketing Name")).build(),
                
                new ColumnDescriptor.Builder(proto.vacancyStatus()).build(),
                new ColumnDescriptor.Builder(proto.rentedStatus()).visible(true).build(),
                new ColumnDescriptor.Builder(proto.scoping()).visible(true).build(),
                new ColumnDescriptor.Builder(proto.rentReadinessStatus()).visible(true).build(),
                new ColumnDescriptor.Builder(proto.unitRent()).build(),
                new ColumnDescriptor.Builder(proto.marketRent()).build(),
                new ColumnDescriptor.Builder(proto.rentDeltaAbsolute()).visible(true).build(),
                new ColumnDescriptor.Builder(proto.rentDeltaRelative()).visible(false).build(),
                new ColumnDescriptor.Builder(proto.rentEndDay()).visible(true).build(),
                new ColumnDescriptor.Builder(proto.moveInDay()).visible(true).build(),
                new ColumnDescriptor.Builder(proto.rentedFromDay()).visible(true).build()
        };//@formatter:on
    }

}
