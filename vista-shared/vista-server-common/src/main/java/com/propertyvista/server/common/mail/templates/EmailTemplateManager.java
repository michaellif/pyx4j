/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.mail.templates;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.server.common.mail.templates.model.BuildingT;

public class EmailTemplateManager {

    public static List<IEntity> getRootObjectTemplates(EmailTemplateType templeate) {
        List<IEntity> templates = new Vector<IEntity>();
        switch (templeate) {
        // PasswordRetrievalCrm:
        // return EmpoyT, LoginURL 

        // case PasswordRetrievalTenant,
        // TenantT,LoginURL
        case ApplicationCreatedApplicant:
//          ApplicationCreatedCoApplicant,
//          ApplicationCreatedGuarantor,
//          ApplicationApproved,
//          ApplicationDeclined;
            templates.add(EntityFactory.create(BuildingT.class));
            // TenantT, LoginURL
            return templates;

        default:
            throw new Error("We missed it");
        }
    }

    /**
     * Used for editing template
     */
    public static List<String> getRootObjectTemplatesNames(EmailTemplateType templeate) {
        List<IEntity> templates = getRootObjectTemplates(templeate);
        List<String> list = new Vector<String>();
        for (IEntity ent : templates) {
            // ent.getValueClass().getSimpleName() // Remove T at the end

            //ent.getEntityMeta().getMemberNames()   ->  propertyCode,  legalName
        }
        //  [building, apparent ] 
        return null;
    }

    public static List<String> getRootObjectTemplatesNamesValues(EmailTemplateType templeate) {
        //  [building.propertyCode, building.legalName ]
        //  [apparent.No, apparent.price]
        return null;
    }

    public static String parsTemplate(String htmlTemplate, Collection<IEntity> data) {
        return null;
    }

}
