/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 30, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public abstract class AbstractGadgetFactory<GADGET_TYPE extends GadgetMetadata> implements IGadgetFactory {

    private final Class<GADGET_TYPE> gadgetMetadataClass;

    protected AbstractGadgetFactory(Class<GADGET_TYPE> gadgetMetadataClassLiteral) {
        gadgetMetadataClass = gadgetMetadataClassLiteral;
    }

    @Override
    public IGadgetInstance createGadget(GadgetMetadata gadgetMetadata) throws Error {
        assert gadgetMetadata != null && gadgetMetadata.getInstanceValueClass().equals(gadgetMetadataClass);
        GadgetInstanceBase<GADGET_TYPE> gadget = createInstance(gadgetMetadata);
        gadget.initView();
        return gadget;
    }

    @Override
    public Class<? extends GadgetMetadata> getGadgetMetadataClass() {
        return gadgetMetadataClass;
    }

    protected abstract GadgetInstanceBase<GADGET_TYPE> createInstance(GadgetMetadata gadgetMetadata) throws Error;
}
