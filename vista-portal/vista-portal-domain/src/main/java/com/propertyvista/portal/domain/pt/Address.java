/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author antonk
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.domain.pt;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Table(name = "pt_address")
public interface Address extends IEntity, IAddress {

    public enum OwnedRented {
        Owned, Rented
    }

    IPrimitive<Date> moveInDate();

    IPrimitive<Date> moveOutDate();

    IPrimitive<Double> payment();

    IPrimitive<String> phone();

    @Caption(name = "Owned/Rented")
    @Editor(type = EditorType.radiogroup)
    IPrimitive<OwnedRented> rented();

    IPrimitive<Boolean> canadian();

    IPrimitive<String> managerName();
}
