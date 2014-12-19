/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 */
package com.propertyvista.operations.client.ui.crud.tools.oapi;

import java.util.EnumSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.operations.domain.imports.OapiConversionFile;
import com.propertyvista.operations.domain.imports.OapiConversionFile.OapiConversionFileType;
import com.propertyvista.operations.rpc.services.OapiConversionFileUploadService;

public class OapiConversionFileFolder extends VistaBoxFolder<OapiConversionFile> {

    private static final I18n i18n = I18n.get(OapiConversionFileFolder.class);

    //private OapiConversionFileTypeSelector oapiFileTypeSelector = new OapiConversionFileTypeSelector();

    public OapiConversionFileFolder(boolean modifyable) {
        super(OapiConversionFile.class, i18n.tr("Conversion file"), modifyable);
    }

    @Override
    protected CForm<OapiConversionFile> createItemForm(IObject<?> member) {
        return new OapiConversionFileEditor();
    }

    public class OapiConversionFileEditor extends CForm<OapiConversionFile> {

        public OapiConversionFileEditor() {
            super(OapiConversionFile.class);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().type()).decorate().componentWidth(150);
            ((CComboBox<OapiConversionFileType>) get(proto().type())).setOptions(EnumSet.allOf(OapiConversionFileType.class));

            formPanel
                    .append(Location.Left,
                            proto().file(),
                            new CFile(GWT.<UploadService<?, ?>> create(OapiConversionFileUploadService.class),
                                    new VistaFileURLBuilder(OapiConversionFile.class))).decorate().componentWidth(150);

            return formPanel;
        }
    }

}