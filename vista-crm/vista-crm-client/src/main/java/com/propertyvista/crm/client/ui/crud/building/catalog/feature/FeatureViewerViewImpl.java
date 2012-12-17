/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog.feature;

import com.google.gwt.core.client.GWT;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.version.FeatureVersionService;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureViewerViewImpl extends CrmViewerViewImplBase<Feature> implements FeatureViewerView {

    public FeatureViewerViewImpl() {
        super(CrmSiteMap.Properties.Feature.class, new FeatureForm(true));
        enableHistorying(Feature.FeatureV.class, GWT.<FeatureVersionService> create(FeatureVersionService.class));
    }
}