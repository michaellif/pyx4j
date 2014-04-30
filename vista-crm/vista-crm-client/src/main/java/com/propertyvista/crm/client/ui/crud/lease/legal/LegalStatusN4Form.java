/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.legal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.services.legal.LegalLetterUploadService;
import com.propertyvista.domain.legal.GenericLegalLetter;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.dto.LegalStatusN4DTO;

public class LegalStatusN4Form extends CForm<LegalStatusN4DTO> {

    private static final I18n i18n = I18n.get(LegalStatusN4Form.class);

    private final boolean uploadable;

    public LegalStatusN4Form(boolean uploadable) {
        super(LegalStatusN4DTO.class);
        this.uploadable = uploadable;
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Full, proto().status()).decorate();
        get(proto().status()).setEditable(false);

        formPanel.append(Location.Full, proto().expiryDate()).decorate();
        formPanel.append(Location.Full, proto().expiry()).decorate();
        formPanel.append(Location.Full, proto().cancellationThreshold()).decorate();
        formPanel.append(Location.Full, proto().terminationDate()).decorate();

        formPanel.append(Location.Full, proto().setOn()).decorate();
        formPanel.append(Location.Full, proto().setBy().name()).decorate().customLabel(i18n.tr("Set By"));
        formPanel.append(Location.Full, proto().details()).decorate();

        formPanel.h2(i18n.tr("Attached Letters"));
        if (uploadable) {
            formPanel.append(Location.Full, proto().letters(), new UploadableLegalLetterFolder());
        } else {
            formPanel.append(Location.Full, proto().letters(), new LegalLetterFolder());
        }
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        boolean isNew = getValue().getPrimaryKey() == null;
        if (isNew) {
            get(proto().setOn()).setVisible(false);
            get(proto().setBy().name()).setVisible(false);
        }

        get(proto().expiryDate()).setVisible(isEditable());
        get(proto().expiry()).setVisible(!isEditable());
    }

    public static class UploadableLegalLetterFolder extends VistaBoxFolder<LegalLetter> {

        public UploadableLegalLetterFolder() {
            super(LegalLetter.class);
        }

        @Override
        protected CForm<LegalLetter> createItemForm(IObject<?> member) {
            return new LegalLetterForm();
        }

        @Override
        protected void addItem() {
            super.addItem(EntityFactory.create(GenericLegalLetter.class));
        }
    }

    public static class LegalLetterForm extends CForm<LegalLetter> {

        public LegalLetterForm() {
            super(LegalLetter.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);
            formPanel.append(Location.Full, proto().file(),
                    new CFile(GWT.<UploadService<?, ?>> create(LegalLetterUploadService.class), new VistaFileURLBuilder(LegalLetter.class))).decorate();
            formPanel.append(Location.Full, proto().notes()).decorate();
            return formPanel;
        }

        @Override
        public void addValidations() {
            super.addValidations();
            get(proto().file()).setMandatory(true);
        }
    }

}
