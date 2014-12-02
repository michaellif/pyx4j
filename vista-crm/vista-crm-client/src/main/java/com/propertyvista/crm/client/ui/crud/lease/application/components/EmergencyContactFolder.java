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
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.tenant.EmergencyContact;

public class EmergencyContactFolder extends VistaBoxFolder<EmergencyContact> {

    private static final I18n i18n = I18n.get(EmergencyContactFolder.class);

    private final boolean collapsed;

    private boolean isMandatory = false;

    private int contactsAmount = 1;

    public EmergencyContactFolder() {
        this(false);
    }

    public EmergencyContactFolder(boolean collapsed) {
        super(EmergencyContact.class);
        this.collapsed = collapsed;
    }

    public void setRestrictions(boolean isMandatory, int contactsAmount) {
        this.isMandatory = isMandatory;
        this.contactsAmount = contactsAmount;
    }

    @Override
    public VistaBoxFolderItemDecorator<EmergencyContact> createItemDecorator() {
        VistaBoxFolderItemDecorator<EmergencyContact> decor = super.createItemDecorator();
        decor.setExpended(isEditable() && !collapsed);
        return decor;
    }

    @Override
    protected CForm<EmergencyContact> createItemForm(IObject<?> member) {
        return new EmergencyContactEditor();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        this.addComponentValidator(new AbstractComponentValidator<IList<EmergencyContact>>() {
            @Override
            public BasicValidationError isValid() {
                if (getValue() == null) {
                    return null;
                }

                if (isMandatory && getValue().size() < contactsAmount) {
                    return new BasicValidationError(getCComponent(), i18n.tr("At least {0} Emergency Contact(s) should be specified", contactsAmount));
                }

                return !EntityGraph.hasBusinessDuplicates(getValue()) ? null : new BasicValidationError(getCComponent(), i18n
                        .tr("Duplicate Emergency Contacts specified"));
            }
        });
    }
}