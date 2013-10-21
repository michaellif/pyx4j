/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.n4;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;
import com.propertyvista.domain.policy.dto.N4PolicyDTOARCodeHolderDTO;

public class N4PolicyForm extends PolicyDTOTabPanelBasedForm<N4PolicyDTO> {

    public static final I18n i18n = I18n.get(N4PolicyForm.class);

    public N4PolicyForm(IForm<N4PolicyDTO> view) {
        super(N4PolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        TwoColumnFlexFormPanel settingsPanel = new TwoColumnFlexFormPanel(i18n.tr("Settings"));
        int row = -1;
        settingsPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().includeSignature())).build());
        settingsPanel.setH1(++row, 0, 2, i18n.tr("The following information will be used for signing N4 letters:"));
        settingsPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().companyName())).build());
        settingsPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().phoneNumber())).build());
        settingsPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().faxNumber())).build());
        settingsPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().emailAddress())).build());
        settingsPanel.setWidget(++row, 0, 2, inject(proto().mailingAddress(), new AddressSimpleEditor()));
        settingsPanel.setH1(++row, 0, 2, i18n.tr("AR Codes used to search for delinquent leases:"));
        settingsPanel.setWidget(++row, 0, 2, inject(proto().arCodes(), new ARCodeFolder()));
        return Arrays.asList(settingsPanel);
    }

    public static class ARCodeFolder extends VistaTableFolder<N4PolicyDTOARCodeHolderDTO> {

        public ARCodeFolder() {
            super(N4PolicyDTOARCodeHolderDTO.class);
            setOrderable(false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                    new EntityFolderColumnDescriptor(proto().arCode(), "200px")
            );//@formatter:on
        }

    }
}
