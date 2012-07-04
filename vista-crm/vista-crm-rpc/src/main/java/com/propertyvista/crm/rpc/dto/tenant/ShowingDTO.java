/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.tenant;

import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.lead.Showing;

@Transient
@ExtendsDBO(Showing.class)
public interface ShowingDTO extends Showing {

    /**
     * Used to hold province used as filter for unit selection dialog
     */
    Province province();

    /**
     * Used to hold city used as filter for unit selection dialog
     */
    IPrimitive<String> city();

}
