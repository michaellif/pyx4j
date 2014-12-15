/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.common;

import java.util.EnumSet;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCollectionCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCollectionCrudHyperlink.AppPlaceBuilder;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.ClientBusinessRules;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateWithinPeriodValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.customer.common.components.IdentificationDocumentFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.LegalQuestionFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.PersonalAssetFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.PersonalIncomeFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.CLeaseTermVHyperlink;
import com.propertyvista.crm.rpc.services.customer.CustomerPictureCrmUploadService;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.LeaseParticipantDTO;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.dto.TenantDTO;
import com.propertyvista.shared.config.VistaFeatures;

public abstract class LeaseParticipantForm<P extends LeaseParticipantDTO<?>> extends CrmEntityForm<P> {

    private static final I18n i18n = I18n.get(LeaseParticipantForm.class);

    private final Class<P> rootClass;

    private final FormPanel previousAddress = new FormPanel(this) {
        @Override
        public void setVisible(boolean visible) {
// this will clean previous address:
//            if (visible != get(proto().screening().screening().version().previousAddress()).isVisible()) {
//                get(proto().screening().screening().version().previousAddress()).reset();
//            }
            get(proto().screening().data().version().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    private final IdentificationDocumentFolder fileUpload = new IdentificationDocumentFolder();

    public LeaseParticipantForm(Class<P> rootClass, IPrimeFormView<P, ?> view) {
        super(rootClass, view);
        this.rootClass = rootClass;
    }

    protected void addScreeningTabs() {
        Tab tab;

        tab = addTab(createIdentificationDocumentsTab(), i18n.tr("Identification"), DataModelPermission.permissionRead(LeaseParticipantScreeningTO.class));
        if (isEditable()) {
            tab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(LeaseParticipantScreeningTO.class));
        }

        tab = addTab(createAddressesTab(), i18n.tr("Addresses"), DataModelPermission.permissionRead(LeaseParticipantScreeningTO.class));
        if (isEditable()) {
            tab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(LeaseParticipantScreeningTO.class));
        }

        tab = addTab(createlegalQuestionsTab(), i18n.tr("General Questions"), DataModelPermission.permissionRead(LeaseParticipantScreeningTO.class));
        if (isEditable()) {
            tab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(LeaseParticipantScreeningTO.class));
        }

        tab = addTab(createIncomesTab(), i18n.tr("Incomes"), DataModelPermission.permissionRead(LeaseParticipantScreeningTO.class));
        if (isEditable()) {
            tab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(LeaseParticipantScreeningTO.class));
        }

        tab = addTab(createAssetsTab(), i18n.tr("Assets"), DataModelPermission.permissionRead(LeaseParticipantScreeningTO.class));
        if (isEditable()) {
            tab.setPermitEnabledPermission(DataModelPermission.permissionUpdate(LeaseParticipantScreeningTO.class));
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().customer().person().email()).setMandatory(!getValue().customer().user().isNull());

        get(proto().yardiApplicantId()).setVisible(VistaFeatures.instance().yardiIntegration());

        if (isEditable()) {
            IdTarget idTarget = null;
            if (rootClass.equals(TenantDTO.class)) {
                idTarget = IdTarget.tenant;
            } else if (rootClass.equals(GuarantorDTO.class)) {
                idTarget = IdTarget.guarantor;
            } else {
                throw new IllegalArgumentException();
            }
            ClientPolicyManager.setIdComponentEditabilityByPolicy(idTarget, get(proto().participantId()), getValue().getPrimaryKey());

            fileUpload.setPolicyEntity(getValue());

            ((PersonalIncomeFolder) (CComponent<?, ?, ?, ?>) get(proto().screening().data().version().incomes())).setPolicyEntity(getValue());
            ((PersonalAssetFolder) (CComponent<?, ?, ?, ?>) get(proto().screening().data().version().assets())).setPolicyEntity(getValue());
        }

        if (rootClass.equals(TenantDTO.class)) {
            get(((TenantDTO) proto()).customer().registeredInPortal()).setVisible(
                    LeaseTermParticipant.Role.portalAccess().contains(((TenantDTO) getValue()).role().getValue()));
        }

        enablePreviousAddress();
    }

    @Override
    public void addValidations() {
        get(proto().customer().person().birthDate()).addComponentValidator(new BirthdayDateValidator());
        get(proto().customer().person().birthDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null && !getValue().ageOfMajority().isNull()) {
                    if (!TimeUtils.isOlderThan(getCComponent().getValue(), getValue().ageOfMajority().getValue())) {
                        return new BasicValidationError(getCComponent(), i18n.tr("This lease participant is too young: the minimum age required is {0}.",
                                getValue().ageOfMajority().getValue()));
                    }
                }
                return null;
            }
        });

        // ------------------------------------------------------------------------------------------------
        @SuppressWarnings("unchecked")
        CForm<PriorAddress> currentAF = ((CForm<PriorAddress>) get(proto().screening().data().version().currentAddress()));

        currentAF.get(currentAF.proto().moveInDate()).addComponentValidator(new PastDateIncludeTodayValidator());
        currentAF.get(currentAF.proto().moveOutDate()).addComponentValidator(new FutureDateIncludeTodayValidator());
        currentAF.get(currentAF.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        // ------------------------------------------------------------------------------------------------
        @SuppressWarnings("unchecked")
        CForm<PriorAddress> previousAF = ((CForm<PriorAddress>) get(proto().screening().data().version().previousAddress()));

        previousAF.get(previousAF.proto().moveInDate()).addComponentValidator(new PastDateValidator());
        previousAF.get(previousAF.proto().moveOutDate()).addComponentValidator(new PastDateValidator());

        // ------------------------------------------------------------------------------------------------
        new StartEndDateWithinPeriodValidation(previousAF.get(previousAF.proto().moveOutDate()), currentAF.get(currentAF.proto().moveInDate()), 1, 0,
                i18n.tr("Current Move In date should be within 1 month of previous Move Out date"));
    }

    protected IsWidget createDetailsTab() {
        FormPanel formPanel = new FormPanel(this);

        String participant = null;
        if (rootClass.equals(TenantDTO.class)) {
            participant = i18n.tr("Tenant");
        } else if (rootClass.equals(GuarantorDTO.class)) {
            participant = i18n.tr("Guarantor");
        } else {
            throw new IllegalArgumentException();
        }

        CImage imageHolder = new CImage(GWT.<CustomerPictureCrmUploadService> create(CustomerPictureCrmUploadService.class), new VistaFileURLBuilder(
                CustomerPicture.class));
        imageHolder.setImageSize(150, 200);
        imageHolder.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.profilePicture()));

        formPanel.append(Location.Left, proto().customer().picture().file(), imageHolder).decorate().customLabel("");

        formPanel.br();
        formPanel.append(Location.Left, proto().participantId()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().yardiApplicantId()).decorate().componentWidth(120);

        formPanel.append(Location.Dual, proto().customer().person().name(), new NameEditor(participant));
        get(proto().customer().person().name()).setEditable(!VistaFeatures.instance().yardiIntegration());

        formPanel.append(Location.Left, proto().customer().person().sex()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().customer().person().homePhone()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().customer().person().mobilePhone()).decorate().componentWidth(200);
        formPanel.append(Location.Right, proto().customer().person().birthDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().customer().person().workPhone()).decorate().componentWidth(200);
        formPanel.append(Location.Right, proto().customer().person().email()).decorate().componentWidth(200);

        formPanel.br(); // lease term / portal registration:

        formPanel.append(Location.Left, proto().leaseTermV(), new CLeaseTermVHyperlink()).decorate();

        if (rootClass.equals(TenantDTO.class)) {
            formPanel.append(Location.Right, ((TenantDTO) proto()).role(), new CEnumLabel()).decorate().componentWidth(150);
            formPanel.append(Location.Right, ((TenantDTO) proto()).customer().registeredInPortal(), new CBooleanLabel()).decorate().componentWidth(50);
        }

        formPanel.hr(); // lease/application(s) info:

        {
            CEntityCollectionCrudHyperlink<IList<Lease>> link = new CEntityCollectionCrudHyperlink<IList<Lease>>(new AppPlaceBuilder<IList<Lease>>() {
                @Override
                public AppPlace createAppPlace(IList<Lease> value) {
                    CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(LeaseDTO.class);
                    place.formListerPlace().queryArg(
                            EntityFactory.getEntityPrototype(LeaseDTO.class).leaseParticipants().$().customer().customerId().getPath().toString(),
                            getValue().customer().customerId().getValue().toString());
                    return place;
                }
            });
            formPanel.append(Location.Left, proto().leasesOfThisCustomer(), link).decorate().componentWidth(50);
        }
        {
            CEntityCollectionCrudHyperlink<IList<Lease>> link = new CEntityCollectionCrudHyperlink<IList<Lease>>(new AppPlaceBuilder<IList<Lease>>() {
                @Override
                public AppPlace createAppPlace(IList<Lease> value) {
                    CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(LeaseApplicationDTO.class);
                    place.formListerPlace().queryArg(
                            EntityFactory.getEntityPrototype(LeaseApplicationDTO.class).leaseParticipants().$().customer().customerId().getPath().toString(),
                            getValue().customer().customerId().getValue().toString());
                    return place;
                }
            });
            formPanel.append(Location.Right, proto().applicationsOfThisCustomer(), link).decorate().componentWidth(50);
        }

        return formPanel;
    }

    protected IsWidget createPaymentMethodsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().paymentMethods(), new PaymentMethodFolder(isEditable()) {
            @SuppressWarnings("unchecked")
            @Override
            protected void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<?, InternationalAddress, ?, ?> comp) {
                if (set) {
                    ((LeaseParticipantEditorPresenter<P>) ((IPrimeEditorView<P>) getParentView()).getPresenter())
                            .getCurrentAddress(new DefaultAsyncCallback<InternationalAddress>() {
                                @Override
                                public void onSuccess(InternationalAddress result) {
                                    comp.setValue(result, false);
                                }
                            });
                } else {
                    comp.setValue(EntityFactory.create(InternationalAddress.class), false);
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void getAllowedPaymentTypes(final AsyncCallback<EnumSet<PaymentType>> callback) {
                ((LeaseParticipantEditorPresenter<P>) ((IPrimeEditorView<P>) getParentView()).getPresenter())
                        .getAllowedPaymentTypes(new DefaultAsyncCallback<Vector<PaymentType>>() {
                            @Override
                            public void onSuccess(Vector<PaymentType> result) {
                                callback.onSuccess(EnumSet.copyOf(result));
                            }
                        });
            }

            @Override
            public String getNameOn() {
                return LeaseParticipantForm.this.getValue().customer().person().name().getStringView();
            }

            @Override
            protected Set<CreditCardType> getAllowedCardTypes() {
                return LeaseParticipantForm.this.getValue().allowedCardTypes().getValue();
            }

            @Override
            protected void addItem() {
                if (LeaseParticipantForm.this.getValue().electronicPaymentsAllowed().getValue(false)) {
                    super.addItem();
                } else {
                    MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Merchant Account is not setup to receive Electronic Payments"));
                }
            }

            @Override
            protected void removeItem(final CFolderItem<LeasePaymentMethod> item) {
                String message = null;
                if (rootClass.equals(TenantDTO.class)) {
                    message = i18n.tr("This Payment Method may be used in AutoPay(s). Do you really want to delete it with corresponding AutoPay(s)?");
                } else if (rootClass.equals(GuarantorDTO.class)) {
                    message = i18n.tr("Do you really want to delete the Payment Method?");
                } else {
                    throw new IllegalArgumentException();
                }

                MessageDialog.confirm(i18n.tr("Please confirm"), message, new Command() {
                    @Override
                    public void execute() {
                        doRemoveItem(item);
                    }
                });
            }

            private void doRemoveItem(CFolderItem<LeasePaymentMethod> item) {
                onPaymentMethodRemove(item.getValue());
                super.removeItem(item);
            }
        });

        return formPanel;
    }

    protected void onPaymentMethodRemove(LeasePaymentMethod lpm) {
    }

    private IsWidget createIdentificationDocumentsTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().screening().data().version().documents(), fileUpload);
        return formPanel;
    }

    private IsWidget createAddressesTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(proto().screening().data().version().currentAddress().getMeta().getCaption());
        formPanel.append(Location.Dual, inject(proto().screening().data().version().currentAddress(), new PriorAddressEditor()));

        previousAddress.h1(proto().screening().data().version().previousAddress().getMeta().getCaption());
        previousAddress.append(Location.Dual, proto().screening().data().version().previousAddress(), new PriorAddressEditor(true));
        formPanel.append(Location.Dual, previousAddress);

        return formPanel;
    }

    private IsWidget createlegalQuestionsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().screening().data().version().legalQuestions(), new LegalQuestionFolder());

        return formPanel;
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(ClientBusinessRules.needPreviousAddress(getValue().screening().data().version().currentAddress().moveInDate().getValue(),
                getValue().screening().yearsToForcingPreviousAddress().getValue(0)));
    }

// Financial: ------------------------------------------------------------------------------------------------

    private IsWidget createIncomesTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().screening().data().version().incomes(), new PersonalIncomeFolder(isEditable()));

        return formPanel;
    }

    private IsWidget createAssetsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().screening().data().version().assets(), new PersonalAssetFolder(isEditable()));

        return formPanel;
    }

    class QuestionsFormPanel extends FormPanel {

        public QuestionsFormPanel(CForm<?> parent) {
            super(parent);
        }

        public void appendQuestion(IObject<?> member) {
            append(Location.Dual, member).decorate().labelWidth(400).labelPosition(LabelPosition.top).useLabelSemicolon(false);
        }
    };
}
