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
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionFolder extends VistaBoxFolder<Concession> {

    public ConcessionFolder() {
        super(Concession.class, false);
    }

    @Override
    protected CForm<Concession> createItemForm(IObject<?> member) {
        return new ConcessionEditor();
    }

    private class ConcessionEditor extends CForm<Concession> {

        public ConcessionEditor() {
            super(Concession.class);
            setEditable(false);
            setViewable(true);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().version().type()).decorate().componentWidth(160);
            formPanel.append(Location.Left, proto().version().value()).decorate().componentWidth(90);
            formPanel.append(Location.Left, proto().version().term()).decorate().componentWidth(160);
            formPanel.append(Location.Left, proto().version().condition()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().version().mixable()).decorate().componentWidth(70);

            formPanel.append(Location.Right, proto().version().effectiveDate()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().version().expirationDate()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().updated()).decorate().componentWidth(120);

            formPanel.append(Location.Dual, proto().version().description()).decorate();

            return formPanel;
        }
    }

    @Override
    public VistaBoxFolderItemDecorator<Concession> createItemDecorator() {
        VistaBoxFolderItemDecorator<Concession> decor = super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }
}