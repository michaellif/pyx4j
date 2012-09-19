/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import java.util.Arrays;
import java.util.List;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class ArrearsGadgetFactory extends AbstractGadget<ArrearsGadgetMetadata> {

    public ArrearsGadgetFactory() {
        super(ArrearsGadgetMetadata.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Arrears.toString());
    }

    @Override
    protected GadgetInstanceBase<ArrearsGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsGadget(gadgetMetadata);
    }

}
