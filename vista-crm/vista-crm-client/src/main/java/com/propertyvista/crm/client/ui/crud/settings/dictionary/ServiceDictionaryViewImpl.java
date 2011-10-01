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
package com.propertyvista.crm.client.ui.crud.settings.dictionary;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceDictionaryViewImpl extends CrmViewerViewImplBase<ServiceItemType> implements ServiceDictionaryView {

    private final IListerView<ServiceItemType> serviceLister;

    private final IListerView<ServiceItemType> featureLister;

    public ServiceDictionaryViewImpl() {
        super(CrmSiteMap.Settings.ServiceDictionary.class);

        serviceLister = new ListerInternalViewImplBase<ServiceItemType>(new ServiceTypeLister());
        featureLister = new ListerInternalViewImplBase<ServiceItemType>(new FeatureTypeLister());

        remove(actionsWidget);

        // create/init/set main form here: 
        CrmEntityForm<ServiceItemType> form = new ServiceDictionaryViewForm(this);
        form.initContent();
        setForm(form);
    }

    @Override
    public IListerView<ServiceItemType> getServiceListerView() {
        return serviceLister;
    }

    @Override
    public IListerView<ServiceItemType> getFeatureListerView() {
        return featureLister;
    }
}