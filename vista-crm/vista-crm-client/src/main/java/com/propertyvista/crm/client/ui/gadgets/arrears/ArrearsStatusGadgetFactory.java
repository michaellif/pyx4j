/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.impl.ArrearsStatusGadget;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class ArrearsStatusGadgetFactory extends AbstractGadgetFactory<ArrearsStatusGadgetMetadata> {

    final static I18n i18n = I18n.get(ArrearsStatusGadgetFactory.class);

    public ArrearsStatusGadgetFactory() {
        super(ArrearsStatusGadgetMetadata.class);
    }

    @Override
    protected GadgetInstanceBase<ArrearsStatusGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsStatusGadget((ArrearsStatusGadgetMetadata) gadgetMetadata);
    }

}
