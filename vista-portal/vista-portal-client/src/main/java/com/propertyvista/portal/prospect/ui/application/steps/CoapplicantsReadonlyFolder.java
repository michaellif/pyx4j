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
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.person.Name;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class CoapplicantsReadonlyFolder extends PortalBoxFolder<CoapplicantDTO> {

    public CoapplicantsReadonlyFolder() {
        super(CoapplicantDTO.class, false);
    }

    @Override
    protected CForm<CoapplicantDTO> createItemForm(IObject<?> member) {
        return new CoapplicantForm();
    }

    class CoapplicantForm extends CForm<CoapplicantDTO> {

        public CoapplicantForm() {
            super(CoapplicantDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);
            formPanel.append(Location.Left, proto().name(), new CEntityLabel<Name>()).decorate();
            formPanel.append(Location.Left, proto().relationship(), new CEnumLabel()).decorate();
            formPanel.append(Location.Left, proto().email(), new CLabel<String>()).decorate();

            return formPanel;
        }

    }
}