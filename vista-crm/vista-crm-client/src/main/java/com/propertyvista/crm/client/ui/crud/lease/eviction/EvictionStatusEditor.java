/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 22, 2015
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

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.rpc.services.legal.eviction.EvictionDocumentUploadService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.eviction.EvictionStatus;
import com.propertyvista.domain.eviction.EvictionStatusRecord;

public class EvictionStatusEditor<S extends EvictionStatus> extends EvictionStatusEditorBase<S> {

    private static final I18n i18n = I18n.get(EvictionStatusEditor.class);

    private final boolean canUploadDocuments;

    public EvictionStatusEditor(Class<S> entityClass, EvictionStepSelectionHandler stepSelectionHandler, boolean canUploadDocuments) {
        super(entityClass, stepSelectionHandler);
        this.canUploadDocuments = canUploadDocuments;
    }

    @Override
    protected FormPanel getPropertyPanel() {
        FormPanel formPanel = super.getPropertyPanel();
        // add status properties here
        formPanel.append(Location.Left, proto().addedOn()).decorate();
        formPanel.append(Location.Right, proto().addedBy(), new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class))).decorate();
        formPanel.append(Location.Dual, proto().note()).decorate();

        return formPanel;
    }

    @Override
    protected FormPanel createContent() {
        FormPanel formPanel = super.createContent();
        // add status records at the end
        formPanel.h1(i18n.tr("Records"));
        formPanel.append(Location.Dual, proto().statusRecords(), new StatusRecordFolder());

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        boolean isNew = getValue().getPrimaryKey() == null;
        if (isNew) {
            get(proto().addedBy()).setVisible(false);
            get(proto().addedOn()).setVisible(false);
        } else {
            get(proto().evictionStep()).setEditable(false);
        }
    }

    class StatusRecordFolder extends VistaBoxFolder<EvictionStatusRecord> {

        public StatusRecordFolder() {
            super(EvictionStatusRecord.class);
            setOrderable(false);
        }

        @Override
        protected CForm<? extends EvictionStatusRecord> createItemForm(IObject<?> member) {
            return new CForm<EvictionStatusRecord>(EvictionStatusRecord.class) {

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Dual, proto().note()).decorate();
                    formPanel.append(Location.Dual, proto().addedOn()).decorate();
                    formPanel.append(Location.Dual, proto().addedBy(), new CEntityCrudHyperlink<Employee>(AppPlaceEntityMapper.resolvePlace(Employee.class)))
                            .decorate();

                    formPanel.h1(i18n.tr("Attachments"));
                    formPanel.append(Location.Dual, proto().attachments(), new UploadableEvictionDocumentFolder(canUploadDocuments));

                    return formPanel;
                }
            };
        }

        @Override
        public VistaBoxFolderItemDecorator<EvictionStatusRecord> createItemDecorator() {
            VistaBoxFolderItemDecorator<EvictionStatusRecord> itemDecorator = super.createItemDecorator();
            itemDecorator.setExpended(false);
            return itemDecorator;
        }
    }

    class UploadableEvictionDocumentFolder extends VistaBoxFolder<EvictionDocument> {

        private final boolean uploadable;

        public UploadableEvictionDocumentFolder(boolean uploadable) {
            super(EvictionDocument.class);
            this.uploadable = uploadable;
            setOrderable(false);
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

        @Override
        public VistaBoxFolderItemDecorator<EvictionDocument> createItemDecorator() {
            VistaBoxFolderItemDecorator<EvictionDocument> itemDecorator = super.createItemDecorator();
            itemDecorator.setExpended(false);
            return itemDecorator;
        }
    }
}
