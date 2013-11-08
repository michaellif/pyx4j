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
package com.propertyvista.portal.prospect.ui.signup;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.portal.rpc.shared.EntityValidationException;
import com.propertyvista.portal.shared.themes.DashboardTheme;

public class SignUpViewImpl extends FlowPanel implements SignUpView {

    private static I18n i18n = I18n.get(SignUpViewImpl.class);

    private final SignUpGadget gadget;

    public SignUpViewImpl() {

        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        gadget = new SignUpGadget(this);
        add(gadget);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });

    }

    private void doLayout(LayoutType layoutType) {
        gadget.doLayout(layoutType);
    }

    @Override
    public void setPresenter(SignUpPresenter presenter) {
        gadget.setPresenter(presenter);
    }

    @Override
    public void showError(String message) {
        MessageDialog.error(i18n.tr("Registration Error"), message);
    }

    @Override
    public void showValidationError(EntityValidationException caught) {
        gadget.showValidationError(caught);
    }

}
