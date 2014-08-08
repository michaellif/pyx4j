/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 5, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.system.yardi;

@SuppressWarnings("serial")
public class YardiResponseException extends YardiServiceException {

    private final String responseXml;

    public YardiResponseException(String responseXml) {
        this(responseXml, null);
    }

    public YardiResponseException(String responseXml, String message) {
        super(message == null ? "Invalid Yardi Response" : message);
        this.responseXml = responseXml;
    }

    public String getResponse() {
        return responseXml;
    }
}
