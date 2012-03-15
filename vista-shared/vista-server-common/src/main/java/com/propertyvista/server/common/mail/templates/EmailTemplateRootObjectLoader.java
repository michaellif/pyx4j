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
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.server.common.mail.templates.model.ApplicationT;
import com.propertyvista.server.common.mail.templates.model.BuildingT;
import com.propertyvista.server.common.mail.templates.model.EmailTemplateContext;
import com.propertyvista.server.common.mail.templates.model.LeaseT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestCrmT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestT;
import com.propertyvista.server.common.mail.templates.model.PortalLinksT;
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
            AbstractUser user = context.user();
            t.requestorName().set(user.name());
            t.passwordResetUrl().setValue(
                    ServerSideConfiguration.instance().getMainApplicationURL() + DeploymentConsts.TENANT_URL + '?' + AuthenticationService.AUTH_TOKEN_ARG + '='
                            + context.accessToken().getValue());
        } else if (tObj instanceof PasswordRequestCrmT) {
            PasswordRequestCrmT t = (PasswordRequestCrmT) tObj;
            AbstractUser user = context.user();
            t.requestorName().set(user.name());
            t.passwordResetUrl().setValue(
                    ServerSideConfiguration.instance().getMainApplicationURL()
                            + AppPlaceInfo.absoluteUrl(DeploymentConsts.CRM_URL, CrmSiteMap.LoginWithToken.class, AuthenticationService.AUTH_TOKEN_ARG, context
                                    .accessToken().getValue()));
        } else if (tObj instanceof TenantT) {
            TenantInLease tenantInLease = context.tenantInLease();
            TenantT t = (TenantT) tObj;
            if (tenantInLease.tenant().user().isValueDetached()) {
                Persistence.service().retrieve(tenantInLease.tenant().user());
            }
            t.name().set(tenantInLease.tenant().user().name());
        } else if (tObj instanceof BuildingT) {
            BuildingT t = (BuildingT) tObj;
            TenantInLease tenantInLease = context.tenantInLease();
            Lease lease = getLease(tenantInLease);
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
            TenantInLease tenantInLease = context.tenantInLease();
            Application app = getApplication(tenantInLease);
            if (tenantInLease.tenant().user().isValueDetached()) {
                Persistence.service().retrieve(tenantInLease.tenant().user());
            }
            t.applicant().set(tenantInLease.tenant().user().name());
            t.refNumber().setValue(app.getPrimaryKey().toString());
        } else if (tObj instanceof LeaseT) {
            LeaseT t = (LeaseT) tObj;
            TenantInLease tenantInLease = context.tenantInLease();
            Lease lease = getLease(tenantInLease);
            if (tenantInLease.tenant().user().isValueDetached()) {
                Persistence.service().retrieve(tenantInLease.tenant().user());
            }
            t.applicant().set(tenantInLease.tenant().user().name());
            t.startDate().setValue(lease.leaseFrom().getStringView());
            t.startDateWeekday().setValue(new SimpleDateFormat("EEEE").format(lease.leaseFrom().getValue()));
        }
        return tObj;
    }

    private static Application getApplication(TenantInLease tenantInLease) {
        if (tenantInLease == null) {
            throw new Error("Context cannot be null");
        }

        if (tenantInLease.application().isValueDetached()) {
            Persistence.service().retrieve(tenantInLease.application());
        }
        if (tenantInLease.application() != null) {
            return tenantInLease.application();
        } else {
            throw new Error("Invalid context. No lease found.");
        }
    }

    private static Lease getLease(TenantInLease tenantInLease) {
        if (tenantInLease == null) {
            throw new Error("Context cannot be null");
        }

        if (tenantInLease.lease().isValueDetached()) {
            Persistence.service().retrieve(tenantInLease.lease());
        }
        if (tenantInLease.lease() != null) {
            return tenantInLease.lease();
        } else {
            throw new Error("Invalid context. No lease found.");
        }
    }

    private static Building getBuilding(Lease lease) {
        if (lease == null) {
            throw new Error("Context cannot be null");
        }
        if (lease.unit().isValueDetached()) {
            Persistence.service().retrieve(lease.unit());
            Persistence.service().retrieve(lease.unit().belongsTo());
        }
        Building bld = lease.unit().belongsTo();
        if (bld != null) {
            Persistence.service().retrieve(bld.contacts().propertyContacts());
            return bld;
        } else {
            throw new Error("Invalid context. No building found.");
        }
    }
}
