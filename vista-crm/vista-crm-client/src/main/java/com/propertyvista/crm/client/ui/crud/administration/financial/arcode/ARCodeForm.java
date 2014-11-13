/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.financial.arcode;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.components.boxes.GlCodeSelectionDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.GlCode;

public class ARCodeForm extends CrmEntityForm<ARCode> implements HasYardiIntegrationMode {

    private static final I18n i18n = I18n.get(ARCodeForm.class);

    private final FormPanel yardiIntegrationPanel;

    public ARCodeForm(IFormView<ARCode, ?> view) {
        super(ARCode.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().type()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().glCode(), new CEntitySelectorHyperlink<GlCode>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(GlCode.class).formViewerPlace(getValue().glCodeCategory().getPrimaryKey());
            }

            @Override
            protected IShowable getSelectorDialog() {
                return new GlCodeSelectionDialog() {
                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItem().isNull()) {
                            get(ARCodeForm.this.proto().glCode()).setValue(getSelectedItem());
                        }
                        return true;
                    }
                };
            }
        }).decorate().componentWidth(200);

        yardiIntegrationPanel = new FormPanel(this);
        yardiIntegrationPanel.h1(i18n.tr("Yardi Integration"));
        yardiIntegrationPanel.append(Location.Left, proto().yardiChargeCodes(), new YardiChargeCodeFolder());

        formPanel.append(Location.Dual, yardiIntegrationPanel);

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("AR Code")));
    }

    @Override
    public void setYardiIntegrationModeEnabled(boolean enabled) {
        yardiIntegrationPanel.setVisible(enabled);
        get(proto().glCode()).setVisible(!enabled);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // disable type change on reserved codes:
        get(proto().type()).inheritEditable(false);
        get(proto().type()).setEditable(!getValue().reserved().isBooleanTrue());
    }
}