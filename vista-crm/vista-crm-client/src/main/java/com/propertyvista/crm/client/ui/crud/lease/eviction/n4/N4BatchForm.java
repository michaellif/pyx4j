/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CPhoneField;
import com.pyx4j.forms.client.ui.CPhoneField.PhoneType;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.editors.InternationalAddressEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.legal.n4.N4BatchItem;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchForm extends CrmEntityForm<N4BatchDTO> {

    private static final I18n i18n = I18n.get(N4BatchForm.class);

    public N4BatchForm(IPrimeFormView<N4BatchDTO, ?> view) {
        super(N4BatchDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().isReadyForService()).decorate();

        formPanel.h1(i18n.tr("General"));
        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().created()).decorate();
        formPanel.append(Location.Left, proto().noticeIssueDate()).decorate();
        formPanel.append(Location.Left, proto().serviceDate()).decorate();

        CField<Employee, ?> employeeBox = isEditable() ? new CEntityComboBox<>(Employee.class) : //
                new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class));
        formPanel.append(Location.Right, proto().signingAgent(), employeeBox).decorate();
        formPanel.append(Location.Right, proto().deliveryMethod()).decorate();
        formPanel.append(Location.Right, proto().deliveryDate()).decorate();

        formPanel.h1(i18n.tr("Contact Information"));
        formPanel.append(Location.Left, proto().companyLegalName()).decorate().customLabel(i18n.tr("Legal Name"));
        formPanel.append(Location.Left, proto().companyEmailAddress()).decorate().customLabel(i18n.tr("Email Address"));
        formPanel.append(Location.Right, proto().companyPhoneNumber(), new CPhoneField(PhoneType.northAmerica)).decorate().customLabel(i18n.tr("Phone Number"));
        formPanel.append(Location.Right, proto().companyFaxNumber(), new CPhoneField(PhoneType.northAmerica)).decorate().customLabel(i18n.tr("Fax Number"));

        formPanel.h3(i18n.tr("Mailing Address"));
        formPanel.append(Location.Dual, proto().companyAddress(), new InternationalAddressEditor());

        formPanel.h1(i18n.tr("Batch Records"));
        N4BatchItemFolder itemFolder = new N4BatchItemFolder();
        itemFolder.setOrderable(false);
        formPanel.append(Location.Dual, proto().items(), itemFolder);

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable() && !getValue().signingAgent().isNull()) {
            get(proto().signingAgent()).setEditable(false);
        }
    }

    class N4BatchItemFolder extends VistaBoxFolder<N4BatchItem> {

        public N4BatchItemFolder() {
            super(N4BatchItem.class);
            setAddable(false);
        }

        @Override
        protected CForm<? extends N4BatchItem> createItemForm(IObject<?> member) {
            return new CForm<N4BatchItem>(N4BatchItem.class) {

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    CEntityLabel<Lease> leaseLabel = isEditable() ? new CEntityLabel<Lease>() : new CEntityCrudHyperlink<Lease>(
                            AppPlaceEntityMapper.resolvePlace(Lease.class));
                    leaseLabel.setFormatter(new IFormatter<Lease, String>() {

                        @Override
                        public String format(Lease value) {
                            return value == null ? null : SimpleMessageFormat.format( //
                                    "{0}, {1}, {2}{3,choice,null# - {3}}", //
                                    value.unit().building().propertyCode(), value.unit(), value.type(), value._applicant());
                        }
                    });

                    formPanel.append(Location.Left, proto().lease(), leaseLabel).decorate();
                    formPanel.append(Location.Right, proto().leaseArrears().totalRentOwning(), new CMoneyLabel()).decorate();
                    formPanel.append(Location.Dual, proto().leaseArrears().unpaidCharges(), new BatchItemChargesFolder(this));

                    return formPanel;
                }
            };
        }

        @Override
        public VistaBoxFolderItemDecorator<N4BatchItem> createItemDecorator() {
            VistaBoxFolderItemDecorator<N4BatchItem> itemDecorator = super.createItemDecorator();
            itemDecorator.setExpended(false);
            itemDecorator.setCaptionFormatter(new IFormatter<N4BatchItem, SafeHtml>() {

                @Override
                public SafeHtml format(N4BatchItem value) {
                    return SafeHtmlUtils.fromString(SimpleMessageFormat.format( //
                            "{0}, {1}, {2} - {3}: {4}{5,choice,null# - {5}}", //
                            value.lease().unit().building().propertyCode(), //
                            value.lease().unit(), //
                            value.lease().expectedMoveIn(), //
                            value.lease().expectedMoveOut(), //
                            value.leaseArrears().totalRentOwning(), //
                            value.lease()._applicant()) //
                            );
                }
            });
            return itemDecorator;
        }

    }

    class BatchItemChargesFolder extends VistaBoxFolder<N4UnpaidCharge> {

        private final CForm<N4BatchItem> parent;

        public BatchItemChargesFolder(CForm<N4BatchItem> parent) {
            super(N4UnpaidCharge.class);
            this.parent = parent;

            setOrderable(false);
        }

        @Override
        protected void addItem(N4UnpaidCharge newEntity) {
            newEntity.rentCharged().setValue(new BigDecimal("0.00"));
            newEntity.rentPaid().setValue(new BigDecimal("0.00"));
            newEntity.rentOwing().setValue(new BigDecimal("0.00"));
            super.addItem(newEntity);
        }

        @Override
        public VistaBoxFolderItemDecorator<N4UnpaidCharge> createItemDecorator() {
            VistaBoxFolderItemDecorator<N4UnpaidCharge> itemDecorator = super.createItemDecorator();
            itemDecorator.setExpended(false);
            return itemDecorator;
        }

        @Override
        protected CForm<? extends N4UnpaidCharge> createItemForm(IObject<?> member) {
            return new CForm<N4UnpaidCharge>(N4UnpaidCharge.class) {

                private CForm<N4UnpaidCharge> getForm() {
                    return this;
                }

                private final ValueChangeHandler<BigDecimal> amountChangeHandler = new ValueChangeHandler<BigDecimal>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                        // update charge total
                        N4UnpaidCharge item = getForm().getValue();
                        item.rentOwing().setValue(item.rentCharged().getValue().subtract(item.rentPaid().getValue()));
                        getForm().refresh(false);

                        // update parent with grand total
                        N4BatchItem rec = parent.getValue();
                        BigDecimal total = BigDecimal.ZERO;
                        for (N4UnpaidCharge owing : rec.leaseArrears().unpaidCharges()) {
                            total = total.add(owing.rentOwing().getValue());
                        }
                        rec.leaseArrears().totalRentOwning().setValue(total);
                        parent.refresh(false);
                    }
                };

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);
                    formPanel.append(Location.Left, proto().fromDate()).decorate();
                    formPanel.append(Location.Left, proto().toDate()).decorate();
                    formPanel.append(Location.Left, proto().arCode()).decorate();
                    formPanel.append(Location.Right, proto().rentCharged()).decorate();
                    formPanel.append(Location.Right, proto().rentPaid()).decorate();
                    formPanel.append(Location.Right, proto().rentOwing()).decorate();

                    if (isEditable()) {
                        get(proto().rentOwing()).setEditable(false);
                        get(proto().rentCharged()).addValueChangeHandler(amountChangeHandler);
                        get(proto().rentPaid()).addValueChangeHandler(amountChangeHandler);
                    }

                    return formPanel;
                }
            };
        }
    }
}
