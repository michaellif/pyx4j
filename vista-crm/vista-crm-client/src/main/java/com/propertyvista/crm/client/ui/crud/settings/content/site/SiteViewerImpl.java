/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteViewerImpl extends CrmViewerViewImplBase<SiteDescriptorDTO> implements SiteViewer {

    private static final I18n i18n = I18n.get(SiteViewerImpl.class);

    public SiteViewerImpl() {
        super(CrmSiteMap.Settings.Content.class, new SiteEditorForm(true));

        // Add actions:
        addToolbarItem(new Button(i18n.tr("Add Child Page"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new AvailableLocaleSelectorDialog(null, new ValueChangeHandler<AvailableLocale>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<AvailableLocale> event) {
                        Key valueKey = form.getValue().getPrimaryKey();
                        if (valueKey != null) { // shouldn't be new unsaved value!..
                            newChild(valueKey, event.getValue());
                        }
                    }
                }).show();
            }
        }).asWidget());
    }

    @Override
    public void viewChild(Key id) {
        ((SiteViewer.Presenter) getPresenter()).viewChild(id);
    }

    @Override
    public void newChild(Key parentid, AvailableLocale locale) {
        ((SiteViewer.Presenter) getPresenter()).editNew(parentid, locale);
    }
}