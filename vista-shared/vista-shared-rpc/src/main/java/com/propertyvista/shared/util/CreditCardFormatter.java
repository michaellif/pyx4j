/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 20, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.shared.util;

import com.pyx4j.commons.PersonalIdentityFormatter;

public class CreditCardFormatter extends PersonalIdentityFormatter {

    public CreditCardFormatter() {
        super("X XXXX XXXX xxxx;XXXX XXXX XXXX xxxx");
    }

}
