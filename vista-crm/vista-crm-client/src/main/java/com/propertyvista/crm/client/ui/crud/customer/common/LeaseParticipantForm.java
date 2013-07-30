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
import java.util.Vector;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.gwt.shared.FileURLBuilder;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.form.IEditor;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.common.CLeaseTermVHyperlink;
import com.propertyvista.crm.rpc.services.customer.CustomerPictureUploadService;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.dto.GuarantorDTO;
import com.propertyvista.dto.LeaseParticipantDTO;
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
        } else {
            get(proto().customer().personScreening()).setVisible(getValue().customer().personScreening().getPrimaryKey() != null);
        }
    }

    @Override
    public void addValidations() {
        get(proto().customer().person().birthDate()).addValueValidator(new PastDateIncludeTodayValidator());
        get(proto().customer().person().birthDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value != null && !getValue().ageOfMajority().isNull()) {
                    if (!TimeUtils.isOlderThan(value, getValue().ageOfMajority().getValue() - 1)) {
                        return new ValidationError(component, i18n.tr("this lease participant is too young: the minimum age required is {0}.", getValue()
                                .ageOfMajority().getValue()));
                    }
                }
                return null;
            }
        });
    }

    protected TwoColumnFlexFormPanel createDetailsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        String participant = null;
        if (rootClass.equals(TenantDTO.class)) {
            participant = i18n.tr("Tenant");
        } else if (rootClass.equals(GuarantorDTO.class)) {
            participant = i18n.tr("Guarantor");
        } else {
            throw new IllegalArgumentException();
        }

        int row = -1;
        CImage<CustomerPicture> imageHolder = new CImage<CustomerPicture>(CustomerPicture.class, CImage.Type.single) {
            @Override
            protected EntityFolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CEntityForm<CustomerPicture> entryForm) {
                VerticalPanel infoPanel = new VerticalPanel();
                infoPanel.add(new FormDecoratorBuilder(entryForm.inject(entryForm.proto().fileName(), new CLabel<String>())).build());
                infoPanel.add(new FormDecoratorBuilder(entryForm.inject(entryForm.proto().caption())).build());
                return infoPanel;
            }
        };
        imageHolder.setImageFileUrlBuilder(new ImageFileURLBuilder());
        imageHolder.setThumbnailFileUrlBuilder(new ImageFileURLBuilder());
        imageHolder.setUploadService(GWT.<CustomerPictureUploadService> create(CustomerPictureUploadService.class));
        imageHolder.setImageSize(150, 200);
        imageHolder.setThumbSize(60, 80);
        imageHolder.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.profilePicture()));

        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().participantId()), 7).build());
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().customer().pictures(), imageHolder)).customLabel("").build());
        main.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_BOTTOM);

        main.setWidget(++row, 0, 2, inject(proto().customer().person().name(), new NameEditor(participant)));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().customer().person().sex()), 7).build());
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().customer().person().birthDate()), 9).build());

        row = 2;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().customer().person().homePhone()), 15).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().customer().person().mobilePhone()), 15).build());

        row = 2;
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().customer().person().workPhone()), 15).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().customer().person().email()), 15).build());

        row = 4;
        if (!isEditable()) {

            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().leaseTermV(), new CLeaseTermVHyperlink()), 15).build());
            if (rootClass.equals(TenantDTO.class)) {
                main.setWidget(row, 1, new FormDecoratorBuilder(inject(((TenantDTO) proto()).role()), 10).build());
            }
            main.setWidget(
                    ++row,
                    0,
                    new FormDecoratorBuilder(inject(proto().customer().personScreening(),
                            new CEntityCrudHyperlink<CustomerScreening>(AppPlaceEntityMapper.resolvePlace(CustomerScreening.class))), 15).build());
        }

        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().customer().person().name()).setViewable(true);
        }

        return main;
    }

    protected TwoColumnFlexFormPanel createPaymentMethodsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        main.setWidget(0, 0, 2, inject(proto().paymentMethods(), new PaymentMethodFolder(isEditable()) {
            @SuppressWarnings("unchecked")
            @Override
            protected void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressSimple> comp) {
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
            protected void addItem() {
                if (LeaseParticipantForm.this.getValue().electronicPaymentsAllowed().isBooleanTrue()) {
                    super.addItem();
                } else {
                    MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Merchant account is not setup to receive Electronic Payments"));
                }
            }
        }));

        return main;
    }

    class ImageFileURLBuilder implements FileURLBuilder<CustomerPicture> {
        @Override
        public String getUrl(CustomerPicture pic) {
            return MediaUtils.createCustomerPictureUrl(pic);
        }
    }
}
