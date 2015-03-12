/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 12, 2014
 * @author VladL
 */
package com.propertyvista.crm.client.ui.crud.policies.legalquestions;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.components.boxes.LocalizedContentFolderBase;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.LegalQuestionsPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LegalQuestionsPolicyItem;
import com.propertyvista.shared.i18n.CompiledLocale;

public class LegalQuestionsPolicyForm extends PolicyDTOTabPanelBasedForm<LegalQuestionsPolicyDTO> {

    private static final I18n i18n = I18n.get(LegalQuestionsPolicyForm.class);

    public LegalQuestionsPolicyForm(IPrimeFormView<LegalQuestionsPolicyDTO, ?> view) {
        super(LegalQuestionsPolicyDTO.class, view);

        addTab(createGeneralTab(), i18n.tr("Legal Questions"));
    }

    private IsWidget createGeneralTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().enabled()).decorate();
        formPanel.append(Location.Dual, proto().questions(), new LegalQuestionFolder(isEditable()));

        return formPanel;
    }

    private class LegalQuestionFolder extends LocalizedContentFolderBase<LegalQuestionsPolicyItem> {

        public LegalQuestionFolder(boolean editable) {
            super(LegalQuestionsPolicyItem.class, editable);
            setAllowDuplicateLocales(true);
        }

        @Override
        protected CForm<? extends LegalQuestionsPolicyItem> createItemForm(IObject<?> member) {
            return new CForm<LegalQuestionsPolicyItem>(LegalQuestionsPolicyItem.class) {

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Dual, proto().locale(), new CLabel<CompiledLocale>()).decorate();
                    formPanel.append(Location.Dual, proto().question()).decorate();

                    return formPanel;
                }
            };
        }
    }
}
