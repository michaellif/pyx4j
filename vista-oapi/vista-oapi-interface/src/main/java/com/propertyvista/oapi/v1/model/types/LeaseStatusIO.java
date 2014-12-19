/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 */
package com.propertyvista.oapi.v1.model.types;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.oapi.xml.Note;
import com.propertyvista.oapi.xml.PrimitiveIO;

@XmlType(name = "LeaseStatusHolder")
public class LeaseStatusIO implements PrimitiveIO<Lease.Status> {

    private Note note;

    private Lease.Status value;

    public LeaseStatusIO() {
    }

    public LeaseStatusIO(Lease.Status value) {
        setValue(value);
    }

    @XmlAttribute
    @Override
    public Note getNote() {
        return note;
    }

    @Override
    public void setNote(Note note) {
        this.note = note;
    }

    @XmlValue
    @Override
    public Lease.Status getValue() {
        return value;
    }

    @Override
    public void setValue(Lease.Status value) {
        this.value = value;
    }
}
