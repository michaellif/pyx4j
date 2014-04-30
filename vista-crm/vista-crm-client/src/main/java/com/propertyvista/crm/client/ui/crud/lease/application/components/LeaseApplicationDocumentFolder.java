/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationDocumentUploadService;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;

public class LeaseApplicationDocumentFolder extends VistaBoxFolder<LeaseApplicationDocument> {

    private static final I18n i18n = I18n.get(LeaseApplicationDocumentFolder.class);

    public LeaseApplicationDocumentFolder() {
        super(LeaseApplicationDocument.class);
    }

    @Override
    protected CForm<LeaseApplicationDocument> createItemForm(IObject<?> member) {
        return new LeaseApplicationDocumentForm();
    }

    private static class LeaseApplicationDocumentForm extends CForm<LeaseApplicationDocument> {

        public LeaseApplicationDocumentForm() {
            super(LeaseApplicationDocument.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicCFormPanel formPanel = new BasicCFormPanel(this);
            formPanel
                    .append(Location.Dual,
                            proto().file(),
                            new CFile(GWT.<UploadService<?, ?>> create(LeaseApplicationDocumentUploadService.class), new VistaFileURLBuilder(
                                    LeaseApplicationDocument.class))).decorate().customLabel(i18n.tr("Agreement Document File"));
            formPanel.append(Location.Dual, proto().isSignedByInk()).decorate();
            formPanel.append(Location.Dual, proto().signedBy()).decorate();
            formPanel.append(Location.Dual, proto().uploader()).decorate();
            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            get(proto().uploader()).setVisible(!getValue().uploader().isNull());
        };

    }

}
