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
package com.propertyvista.crm.client.ui.crud.lease;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.essentials.client.ConfirmCommand;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.validators.DateInPeriodValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.components.boxes.LeaseTermSelectorDialog;
import com.propertyvista.crm.client.ui.components.boxes.ReasonBox;
import com.propertyvista.crm.client.ui.crud.billing.adjustments.LeaseAdjustmentLister;
import com.propertyvista.crm.client.ui.crud.billing.bill.BillLister;
import com.propertyvista.crm.client.ui.crud.lease.common.LeaseViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.common.deposit.DepositLifecycleLister;
import com.propertyvista.crm.client.ui.crud.lease.common.dialogs.N4GenerationQueryDialog;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.services.lease.ac.SendMail;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseViewerViewImpl extends LeaseViewerViewImplBase<LeaseDTO> implements LeaseViewerView {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    private final ILister<DepositLifecycleDTO> depositLister;

    private final ILister<BillDataDTO> billLister;

    private final ILister<LeaseAdjustment> adjustmentLister;

    private final ILister<MaintenanceRequestDTO> maintenanceLister;

    private final Button leaseAgreementButton;

    private final MenuItem viewApplication;

    private final MenuItem sendMailAction;

    private final MenuItem runBillAction;

    private final MenuItem noticeAction;

    private final MenuItem cancelNoticeAction;

    private final MenuItem evictAction;

    private final MenuItem cancelEvictAction;

    private final MenuItem terminateAction;

    private final MenuItem cancelTerminateAction;

    private final MenuItem activateAction;

    private final MenuItem moveOutAction;

    private final MenuItem closeAction;

    private final MenuItem cancelAction;

    private final MenuItem renewAction;

    private Button renewButton;

    private MenuItem offerAction;

    private MenuItem viewOfferedTerms;

    private final MenuItem maintenanceAction;

    private final MenuItem yardiImportAction;

    public LeaseViewerViewImpl() {
        depositLister = new ListerInternalViewImplBase<DepositLifecycleDTO>(new DepositLifecycleLister());
        billLister = new ListerInternalViewImplBase<BillDataDTO>(new BillLister());
        adjustmentLister = new ListerInternalViewImplBase<LeaseAdjustment>(new LeaseAdjustmentLister());
        maintenanceLister = new ListerInternalViewImplBase<MaintenanceRequestDTO>(new MaintenanceRequestLister());

        // set main form here:
        setForm(new LeaseForm(this) {
            @Override
            public void onTenantInsuranceOwnerClicked(Tenant tenantId) {
                ((LeaseViewerView.Presenter) getPresenter()).onInsuredTenantClicked(tenantId);
            }
        });

        // Buttons:

        leaseAgreementButton = new Button(i18n.tr("Lease Agreement"));
        ButtonMenuBar leaseAgreementDocumentMenu = new ButtonMenuBar();

        leaseAgreementDocumentMenu.addItem(new MenuItem(i18n.tr("Signing Progress/Upload..."), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).signingProgressOrUploadAgreement();
            }
        }));

        leaseAgreementDocumentMenu.addSeparator();

        leaseAgreementDocumentMenu.addItem(new MenuItem(i18n.tr("Download for Signing"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).downloadAgreementForSigning();
            }
        }));

        leaseAgreementButton.setMenu(leaseAgreementDocumentMenu);
        addHeaderToolbarItem(leaseAgreementButton);

        // Views:

        addView(viewApplication = new MenuItem(i18n.tr("View Application"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).viewApplication();
            }
        }));

        addView(new MenuItem(i18n.tr("View Legal State"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).legalState();
            }
        }));

        addView(new MenuItem(i18n.tr("View Deleted AutoPays"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).viewDeletedPaps(null);
            }
        }));

        // Actions:

        addAction(sendMailAction = new MenuItem(i18n.tr("Send Mail..."), new Command() {
            @Override
            public void execute() {
                sendMailActionExecuter();
            }
        }), new ActionPermission(SendMail.class));

        runBillAction = new MenuItem(i18n.tr("Run Bill"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).startBilling();
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(runBillAction);
        }

        addAction(maintenanceAction = new MenuItem(i18n.tr("Create Maintenance Request"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).createMaintenanceRequest();
            }
        }), DataModelPermission.permissionCreate(MaintenanceRequestDTO.class));

        noticeAction = new MenuItem(i18n.tr("Notice..."), new Command() {
            @Override
            public void execute() {
                noticeActionExecuter();
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(noticeAction);
        }

        cancelNoticeAction = new MenuItem(i18n.tr("Cancel Notice"), new Command() {
            @Override
            public void execute() {
                cancelCmpletion(CompletionType.Notice);
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(cancelNoticeAction);
        }

        evictAction = new MenuItem(i18n.tr("Evict..."), new Command() {
            @Override
            public void execute() {
                evictActionExecuter();
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(evictAction);
        }

        cancelEvictAction = new MenuItem(i18n.tr("Cancel Eviction"), new Command() {
            @Override
            public void execute() {
                cancelCmpletion(CompletionType.Eviction);
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(cancelEvictAction);
        }

        terminateAction = new MenuItem(i18n.tr("Terminate..."), new Command() {
            @Override
            public void execute() {
                terminateActionExecuter();
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(terminateAction);
        }

        cancelTerminateAction = new MenuItem(i18n.tr("Cancel Termination"), new Command() {
            @Override
            public void execute() {
                cancelCmpletion(CompletionType.Termination);
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(cancelTerminateAction);
        }

        yardiImportAction = new MenuItem(i18n.tr("Update From Yardi"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).updateFromYardi();
            }
        });
        if (VistaFeatures.instance().yardiIntegration()) {
            addAction(yardiImportAction);
        }

        activateAction = new MenuItem(i18n.tr("Activate"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerView.Presenter) getPresenter()).activate();
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(activateAction);
        }

        moveOutAction = new MenuItem(i18n.tr("Move Out"), new ConfirmCommand(i18n.tr("Move Out"),
                i18n.tr("This will make this unit available to other leases (Tenant has brought the key).") + "<br/><br/>"
                        + i18n.tr("Are you sure you would like to continue?"), new Command() {
                    @Override
                    public void execute() {
                        ((LeaseViewerView.Presenter) getPresenter()).moveOut();
                    }
                }));
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(moveOutAction);
        }

        closeAction = new MenuItem(i18n.tr("Close"), new Command() {
            @Override
            public void execute() {
                closeActionExecuter();
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(closeAction);
        }

        cancelAction = new MenuItem(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                cancelActionExecuter();
            }
        });
        if (!VistaFeatures.instance().yardiIntegration()) {
            addAction(cancelAction);
        }

        // Renewing stuff : ---------------------------------------------------

        if (VistaTODO.VISTA_1789_Renew_Lease) {
            renewButton = new Button(i18n.tr("Renew"));
            Button.ButtonMenuBar renewMenu = renewButton.createMenu();

            offerAction = new MenuItem(i18n.tr("Create Offer"), new Command() {
                @Override
                public void execute() {
                    offerActionExecuter();
                }
            });
            renewMenu.addItem(offerAction);

            viewOfferedTerms = new MenuItem(i18n.tr("View Offers..."), new Command() {
                @Override
                public void execute() {
                    viewOfferedTermsExecuter();
                }
            });
            renewMenu.addItem(viewOfferedTerms);

            renewButton.setMenu(renewMenu);
            if (!VistaFeatures.instance().yardiIntegration()) {
                addHeaderToolbarItem(renewButton.asWidget());
            }

        } else if (VistaTODO.VISTA_2242_Simple_Lease_Renewal) {

            renewAction = new MenuItem(i18n.tr("Renew"), new Command() {
                @Override
                public void execute() {
                    renewActionExecuter();
                }
            });
            if (!VistaFeatures.instance().yardiIntegration()) {
                addAction(renewAction);
            }
        }
    }

    private void sendMailActionExecuter() {
        ((LeaseViewerView.Presenter) getPresenter()).retrieveUsers(new DefaultAsyncCallback<List<LeaseTermParticipant<?>>>() {
            @Override
            public void onSuccess(List<LeaseTermParticipant<?>> result) {
                new SendMailBox(result) {
                    @Override
                    public boolean onClickOk() {
                        ((LeaseViewerView.Presenter) getPresenter()).sendMail(getSelectedItems(), getEmailType());
                        return true;
                    }
                }.show();
            }
        });
    }

    private void noticeActionExecuter() {
        new TermLeaseBox(CompletionType.Notice) {
            @Override
            public boolean onClickOk() {
                if (!isValid()) {
                    return false;
                }
                ((LeaseViewerView.Presenter) getPresenter()).createCompletionEvent(CompletionType.Notice, getValue().moveOutSubmissionDate().getValue(),
                        getValue().expectedMoveOut().getValue(), null);
                return true;
            }
        }.show();
    }

    private void evictActionExecuter() {
        new TermLeaseBox(CompletionType.Eviction) {
            @Override
            public boolean onClickOk() {
                if (!isValid()) {
                    return false;
                }
                ((LeaseViewerView.Presenter) getPresenter()).createCompletionEvent(CompletionType.Eviction, getValue().moveOutSubmissionDate().getValue(),
                        getValue().expectedMoveOut().getValue(), null);
                return true;
            }
        }.show();
    }

    private void terminateActionExecuter() {
        new TermLeaseBox(CompletionType.Termination) {
            @Override
            public boolean onClickOk() {
                if (!isValid()) {
                    return false;
                }
                ((LeaseViewerView.Presenter) getPresenter()).createCompletionEvent(CompletionType.Termination, getValue().moveOutSubmissionDate().getValue(),
                        getValue().expectedMoveOut().getValue(), getValue().terminationLeaseTo().getValue());
                return true;
            }
        }.show();
    }

    private void closeActionExecuter() {
        new ReasonBox(i18n.tr("Close Lease")) {
            @Override
            public boolean onClickOk() {
                if (CommonsStringUtils.isEmpty(getReason())) {
                    MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                    return false;
                }
                ((LeaseViewerView.Presenter) getPresenter()).closeLease(getReason());
                return true;
            }
        }.show();
    }

    private void cancelActionExecuter() {
        new ReasonBox(i18n.tr("Cancel Lease")) {
            @Override
            public boolean onClickOk() {
                if (CommonsStringUtils.isEmpty(getReason())) {
                    MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                    return false;
                }
                ((LeaseViewerView.Presenter) getPresenter()).cancelLease(getReason());
                return true;
            }
        }.show();
    }

    private void offerActionExecuter() {
        new SelectEnumDialog<LeaseTerm.Type>(i18n.tr("Select Term Type"), LeaseTerm.Type.renew()) {
            @Override
            public boolean onClickOk() {
                ((LeaseViewerView.Presenter) getPresenter()).createOffer(getSelectedType());
                return true;
            }
        }.show();
    }

    private void viewOfferedTermsExecuter() {
        new LeaseTermSelectorDialog(LeaseViewerViewImpl.this) {
            {
                setParentFiltering(getForm().getValue().getPrimaryKey());
                addFilter(PropertyCriterion.eq(proto().status(), LeaseTerm.Status.Offer));
            }

            @Override
            public void onClickOk() {
                if (!getSelectedItem().isNull()) {
                    ((LeaseViewerView.Presenter) getPresenter()).viewTerm(getSelectedItem());
                }
            }
        }.show();
    }

    private void renewActionExecuter() {
        new RenewLeaseBox() {
            @Override
            public boolean onClickOk() {
                LogicalDate newDate = getEndLeaseDate();

                if (newDate == null) {
                    MessageDialog.error(i18n.tr("Error"), i18n.tr("Please select the date!"));
                    return false;
                }
                if (!getForm().getValue().currentTerm().termTo().getValue().before(newDate)) {
                    MessageDialog.error(i18n.tr("Error"), i18n.tr("New Lease End date should be greater then current one!"));
                    return false;
                }

                ((LeaseViewerView.Presenter) getPresenter()).simpleLeaseRenew(newDate);
                return true;
            }
        }.show();
    }

    @Override
    public void reset() {
        setViewVisible(viewApplication, false);

        setActionVisible(sendMailAction, false);
        setActionVisible(runBillAction, false);

        setActionVisible(maintenanceAction, false);

        setActionVisible(noticeAction, false);
        setActionVisible(cancelNoticeAction, false);
        setActionVisible(evictAction, false);
        setActionVisible(cancelEvictAction, false);
        setActionVisible(terminateAction, false);
        setActionVisible(cancelTerminateAction, false);
        setActionVisible(activateAction, false);
        setActionVisible(moveOutAction, false);
        setActionVisible(closeAction, false);
        setActionVisible(cancelAction, false);

        leaseAgreementButton.setVisible(false);

        super.reset();
    }

    @Override
    public void populate(LeaseDTO value) {
        super.populate(value);

        Lease.Status status = value.status().getValue();

        // set buttons state:
        CompletionType completion = value.completion().getValue();

        setViewVisible(viewApplication, !value.leaseApplication().isNull());

        setActionVisible(sendMailAction, status.isCurrent());
        setActionVisible(runBillAction, status.isCurrent());

        setActionVisible(maintenanceAction, !status.isFormer());

        setActionVisible(noticeAction, status == Status.Active && completion == null);
        setActionVisible(cancelNoticeAction, completion == CompletionType.Notice && value.actualMoveOut().isNull() && !status.isFormer());

        setActionVisible(evictAction, status == Status.Active && completion == null);
        setActionVisible(cancelEvictAction, completion == CompletionType.Eviction && value.actualMoveOut().isNull() && !status.isFormer());

        setActionVisible(terminateAction, status == Status.Active && completion == null);
        setActionVisible(cancelTerminateAction, completion == CompletionType.Termination && value.actualMoveOut().isNull() && !status.isFormer());

        setActionVisible(moveOutAction, status.isOperative() && completion != null && value.actualMoveOut().isNull());
        setActionHighlighted(moveOutAction, status.isOperative() && completion != null && value.actualMoveOut().isNull());

        boolean isTimeOver = (!value.leaseTo().isNull() && value.leaseTo().getValue().before(new LogicalDate(ClientContext.getServerDate())));
        setActionVisible(activateAction, EnumSet.of(Status.NewLease, Status.ExistingLease).contains(status) && !isTimeOver);
        setActionHighlighted(activateAction, activateAction.isVisible());

        setActionVisible(closeAction, status == Status.Completed);
        setActionVisible(cancelAction, status.isDraft() || status == Status.Approved);

        setActionVisible(newPaymentAction, !status.isFormer() && isPaymentAccepted(value));

        if (VistaTODO.VISTA_1789_Renew_Lease) {
            renewButton.setVisible(status == Status.Active && completion == null && value.nextTerm().isNull());
        } else if (VistaTODO.VISTA_2242_Simple_Lease_Renewal) {
            setActionVisible(renewAction, status == Status.Active && completion == null);
        }

        leaseAgreementButton.setVisible(status.isDraft());
    }

    @Override
    public ILister<DepositLifecycleDTO> getDepositListerView() {
        return depositLister;
    }

    @Override
    public ILister<BillDataDTO> getBillListerView() {
        return billLister;
    }

    @Override
    public ILister<LeaseAdjustment> getLeaseAdjustmentListerView() {
        return adjustmentLister;
    }

    @Override
    public ILister<MaintenanceRequestDTO> getMaintenanceListerView() {
        return maintenanceLister;
    }

    @Override
    public void reportSendMailActionResult(String message) {
        MessageDialog.info(message);
    }

    @Override
    public void reportCancelNoticeFailed(UserRuntimeException caught) {
        MessageDialog.error(i18n.tr("Cancel Notice Failed"), caught.getMessage());
    }

    private abstract class TermLeaseBox extends OkCancelDialog {

        private final CompletionType action;

        private final boolean showTermination;

        private CForm<LeaseDTO> content;

        public TermLeaseBox(CompletionType action) {
            super(i18n.tr("Please fill"));

            this.action = action;
            this.showTermination = (action == CompletionType.Termination);

            setBody(createBody());
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            content = new CForm<LeaseDTO>(LeaseDTO.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Left, proto().moveOutSubmissionDate()).decorate().componentWidth(120)
                            .customLabel(action.toString() + i18n.tr(" Submission Date"));
                    formPanel.append(Location.Left, proto().expectedMoveOut()).decorate().componentWidth(120);

                    if (showTermination) {
                        formPanel.append(Location.Left, proto().terminationLeaseTo()).decorate().componentWidth(120)
                                .customLabel(i18n.tr("Lease Termination Date"));
                    }

                    // just for validation purpose:
                    inject(proto().currentTerm().termFrom());
                    inject(proto().currentTerm().termTo());

                    return formPanel;
                }

                @Override
                protected void onValueSet(boolean populate) {
                    super.onValueSet(populate);

                    if (getValue().moveOutSubmissionDate().isNull()) {
                        get(proto().moveOutSubmissionDate()).setValue(new LogicalDate(ClientContext.getServerDate()));
                    }
                    if (getValue().expectedMoveOut().isNull()) {
                        get(proto().expectedMoveOut()).setValue(new LogicalDate(ClientContext.getServerDate()));
                    }
                    if (showTermination) {
                        if (getValue().terminationLeaseTo().isNull()) {
                            get(proto().terminationLeaseTo()).setValue(getValue().currentTerm().termTo().getValue());
                        }
                    }
                }

                @Override
                public void addValidations() {
                    super.addValidations();

                    new DateInPeriodValidation(get(proto().currentTerm().termFrom()), get(proto().moveOutSubmissionDate()),
                            get(proto().currentTerm().termTo()), i18n.tr("The Date Should Be Within The Lease Period"));

                    new DateInPeriodValidation(get(proto().currentTerm().termFrom()), get(proto().expectedMoveOut()), get(proto().currentTerm().termTo()),
                            i18n.tr("The Date Should Be Within The Lease Period"));

                    new StartEndDateValidation(get(proto().moveOutSubmissionDate()), get(proto().expectedMoveOut()),
                            i18n.tr("The Notice Date Must Be Earlier Than The Expected Move Out date"));

                    get(proto().moveOutSubmissionDate()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveOut())));
                    get(proto().expectedMoveOut()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().moveOutSubmissionDate())));

                    if (showTermination) {
                        new DateInPeriodValidation(get(proto().currentTerm().termFrom()), get(proto().terminationLeaseTo()),
                                get(proto().currentTerm().termTo()), i18n.tr("The Date Should Be Within The Lease Period"));

                        new StartEndDateValidation(get(proto().expectedMoveOut()), get(proto().terminationLeaseTo()),
                                i18n.tr("The Terminationi Date Can't Be Earlier Than The Expected Move Out date"));

                        get(proto().terminationLeaseTo()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(get(proto().expectedMoveOut())));
                    }
                }
            };

            content.init();
            content.populate((LeaseDTO) getForm().getValue().duplicate());
            return content.asWidget();
        }

        public boolean isValid() {
            return content.isValid();
        }

        public LeaseDTO getValue() {
            return content.getValue();
        }
    }

    private abstract class SendMailBox extends EntitySelectorListDialog<LeaseTermParticipant<?>> {

        private final List<LeaseTermParticipant<?>> tenants = new ArrayList<LeaseTermParticipant<?>>();

        private final List<LeaseTermParticipant<?>> guarantors = new ArrayList<LeaseTermParticipant<?>>();

        private CComboBox<EmailTemplateType> emailType;

        public SendMailBox(List<LeaseTermParticipant<?>> participants) {
            super(i18n.tr("Send Mail"), true, Arrays.<LeaseTermParticipant<?>> asList());

            // Classify participants:
            for (LeaseTermParticipant<?> participant : participants) {
                if (participant.isInstanceOf(LeaseTermTenant.class)) {
                    tenants.add(participant);
                } else if (participant.isInstanceOf(LeaseTermGuarantor.class)) {
                    guarantors.add(participant);
                }
            }

            getOkButton().setTextLabel(i18n.tr("Send"));

            if (emailType.getOptions().size() == 1) {
                emailType.setValue(emailType.getOptions().get(0), true);
                emailType.setEditable(false);
            }
        }

        @Override
        protected Widget initBody(boolean isMultiselectAllowed, List<LeaseTermParticipant<?>> data) {
            VerticalPanel body = new VerticalPanel();

            body.add(new HTML(i18n.tr("Email Type:")));
            body.add(initEmailTypes(Arrays.asList(EmailTemplateType.TenantInvitation)));

            body.add(new HTML("&nbsp"));

            body.add(new HTML(i18n.tr("Recipient(s):")));
            body.add(super.initBody(isMultiselectAllowed, data));

            body.setSpacing(4);
            body.setWidth("100%");
            return body;
        }

        private Widget initEmailTypes(Collection<EmailTemplateType> opt) {
            emailType = new CComboBox<EmailTemplateType>();
            emailType.setOptions(opt);
            emailType.addValueChangeHandler(new ValueChangeHandler<EmailTemplateType>() {
                @Override
                public void onValueChange(ValueChangeEvent<EmailTemplateType> event) {
                    /*
                     * Update recipients list according to selected email type:
                     */
                    switch (event.getValue()) {
                    case TenantInvitation:
                        setData(tenants);
                        break;
                    default:
                        setData(Arrays.<LeaseTermParticipant<?>> asList());
                        break;
                    }
                }
            });
            emailType.setMandatory(true);
            return emailType.asWidget();
        }

        protected EmailTemplateType getEmailType() {
            return emailType.getValue();
        }
    }

    public void cancelCmpletion(final Lease.CompletionType completionType) {
        ((LeaseViewerView.Presenter) getPresenter()).isCancelCompletionEventAvailable(new DefaultAsyncCallback<CancelMoveOutConstraintsDTO>() {
            @Override
            public void onSuccess(final CancelMoveOutConstraintsDTO result) {
                if (result.reason().isNull()) {
                    new ReasonBox(i18n.tr("Cancel Notice")) {
                        @Override
                        public boolean onClickOk() {
                            if (CommonsStringUtils.isEmpty(getReason())) {
                                MessageDialog.error(i18n.tr("Error"), i18n.tr("Please fill the reason"));
                                return false;
                            }
                            ((LeaseViewerView.Presenter) getPresenter()).cancelCompletionEvent(getReason());
                            return true;
                        }
                    }.show();
                } else {
                    String caption = i18n.tr("Error");
                    String message = i18n.tr("Can't cancel the ") + completionType.toString();
                    switch (result.reason().getValue()) {
                    case LeasedOrReserved:
                        message += i18n.tr(" the unit is reserved/leased by another Application/Lease already.") + "<br/><br/>"
                                + i18n.tr("Do you want to view it?");
                        MessageDialog.confirm(caption, message, new Command() {
                            @Override
                            public void execute() {
                                CrudAppPlace place;
                                switch (result.leaseStub().status().getValue()) {
                                case Application:
                                    place = new CrmSiteMap.Tenants.LeaseApplication();
                                    break;
                                case Active:
                                case Approved:
                                case Cancelled:
                                case Closed:
                                case Completed:
                                case ExistingLease:
                                case NewLease:
                                    place = new CrmSiteMap.Tenants.Lease();
                                    break;
                                default:
                                    throw new UserRuntimeException("Unsupported lease status.");
                                }

                                AppSite.getPlaceController().goTo(place.formViewerPlace(result.leaseStub().getPrimaryKey()));
                            }
                        });
                        break;
                    case RenovatedOrOffMarket:
                        message += i18n.tr(" - Unit is Renovated/Off-Market.");
                        MessageDialog.error(caption, message);
                        break;
                    case MoveOutNotExpected:
                        message += i18n.tr(" - Move-Out is not expected.");
                        MessageDialog.error(caption, message);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported reason value!");
                    }
                }
            }
        });
    }

    private void issueN4() {
        new N4GenerationQueryDialog() {
            @Override
            public boolean onClickOk() {
                if (super.onClickOk()) {
                    ((LeaseViewerView.Presenter) getPresenter()).issueN4(getValue());
                    return true;
                } else {
                    return false;
                }
            };
        }.show();
    }

    private abstract class RenewLeaseBox extends OkCancelDialog {

        private final CDatePicker endLeaseDate = new CDatePicker();

        public RenewLeaseBox() {
            super(i18n.tr("Renew Lease"));

            setBody(createBody());
            setDialogPixelWidth(250);

            endLeaseDate.setValue(getForm().getValue().currentTerm().termTo().getValue());
            endLeaseDate.asWidget().setWidth("9em");
        }

        protected Widget createBody() {
            VerticalPanel body = new VerticalPanel();
            body.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            body.setWidth("100%");
            body.setSpacing(4);

            body.add(new HTML(i18n.tr("Select new Lease End date:")));
            body.add(endLeaseDate);

            return body;
        }

        public LogicalDate getEndLeaseDate() {
            return (endLeaseDate.getValue() != null ? new LogicalDate(endLeaseDate.getValue()) : null);
        }
    }

}