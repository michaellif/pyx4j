/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.n4generation.base;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.BulkEditableEntity;

public abstract class BulkEditableEntityForm<Item extends BulkEditableEntity> extends CEntityDecoratableForm<Item> {

    public BulkEditableEntityForm(Class<Item> clazz) {
        super(clazz);
    }

    public void setChecked(boolean isChecked) {
        get(proto().isSelected()).setValue(isChecked);
    }

}
