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
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.lease.eviction.EvictionStatusEditor;
import com.propertyvista.domain.eviction.EvictionStatusN4;
import com.propertyvista.domain.legal.n4.N4LeaseData;

public class N4EvictionStatusEditor extends EvictionStatusEditor<EvictionStatusN4> {

    private static final I18n i18n = I18n.get(N4EvictionStatusEditor.class);

    public N4EvictionStatusEditor(EvictionStepSelectionHandler stepSelectionHandler, boolean canUploadDocuments) {
        super(EvictionStatusN4.class, stepSelectionHandler, canUploadDocuments);
    }

    @Override
    protected FormPanel getPropertyPanel() {
        FormPanel formPanel = super.getPropertyPanel();
        // add N4 specific properties here
        formPanel.h1(i18n.tr("N4 Summary"));
        formPanel.append(Location.Left, proto().terminationDate()).decorate();
        formPanel.append(Location.Right, proto().expiryDate()).decorate();
        formPanel.append(Location.Right, proto().cancellationBalance()).decorate();
        formPanel.append(Location.Dual, proto().n4Data(), new N4LeaseDataEditor());

        formPanel.h1(i18n.tr("N4 Lease Arrears"));
        formPanel.append(Location.Dual, proto().leaseArrears(), new N4LeaseArrearsEditor());

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

    }

    static class N4LeaseDataEditor extends CForm<N4LeaseData> {

        public N4LeaseDataEditor() {
            super(N4LeaseData.class);
        }

        @Override
        protected IsWidget createContent() {
            return new N4DataEditorPanel<>(this);
        }

    }
}
