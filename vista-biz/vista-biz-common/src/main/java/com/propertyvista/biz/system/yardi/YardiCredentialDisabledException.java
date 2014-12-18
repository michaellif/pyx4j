/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2014
 * @author stanp
 */
package com.propertyvista.biz.system.yardi;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;

@SuppressWarnings("serial")
public class YardiCredentialDisabledException extends UserRuntimeException {

    private static I18n i18n = I18n.get(YardiCredentialDisabledException.class);

    private static final String YARDI_CREDENTIALS_ERROR = "Yardi interface is temporarily disabled.";

    public YardiCredentialDisabledException() {
        super(i18n.tr(YARDI_CREDENTIALS_ERROR));
    }
}
