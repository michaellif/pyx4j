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

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.dialog.OkDialog;

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
        new SelectTypeBox() {
            @Override
            public boolean onClickOk() {
                defaultCaption = getSelectedType().toString();
                callback.onSuccess(getSelectedType());
                return true;
            }
        }.show();
    }

    private abstract class SelectTypeBox extends OkDialog {

        private final CComboBox<Service.Type> types;

        public SelectTypeBox() {
            super(i18n.tr("Select Service Type"));

            types = new CComboBox<Service.Type>(i18n.tr("Types"), true);
            types.setOptions(EnumSet.allOf(Service.Type.class));
            types.setValue(types.getOptions().get(0));
            types.setWidth("100%");

            setBody(types.asWidget());
            setSize("250px", "100px");
        }

        public Service.Type getSelectedType() {
            return types.getValue();
        }
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
