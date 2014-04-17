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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.services.legal.LegalLetterUploadService;
import com.propertyvista.domain.legal.GenericLegalLetter;
import com.propertyvista.domain.legal.LegalLetter;
import com.propertyvista.dto.LegalStatusDTO;

public class LegalStatusForm extends CEntityForm<LegalStatusDTO> {

    private static final I18n i18n = I18n.get(LegalStatusForm.class);

    private final boolean uploadable;

    public LegalStatusForm(boolean uploadable) {
        super(LegalStatusDTO.class);
        this.uploadable = uploadable;
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, 2, inject(proto().status(), new FieldDecoratorBuilder().componentWidth("200px").build()));
        get(proto().status()).setEditable(false);

        panel.setWidget(++row, 0, 2, inject(proto().expiryDate(), new FieldDecoratorBuilder().componentWidth("200px").build()));
        panel.setWidget(++row, 0, 2, inject(proto().expiry(), new FieldDecoratorBuilder().componentWidth("200px").build()));

        panel.setWidget(++row, 0, 2, inject(proto().setOn(), new FieldDecoratorBuilder().componentWidth("200px").build()));
        panel.setWidget(++row, 0, 2, inject(proto().setBy().name(), new FieldDecoratorBuilder().customLabel(i18n.tr("Set By")).componentWidth("200px").build()));
        panel.setWidget(++row, 0, 2, inject(proto().details(), new FieldDecoratorBuilder().componentWidth("200px").build()));

        panel.setH2(++row, 0, 2, i18n.tr("Attached Letters"));
        if (uploadable) {
            panel.setWidget(++row, 0, 2, inject(proto().letters(), new UploadableLegalLetterFolder()));
        } else {
            panel.setWidget(++row, 0, 2, inject(proto().letters(), new LegalLetterFolder()));
        }
        return panel;
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
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof LegalLetter) {
                return new LegalLetterForm();
            }
            return super.create(member);
        }

        @Override
        protected void addItem() {
            super.addItem(EntityFactory.create(GenericLegalLetter.class));
        }
    }

    public static class LegalLetterForm extends CEntityForm<LegalLetter> {

        public LegalLetterForm() {
            super(LegalLetter.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
            int row = -1;
            panel.setWidget(
                    ++row,
                    0,
                    2,
                    inject(proto().file(), new CFile(GWT.<UploadService<?, ?>> create(LegalLetterUploadService.class), new VistaFileURLBuilder(
                            LegalLetter.class)), new FieldDecoratorBuilder().build()));
            panel.setWidget(++row, 0, 2, inject(proto().notes(), new FieldDecoratorBuilder().build()));
            return panel;
        }

        @Override
        public void addValidations() {
            super.addValidations();
            get(proto().file()).setMandatory(true);
        }
    }

}
