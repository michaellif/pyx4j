/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 3, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.oapi.xml.AbstractListIO;
import com.propertyvista.oapi.xml.Action;

@XmlRootElement(name = "contacts")
public class ContactListIO extends AbstractListIO<ContactIO> {

    public ContactListIO() {
        super();
    }

    public ContactListIO(Action action) {
        super(action);
    }

    @Override
    @XmlElement(name = "contact")
    public ArrayList<ContactIO> getList() {
        return super.getList();
    };
}
