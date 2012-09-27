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

import com.propertyvista.domain.dashboard.gadgets.common.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingListerGadgetMetadata;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerGadgetMetadataDefaultSettings extends AbstractGadgetMetadataCommonDefaultSettings<BuildingListerGadgetMetadata> {

    private static final I18n i18n = I18n.get(BuildingListerGadgetMetadataDefaultSettings.class);

    @Override
    public void init(BuildingListerGadgetMetadata gadgetMetadata) {
        super.init(gadgetMetadata);
        gadgetMetadata.pageSize().setValue(10);
        gadgetMetadata.columnDescriptors().addAll(defineBuildingListerColumns());
    };

    private List<ColumnDescriptorEntity> defineBuildingListerColumns() {

        BuildingDTO proto = EntityFactory.getEntityPrototype(BuildingDTO.class);

        return Arrays.asList(//@formatter:off
                defColumn(proto.complex()).visible(false).build(),
                defColumn(proto.propertyCode()).build(),
                defColumn(proto.propertyManager()).build(),
                defColumn(proto.marketing().name()).title(i18n.tr("Marketing Name")).build(),
                defColumn(proto.info().name()).build(),
                defColumn(proto.info().type()).build(),
                defColumn(proto.info().shape()).visible(false).build(),
                defColumn(proto.info().address().streetName()).visible(false).build(),
                defColumn(proto.info().address().city()).build(),
                defColumn(proto.info().address().province()).build(),
                defColumn(proto.info().address().country()).build()
        );//@formatter:on
    }

}
