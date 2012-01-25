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
package com.propertyvista.common.client.ui.components.folders;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.common.client.ui.components.editors.GuarantorInLeaseEditor;
import com.propertyvista.domain.tenant.GuarantorInLease;

public class GuarantorInLeaseFolder extends VistaBoxFolder<GuarantorInLease> {

    private final boolean twoColumns;

    public GuarantorInLeaseFolder(boolean modifyable) {
        this(modifyable, true);
    }

    public GuarantorInLeaseFolder(boolean modifyable, boolean twoColumns) {
        super(GuarantorInLease.class, modifyable);
        this.twoColumns = twoColumns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof GuarantorInLease) {
            return new GuarantorInLeaseEditor(twoColumns);
        }
        return super.create(member);
    }
}