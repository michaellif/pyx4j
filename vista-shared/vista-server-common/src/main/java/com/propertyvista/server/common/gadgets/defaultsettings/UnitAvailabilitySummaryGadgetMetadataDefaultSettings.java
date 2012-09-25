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

import static com.propertyvista.server.common.gadgets.ColumnDescriptorEntityBuilder.defColumn;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO;
import com.propertyvista.domain.dashboard.gadgets.common.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilitySummaryGadgetMetadata;
import com.propertyvista.server.common.gadgets.GadgetMetadataCommonDefaultSettings;

public class UnitAvailabilitySummaryGadgetMetadataDefaultSettings extends GadgetMetadataCommonDefaultSettings<UnitAvailabilitySummaryGadgetMetadata> {

    @Override
    public void init(UnitAvailabilitySummaryGadgetMetadata gadgetMetadata) {
        super.init(gadgetMetadata);
        gadgetMetadata.columnDescriptors().addAll(defineUnitSummaryStatusColumns());
    }

    private List<? extends ColumnDescriptorEntity> defineUnitSummaryStatusColumns() {
        UnitAvailabilityStatusSummaryLineDTO proto = EntityFactory.create(UnitAvailabilityStatusSummaryLineDTO.class);
        return Arrays.asList(//@formatter:off
                defColumn(proto.category()).title("").sortable(false).build(),
                defColumn(proto.units()).sortable(false).build(),
                defColumn(proto.percentage()).sortable(false).build()
        );//@formatter:on
    }
}
