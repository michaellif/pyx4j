/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.person.Name;
import com.propertyvista.portal.rpc.portal.prospect.dto.DependentDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class DependentsReadonlyFolder extends PortalBoxFolder<DependentDTO> {

    private static final I18n i18n = I18n.get(DependentsReadonlyFolder.class);

    public DependentsReadonlyFolder() {
        super(DependentDTO.class, false);
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof DependentDTO) {
            return (T) new CoapplicantForm();
        } else {
            return super.create(member);
        }
    }

    class CoapplicantForm extends CEntityForm<DependentDTO> {

        public CoapplicantForm() {
            super(DependentDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0, inject(proto().name(), new CEntityLabel<Name>(), new FieldDecoratorBuilder().build()));
            mainPanel.setWidget(++row, 0, inject(proto().birthDate(), new CDateLabel(), new FieldDecoratorBuilder().build()));
            mainPanel.setWidget(++row, 0, inject(proto().relationship(), new CEnumLabel(), new FieldDecoratorBuilder().build()));

            return mainPanel;
        }
    }
}