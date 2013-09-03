/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.dashboard;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.portal.rpc.portal.web.dto.ServicesDashboardDTO;
import com.propertyvista.portal.web.client.themes.DashboardTheme;

public class ServicesDashboardViewImpl extends FlowPanel implements ServicesDashboardView {

    @SuppressWarnings("unused")
    private static final I18n i18n = I18n.get(ServicesDashboardViewImpl.class);

    private ServicesDashboardPresenter presenter;

    private final InsuranceSummaryGadget insuranceGadget;

    public ServicesDashboardViewImpl() {

        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        insuranceGadget = new InsuranceSummaryGadget(this);
        insuranceGadget.asWidget().setWidth("100%");

        add(insuranceGadget);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    private void doLayout(LayoutType layoutType) {
    }

    @Override
    public void setPresenter(ServicesDashboardPresenter presenter) {
        this.presenter = presenter;
    }

    protected ServicesDashboardPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void populate(ServicesDashboardDTO result) {
        insuranceGadget.populate(result.insuranceSummary());
    }

}
