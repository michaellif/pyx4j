/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 18, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class AdjustmentFolder extends PortalBoxFolder<BillableItemAdjustment> {

    public AdjustmentFolder() {
        super(BillableItemAdjustment.class, false);
    }

    @Override
    protected CForm<BillableItemAdjustment> createItemForm(IObject<?> member) {
        return new CAdjustmentViewer();
    }

    private class CAdjustmentViewer extends CForm<BillableItemAdjustment> {

        public CAdjustmentViewer() {
            super(BillableItemAdjustment.class);
            setEditable(false);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().value()).decorate();
            formPanel.append(Location.Left, proto().effectiveDate(), new CDateLabel()).decorate();
            formPanel.append(Location.Left, proto().expirationDate(), new CDateLabel()).decorate();

            return formPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().effectiveDate()).setVisible(!getValue().effectiveDate().isNull());
            get(proto().expirationDate()).setVisible(!getValue().expirationDate().isNull());
        }
    };
}
