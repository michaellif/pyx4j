/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.pmc.fee;

import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;

//TODO
@Transient
@Table(prefix = "fee", namespace = VistaNamespace.adminNamespace)
public interface PmcEquifaxFee extends AbstractEquifaxFee {

    @Owner
    Pmc pmc();
}
