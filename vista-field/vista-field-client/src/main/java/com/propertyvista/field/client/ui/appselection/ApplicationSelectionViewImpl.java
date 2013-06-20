/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.field.client.ui.appselection;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.field.client.theme.FieldTheme;
import com.propertyvista.field.rpc.FieldSiteMap;

public class ApplicationSelectionViewImpl extends VerticalPanel implements ApplicationSelectionView {

    private final static I18n i18n = I18n.get(ApplicationSelectionViewImpl.class);

    private Presenter presenter;

    public ApplicationSelectionViewImpl() {
        setSize("100%", "100%");
        setVerticalAlignment(ALIGN_MIDDLE);
        setHorizontalAlignment(ALIGN_CENTER);

        final Button propertyManagerButton = createButton(i18n.tr("Property Manager"), new Command() {
            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new FieldSiteMap.Properties.Building());
            }
        });

        final Button leasingAgentButton = createButton(i18n.tr("Leasing Agent"), new Command() {
            @Override
            public void execute() {
                Window.alert("Leasing Agent page goes next..");
            }
        });

        final Button securityButton = createButton(i18n.tr("Security"), new Command() {
            @Override
            public void execute() {
                Window.alert("Security page goes next..");
            }
        });

        final Button maintenanceButton = createButton(i18n.tr("Maintenance"), new Command() {
            @Override
            public void execute() {
                Window.alert("Maintenance page goes next..");
            }
        });

        VerticalPanel buttonsHolder = new VerticalPanel();
        buttonsHolder.add(propertyManagerButton);
        buttonsHolder.add(leasingAgentButton);
        buttonsHolder.add(securityButton);
        buttonsHolder.add(maintenanceButton);
        add(buttonsHolder);
    }

    @Override
    public void setPresenter(ApplicationSelectionView.Presenter presenter) {
        this.presenter = presenter;
    }

    private Button createButton(String name, Command command) {
        Button button = new Button(name, command);
        button.setStyleName(FieldTheme.StyleName.AppSelectionButton.name());
        return button;
    }
}
