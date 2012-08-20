/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.complex;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.visor.dashboard.DashboardSelectorDialog;
import com.propertyvista.crm.client.visor.dashboard.IDashboardVisorController;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.dto.ComplexDTO;

public class ComplexViewerViewImpl extends CrmViewerViewImplBase<ComplexDTO> implements ComplexViewerView {

    private static final I18n i18n = I18n.get(ComplexViewerViewImpl.class);

    public ComplexViewerViewImpl() {
        super(CrmSiteMap.Properties.Complex.class);

        addHeaderToolbarItem(new Button(i18n.tr("Dashboard"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new DashboardSelectorDialog() {

                    @Override
                    public boolean onClickOk() {
                        List<DashboardMetadata> dashboards = getSelectedItems();
                        if (!dashboards.isEmpty()) {
                            IDashboardVisorController controller = ((ComplexViewerView.Presenter) getPresenter()).getDashboardController(dashboards.get(0),
                                    getForm().getValue().buildings());
                            controller.show(ComplexViewerViewImpl.this);
                            return true;
                        } else {
                            return false;
                        }

                    }

                }.show();
            }
        }).asWidget());

        setForm(new ComplexForm(true));
    }

    @Override
    public void populate(ComplexDTO value) {
        super.populate(value);
    }

}
