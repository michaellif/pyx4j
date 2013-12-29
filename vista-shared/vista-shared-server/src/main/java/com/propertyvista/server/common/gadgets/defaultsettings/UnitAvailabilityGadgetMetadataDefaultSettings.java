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

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMetadata;

public class UnitAvailabilityGadgetMetadataDefaultSettings extends AbstractGadgetMetadataCommonDefaultSettings<UnitAvailabilityGadgetMetadata> {

    private static final I18n i18n = I18n.get(UnitAvailabilityGadgetMetadataDefaultSettings.class);

    @Override
    public void init(UnitAvailabilityGadgetMetadata gadgetMetadata) {
        super.init(gadgetMetadata);
        gadgetMetadata.unitStatusListerSettings().pageSize().setValue(10);
        gadgetMetadata.filterPreset().setValue(UnitAvailabilityGadgetMetadata.FilterPreset.VacantAndNotice);
    }

}
