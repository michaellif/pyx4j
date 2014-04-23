/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.ui.CEntityLabel;

import com.propertyvista.domain.tenant.lease.BillableItem;

public class PapBillableItemLabel extends CEntityLabel<BillableItem> {

    public PapBillableItemLabel() {
        super();
        setFormatter(new IFormatter<BillableItem, String>() {
            @Override
            public String format(BillableItem value) {
                if (value != null) {
                    String res = "";

                    if (!value.description().isNull()) {
                        res += value.description().getValue();
                    } else if (!value.item().isNull()) {
                        res += value.item().name().getStringView();
                    }

                    return res;
                }

                return null;
            }

        });
    }

}