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
 * @version $Id$
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
                
                // references                
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.building().propertyCode()).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.building().externalId()).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.building().info().name()).title(i18n.tr("Building Name")).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.building().info().address()).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.building().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.unit().info().number()).title(i18n.tr("Unit Name")).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.floorplan().name()).visible(false).title(i18n.tr("Floorplan Name")).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.floorplan().marketingName()).visible(false).title(i18n.tr("Floorplan Marketing Name")).build(),
                
                // status
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.vacancyStatus()).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.rentedStatus()).visible(true).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.scoping()).visible(true).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.rentReadinessStatus()).visible(true).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.unitRent()).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.marketRent()).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.rentDeltaAbsolute()).visible(true).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.rentDeltaRelative()).visible(false).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.rentEndDay()).visible(true).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.moveInDay()).visible(true).build(),
                (ColumnDescriptor) new ColumnDescriptor.Builder(proto.rentedFromDay()).visible(true).build()
        };//@formatter:on
    }

}
