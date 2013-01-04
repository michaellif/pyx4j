/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.pmc.info.PmcSignature;

public class PmcSignatureForm extends CEntityDecoratableForm<PmcSignature> {

    private static final I18n i18n = I18n.get(PmcSignatureForm.class);

    private String realFullName;

    public PmcSignatureForm() {
        super(PmcSignature.class);
        realFullName = null;
    }

    /** sets the full name used for comparison, set to <code>null</code> null to turn off validation */
    public void setFullName(String realFullName) {
        this.realFullName = realFullName;
    }

    @Override
    public IsWidget createContent() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setSpacing(20);

        panel.add(new DecoratorBuilder(inject(proto().fullName())).customLabel("").useLabelSemicolon(false).labelWidth(0).componentWidth(15).build());
        ((CTextField) get(proto().fullName())).setWatermark(i18n.tr("SIGN FULL NAME HERE"));
        get(proto().fullName()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String, ?> component, String userSignature) {
                if (realFullName != null && userSignature != null && !realFullName.equals(userSignature)) {
                    return new ValidationError(component, i18n.tr("The signature doesn't match the name"));
                } else {
                    return null;
                }
            }
        });

        panel.add(inject(proto().timestamp()));
        get(proto().timestamp()).setViewable(true);

        panel.add(inject(proto().ipAddress()));
        get(proto().ipAddress()).setViewable(true);

        return panel;
    }
}
