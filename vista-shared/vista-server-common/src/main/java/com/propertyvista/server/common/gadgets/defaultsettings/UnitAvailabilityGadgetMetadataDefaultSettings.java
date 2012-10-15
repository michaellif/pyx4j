/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.gadgets.defaultsettings;

import static com.propertyvista.server.common.gadgets.defaultsettings.ColumnDescriptorEntityBuilder.defColumn;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.common.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMetadata;

public class UnitAvailabilityGadgetMetadataDefaultSettings extends AbstractGadgetMetadataCommonDefaultSettings<UnitAvailabilityGadgetMetadata> {

    private static final I18n i18n = I18n.get(UnitAvailabilityGadgetMetadataDefaultSettings.class);

    @Override
    public void init(UnitAvailabilityGadgetMetadata gadgetMetadata) {
        super.init(gadgetMetadata);
        gadgetMetadata.pageSize().setValue(10);
        gadgetMetadata.filterPreset().setValue(UnitAvailabilityGadgetMetadata.FilterPreset.VacantAndNotice);
        gadgetMetadata.columnDescriptors().addAll(defineUnitAvailabilityReportColumns());
    }

    private List<? extends ColumnDescriptorEntity> defineUnitAvailabilityReportColumns() {
        UnitAvailabilityStatus proto = EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class);

        return Arrays.asList(//@formatter:off
                // references
                defColumn(proto.building().propertyCode()).build(),
                defColumn(proto.building().externalId()).visible(false).build(),
                defColumn(proto.building().info().name()).visible(false).title(i18n.ntr("Building Name")).build(),
                defColumn(proto.building().info().address()).visible(false).build(),
                defColumn(proto.building().propertyManager().name()).visible(false).title(i18n.ntr("Property Manager")).build(),                    
                defColumn(proto.building().complex().name()).visible(false).title(i18n.ntr("Complex")).build(),
                defColumn(proto.unit().info().number()).title(i18n.ntr("Unit Name")).build(),
                defColumn(proto.floorplan().name()).visible(false).title(i18n.ntr("Floorplan Name")).build(),
                defColumn(proto.floorplan().marketingName()).visible(false).title(i18n.ntr("Floorplan Marketing Name")).build(),
                
                // status
                defColumn(proto.vacancyStatus()).build(),
                defColumn(proto.rentedStatus()).visible(true).build(),
                defColumn(proto.scoping()).visible(true).build(),
                defColumn(proto.rentReadinessStatus()).visible(true).build(),
                defColumn(proto.unitRent()).build(),
                defColumn(proto.marketRent()).build(),
                defColumn(proto.rentDeltaAbsolute()).visible(true).build(),
                defColumn(proto.rentDeltaRelative()).visible(false).build(),
                defColumn(proto.rentEndDay()).visible(true).build(),
                defColumn(proto.moveInDay()).visible(true).build(),
                defColumn(proto.rentedFromDay()).visible(true).build(),                
                defColumn(proto.daysVacant()).sortable(false).build(),
                defColumn(proto.revenueLost()).sortable(false).build()
        );//@formatter:on
    }

}
