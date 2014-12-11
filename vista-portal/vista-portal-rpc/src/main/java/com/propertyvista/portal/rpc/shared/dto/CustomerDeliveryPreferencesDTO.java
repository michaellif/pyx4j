/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 23, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.shared.dto;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.settings.PmcCompanyInfo;
import com.propertyvista.domain.tenant.CustomerDeliveryPreferences;

@Transient
@ExtendsBO
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@SecurityEnabled
public interface CustomerDeliveryPreferencesDTO extends CustomerDeliveryPreferences {

}
