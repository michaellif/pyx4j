/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2013
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import com.propertyvista.domain.tenant.insurance.GeneralInsuranceCertificate;
import com.propertyvista.domain.tenant.lease.Tenant;

public interface GeneralInsuranceFacade {

    public void createGeneralTenantInsurance(Tenant tenantId, GeneralInsuranceCertificate certifcateId);

    public void deleteGeneralInsurance(GeneralInsuranceCertificate deletedCertificate);

}
