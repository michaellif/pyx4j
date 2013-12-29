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
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;

public final class AvailabilityReportTableColumnsHolder {

    private static final I18n i18n = I18n.get(AvailabilityReportTableColumnsHolder.class);

    public static MemberColumnDescriptor[] AVAILABILITY_TABLE_COLUMNS;

    static {
        UnitAvailabilityStatus proto = EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class);

        AVAILABILITY_TABLE_COLUMNS = new MemberColumnDescriptor[] {//@formatter:off
                
                // references                
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().propertyCode()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().externalId()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().info().name()).title(i18n.tr("Building Name")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().info().address()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().propertyManager().name()).title(i18n.tr("Property Manager")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.unit().info().number()).title(i18n.tr("Unit Name")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.floorplan().name()).visible(false).title(i18n.tr("Floorplan Name")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.floorplan().marketingName()).visible(false).title(i18n.tr("Floorplan Marketing Name")).build(),
                
                // status
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.vacancyStatus()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentedStatus()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.scoping()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentReadinessStatus()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.unitRent()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.marketRent()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentDeltaAbsolute()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentDeltaRelative()).visible(false).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentEndDay()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.moveInDay()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentedFromDay()).visible(true).build()
        };//@formatter:on
    }

}
