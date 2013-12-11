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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.portal.rpc.portal.prospect.dto.OptionDTO;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class ApplicationOptionsFolder extends CEntityFolder<OptionDTO> {

    private static final I18n i18n = I18n.get(ApplicationOptionsFolder.class);

    private final ApplicationWizardViewImpl view;

    public ApplicationOptionsFolder(ApplicationWizardViewImpl view) {
        super(OptionDTO.class);
        this.view = view;
        setEditable(false);
    }

    @Override
    public IFolderItemDecorator<OptionDTO> createItemDecorator() {
        BoxFolderItemDecorator<OptionDTO> decor = new BoxFolderItemDecorator<OptionDTO>(VistaImages.INSTANCE);
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected IFolderDecorator<OptionDTO> createFolderDecorator() {
        return new BoxFolderDecorator<OptionDTO>(VistaImages.INSTANCE);
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
