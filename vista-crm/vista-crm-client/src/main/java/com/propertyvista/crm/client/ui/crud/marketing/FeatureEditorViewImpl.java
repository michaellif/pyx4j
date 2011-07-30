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
package com.propertyvista.crm.client.ui.crud.marketing;

import java.util.ArrayList;
import java.util.Collection;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.widgets.client.dialog.DialogPanel;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ParkingRent;
import com.propertyvista.domain.financial.offering.ResidentialRent;
import com.propertyvista.domain.financial.offering.StorageRent;
import com.propertyvista.domain.financial.offeringnew.Concession;

public class FeatureEditorViewImpl extends CrmEditorViewImplBase<Feature> implements FeatureEditorView {

    private final FeatureViewDelegate delegate;

    public FeatureEditorViewImpl() {
        super(CrmSiteMap.Properties.Feature.class);
        delegate = new FeatureViewDelegate(false);
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

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Class<? extends Feature>> callback) {
        final SelectTypeBox box = new SelectTypeBox();
        box.setPopupPositionAndShow(new PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                box.setPopupPosition((Window.getClientWidth() - offsetWidth) / 2, (Window.getClientHeight() - offsetHeight) / 3);
            }
        });
        box.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                defaultCaption = box.getSelectedType().name;
                callback.onSuccess(box.getSelectedType().type);
            }
        });

        box.show();
    }

    private class SelectTypeBox extends DialogPanel {

        private final I18n i18n = I18nFactory.getI18n(SelectTypeBox.class);

        private class FeatureType {

            private final String name;

            private final Class<? extends Feature> type;

            public FeatureType(Class<? extends Feature> type, String name) {
                this.type = type;
                this.name = name;
            }

            @Override
            public String toString() {
                return name;
            }
        }

        private final CComboBox<FeatureType> features = new CComboBox<FeatureType>("Features", true);

        public SelectTypeBox() {
            super(false, true);
            setCaption(i18n.tr("Select Feature Type"));

            final Button btnOk = new Button(i18n.tr("Ok"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });
            btnOk.setEnabled(false);

            Collection<FeatureType> opt = new ArrayList<FeatureType>();

            // fill available variants here:
            opt.add(new FeatureType(ResidentialRent.class, i18n.tr("Residential Rent")));
            opt.add(new FeatureType(ParkingRent.class, i18n.tr("Parking Rent")));
            opt.add(new FeatureType(StorageRent.class, i18n.tr("Storage Rent")));

            features.setOptions(opt);
            features.setWidth("100%");
            features.addValueChangeHandler(new ValueChangeHandler<FeatureType>() {
                @Override
                public void onValueChange(ValueChangeEvent<FeatureType> event) {
                    btnOk.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(features);
            vPanel.add(btnOk);
            vPanel.setCellHorizontalAlignment(btnOk, HasHorizontalAlignment.ALIGN_CENTER);
            vPanel.setSpacing(8);
            vPanel.setSize("100%", "100%");

            setContentWidget(vPanel);
            setSize("250px", "100px");
        }

        public FeatureType getSelectedType() {
            return features.getValue();
        }
    }
}
