/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.leasexpiration;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.impl.LeaseExpirationGadget;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class LeaseExpirationGadgetFactory extends AbstractGadgetFactory<LeaseExpirationGadgetMetadata> {

    public LeaseExpirationGadgetFactory() {
        super(LeaseExpirationGadgetMetadata.class);
    }

    @Override
    protected GadgetInstanceBase<LeaseExpirationGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new LeaseExpirationGadget((LeaseExpirationGadgetMetadata) gadgetMetadata);
    }

}
