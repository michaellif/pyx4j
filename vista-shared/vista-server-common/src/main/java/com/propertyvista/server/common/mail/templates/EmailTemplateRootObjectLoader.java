/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.common.mail.templates;

import java.text.SimpleDateFormat;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.server.common.mail.templates.model.ApplicationT;
import com.propertyvista.server.common.mail.templates.model.BuildingT;
import com.propertyvista.server.common.mail.templates.model.LeaseT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestCrmT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestT;
import com.propertyvista.server.common.mail.templates.model.PortalLinksT;
import com.propertyvista.server.common.mail.templates.model.EmailTemplateContext;
import com.propertyvista.server.common.mail.templates.model.TenantT;

public class EmailTemplateRootObjectLoader {

    public static <T extends IEntity> T loadRootObject(T tObj, EmailTemplateContext context) {
        if (tObj == null || context == null) {
            throw new Error("Loading object or Context cannot be null");
        }
        if (tObj instanceof PortalLinksT) {
            PortalLinksT t = (PortalLinksT) tObj;
            String portalBaseUrl = ServerSideConfiguration.instance().getMainApplicationURL();
            t.portalHomeUrl().setValue(portalBaseUrl + DeploymentConsts.PORTAL_URL);
            t.tenantHomeUrl().setValue(portalBaseUrl + DeploymentConsts.TENANT_URL);
            t.ptappHomeUrl().setValue(portalBaseUrl + AppPlaceInfo.absoluteUrl(DeploymentConsts.PTAPP_URL, null));
        } else if (tObj instanceof PasswordRequestT) {
            PasswordRequestT t = (PasswordRequestT) tObj;
            TenantUser tenant = context.tenant();
            t.requestorName().set(tenant.name());
            t.passwordResetUrl().setValue(
                    ServerSideConfiguration.instance().getMainApplicationURL() + DeploymentConsts.TENANT_URL + '?' + AuthenticationService.AUTH_TOKEN_ARG + '='
                            + context.accessToken().getValue());
        } else if (tObj instanceof PasswordRequestCrmT) {
            PasswordRequestCrmT t = (PasswordRequestCrmT) tObj;
            CrmUser user = context.crmUser();
            t.requestorName().set(user.name());
            t.passwordResetUrl().setValue(
                    ServerSideConfiguration.instance().getMainApplicationURL()
                            + AppPlaceInfo.absoluteUrl(DeploymentConsts.CRM_URL, CrmSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, context
                                    .accessToken().getValue()));
        } else if (tObj instanceof TenantT) {
            TenantUser tenant = context.tenant();
            TenantT t = (TenantT) tObj;
            t.name().set(tenant.name());
        } else if (tObj instanceof BuildingT) {
            BuildingT t = (BuildingT) tObj;
            TenantUser tenant = context.tenant();
            Application app = getApplication(tenant);
            Lease lease = getLease(app);
            Building bld = getBuilding(lease);
            t.propertyCode().set(bld.propertyCode());
            t.propertyName().set(bld.marketing().name());
            t.website().set(bld.contacts().website());
            t.address().setValue(bld.info().address().getStringView());
            // set contact info
            for (PropertyContact cont : bld.contacts().propertyContacts()) {
                if (cont.type().getValue().equals(PropertyContact.Type.administrator)) {
                    t.administrator().name().set(cont.name());
                    t.administrator().phone().set(cont.phone());
                    t.administrator().email().set(cont.email());
                } else if (cont.type().getValue().equals(PropertyContact.Type.superintendent)) {
                    t.superintendent().name().set(cont.name());
                    t.superintendent().phone().set(cont.phone());
                    t.superintendent().email().set(cont.email());
                } else if (cont.type().getValue().equals(PropertyContact.Type.mainOffice)) {
                    t.mainOffice().name().set(cont.name());
                    t.mainOffice().phone().set(cont.phone());
                    t.mainOffice().email().set(cont.email());
                }
            }
        } else if (tObj instanceof ApplicationT) {
            ApplicationT t = (ApplicationT) tObj;
            TenantUser tenant = context.tenant();
            Application app = getApplication(tenant);
            t.applicant().set(tenant.name());
            t.refNumber().setValue(app.belongsTo().getPrimaryKey().toString());
        } else if (tObj instanceof LeaseT) {
            LeaseT t = (LeaseT) tObj;
            TenantUser tenant = context.tenant();
            Application app = getApplication(tenant);
            Lease lease = getLease(app);
            t.applicant().set(tenant.name());
            t.startDate().setValue(lease.leaseFrom().getStringView());
            t.startDateWeekday().setValue(new SimpleDateFormat("EEEE").format(lease.leaseFrom().getValue()));
        }
        return tObj;
    }

    private static Application getApplication(TenantUser tu) {
        if (tu == null) {
            throw new Error("Context cannot be null");
        }
        EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), tu));
        Application app = Persistence.service().retrieve(criteria);
        if (app != null) {
            return app;
        } else {
            throw new Error("Invalid context. No application found.");
        }
    }

    private static Lease getLease(Application app) {
        if (app == null) {
            throw new Error("Context cannot be null");
        }
        if (app.lease().isValueDetached()) {
            Persistence.service().retrieve(app.lease());
        }
        if (app.lease() != null) {
            return app.lease();
        } else {
            throw new Error("Invalid context. No lease found.");
        }
    }

    private static Building getBuilding(Lease lease) {
        if (lease == null) {
            throw new Error("Context cannot be null");
        }
        if (lease.unit().floorplan().building().isValueDetached()) {
            Persistence.service().retrieve(lease.unit().floorplan().building());
        }
        Building bld = lease.unit().floorplan().building();
        if (bld != null) {
            return bld;
        } else {
            throw new Error("Invalid context. No building found.");
        }
    }
}
