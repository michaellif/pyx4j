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
package com.propertyvista.crm.client.ui.crud.lease2;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.dto.LeaseTermDTO;

public class ConcessionFolder extends VistaBoxFolder<Concession> {

    private static final I18n i18n = I18n.get(ConcessionFolder.class);

    private final CEntityForm<LeaseTermDTO> leaseTerm;

    public ConcessionFolder(boolean modifyable, CEntityForm<LeaseTermDTO> parent) {
        super(Concession.class, modifyable);
        this.leaseTerm = parent;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Concession) {
            return new ConcessionEditor();
        }
        return super.create(member);

    }

    private class ConcessionEditor extends CEntityDecoratableForm<Concession> {

        public ConcessionEditor() {
            super(Concession.class);
            setEditable(false);
//            setViewable(true);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

            int row = -1;
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().type()), 12).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().value()), 7).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().term()), 12).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().condition()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().mixable()), 5).build());

            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().version().description()), 60).build());
            content.getFlexCellFormatter().setColSpan(row, 0, 2);

            row = -1;
            content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().version().effectiveDate()), 9).build());
            content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().version().expirationDate()), 9).build());
            content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().updated()), 9).build());

            content.getColumnFormatter().setWidth(0, "50%");
            content.getColumnFormatter().setWidth(1, "50%");

            return content;
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