/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.Locale;

public class PmcDomainNameToNamespaceMappingRuleImpl implements PmcDomainNameToNamespaceMappingRule {

    @Override
    public String makeNamespace(String pmcDomainName) {
        return (pmcDomainName.matches("[0-9].*") ? "d" + pmcDomainName : pmcDomainName).replace('-', '_').toLowerCase(Locale.ENGLISH);
    }

}
