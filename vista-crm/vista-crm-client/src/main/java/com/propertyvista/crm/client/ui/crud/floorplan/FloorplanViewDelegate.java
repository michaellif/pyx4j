/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.floorplan;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureLister;
import com.propertyvista.domain.financial.offering.Feature;

public class FloorplanViewDelegate implements FloorplanView {

    private final IListerView<Feature> featuresLister;

    public FloorplanViewDelegate(boolean readOnly) {
        featuresLister = new ListerInternalViewImplBase<Feature>(new FeatureLister());
    }

    @Override
    public IListerView<Feature> getFeaturesListerView() {
        return featuresLister;
    }
}
