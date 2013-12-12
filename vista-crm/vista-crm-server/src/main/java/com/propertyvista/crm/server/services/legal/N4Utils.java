/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-23
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.legal;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.legal.n4.N4LegalLetter;
import com.propertyvista.domain.tenant.lease.Lease;

public class N4Utils {

    private static final I18n i18n = I18n.get(N4Utils.class);

    public static String pastN4sCount(Lease leaseIdStub) {
        EntityQueryCriteria<N4LegalLetter> criteria = EntityQueryCriteria.create(N4LegalLetter.class);
        criteria.eq(criteria.proto().lease(), leaseIdStub);

        int count = Persistence.service().count(criteria);
        if (count > 0) {
            criteria.desc(criteria.proto().generatedOn());
            N4LegalLetter n4 = Persistence.service().retrieve(criteria);
            return i18n.tr("{0} (last: {1,date,MM/dd/yy})", count, n4.generatedOn().getValue());
        } else {
            return "0";
        }
    }
}
