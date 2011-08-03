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
package com.propertyvista.crm.client.ui.crud.marketing;

import com.pyx4j.site.client.ui.crud.IListerView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.ParkingRent;
import com.propertyvista.domain.financial.offering.ResidentialRent;
import com.propertyvista.domain.financial.offering.StorageRent;
import com.propertyvista.domain.financial.offeringnew.Concession;
import com.propertyvista.domain.financial.offeringnew.Feature;

public class FeatureViewerViewImpl extends CrmViewerViewImplBase<Feature> implements FeatureViewerView {

    private final FeatureViewDelegate delegate;

    public FeatureViewerViewImpl() {
        super(CrmSiteMap.Properties.Feature.class);
        delegate = new FeatureViewDelegate(true);
    }

    @Override
    public void populate(Feature value) {
        if (value instanceof ResidentialRent) {
            CrmEntityForm<ResidentialRent> formResidential = new ResidentialRentEditorForm(this);
            formResidential.initialize();
            setForm(formResidential);
        } else if (value instanceof ParkingRent) {
            CrmEntityForm<ParkingRent> formParking = new ParkingRentEditorForm(this);
            formParking.initialize();
            setForm(formParking);
        } else if (value instanceof StorageRent) {
            CrmEntityForm<StorageRent> formStorage = new StorageRentEditorForm(this);
            formStorage.initialize();
            setForm(formStorage);
        }
        super.populate(value);
    }

    @Override
    public IListerView<Concession> getConcessionsListerView() {
        return delegate.getConcessionsListerView();
    }
}