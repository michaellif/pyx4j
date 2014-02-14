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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.pmc.info.PmcSignature;

public class PmcSignatureForm extends CEntityForm<PmcSignature> {

    private static final I18n i18n = I18n.get(PmcSignatureForm.class);

    public enum Styles implements IStyleName {
        PmcSignatureFormRepName, PmcSignatureFormPmcLegalName;
    }

    private String realFullName;

    private Label nameLabel;

    private Label pmcLegalNameLabel;

    public PmcSignatureForm() {
        super(PmcSignature.class);
        realFullName = null;
    }

    /** sets the full name used for comparison, set to <code>null</code> null to turn off validation */
    public void setFullName(String realFullName) {
        this.realFullName = realFullName;
        this.nameLabel.setText(realFullName);
    }

    public void setPmcLegalName(String pmcLegalName) {
        this.pmcLegalNameLabel.setText(pmcLegalName);
    }

    @Override
    public IsWidget createContent() {
        HorizontalPanel contentPanel = new HorizontalPanel();
        contentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        contentPanel.setSpacing(20);

        FlowPanel nameLabelPanel = new FlowPanel();
        nameLabel = new Label();
        nameLabel.addStyleName(Styles.PmcSignatureFormRepName.name());
        nameLabel.getElement().getStyle().setDisplay(Display.BLOCK);
        nameLabelPanel.add(nameLabel);

        pmcLegalNameLabel = new Label();
        pmcLegalNameLabel.addStyleName(Styles.PmcSignatureFormPmcLegalName.name());
        pmcLegalNameLabel.getElement().getStyle().setDisplay(Display.BLOCK);
        nameLabelPanel.add(pmcLegalNameLabel);
        contentPanel.add(nameLabelPanel);

        contentPanel.add(new WidgetDecoratorRightLabel(inject(proto().fullName()), 20, 0));
        get(proto().fullName()).setTitle("");

        ((CTextField) get(proto().fullName())).setWatermark(i18n.tr("SIGN FULL NAME HERE"));
        get(proto().fullName()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public FieldValidationError isValid(CComponent<String> component, String userSignature) {
                if (realFullName != null && userSignature != null && !realFullName.equals(userSignature)) {
                    return new FieldValidationError(component, i18n.tr("The signature doesn't match the name"));
                } else {
                    return null;
                }
            }
        });

        contentPanel.add(inject(proto().timestamp()));
        get(proto().timestamp()).setViewable(true);

        contentPanel.add(inject(proto().ipAddress()));
        get(proto().ipAddress()).setViewable(true);

        return contentPanel;
    }
}
