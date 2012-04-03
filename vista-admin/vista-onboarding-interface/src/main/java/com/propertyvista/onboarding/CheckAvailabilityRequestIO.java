/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 * the License.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding;

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CheckAvailabilityRequestIO extends RequestIO {

    /**
     * May contain up to 63 characters. The characters allowed in a label are a subset of the ASCII character set, and includes the characters a through z, A
     * through Z, digits 0 through 9, and the hyphen.
     */
    @NotNull
    IPrimitive<String> dnsName();

    /**
     * Do not send it
     */
    @Override
    @XmlTransient
    IPrimitive<String> onboardingAccountId();

}
