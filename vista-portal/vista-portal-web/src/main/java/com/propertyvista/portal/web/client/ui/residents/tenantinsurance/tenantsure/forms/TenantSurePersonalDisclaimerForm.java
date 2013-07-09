/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.forms;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePersonalDisclaimerHolderDTO;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.resources.TenantSureResources;

public class TenantSurePersonalDisclaimerForm extends CEntityDecoratableForm<TenantSurePersonalDisclaimerHolderDTO> {

    private static final I18n i18n = I18n.get(TenantSurePersonalDisclaimerForm.class);

    private SimplePanel personalDisclaimerHolder;

    public TenantSurePersonalDisclaimerForm() {
        super(TenantSurePersonalDisclaimerHolderDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();
        int row = -1;

        contentPanel.setH1(++row, 0, 1, i18n.tr("Personal Disclaimer Terms"));
        contentPanel.setWidget(++row, 0, personalDisclaimerHolder = new SimplePanel());
        contentPanel.setBR(++row, 0, 1);
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().isAgreed(), new CCheckBox())).build());

        return contentPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        HTMLPanel personalDisclaimer = new HTMLPanel(getValue().terms().getValue());
        Anchor privacyPolicyAnchor = new Anchor(i18n.tr("Privacy Policy"));
        privacyPolicyAnchor.setHref(TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF);
        privacyPolicyAnchor.setTarget("_blank");
        personalDisclaimer.addAndReplaceElement(privacyPolicyAnchor, TenantSureResources.PRIVACY_POLICY_ANCHOR_ID);
        personalDisclaimerHolder.setWidget(personalDisclaimer);
    }
}
