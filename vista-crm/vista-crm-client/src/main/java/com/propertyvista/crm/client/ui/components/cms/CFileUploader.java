/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 30, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.cms;

import com.pyx4j.forms.client.ui.CComponent;

import com.propertyvista.domain.File;

public class CFileUploader extends CComponent<File, NativeFileUploader> {

    @Override
    protected NativeFileUploader createWidget() {
        return new NativeFileUploader(this);
    }

}
