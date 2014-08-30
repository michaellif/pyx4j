/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlAttribute;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.StringIO;

public class GlCodeIO extends AbstractElementIO {

    @XmlAttribute
    public String codeId;

    public StringIO description;

    public GlCodeCategoryIO glCodeCategory;

    @Override
    public boolean equals(Object obj) {
        return codeId == ((GlCodeIO) obj).codeId;
    }

    @Override
    public int hashCode() {
        return codeId.hashCode();
    }

}
