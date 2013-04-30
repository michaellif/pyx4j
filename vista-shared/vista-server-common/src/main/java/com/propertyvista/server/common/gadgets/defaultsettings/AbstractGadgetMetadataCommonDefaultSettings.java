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

import java.util.UUID;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata.RefreshInterval;
import com.propertyvista.domain.dashboard.gadgets.util.ListerUserSettings;

public class AbstractGadgetMetadataCommonDefaultSettings<G extends GadgetMetadata> implements GadgetMetadataDefaultSettings<G> {

    @Override
    public void init(G gadgetMetadata) {
        gadgetMetadata.refreshInterval().setValue(RefreshInterval.Never);
        gadgetMetadata.gadgetId().setValue(UUID.randomUUID().toString());
        for (String memberName : gadgetMetadata.getEntityMeta().getMemberNames()) {
            if (gadgetMetadata.getMember(memberName) instanceof ListerUserSettings) {
                ((ListerUserSettings) gadgetMetadata.getMember(memberName)).pageSize().setValue(10);
            }
        }
    }

}
