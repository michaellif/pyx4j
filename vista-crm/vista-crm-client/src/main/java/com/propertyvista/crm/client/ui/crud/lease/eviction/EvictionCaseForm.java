/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionDocumentUploadService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.eviction.EvictionStatus;
import com.propertyvista.domain.eviction.EvictionStatusRecord;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionCaseForm extends CrmEntityForm<EvictionCaseDTO> {

    private static final I18n i18n = I18n.get(EvictionCaseForm.class);

    private final boolean uploadable;

    public EvictionCaseForm(IPrimeFormView<EvictionCaseDTO, ?> view, boolean uploadable) {
        super(EvictionCaseDTO.class, view);
        this.uploadable = uploadable;

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().createdOn()).decorate();
        formPanel.append(Location.Dual, proto().createdBy(), new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class))).decorate();
        formPanel.append(Location.Dual, proto().updatedOn()).decorate();
        formPanel.append(Location.Dual, proto().closedOn()).decorate();
        formPanel.append(Location.Dual, proto().note()).decorate();

        formPanel.h1(i18n.tr("Status History"));
        formPanel.append(Location.Dual, proto().history(), new StatusHistoryFolder());

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        boolean isNew = getValue().getPrimaryKey() == null;
        if (isNew) {
            get(proto().createdOn()).setVisible(false);
            get(proto().createdBy()).setVisible(false);
            get(proto().updatedOn()).setVisible(false);
            get(proto().closedOn()).setVisible(false);
        }
    }

    class StatusHistoryFolder extends VistaBoxFolder<EvictionStatus> {

        public StatusHistoryFolder() {
            super(EvictionStatus.class);
        }

        @Override
        protected CForm<? extends EvictionStatus> createItemForm(IObject<?> member) {
            return new CForm<EvictionStatus>(EvictionStatus.class) {

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Dual, proto().evictionStep()).decorate();
                    formPanel.append(Location.Dual, proto().addedOn()).decorate();
                    formPanel.append(Location.Dual, proto().addedBy()).decorate();

                    formPanel.h1(i18n.tr("Records"));
                    formPanel.append(Location.Dual, proto().statusRecords(), new StatusRecordFolder());

                    return formPanel;
                }

            };
        }
    }

    class StatusRecordFolder extends VistaBoxFolder<EvictionStatusRecord> {

        public StatusRecordFolder() {
            super(EvictionStatusRecord.class);
        }

        @Override
        protected CForm<? extends EvictionStatusRecord> createItemForm(IObject<?> member) {
            return new CForm<EvictionStatusRecord>(EvictionStatusRecord.class) {

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Dual, proto().note()).decorate();
                    formPanel.append(Location.Dual, proto().addedOn()).decorate();
                    formPanel.append(Location.Dual, proto().addedBy()).decorate();

                    formPanel.h1(i18n.tr("Attachments"));
                    formPanel.append(Location.Dual, proto().attachments(), new UploadableEvictionDocumentFolder());

                    return formPanel;
                }
            };
        }

    }

    class UploadableEvictionDocumentFolder extends VistaBoxFolder<EvictionDocument> {

        public UploadableEvictionDocumentFolder() {
            super(EvictionDocument.class);
        }

        @Override
        protected CForm<EvictionDocument> createItemForm(IObject<?> member) {
            return new CForm<EvictionDocument>(EvictionDocument.class) {

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Dual, proto().title()).decorate();
                    formPanel.append(Location.Dual, proto().note()).decorate();
                    formPanel.append(Location.Dual, proto().addedOn()).decorate();
                    formPanel.append(Location.Dual, proto().file(), new CFile( //
                            uploadable ? GWT.<UploadService<?, ?>> create(EvictionDocumentUploadService.class) : null, //
                            new VistaFileURLBuilder(EvictionDocument.class) //
                            )).decorate();

                    return formPanel;
                }

                @Override
                public void addValidations() {
                    super.addValidations();
                    get(proto().file()).setMandatory(true);
                }
            };
        }
    }
}
