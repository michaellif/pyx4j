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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.widgets.client.dialog.DialogPanel;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureEditorViewImpl extends CrmEditorViewImplBase<Feature> implements FeatureEditorView {

    public FeatureEditorViewImpl() {
        super(CrmSiteMap.Properties.Feature.class, new FeatureEditorForm());
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Feature.Type> callback) {
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
                defaultCaption = box.getSelectedType().toString();
                callback.onSuccess(box.getSelectedType());
            }
        });

        box.show();
    }

    private class SelectTypeBox extends DialogPanel {

        private final I18n i18n = I18nFactory.getI18n(SelectTypeBox.class);

        private final CComboBox<Feature.Type> types = new CComboBox<Feature.Type>(i18n.tr("Types"), true);

        public SelectTypeBox() {
            super(false, true);
            setCaption(i18n.tr("Select Feature Type"));

            final Button btnOk = new Button(i18n.tr("OK"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });

            types.setOptions(EnumSet.allOf(Feature.Type.class));
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

        public Feature.Type getSelectedType() {
            return types.getValue();
        }
    }
}
