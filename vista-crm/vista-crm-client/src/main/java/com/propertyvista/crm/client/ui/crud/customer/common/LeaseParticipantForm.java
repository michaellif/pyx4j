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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.CBooleanLabel;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IEditor;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCollectionCrudHyperlink;
import com.pyx4j.site.client.ui.prime.misc.CEntityCollectionCrudHyperlink.AppPlaceBuilder;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.common.CLeaseTermVHyperlink;
import com.propertyvista.crm.rpc.services.customer.CustomerPictureCrmUploadService;
import com.propertyvista.domain.contact.AddressSimple;
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

public class LeaseParticipantForm<P extends LeaseParticipantDTO<?>> extends CrmEntityForm<P> {

    private static final I18n i18n = I18n.get(LeaseParticipantForm.class);

    private final Class<P> rootClass;

    public LeaseParticipantForm(Class<P> rootClass, IForm<P> view) {
        super(rootClass, view);
        this.rootClass = rootClass;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().customer().person().email()).setMandatory(!getValue().customer().user().isNull());

        get(proto().yardiApplicantId()).setVisible(VistaFeatures.instance().yardiIntegration());
        get(proto().screening()).setVisible(getValue().screening().getPrimaryKey() != null);

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
        }

        if (rootClass.equals(TenantDTO.class)) {
            get(((TenantDTO) proto()).customer().registeredInPortal()).setVisible(
                    LeaseTermParticipant.Role.portalAccess().contains(((TenantDTO) getValue()).role().getValue()));
        }
    }

    @Override
    public void addValidations() {
        get(proto().customer().person().birthDate()).addComponentValidator(new BirthdayDateValidator());
        get(proto().customer().person().birthDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && !getValue().ageOfMajority().isNull()) {
                    if (!TimeUtils.isOlderThan(getComponent().getValue(), getValue().ageOfMajority().getValue())) {
                        return new FieldValidationError(getComponent(), i18n.tr("This lease participant is too young: the minimum age required is {0}.",
                                getValue().ageOfMajority().getValue()));
                    }
                }
                return null;
            }
        });
    }

    protected IsWidget createDetailsTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

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
        formPanel.append(Location.Right, proto().customer().person().birthDate()).decorate().componentWidth(150);
        formPanel.append(Location.Right, proto().customer().person().workPhone()).decorate().componentWidth(200);
        formPanel.append(Location.Right, proto().customer().person().email()).decorate().componentWidth(200);

        formPanel.br(); // lease term / portal registration:

        formPanel.append(Location.Left, proto().leaseTermV(), new CLeaseTermVHyperlink()).decorate();
        formPanel.append(Location.Left, proto().screening(),
                new CEntityCrudHyperlink<LeaseParticipantScreeningTO>(AppPlaceEntityMapper.resolvePlace(LeaseParticipantScreeningTO.class))).decorate();

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
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Dual, proto().paymentMethods(), new PaymentMethodFolder(isEditable()) {
            @SuppressWarnings("unchecked")
            @Override
            protected void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<?, AddressSimple, ?> comp) {
                if (set) {
                    ((LeaseParticipantEditorPresenter<P>) ((IEditor<P>) getParentView()).getPresenter())
                            .getCurrentAddress(new DefaultAsyncCallback<AddressSimple>() {
                                @Override
                                public void onSuccess(AddressSimple result) {
                                    comp.setValue(result, false);
                                }
                            });
                } else {
                    comp.setValue(EntityFactory.create(AddressSimple.class), false);
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void getAllowedPaymentTypes(final AsyncCallback<EnumSet<PaymentType>> callback) {
                ((LeaseParticipantEditorPresenter<P>) ((IEditor<P>) getParentView()).getPresenter())
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
}
