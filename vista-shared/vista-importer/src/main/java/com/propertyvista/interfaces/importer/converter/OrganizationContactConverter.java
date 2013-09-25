/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.entity.shared.utils.EntityBinder;

import com.propertyvista.domain.company.OrganizationContact;
import com.propertyvista.interfaces.importer.model.ContactIO;

public class OrganizationContactConverter extends EntityBinder<OrganizationContact, ContactIO> {

    public OrganizationContactConverter() {
        super(OrganizationContact.class, ContactIO.class, false);
    }

    @Override
    protected void bind() {
        bind(toProto.name(), boProto.person().name());
        bind(toProto.title(), boProto.person().title());
        bind(toProto.description(), boProto.description());
        bind(toProto.email(), boProto.person().email());
        bind(toProto.workPhone(), boProto.person().workPhone());
    }
}
