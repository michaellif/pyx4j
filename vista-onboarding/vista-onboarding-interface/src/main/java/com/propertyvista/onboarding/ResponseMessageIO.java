/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.onboarding;

import javax.xml.bind.annotation.XmlRootElement;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@XmlRootElement
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface ResponseMessageIO extends IEntity {

    public enum StatusCode {

        OK,

        MessageFormatError,

        AuthenticationFailed,

        ReadOnly,

        SystemDown,

        SystemError

    }

    /**
     * Status of the processing of complete request.
     */
    @NotNull
    IPrimitive<StatusCode> status();

    /**
     * Contains the error message when Vista CRM running in debug mode
     */
    IPrimitive<String> errorMessage();

    /**
     * Unique identifier for the XML message.
     * Returned unchanged from the request.
     */
    IPrimitive<String> messageId();

    IList<ResponseIO> responses();

}
