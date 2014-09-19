/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.portal.rpc.portal.prospect.dto.OptionDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class ApplicationOptionsFolder extends PortalBoxFolder<OptionDTO> {

    private static final I18n i18n = I18n.get(ApplicationOptionsFolder.class);

    public ApplicationOptionsFolder(ApplicationWizardView view) {
        super(OptionDTO.class, false);
        setEditable(false);
    }

    @Override
    protected CForm<OptionDTO> createItemForm(IObject<?> member) {
        return new ApplicationOptionForm();
    }

    class ApplicationOptionForm extends CForm<OptionDTO> {

        public ApplicationOptionForm() {
            super(OptionDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Left, proto().item(), new CEntityLabel<ProductItem>()).decorate().componentWidth(200).customLabel(i18n.tr("Item Name"));
            formPanel.append(Location.Left, proto().price(), new CMoneyLabel()).decorate();
            return formPanel;
        }
    }
}
