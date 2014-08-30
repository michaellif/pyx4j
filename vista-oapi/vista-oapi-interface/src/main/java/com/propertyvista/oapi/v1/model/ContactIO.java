/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlAttribute;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.StringIO;

public class ContactIO extends AbstractElementIO {

    @XmlAttribute
    public String name;

    public StringIO newName;

    public StringIO email;

    public StringIO phone;

    @Override
    public boolean equals(Object obj) {
        return name == ((ContactIO) obj).name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
