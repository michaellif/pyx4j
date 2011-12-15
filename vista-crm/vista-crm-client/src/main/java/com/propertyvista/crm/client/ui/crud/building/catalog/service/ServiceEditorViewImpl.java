/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog.service;

import java.util.EnumSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.common.client.ui.components.SelectTypeDialog;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceEditorViewImpl extends CrmEditorViewImplBase<Service> implements ServiceEditorView {

    private final IListerView<Feature> featureLister;

    private final IListerView<Concession> concessionLister;

    public ServiceEditorViewImpl() {
        super(CrmSiteMap.Properties.Service.class);
        featureLister = new ListerInternalViewImplBase<Feature>(new SelectFeatrueLister());
        concessionLister = new ListerInternalViewImplBase<Concession>(new SelectConcessionLister());

        // set main form here: 
        setForm(new ServiceEditorForm());
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Service.Type> callback) {
        new SelectTypeDialog<Service.Type>(i18n.tr("Select Service Type"), EnumSet.allOf(Service.Type.class)) {
            @Override
            public boolean onClickOk() {
                defaultCaption = getSelectedType().toString();
                callback.onSuccess(getSelectedType());
                return true;
            }

            @Override
            public String defineWidth() {
                return "250px";
            }

            @Override
            public String defineHeight() {
                return "100px";
            }
        }.show();
    }

    @Override
    public IListerView<Feature> getFeatureListerView() {
        return featureLister;
    }

    @Override
    public IListerView<Concession> getConcessionListerView() {
        return concessionLister;
    }
}
