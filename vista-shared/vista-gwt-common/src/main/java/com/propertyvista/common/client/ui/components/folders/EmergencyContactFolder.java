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
package com.propertyvista.common.client.ui.components.folders;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.editors.EmergencyContactEditor;
import com.propertyvista.domain.tenant.EmergencyContact;

public class EmergencyContactFolder extends VistaBoxFolder<EmergencyContact> {

    private final boolean modifyable;

    private final boolean twoColumns;

    public EmergencyContactFolder(boolean modifyable) {
        this(modifyable, true);
    }

    public EmergencyContactFolder(boolean modifyable, boolean twoColumns) {
        super(EmergencyContact.class, modifyable);
        this.modifyable = modifyable;
        this.twoColumns = twoColumns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof EmergencyContact) {
            return new EmergencyContactEditor(twoColumns);
        } else {
            return super.create(member);
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (modifyable && getValue().isEmpty()) {
            addItem(); // at least one Emergency Contact should be present!..
        }
    }
}