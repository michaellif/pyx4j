/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-19
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.LeaseTermV;

public class CLeaseTermVHyperlink extends CEntityLabel<LeaseTermV> {

    public CLeaseTermVHyperlink() {
        super();

        setNavigationCommand(new Command() {
            @Override
            public void execute() {
                if (!getValue().isNull()) {
                    AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(LeaseTerm.class).formViewerPlace(getValue().holder().getPrimaryKey()));
                }
            }
        });

        setFormat(new IFormat<LeaseTermV>() {
            @Override
            public String format(LeaseTermV value) {
                if (value != null) {
                    return value.holder().getStringView();
                } else {
                    return null;
                }
            }

            @Override
            public LeaseTermV parse(String string) {
                return null;
            }
        });
    }
}
