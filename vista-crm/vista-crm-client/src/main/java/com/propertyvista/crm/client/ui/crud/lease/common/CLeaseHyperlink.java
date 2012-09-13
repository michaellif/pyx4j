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

import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseDTO;

public class CLeaseHyperlink extends CEntityHyperlink<Lease> {

    public CLeaseHyperlink() {
        super(null);

        setCommand(new Command() {
            @Override
            public void execute() {
                if (!getValue().isNull()) {
                    CrudAppPlace place;
                    if (getValue().status().getValue().isDraft()) {
                        place = AppPlaceEntityMapper.resolvePlace(LeaseApplicationDTO.class);
                    } else {
                        place = AppPlaceEntityMapper.resolvePlace(LeaseDTO.class);
                    }
                    AppSite.getPlaceController().goTo(place.formViewerPlace(getValue().getPrimaryKey()));
                }
            }
        });
    }
}
