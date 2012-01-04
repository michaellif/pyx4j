/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 8, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.summary;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.portal.rpc.ptapp.dto.LegalTermsDescriptorDTO;

public class LeaseTemsFolder extends VistaBoxFolder<LegalTermsDescriptorDTO> {

    private static I18n i18n = I18n.get(LeaseTemsFolder.class);

    public final static String DEFAULT_STYLE_PREFIX = "LeaseTemsFolder";

    public static enum StyleSuffix implements IStyleName {
        LegalTermsDescriptor
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    public LeaseTemsFolder() {
        super(LegalTermsDescriptorDTO.class, false);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof LegalTermsDescriptorDTO) {
            return new DigitalSignatureEditor();
        }
        return super.create(member);
    }

    private class DigitalSignatureEditor extends CEntityDecoratableEditor<LegalTermsDescriptorDTO> {

        public DigitalSignatureEditor() {
            super(LegalTermsDescriptorDTO.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            main.setWidget(++row, 0, inject(proto().content().content()));
            main.setBR(++row, 0, 2);
            main.setWidget(++row, 0, inject(proto().agrees(), new AgreeFolder()));

            main.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.LegalTermsDescriptor.name());
            return main;
        }
    }
}
