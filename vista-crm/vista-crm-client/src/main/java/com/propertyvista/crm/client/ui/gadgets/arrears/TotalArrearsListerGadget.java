/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 14, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.arrears.Arrears;

public class TotalArrearsListerGadget extends ArrearsListerGadget {

    public TotalArrearsListerGadget(GadgetMetadata gmd) {
        super(gmd);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.TotalArrearsGadget);
        gmd.name().setValue(GadgetType.TotalArrearsGadget.toString());
    }

    @Override
    protected Arrears arrearsProto() {
        return proto().totalArrears();
    }
}
