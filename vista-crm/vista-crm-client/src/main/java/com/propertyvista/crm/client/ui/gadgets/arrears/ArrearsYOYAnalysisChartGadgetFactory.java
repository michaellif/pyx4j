/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.impl.ArrearsYOYAnalysisChartGadget;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class ArrearsYOYAnalysisChartGadgetFactory extends AbstractGadgetFactory<ArrearsYOYAnalysisChartGadgetMetadata> {

    public ArrearsYOYAnalysisChartGadgetFactory() {
        super(ArrearsYOYAnalysisChartGadgetMetadata.class);
    }

    @Override
    protected GadgetInstanceBase<ArrearsYOYAnalysisChartGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsYOYAnalysisChartGadget((ArrearsYOYAnalysisChartGadgetMetadata) gadgetMetadata);
    }
}