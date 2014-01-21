/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.prospect;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n
public enum OnlineApplicationWizardStepMeta {

    Unit, Options, Lease, People, AboutYou, AdditionalInfo, Financial, Contacts, Legal, Summary, Payment, Confirmation;

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }
}
