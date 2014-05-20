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
package com.propertyvista.crm.client.ui.crud.lease.common.term;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.dto.LeaseTermDTO;

public class ConcessionFolder extends VistaBoxFolder<Concession> {

    private static final I18n i18n = I18n.get(ConcessionFolder.class);

    private final CForm<LeaseTermDTO> leaseTerm;

    public ConcessionFolder(boolean modifyable, CForm<LeaseTermDTO> parent) {
        super(Concession.class, modifyable);
        this.leaseTerm = parent;
    }

    @Override
    protected CForm<Concession> createItemForm(IObject<?> member) {
        return new ConcessionEditor();
    }

    private class ConcessionEditor extends CForm<Concession> {

        public ConcessionEditor() {
            super(Concession.class);
            setEditable(false);
//            setViewable(true);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().version().type()).decorate().componentWidth(160);
            formPanel.append(Location.Left, proto().version().value()).decorate().componentWidth(100);
            formPanel.append(Location.Left, proto().version().term()).decorate().componentWidth(160);
            formPanel.append(Location.Left, proto().version().condition()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().version().mixable()).decorate().componentWidth(80);
            formPanel.append(Location.Right, proto().version().effectiveDate()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().version().expirationDate()).decorate().componentWidth(120);
            formPanel.append(Location.Right, proto().updated()).decorate().componentWidth(120);
            formPanel.append(Location.Dual, proto().version().description()).decorate();

            return formPanel;
        }
    }

    @Override
    public IFolderItemDecorator<Concession> createItemDecorator() {
        BoxFolderItemDecorator<Concession> decor = (BoxFolderItemDecorator<Concession>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected void addItem() {
        if (leaseTerm.getValue().version().leaseProducts().serviceItem().isNull()) {
            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select A Service Item First"));
        } else {

            new EntitySelectorListDialog<Concession>(i18n.tr("Select Concessions"), true, leaseTerm.getValue().selectedConcessions()) {
                @Override
                public boolean onClickOk() {
                    for (Concession item : getSelectedItems()) {
                        addItem(item);
                    }
                    return true;
                }
            }.show();
        }

    }
}