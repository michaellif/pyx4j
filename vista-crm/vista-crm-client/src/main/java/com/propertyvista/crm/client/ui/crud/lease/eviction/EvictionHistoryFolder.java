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
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.dto.EvictionCaseDTO;

public class EvictionHistoryFolder extends VistaBoxFolder<EvictionCaseDTO> {

    public EvictionHistoryFolder() {
        super(EvictionCaseDTO.class);
        inheritEditable(false);
        inheritViewable(false);

        setEditable(true);
        setViewable(false);

        setRemovable(true);
        setAddable(false);
        setOrderable(false);
    }

    @Override
    protected CForm<EvictionCaseDTO> createItemForm(IObject<?> member) {
        EvictionCaseForm form = new EvictionCaseForm(false);
        form.inheritViewable(false);
        form.setViewable(true);
        return form;
    }

}
