/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.oapi.v1.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.propertyvista.oapi.xml.AbstractListIO;
import com.propertyvista.oapi.xml.Note;

@XmlType(name = "UnitList")
@XmlRootElement(name = "units")
public class UnitListIO extends AbstractListIO<UnitIO> {

    public UnitListIO() {
        super();
    }

    public UnitListIO(Note note) {
        super(note);
    }

    @Override
    @XmlElement(name = "unit")
    public ArrayList<UnitIO> getList() {
        return super.getList();
    };
}
