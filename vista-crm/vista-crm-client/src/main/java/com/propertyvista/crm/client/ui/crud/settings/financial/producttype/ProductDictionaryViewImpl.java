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
package com.propertyvista.crm.client.ui.crud.settings.financial.producttype;

import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.Utility;

public class ProductDictionaryViewImpl extends CrmViewerViewImplBase<ProductItemType> implements ProductDictionaryView {

    private final ILister<ServiceItemType> serviceLister;

    private final ILister<FeatureItemType> featureLister;

    private final ILister<Utility> utilityLister;

    public ProductDictionaryViewImpl() {
        super(true);
        setNotesVisible(false); // disable notes for this view

        serviceLister = new ListerInternalViewImplBase<ServiceItemType>(new ServiceTypeLister());
        featureLister = new ListerInternalViewImplBase<FeatureItemType>(new FeatureTypeLister());
        utilityLister = new ListerInternalViewImplBase<Utility>(new UtilityLister());

        // set main form here: 
        setForm(new ProductDictionaryViewForm(this));
    }

    @Override
    public ILister<ServiceItemType> getServiceListerView() {
        return serviceLister;
    }

    @Override
    public ILister<FeatureItemType> getFeatureListerView() {
        return featureLister;
    }

    @Override
    public ILister<Utility> getUtilityListerView() {
        return utilityLister;
    }
}