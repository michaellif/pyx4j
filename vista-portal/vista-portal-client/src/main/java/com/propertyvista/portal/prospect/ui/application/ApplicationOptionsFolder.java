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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.portal.rpc.portal.prospect.dto.OptionDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class ApplicationOptionsFolder extends PortalBoxFolder<OptionDTO> {

    private static final I18n i18n = I18n.get(ApplicationOptionsFolder.class);

    private final ApplicationWizardView view;

    public ApplicationOptionsFolder(ApplicationWizardView view) {
        super(OptionDTO.class, false);
        this.view = view;
        setEditable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof OptionDTO) {
            return new ApplicationOptionForm();
        } else {
            return super.create(member);
        }
    }

    class ApplicationOptionForm extends CEntityForm<OptionDTO> {

        public ApplicationOptionForm() {
            super(OptionDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0,
                    new FormWidgetDecoratorBuilder(inject(proto().item(), new CEntityLabel<ProductItem>()), 200).customLabel(i18n.tr("Item Name")).build());

            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().price(), new CMoneyLabel()), 100).build());
            return mainPanel;
        }
    }
}
