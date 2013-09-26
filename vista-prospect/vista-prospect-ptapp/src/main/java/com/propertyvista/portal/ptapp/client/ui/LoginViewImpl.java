/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.login.AbstractLoginViewImpl;
import com.propertyvista.domain.DemoData;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;

public class LoginViewImpl extends AbstractLoginViewImpl {

    private static final I18n i18n = I18n.get(LoginViewImpl.class);

    public LoginViewImpl() {
        super(i18n.tr("Login"));
    }

    @Override
    protected void createContent() {
        FlowPanel leftColumn = new FlowPanel();
        leftColumn.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        leftColumn.setWidth("100%");
        setWidget(0, 0, leftColumn);
        getFlexCellFormatter().setWidth(0, 0, "50%");

        if (!VistaTODO.enableWelcomeWizardDemoMode) {
            HTML requirements = new HTML(PortalResources.INSTANCE.requirements().getText());
            requirements.getElement().getStyle().setPaddingLeft(95, Unit.PX);
            requirements.getElement().getStyle().setPaddingBottom(45, Unit.PX);

            requirements.getElement().getStyle().setProperty("background", "url(" + PortalImages.INSTANCE.requirements().getURL() + ") no-repeat");
            leftColumn.add(requirements);

            HTML time = new HTML(PortalResources.INSTANCE.time().getText());
            time.getElement().getStyle().setPaddingLeft(95, Unit.PX);
            time.getElement().getStyle().setPaddingBottom(45, Unit.PX);

            time.getElement().getStyle().setProperty("background", "url(" + PortalImages.INSTANCE.time().getURL() + ") no-repeat");
            leftColumn.add(time);

            HTML dontWorry = new HTML(PortalResources.INSTANCE.dontWorry().getText());
            dontWorry.getElement().getStyle().setPaddingLeft(95, Unit.PX);
            dontWorry.getElement().getStyle().setPaddingBottom(45, Unit.PX);

            dontWorry.getElement().getStyle().setProperty("background", "url(" + PortalImages.INSTANCE.dontWorry().getURL() + ") no-repeat");
            leftColumn.add(dontWorry);
        }

        FlowPanel rightColumn = new FlowPanel();
        rightColumn.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        rightColumn.getElement().getStyle().setMarginLeft(5, Unit.PCT);
        rightColumn.add(form);
        setWidget(0, 1, rightColumn);
        getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
    }

    @Override
    protected List<DevLoginData> devLoginValues() {
        return Arrays.asList(//@formatter:off
                new DevLoginData(DemoData.UserType.PTENANT, 'Q'),
                new DevLoginData(DemoData.UserType.PCOAPPLICANT, 'E')
        );//@formatter:on
    }

    @Override
    public void setDevLogin(List<? extends DevLoginCredentials> devLoginData, String appModeName) {
        // TODO Auto-generated method stub

    }
}
