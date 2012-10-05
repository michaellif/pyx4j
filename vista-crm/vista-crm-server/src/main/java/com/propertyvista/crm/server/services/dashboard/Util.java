/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard;

import com.pyx4j.site.server.services.customization.CustomizationPersistenceHelper;

import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.dashboard.GadgetMetadataHolder;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class Util {

    public static CustomizationPersistenceHelper<GadgetMetadata> gadgetStorage() {
        return new CustomizationPersistenceHelper<GadgetMetadata>(GadgetMetadataHolder.class, GadgetMetadata.class);
    }

    public static String makeShadowId(String id) {
        return id + ":" + CrmAppContext.getCurrentUserPrimaryKey().toString();
    }

}
