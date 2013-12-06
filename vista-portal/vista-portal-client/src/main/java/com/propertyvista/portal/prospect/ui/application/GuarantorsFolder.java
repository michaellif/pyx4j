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
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.portal.rpc.portal.prospect.dto.GuarantorDTO;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class GuarantorsFolder extends CEntityFolder<GuarantorDTO> {

    private static final I18n i18n = I18n.get(GuarantorsFolder.class);

    private final ApplicationWizardViewImpl view;

    public GuarantorsFolder(ApplicationWizardViewImpl view) {
        super(GuarantorDTO.class);
        this.view = view;
        setRemovable(true);
        setOrderable(true);
    }

    @Override
    public IFolderItemDecorator<GuarantorDTO> createItemDecorator() {
        BoxFolderItemDecorator<GuarantorDTO> decor = new BoxFolderItemDecorator<GuarantorDTO>(VistaImages.INSTANCE);
        return decor;
    }

    @Override
    protected IFolderDecorator<GuarantorDTO> createFolderDecorator() {
        return new BoxFolderDecorator<GuarantorDTO>(VistaImages.INSTANCE, i18n.tr("Add Guarantor"));
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof GuarantorDTO) {
            return new GuarantorForm();
        } else {
            return super.create(member);
        }
    }

    class GuarantorForm extends CEntityForm<GuarantorDTO> {

        public GuarantorForm() {
            super(GuarantorDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().firstName())).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().lastName())).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().email())).build());

            return mainPanel;
        }

    }
}
