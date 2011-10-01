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
package com.propertyvista.crm.client.ui.crud.building.catalog;

import java.util.EnumSet;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.dialog.DialogPanel;

import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
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

        // create/init/set main form here: 
        CrmEntityForm<Service> form = new ServiceEditorForm(this);
        form.initContent();
        setForm(form);
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Service.Type> callback) {
        new ShowPopUpBox<SelectTypeBox>(new SelectTypeBox()) {
            @Override
            public void onClose(SelectTypeBox box) {
                defaultCaption = box.getSelectedType().toString();
                callback.onSuccess(box.getSelectedType());
            }
        };
    }

    private class SelectTypeBox extends DialogPanel {

        private final I18n i18n = I18nFactory.getI18n(SelectTypeBox.class);

        private final CComboBox<Service.Type> types = new CComboBox<Service.Type>(i18n.tr("Types"), true);

        public SelectTypeBox() {
            super(false, true);
            setCaption(i18n.tr("Select Service Type"));

            final Button btnOk = new Button(i18n.tr("OK"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });

            types.setOptions(EnumSet.allOf(Service.Type.class));
            types.setValue(types.getOptions().get(0));
            types.setWidth("100%");

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(types);
            vPanel.add(btnOk);
            vPanel.setCellHorizontalAlignment(btnOk, HasHorizontalAlignment.ALIGN_CENTER);
            vPanel.setSpacing(8);
            vPanel.setSize("100%", "100%");

            setContentWidget(vPanel);
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
