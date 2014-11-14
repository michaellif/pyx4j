/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.tools.oapi;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.OapiConversionDTO;

public class OapiForm extends OperationsEntityForm<OapiConversionDTO> {

    private static final I18n i18n = I18n.get(OapiForm.class);

    public OapiForm(IPrimeFormView<OapiConversionDTO, ?> view) {
        super(OapiConversionDTO.class, view);

        selectTab(addTab(createDetailsTab(), i18n.tr("General")));
    }

    private FormPanel createDetailsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().description()).decorate();

        formPanel.h2(i18n.tr("Conversion Files"));
        formPanel.append(Location.Dual, proto().conversionFiles(), new OapiConversionFileFolder(isEditable()));

        return formPanel;
    }
}
