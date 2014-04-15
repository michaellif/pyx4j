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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.formatters.PercentageFormat;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationStatus;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.shared.themes.DashboardTheme;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class ApplicationStatusPageViewImpl extends FlowPanel implements ApplicationStatusPageView {

    private final static I18n i18n = I18n.get(ApplicationStatusPageViewImpl.class);

    private ApplicationStatusPagePresenter presenter;

    private final ApplicationStatusGadget statusGadget;

    private final ApplicationProgressGadget progressGadget;

    private Label messageLabel;

    private Button continueApplicationButton;

    private Button restartApplicationButton;

    private Button sendUpdateButton;

    public ApplicationStatusPageViewImpl() {
        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        statusGadget = new ApplicationStatusGadget();
        add(statusGadget);

        progressGadget = new ApplicationProgressGadget();
        progressGadget.setVisible(false);
        add(progressGadget);

    }

    @Override
    public void setPresenter(IViewerPresenter<MasterOnlineApplicationStatus> presenter) {
        this.presenter = (ApplicationStatusPagePresenter) presenter;
    }

    @Override
    public ApplicationStatusPagePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void populate(MasterOnlineApplicationStatus masterAppStatus) {

        continueApplicationButton.setVisible(false);
        restartApplicationButton.setVisible(false);
        sendUpdateButton.setVisible(false);
        progressGadget.setVisible(false);

        if (masterAppStatus == null) {
            progressGadget.populate(null);
            messageLabel.setText(null);

        } else {

            switch (masterAppStatus.status().getValue()) {
            case Incomplete:

                OnlineApplicationStatus userAppStatus = presenter.getUserApplicationStatus();

                switch (userAppStatus.status().getValue()) {
                case Incomplete:
                case Invited:
                    messageLabel.setText(i18n.tr("Your application progress is {0}. Click 'Continue Application' button below to continue your application.",
                            new PercentageFormat("#").format(userAppStatus.progress().getValue())));
                    continueApplicationButton.setVisible(true);
                    break;
                case Submitted:
                case InformationRequested:
                    messageLabel.setText(i18n.tr("Your application has been submitted. "
                            + "We are waiting for application submission from other co-applicants."));
                    break;
                }

                if (masterAppStatus.individualApplications().size() > 1) {
                    progressGadget.populate(masterAppStatus);
                    progressGadget.setVisible(true);
                }

                if (LeaseTermParticipant.Role.Applicant.equals(userAppStatus.role().getValue())) {
                    if (false) { //TODO VISTA-4301
                        sendUpdateButton.setVisible(true);
                    }
                }

                break;
            case Submitted:
            case InformationRequested: // TODO for now information request will be processed and entered by CRM manually.
                messageLabel.setText(i18n.tr("Your application is still pending. "
                        + "You will be contacted from our office management team once the application has been processed."));
                break;
            case Approved:
                messageLabel.setText(i18n.tr("Your application has been reviewed and approved! "
                        + "Our office management team representative will contact you shortly to make move-in arrangements."));
                break;
            case Cancelled:
                messageLabel.setText(i18n.tr("Your application has been reviewed and unfortunately has not met our move-in criteria. "
                        + "If you would like re-apply with a Guarantor, please start a new application. Thank you."));
                restartApplicationButton.setVisible(true);
                break;
            }
        }
    }

    @Override
    public void reset() {
        progressGadget.reset();
    }

    class ApplicationStatusGadget extends AbstractGadget<ApplicationStatusPageViewImpl> {

        ApplicationStatusGadget() {
            super(ApplicationStatusPageViewImpl.this, null, i18n.tr("Application Status"), ThemeColor.contrast2, 1);

            FlowPanel viewPanel = new FlowPanel();
            viewPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

            messageLabel = new Label();
            viewPanel.add(messageLabel);

            setContent(viewPanel);

            setActionsToolbar(new ApplicationStatusToolbar());

        }

        class ApplicationStatusToolbar extends GadgetToolbar {
            public ApplicationStatusToolbar() {

                continueApplicationButton = new Button("Continue Application", new Command() {

                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Application());
                    }
                });
                continueApplicationButton.setVisible(false);
                continueApplicationButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
                addItem(continueApplicationButton);

                restartApplicationButton = new Button("Restart Application", new Command() {

                    @Override
                    public void execute() {
                    }
                });
                restartApplicationButton.setVisible(false);
                restartApplicationButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
                addItem(restartApplicationButton);
            }
        }
    }

    class ApplicationProgressGadget extends AbstractGadget<ApplicationStatusPageViewImpl> {

        private final ApplicationProgressForm progressForm;

        ApplicationProgressGadget() {
            super(ApplicationStatusPageViewImpl.this, null, i18n.tr("Application Progress"), ThemeColor.contrast2, 1);

            progressForm = new ApplicationProgressForm();
            progressForm.setEditable(false);
            progressForm.initContent();
            setContent(progressForm);

            setActionsToolbar(new ApplicationProgressToolbar());

        }

        public void populate(MasterOnlineApplicationStatus masterAppStatus) {
            progressForm.populate(masterAppStatus);
        }

        public void reset() {
            progressForm.reset();
        }

        class ApplicationProgressToolbar extends GadgetToolbar {
            public ApplicationProgressToolbar() {

                sendUpdateButton = new Button("Send Status Update", new Command() {

                    @Override
                    public void execute() {
                    }
                });
                sendUpdateButton.setVisible(false);
                sendUpdateButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
                addItem(sendUpdateButton);
            }
        }

        class ApplicationProgressForm extends CEntityForm<MasterOnlineApplicationStatus> {

            public ApplicationProgressForm() {
                super(MasterOnlineApplicationStatus.class);
                setViewable(true);
            }

            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel main = new BasicFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, inject(proto().progress(), new FormWidgetDecoratorBuilder().build()));
                main.setBR(++row, 0, 1);
                main.setWidget(++row, 0, inject(proto().individualApplications(), new ApplicationStatusFolder()));

                return main;
            }

            class ApplicationStatusFolder extends VistaTableFolder<OnlineApplicationStatus> {

                public ApplicationStatusFolder() {
                    super(OnlineApplicationStatus.class, false);
                }

                @Override
                public List<EntityFolderColumnDescriptor> columns() {
                    ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<>();
                    columns.add(new EntityFolderColumnDescriptor(proto().customer(), "200px"));
                    columns.add(new EntityFolderColumnDescriptor(proto().progress(), "80px"));
                    return columns;
                }
            }
        }
    }
}
