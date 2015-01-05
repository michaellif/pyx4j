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
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
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
import com.propertyvista.domain.legal.n4.N4RentOwingForPeriod;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchForm extends CrmEntityForm<N4BatchDTO> {

    private static final I18n i18n = I18n.get(N4BatchForm.class);

    public N4BatchForm(IPrimeFormView<N4BatchDTO, ?> view) {
        super(N4BatchDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("General:"));
        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().created()).decorate();

        formPanel.append(Location.Right, proto().noticeDate()).decorate();
        // TODO - use link viewer new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class))
        CField<Employee, ?> employeeBox = isEditable() ? new CEntityComboBox<>(Employee.class) : //
                new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class));
        formPanel.append(Location.Right, proto().signingEmployee(), employeeBox).decorate();

        formPanel.h1(i18n.tr("Contact Information:"));
        formPanel.append(Location.Left, proto().companyLegalName()).decorate().customLabel(i18n.tr("Legal Name"));
        formPanel.append(Location.Left, proto().companyEmailAddress()).decorate().customLabel(i18n.tr("Email Address"));
        formPanel.append(Location.Right, proto().companyPhoneNumber(), new CPhoneField(PhoneType.northAmerica)).decorate().customLabel(i18n.tr("Phone Number"));
        formPanel.append(Location.Right, proto().companyFaxNumber(), new CPhoneField(PhoneType.northAmerica)).decorate().customLabel(i18n.tr("Fax Number"));
        formPanel.h3(i18n.tr("Mailing Address:"));
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

        if (isEditable() && !getValue().signingEmployee().isNull()) {
            get(proto().signingEmployee()).setEditable(false);
        }
    }

    class N4BatchItemFolder extends VistaBoxFolder<N4BatchItem> {

        public N4BatchItemFolder() {
            super(N4BatchItem.class);
        }

        @Override
        protected CForm<? extends N4BatchItem> createItemForm(IObject<?> member) {
            return new CForm<N4BatchItem>(N4BatchItem.class) {

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);
                    formPanel.append(Location.Left, proto().lease().unit().building()).decorate();
                    formPanel.append(Location.Right, proto().lease()).decorate();

                    formPanel.append(Location.Dual, proto().rentOwingBreakdown(), new BatchItemChargesFolder(this));

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
                            "{0}, {1}: {2}", value.lease().unit().building().propertyCode(), value.lease().unit(), value.totalRentOwning()));
                }
            });
            return itemDecorator;
        }

    }

    class BatchItemChargesFolder extends VistaBoxFolder<N4RentOwingForPeriod> {

        private final CForm<N4BatchItem> parent;

        public BatchItemChargesFolder(CForm<N4BatchItem> parent) {
            super(N4RentOwingForPeriod.class);
            this.parent = parent;
        }

        @Override
        protected CForm<? extends N4RentOwingForPeriod> createItemForm(IObject<?> member) {
            return new CForm<N4RentOwingForPeriod>(N4RentOwingForPeriod.class) {

                private final ValueChangeHandler<BigDecimal> amountChangeHandler = new ValueChangeHandler<BigDecimal>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                        N4RentOwingForPeriod item = getValue();
                        get(proto().rentOwing()).setValue(item.rentCharged().getValue().subtract(item.rentPaid().getValue()));
                        N4BatchItem rec = parent.getValue();
                        // update parent
                        BigDecimal total = BigDecimal.ZERO;
                        for (N4RentOwingForPeriod owing : rec.rentOwingBreakdown()) {
                            total = total.add(owing.rentOwing().getValue());
                        }
                        rec.totalRentOwning().setValue(total);
                        parent.refresh(true);
                    }
                };

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);
                    formPanel.append(Location.Left, proto().fromDate()).decorate();
                    formPanel.append(Location.Left, proto().toDate()).decorate();
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
