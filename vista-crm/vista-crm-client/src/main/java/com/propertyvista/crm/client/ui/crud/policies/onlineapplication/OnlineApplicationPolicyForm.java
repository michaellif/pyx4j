/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.onlineapplication;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CSignature;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.OnlineApplicationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.OnlineApplicationLegalTerm;

public class OnlineApplicationPolicyForm extends PolicyDTOTabPanelBasedForm<OnlineApplicationPolicyDTO> {

    private static final I18n i18n = I18n.get(OnlineApplicationPolicyForm.class);

    public OnlineApplicationPolicyForm(IForm<OnlineApplicationPolicyDTO> view) {
        super(OnlineApplicationPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createPaymentAuthorizationPanel());
    }

    private TwoColumnFlexFormPanel createPaymentAuthorizationPanel() {
        TwoColumnFlexFormPanel container = new TwoColumnFlexFormPanel(i18n.tr("Legal Tab"));
        int row = -1;
        container.setWidget(++row, 0, 2, inject(proto().customLegalTabTitle(), new LegalTabTitleFolder(isEditable())));
        container.setWidget(++row, 0, 2, inject(proto().terms(), new LegalTermFolder(isEditable())));
        return container;
    }

    private static class LegalTermFolder extends VistaBoxFolder<OnlineApplicationLegalTerm> {

        public LegalTermFolder(boolean isEditable) {
            super(OnlineApplicationLegalTerm.class, isEditable);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if ((member instanceof OnlineApplicationLegalTerm)) {
                return new LegalTermEditor();
            }
            return super.create(member);
        }

        class LegalTermEditor extends CEntityForm<OnlineApplicationLegalTerm> {

            public LegalTermEditor() {
                super(OnlineApplicationLegalTerm.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                int row = -1;

                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().content(), new LegalTermContentFolder(isEditable()))).build());
                main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().signatureType(), new CSignature())).build());

                return main;
            }
        }
    }
}
