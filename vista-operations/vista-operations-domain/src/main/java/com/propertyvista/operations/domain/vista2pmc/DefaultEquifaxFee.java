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
 */
package com.propertyvista.operations.domain.vista2pmc;

import com.pyx4j.entity.annotations.Table;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;

@Table(prefix = "fee", namespace = VistaNamespace.operationsNamespace)
public interface DefaultEquifaxFee extends AbstractEquifaxFee {

}
