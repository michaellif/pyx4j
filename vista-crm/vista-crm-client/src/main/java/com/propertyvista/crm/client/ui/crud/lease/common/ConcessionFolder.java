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
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.financial.offering.Concession;

public class ConcessionFolder extends VistaBoxFolder<Concession> {

    private static final I18n i18n = I18n.get(ConcessionFolder.class);

    public ConcessionFolder() {
        super(Concession.class, false);
    }

    @Override
    protected CEntityForm<Concession> createItemForm(IObject<?> member) {
        return new ConcessionEditor();
    }

    private class ConcessionEditor extends CEntityForm<Concession> {

        public ConcessionEditor() {
            super(Concession.class);
            setEditable(false);
            setViewable(true);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

            int row = -1;
            content.setWidget(++row, 0, inject(proto().version().type(), new FieldDecoratorBuilder(12).build()));
            content.setWidget(++row, 0, inject(proto().version().value(), new FieldDecoratorBuilder(7).build()));
            content.setWidget(++row, 0, inject(proto().version().term(), new FieldDecoratorBuilder(12).build()));
            content.setWidget(++row, 0, inject(proto().version().condition(), new FieldDecoratorBuilder(10).build()));
            content.setWidget(++row, 0, inject(proto().version().mixable(), new FieldDecoratorBuilder(5).build()));

            content.setWidget(++row, 0, inject(proto().version().description(), new FieldDecoratorBuilder(60).build()));
            content.getFlexCellFormatter().setColSpan(row, 0, 2);

            row = -1;
            content.setWidget(++row, 1, inject(proto().version().effectiveDate(), new FieldDecoratorBuilder(9).build()));
            content.setWidget(++row, 1, inject(proto().version().expirationDate(), new FieldDecoratorBuilder(9).build()));
            content.setWidget(++row, 1, inject(proto().updated(), new FieldDecoratorBuilder(9).build()));

            return content;
        }
    }

    @Override
    public IFolderItemDecorator<Concession> createItemDecorator() {
        BoxFolderItemDecorator<Concession> decor = (BoxFolderItemDecorator<Concession>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }
}