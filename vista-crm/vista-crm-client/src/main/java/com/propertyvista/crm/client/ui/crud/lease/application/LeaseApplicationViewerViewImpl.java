/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;
import com.pyx4j.widgets.client.dialog.YesNoOption;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.crm.client.ui.components.boxes.ReasonBox;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewBase;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewImplBase;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO.Action;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationDocumentUploadService;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionApprove;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionDecline;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationDecisionMoreInfo;
import com.propertyvista.crm.rpc.services.lease.ac.CreditCheckRun;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lease.LeaseApplication.Status;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationViewerViewImpl extends LeaseViewerViewImplBase<LeaseApplicationDTO> implements LeaseApplicationViewerView {

    private static final I18n i18n = I18n.get(LeaseApplicationViewerViewImpl.class);

    private final Button editButton;

    private final Button documentsButton;

    private final MenuItem viewLease;

    private final MenuItem createOnlineApplication;

    private final MenuItem cancelOnlineApplication;

    private final MenuItem inviteAction;

    private final MenuItem creditCheckAction;

    private final MenuItem approveAction;

    private final MenuItem moreInfoAction;

    private final MenuItem declineAction;

    private final MenuItem cancelAction;

    private static final String INVITE = i18n.tr("Invite");

    private static final String APPROVE = i18n.tr("Approve");

    private static final String MORE_INFO = i18n.tr("More Info");

    private static final String DECLINE = i18n.tr("Decline");

    private static final String CANCEL = i18n.tr("Cancel");

    public LeaseApplicationViewerViewImpl() {
        setForm(new LeaseApplicationForm(this));

        // Buttons:

        documentsButton = new Button(i18n.tr("Documents"));
        ButtonMenuBar applicationDocumentMenu = new ButtonMenuBar();

        applicationDocumentMenu.addItem(new MenuItem(i18n.tr("Manually Sign Application..."), new Command() {
            @Override
            public void execute() {
                downloadApplicationDocument();
            }
        }));

        applicationDocumentMenu.addItem(new MenuItem(i18n.tr("Upload Signed Application..."), new Command() {
            @Override
            public void execute() {
                uploadApplicationDocument();
            }
        }));

        applicationDocumentMenu.addItem(new MenuItem(i18n.tr("Download Draft Lease Agreement"), new Command() {
            @Override
            public void execute() {
                ((LeaseApplicationViewerView.Presenter) getPresenter()).downloadDraftLeaseAgreement();
            }
        }));

        documentsButton.setMenu(applicationDocumentMenu);
        addHeaderToolbarItem(documentsButton.asWidget());

        // ------------------------------------------------------------------------------------------------------------

        editButton = new Button(i18n.tr("Edit"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).editTerm(getForm().getValue().currentTerm());
            }
        });
        addHeaderToolbarItem(editButton.asWidget());

        // Views:
        viewLease = new MenuItem(i18n.tr("View Lease"), new Command() {
            @Override
            public void execute() {
                ((LeaseApplicationViewerView.Presenter) getPresenter()).viewLease();
            }
        });
        addView(viewLease);

        // Actions:

        createOnlineApplication = new MenuItem(i18n.tr("Start Online Application"), new Command() {
            @Override
            public void execute() {
                ((LeaseApplicationViewerView.Presenter) getPresenter()).startOnlineApplication();
            }
        });
        if (VistaFeatures.instance().onlineApplication()) {
            addAction(createOnlineApplication);
        }

        cancelOnlineApplication = new MenuItem(i18n.tr("Cancel Online Application"), new Command() {
            @Override
            public void execute() {
                ((LeaseApplicationViewerView.Presenter) getPresenter()).cancelOnlineApplication();
            }
        });
        if (VistaFeatures.instance().onlineApplication()) {
            addAction(cancelOnlineApplication);
        }

        inviteAction = new MenuItem(INVITE, new Command() {
            @Override
            public void execute() {
                inviteActionExecuter();
            }
        });
        if (VistaFeatures.instance().onlineApplication()) {
            addAction(inviteAction);
        }

        creditCheckAction = new MenuItem(i18n.tr("Credit Check"), new Command() {
            @Override
            public void execute() {
                checkActionExecuter();
            }
        });
        if (!VistaTODO.Equifax_Off_VISTA_478 && VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            addAction(creditCheckAction, new ActionPermission(CreditCheckRun.class));
        }

        // TODO Move Lease Action

        approveAction = new MenuItem(APPROVE, new Command() {
            @Override
            public void execute() {
                approveActionExecuter();
            }
        });
        addAction(approveAction, new ActionPermission(ApplicationDecisionApprove.class));

        moreInfoAction = new MenuItem(MORE_INFO, new Command() {
            @Override
            public void execute() {
                moreInfoActionExecuter();
            }
        });
        if (!VistaTODO.VISTA_4484_Action_More_Info_should_be_hidden_as_not_fully_implemented) {
            addAction(moreInfoAction, new ActionPermission(ApplicationDecisionMoreInfo.class));
        }

        declineAction = new MenuItem(DECLINE, new Command() {
            @Override
            public void execute() {
                declineActionExecuter();
            }
        });
        addAction(declineAction, new ActionPermission(ApplicationDecisionDecline.class));

        cancelAction = new MenuItem(CANCEL, new Command() {
            @Override
            public void execute() {
                cancelActionExecuter();
            }
        });
        addAction(cancelAction);
    }

    private void inviteActionExecuter() {
        ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseTermParticipant<?>>>() {
            @Override
            public void onSuccess(List<LeaseTermParticipant<?>> result) {
                new EntitySelectorListDialog<LeaseTermParticipant<?>>(i18n.tr("Select Tenants/Guarantors To Send An Invitation To"), true, result) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseApplicationViewerView.Presenter) getPresenter()).inviteUsers(getSelectedItems());
                        return true;
                    }
                }.show();
            }
        });
    }

    private void checkActionExecuter() {
        ((LeaseApplicationViewerView.Presenter) getPresenter()).getCreditCheckServiceStatus(new DefaultAsyncCallback<PmcEquifaxStatus>() {
            @Override
            public void onSuccess(PmcEquifaxStatus result) {
                switch (result) {
                case Active:
                    ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseTermParticipant<?>>>() {
                        @Override
                        public void onSuccess(List<LeaseTermParticipant<?>> result) {
                            new EntitySelectorListDialog<LeaseTermParticipant<?>>(i18n.tr("Select Tenants/Guarantors To Check"), true, result) {
                                @Override
                                public boolean onClickOk() {
                                    ((LeaseApplicationViewerView.Presenter) getPresenter()).creditCheck(getSelectedItems());
                                    return true;
                                }
                            }.show();
                        }
                    });
                    break;
                case NotRequested:
                    if (SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaAccountOwner_OLD)) {
                        new CreditCheckSubscribeDialog().show();
                    } else {
                        reportCreditCheckServiceInactive();
                    }
                    break;
                default:
                    reportCreditCheckServiceInactive();
                }
            }
        });
    }

    private void approveActionExecuter() {
        new ActionBox(APPROVE) {
            @Override
            public boolean onClickOk() {
                ((LeaseApplicationViewerView.Presenter) getPresenter()).applicationAction(updateValue(Action.Approve));
                return true;
            }
        }.show();
    }

    private void moreInfoActionExecuter() {
        ((LeaseViewerViewBase.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseTermParticipant<?>>>() {
            @Override
            public void onSuccess(List<LeaseTermParticipant<?>> result) {
                new EntitySelectorListDialog<LeaseTermParticipant<?>>(i18n.tr("Select Tenants/Guarantors To Acquire Info"), true, result) {

                    @Override
                    public boolean onClickOk() {
                        // TODO make the credit check happen
                        return true;
                    }
                }.show();
            }
        });
    }

    private void declineActionExecuter() {
        new ActionBox(DECLINE) {
            @Override
            public boolean onClickOk() {
                ((LeaseApplicationViewerView.Presenter) getPresenter()).applicationAction(updateValue(Action.Decline));
                return true;
            }
        }.show();
    }

    private void cancelActionExecuter() {
        new ActionBox(CANCEL) {
            @Override
            public boolean onClickOk() {
                if (CommonsStringUtils.isEmpty(getReason())) {
                    MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                    return false;
                }
                ((LeaseApplicationViewerView.Presenter) getPresenter()).applicationAction(updateValue(Action.Cancel));
                return true;
            }
        }.show();
    }

    @Override
    public void reset() {
        setViewVisible(viewLease, false);

        setActionVisible(createOnlineApplication, false);
        setActionVisible(cancelOnlineApplication, false);
        setActionVisible(inviteAction, false);
        setActionVisible(creditCheckAction, false);
        setActionVisible(approveAction, false);
        setActionVisible(moreInfoAction, false);
        setActionVisible(declineAction, false);
        setActionVisible(cancelAction, false);

        editButton.setVisible(false);
        documentsButton.setVisible(false);

        super.reset();
    }

    @Override
    public void populate(LeaseApplicationDTO value) {
        super.populate(value);

        Status status = value.leaseApplication().status().getValue();

        // set buttons state:
        setViewVisible(viewLease, status.isCurrent());

        setActionVisible(createOnlineApplication, status == Status.Created);
        setActionVisible(cancelOnlineApplication, status == Status.OnlineApplication);
        setActionVisible(inviteAction, status == Status.OnlineApplication);
        setActionVisible(creditCheckAction, status.isDraft());
        setActionVisible(approveAction, status.isDraft());
        setActionVisible(moreInfoAction, status.isDraft() && status != Status.Created);
        setActionVisible(declineAction, status.isDraft());
        setActionVisible(cancelAction, status.isDraft());

        setActionVisible(newPaymentAction, status.isDraft() && isPaymentAccepted(value));

        // edit/view terms enabling logic:
        BigDecimal progress = (value.masterApplicationStatus().progress().isNull() ? BigDecimal.ZERO : value.masterApplicationStatus().progress().getValue());
        editButton.setVisible(status.isDraft() && progress.compareTo(BigDecimal.ZERO) == 0);
        termsButton.setVisible(!status.isDraft());

        documentsButton.setVisible(status.isDraft());
    }

    private abstract class ActionBox extends ReasonBox {

        public ActionBox(String title) {
            super(title);
        }

        public LeaseApplicationActionDTO updateValue(Action status) {
            LeaseApplicationActionDTO action = EntityFactory.create(LeaseApplicationActionDTO.class);
            action.leaseId().set(getForm().getValue().createIdentityStub());
            action.decisionReason().setValue(getReason());
            action.action().setValue(status);
            return action;
        }
    }

    @Override
    public void reportStartOnlineApplicationSuccess() {
        MessageDialog.info(i18n.tr("Started Online Application"));
    }

    @Override
    public void reportCancelOnlineApplicationSuccess() {
        MessageDialog.info(i18n.tr("Online Application has been canceled"));
    }

    @Override
    public void reportInviteUsersActionResult(String message) {
        MessageDialog.info(message);
    }

    @Override
    public void reportInviteUsersActionFailure(String message) {
        MessageDialog.error(i18n.tr("Invitation Failed"), message);
    }

    @Override
    public void reportCreditCheckActionResult(String message) {
        MessageDialog.info(message);
    }

    public void reportCreditCheckServiceInactive() {
        MessageDialog.info(i18n.tr("No credit check service for this account has been set activated."));
    }

    private void uploadApplicationDocument() {
        new UploadApplicationDocumentDialog(getParticipants()).show();
    }

    private void downloadApplicationDocument() {
        new DownloadApplicationDocumentDialog(getParticipants()).show();
    }

    private List<LeaseTermParticipant<?>> getParticipants() {
        List<LeaseTermParticipant<?>> leaseTermPariticipants = new LinkedList<>();
        for (LeaseTermTenant leaseTermTenant : getForm().getValue().currentTerm().version().tenants()) {
            leaseTermPariticipants.add(leaseTermTenant);
        }
        for (LeaseTermGuarantor leaseTermGuarantor : getForm().getValue().currentTerm().version().guarantors()) {
            leaseTermPariticipants.add(leaseTermGuarantor);
        }
        return leaseTermPariticipants;
    }

    private class CreditCheckSubscribeDialog extends Dialog implements YesNoOption {

        public CreditCheckSubscribeDialog() {
            super(i18n.tr("Credit Check"));
            setBody(new HTML(i18n.tr("No credit check service for this account has been set up.") + "<br/>" + i18n.tr("Do you want to apply now?")));
            setDialogOptions(this);
        }

        @Override
        public boolean onClickYes() {
            // TODO : go to credit check application...
            return true;
        }

        @Override
        public boolean onClickNo() {
            return true;
        }
    }

    private class DownloadApplicationDocumentDialog extends OkCancelDialog {

        private final SelectParticipantForm form;

        public DownloadApplicationDocumentDialog(List<LeaseTermParticipant<?>> participants) {
            super(i18n.tr("Download Blank Application Document "));
            form = new SelectParticipantForm(participants);
            form.init();
            form.populateNew();
            setBody(form);
        }

        @Override
        public boolean onClickOk() {
            form.setVisitedRecursive();
            form.revalidate();
            if (form.isValid()) {
                ((LeaseApplicationViewerView.Presenter) LeaseApplicationViewerViewImpl.this.getPresenter()).downloadBlankLeaseApplicationDocument(form
                        .getValue().selectParticipant());
                return true;
            } else {
                return false;
            }
        }

    }

    @Transient
    public interface SelectParticipant extends IEntity {
        LeaseTermParticipant<?> selectParticipant();
    }

    private static class SelectParticipantForm extends CForm<SelectParticipant> {

        private CComboBox<LeaseTermParticipant<?>> selectCombo;

        public SelectParticipantForm(List<LeaseTermParticipant<?>> participants) {
            super(SelectParticipant.class);
            selectCombo = new CComboBox<LeaseTermParticipant<?>>() {
                @Override
                public String getItemName(LeaseTermParticipant<?> o) {
                    return (o != null) ? formatLeaseParticipant(o) : super.getItemName(o);
                }
            };
            selectCombo.setOptions(participants);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Left, proto().selectParticipant(), selectCombo).decorate();
            return formPanel;
        }

    }

    private class UploadApplicationDocumentDialog extends OkCancelDialog {

        private final LeaseApplicationDocumentUploadForm form;

        public UploadApplicationDocumentDialog(List<LeaseTermParticipant<?>> signerOptions) {
            super(i18n.tr("Upload Application Document"));
            form = new LeaseApplicationDocumentUploadForm(signerOptions);
            form.init();
            form.populateNew();
            setBody(form);
        }

        @Override
        public boolean onClickOk() {
            form.setVisitedRecursive();
            form.revalidate();
            if (form.isValid()) {
                ((LeaseApplicationViewerView.Presenter) LeaseApplicationViewerViewImpl.this.getPresenter()).saveLeaseApplicationDocument(form.getValue());
                return true;
            } else {
                return false;
            }
        }
    }

    private static class LeaseApplicationDocumentUploadForm extends CForm<LeaseApplicationDocument> {

        private CComboBox<LeaseTermParticipant<?>> signedByCombo;

        public LeaseApplicationDocumentUploadForm(List<LeaseTermParticipant<?>> signerOptions) {
            super(LeaseApplicationDocument.class);
            signedByCombo = new CComboBox<LeaseTermParticipant<?>>() {
                @Override
                public String getItemName(LeaseTermParticipant<?> o) {
                    return (o != null) ? formatLeaseParticipant(o) : super.getItemName(o);
                }
            };
            signedByCombo.setOptions(signerOptions);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel
                    .append(Location.Left,
                            proto().file(),
                            new CFile(GWT.<UploadService<?, ?>> create(LeaseApplicationDocumentUploadService.class), new VistaFileURLBuilder(
                                    LeaseApplicationDocument.class))).decorate().customLabel(i18n.tr("Agreement Document File"));
            formPanel.append(Location.Left, proto().signedBy(), signedByCombo).decorate();
            return formPanel;
        }

        @Override
        public void addValidations() {
            super.addValidations();
            get(proto().file()).setMandatory(true);
        }
    }

    private static String formatLeaseParticipant(LeaseTermParticipant<?> participant) {
        return !participant.isNull() ? participant.leaseParticipant().customer().person().name().getStringView() + " ("
                + participant.role().getValue().toString() + ")" : "";
    }

}