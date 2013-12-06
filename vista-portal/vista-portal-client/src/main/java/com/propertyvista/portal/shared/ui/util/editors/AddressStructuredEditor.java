/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.domain.contact.AddressStructured;

public class AddressStructuredEditor extends AddressStructuredEditorImpl<AddressStructured> {

    public AddressStructuredEditor() {
        super(AddressStructured.class);
    }

    public AddressStructuredEditor(boolean showUnit) {
        super(AddressStructured.class, showUnit);
    }

    @Override
    public IsWidget createContent() {
        return internalCreateContent();
    }
}
