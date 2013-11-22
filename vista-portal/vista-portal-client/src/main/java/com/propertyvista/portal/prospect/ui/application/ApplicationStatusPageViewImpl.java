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
package com.propertyvista.portal.prospect.ui.application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class ApplicationStatusPageViewImpl extends FlowPanel implements ApplicationStatusPageView {

    private final static I18n i18n = I18n.get(ApplicationStatusPageViewImpl.class);

    private ApplicationStatusPagePresenter presenter;

    private final ApplicationStatusGadget statusGadget;

    private final ApplicationProgressGadget progressGadget;

    public ApplicationStatusPageViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        statusGadget = new ApplicationStatusGadget();
        add(statusGadget);

        progressGadget = new ApplicationProgressGadget();
        add(progressGadget);

    }

    @Override
    public void setPresenter(ApplicationStatusPagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(MasterOnlineApplicationStatus status) {

        switch (status.status().getValue()) {
        case Incomplete:
            //=================== 1.
            //"Your application progress is 55%. Click 'Continue Application' button below to complete your application."

            // Application progress

            // Button "Continue Application"

            //=================== 2.
            //Your application has been submitted. We are waiting for application submission from other co-applicants. 

            //Application progress

            //Click 'Send Status Update' below to resend application status update to the email address(es) we have on file.

            // Button "Send Status Update"

            break;
        case Submitted:
        case InformationRequested: // TODO for now information request will be processed and entered by CRM manually.

            //Your application is still pending. You will be contacted from
            // our office management team once the application has been processed.

            break;
        case Approved:

            // Your application has been reviewed and approved! 
            // Our office management team representative will contact you shortly to make move-in arrangements

            break;
        case Cancelled:

            // Your application has been reviewed and unfortunately has not met our move-in criteria. If you would like re-apply
            // with a Guarantor, please start a new application. Thank you.

            // Button "Restart Application"

            break;

        }

    }

    class ApplicationStatusGadget extends AbstractGadget<ApplicationStatusPageViewImpl> {

        ApplicationStatusGadget() {
            super(ApplicationStatusPageViewImpl.this, null, i18n.tr("Application Status"), ThemeColor.foreground, 0.3);

            setActionsToolbar(new ApplicationStatusToolbar());

        }

        class ApplicationStatusToolbar extends GadgetToolbar {
            public ApplicationStatusToolbar() {

                final Button restartApplicationButton = new Button("Restart Application", new Command() {

                    @Override
                    public void execute() {
                    }
                });

                restartApplicationButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.foreground, 0.4));
                addItem(restartApplicationButton);
            }
        }
    }

    class ApplicationProgressGadget extends AbstractGadget<ApplicationStatusPageViewImpl> {

        ApplicationProgressGadget() {
            super(ApplicationStatusPageViewImpl.this, null, i18n.tr("Application Progress"), ThemeColor.foreground, 0.3);

            setActionsToolbar(new ApplicationProgressToolbar());

        }

        class ApplicationProgressToolbar extends GadgetToolbar {
            public ApplicationProgressToolbar() {

                final Button sendUpdateButton = new Button("Send Status Update", new Command() {

                    @Override
                    public void execute() {
                    }
                });

                sendUpdateButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.foreground, 0.4));
                addItem(sendUpdateButton);
            }
        }
    }

}
