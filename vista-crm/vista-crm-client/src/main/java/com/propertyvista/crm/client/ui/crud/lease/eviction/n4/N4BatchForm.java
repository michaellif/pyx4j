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

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.N4BatchDTO;

public class N4BatchForm extends CrmEntityForm<N4BatchDTO> {

    private static final I18n i18n = I18n.get(N4BatchForm.class);

    public N4BatchForm(IPrimeFormView<N4BatchDTO, ?> view) {
        super(N4BatchDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().isReadyForService()).decorate();

        formPanel.h1(i18n.tr("General"));
        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Right, proto().created(), new CDateLabel()).decorate();
        formPanel.append(Location.Dual, new N4DataEditorPanel<>(this));

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabEnabled(addTab(isEditable() ? new HTML() : ((N4BatchViewerView) getParentView()).getItemLister(), i18n.tr("Batch Items")), !isEditable());
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().serviceDate()).setVisible(!getValue().serviceDate().isNull());
        get(proto().deliveryDate()).setVisible(!getValue().serviceDate().isNull());
    }
}
