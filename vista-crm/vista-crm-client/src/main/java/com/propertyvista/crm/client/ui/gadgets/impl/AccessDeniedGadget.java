/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-09
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.gadgets.impl;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.AccessDeniedGagetMetadata;

public class AccessDeniedGadget extends GadgetInstanceBase<AccessDeniedGagetMetadata> {

    private static final I18n i18n = I18n.get(AccessDeniedGadget.class);

    public AccessDeniedGadget(AccessDeniedGagetMetadata metadata) {
        super(metadata, AccessDeniedGagetMetadata.class);
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                populateSucceded();
            }
        });
    }

    @Override
    protected Widget initContentPanel() {
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.setSize("100%", "5em");

        Label label = new Label(i18n.tr("Access Denied: Your user roles don't allow you to access this gadget."));
        label.setSize("100%", "100%");
        label.getElement().getStyle().setProperty("marginTop", "2em");
        label.getElement().getStyle().setProperty("marginLeft", "auto");
        label.getElement().getStyle().setProperty("marginRight", "auto");
        contentPanel.add(label);
        return new ScrollPanel(contentPanel);
    }

    @Override
    public String getName() {
        return getMetadata().gadgetName().getValue();
    };

}
