/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.text.ParseException;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.legal.n4.N4LegalLetter;

public class N4LegalLetterFolder extends VistaBoxFolder<N4LegalLetter> {

    private static final I18n i18n = I18n.get(N4LegalLetter.class);

    public N4LegalLetterFolder() {
        super(N4LegalLetter.class);
        setAddable(false);
        setViewable(true);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof N4LegalLetter) {
            return new N4LegalLetterForm();
        }
        return super.create(member);
    }

    private static class N4LegalLetterForm extends CEntityDecoratableForm<N4LegalLetter> {

        public N4LegalLetterForm() {
            super(N4LegalLetter.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
            int row = -1;
            panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().generatedOn())).build());
            panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().amountOwed())).build());
            panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().file().fileName(), createN4Link())).build());
            return panel;
        }

        private CTextField createN4Link() {
            CTextField n4Link = new CTextField();
            n4Link.setViewable(true);
            n4Link.setNavigationCommand(new Command() {
                @Override
                public void execute() {
                    Window.open(MediaUtils.createLegalLetterDocumentUrl(N4LegalLetterForm.this.getValue()), "_blank", null);
                }
            });
            n4Link.setFormat(new IFormat<String>() {
                @Override
                public String format(String value) {
                    if (value == null || value.equals("")) {
                        return i18n.tr("No File");
                    } else {
                        return value;
                    }
                }

                @Override
                public String parse(String string) throws ParseException {
                    return string;
                }
            });
            return n4Link;
        }
    }
}
