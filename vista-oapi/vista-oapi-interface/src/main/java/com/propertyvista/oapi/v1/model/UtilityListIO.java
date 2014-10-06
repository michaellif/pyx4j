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

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.propertyvista.oapi.xml.AbstractListIO;
import com.propertyvista.oapi.xml.Note;

@XmlType(name = "UtilityList")
@XmlRootElement(name = "utilities")
public class UtilityListIO extends AbstractListIO<UtilityIO> {

    public UtilityListIO() {
        super();
    }

    public UtilityListIO(Note note) {
        super(note);
    }

    @Override
    @XmlElement(name = "utility")
    public List<UtilityIO> getList() {
        return super.getList();
    };
}
