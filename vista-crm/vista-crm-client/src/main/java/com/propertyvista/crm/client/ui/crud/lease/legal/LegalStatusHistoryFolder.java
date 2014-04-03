/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.legal;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.legal.LegalStatus;

public class LegalStatusHistoryFolder extends VistaBoxFolder<LegalStatus> {

    public LegalStatusHistoryFolder() {
        super(LegalStatus.class);
        inheritEditable(false);
        inheritViewable(false);

        setEditable(true);
        setViewable(false);

        setRemovable(true);
        setAddable(false);
        setOrderable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof LegalStatus) {
            LegalStatusForm form = new LegalStatusForm(false);
            form.inheritViewable(false);
            form.setViewable(true);
            return form;
        }
        return super.create(member);
    }

    @Override
    public IFolderItemDecorator<LegalStatus> createItemDecorator() {
        VistaBoxFolderItemDecorator<LegalStatus> d = (VistaBoxFolderItemDecorator<LegalStatus>) super.createItemDecorator();
        d.setExpended(false);
        return d;
    }

}
