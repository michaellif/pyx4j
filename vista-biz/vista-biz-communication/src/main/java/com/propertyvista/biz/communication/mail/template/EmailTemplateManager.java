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
package com.propertyvista.biz.communication.mail.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

import com.propertyvista.biz.communication.mail.template.model.ApplicationT;
import com.propertyvista.biz.communication.mail.template.model.AutopayAgreementT;
import com.propertyvista.biz.communication.mail.template.model.BuildingT;
import com.propertyvista.biz.communication.mail.template.model.CompanyInfoT;
import com.propertyvista.biz.communication.mail.template.model.LeaseT;
import com.propertyvista.biz.communication.mail.template.model.MaintenanceRequestT;
import com.propertyvista.biz.communication.mail.template.model.MaintenanceRequestWOT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestCrmT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestProspectT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestTenantT;
import com.propertyvista.biz.communication.mail.template.model.PaymentT;
import com.propertyvista.biz.communication.mail.template.model.PortalLinksT;
import com.propertyvista.biz.communication.mail.template.model.TenantT;
import com.propertyvista.domain.communication.EmailTemplateType;

public class EmailTemplateManager {

    private static final Logger log = LoggerFactory.getLogger(EmailTemplateManager.class);

    public static List<IEntity> getTemplateDataObjects(EmailTemplateType template) {
        List<IEntity> values = new Vector<IEntity>();
        // add PortalLinks to all templates
        values.add(EntityFactory.create(PortalLinksT.class));
        values.add(EntityFactory.create(CompanyInfoT.class));
        switch (template) {
        case PasswordRetrievalCrm:
            values.add(EntityFactory.create(PasswordRequestCrmT.class));
            break;
        case PasswordRetrievalProspect:
            values.add(EntityFactory.create(PasswordRequestProspectT.class));
            break;
        case PasswordRetrievalTenant:
            values.add(EntityFactory.create(PasswordRequestTenantT.class));
            break;
        case ProspectWelcome:
            values.add(EntityFactory.create(ApplicationT.class));
            break;
        case ApplicationApproved:
            values.add(EntityFactory.create(ApplicationT.class));
            values.add(EntityFactory.create(LeaseT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        case ApplicationCreatedApplicant:
        case ApplicationCreatedCoApplicant:
        case ApplicationCreatedGuarantor:
        case ApplicationDeclined:
            values.add(EntityFactory.create(ApplicationT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        case TenantInvitation:
            values.add(EntityFactory.create(PasswordRequestTenantT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        case MaintenanceRequestCreatedPMC:
            values.add(EntityFactory.create(MaintenanceRequestT.class));
            break;
        case MaintenanceRequestCreatedTenant:
            values.add(EntityFactory.create(MaintenanceRequestT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        case MaintenanceRequestUpdated:
            values.add(EntityFactory.create(MaintenanceRequestT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        case MaintenanceRequestCompleted:
            values.add(EntityFactory.create(MaintenanceRequestT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        case MaintenanceRequestCancelled:
            values.add(EntityFactory.create(MaintenanceRequestT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        case MaintenanceRequestEntryNotice:
            values.add(EntityFactory.create(MaintenanceRequestT.class));
            values.add(EntityFactory.create(MaintenanceRequestWOT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        case AutoPaySetupConfirmation:
            values.add(EntityFactory.create(AutopayAgreementT.class));
            values.add(EntityFactory.create(TenantT.class));
            values.add(EntityFactory.create(LeaseT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        case OneTimePaymentSubmitted:
        case PaymentReceipt:
        case PaymentReceiptWithConvenienceFee:
        case PaymentReturned:
            values.add(EntityFactory.create(PaymentT.class));
            values.add(EntityFactory.create(TenantT.class));
            values.add(EntityFactory.create(LeaseT.class));
            values.add(EntityFactory.create(BuildingT.class));
            break;
        default:
            throw new Error("Unknown EmailTemplateType: " + template.name());
        }
        return values;
    }

    /**
     * Used for template editing
     */
    public static List<String> getTemplateDataObjectSelection(EmailTemplateType template) {
        List<IEntity> dataObjects = getTemplateDataObjects(template);
        List<String> selection = new ArrayList<String>();
        for (IEntity obj : dataObjects) {
            for (Path path : getTemplateEntityMemberGraph(obj, new ArrayList<Path>())) {
                selection.add(pathToVarname(path.toString()));
            }
        }
        return selection;
    }

    public static String parseTemplate(String htmlTemplate, Collection<IEntity> data) {
        // Simple two pass parsing to support header and footer
        return parseTemplateImpl(parseTemplateImpl(htmlTemplate, data), data);
    }

    private static String parseTemplateImpl(String htmlTemplate, Collection<IEntity> data) {
        final StringBuilder buffer = new StringBuilder();

        int start, pos = 0;
        while ((start = lowerPositive(htmlTemplate.indexOf("$$", pos), htmlTemplate.indexOf("${", pos))) != -1) {
            buffer.append(htmlTemplate.substring(pos, start));

            if (htmlTemplate.charAt(start + 1) == '$') {
                buffer.append("$");
                pos = start + 2;
                continue;
            }

            pos = start;
            final int varStart = start + 2;
            final int varEnd = htmlTemplate.indexOf('}', varStart);
            if (varEnd != -1) {
                final String varName = htmlTemplate.substring(varStart, varEnd);
                final String value = getVarValue(varName, data);
                if (value != null) {
                    buffer.append(value);
                }
                pos = varEnd + 1;
            } else {
                break;
            }
        }

        if (pos < htmlTemplate.length()) {
            buffer.append(htmlTemplate.substring(pos));
        }

        return buffer.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEntity> T getProto(EmailTemplateType type, Class<T> tClass) {
        for (IEntity obj : getTemplateDataObjects(type)) {
            if (tClass.isAssignableFrom(obj.getClass())) {
                return (T) obj;
            }
        }
        throw new Error("Invalid proto " + tClass.getName() + " for template " + type.name());
    }

    public static String getVarname(IObject<?> member) {
        return "${" + pathToVarname(new Path(member).toString()) + "}";
    }

    private static int lowerPositive(final int i1, final int i2) {
        if (i2 < 0) {
            return i1;
        } else if (i1 < 0) {
            return i2;
        } else {
            return i1 < i2 ? i1 : i2;
        }
    }

    private static String getVarValue(String varName, Collection<IEntity> data) {
        Path fromPath = new Path(varnameToPath(varName));
        for (IEntity obj : data) {
            // first, compare obj name against the root object of the given Path
            String objClassName = GWTJava5Helper.getSimpleName(obj.getObjectClass());
            if (objClassName.equals(fromPath.getRootObjectClassName())) {
                // do lookup by member's path
                return getStringMemberValue(fromPath, obj);
            }
        }
        return null;
    }

    private static String getStringMemberValue(Path memberPath, IEntity toEntity) {
        try {
            IObject<?> member = toEntity.getMember(memberPath);
            if ((member instanceof IEntity) && ((IEntity) member).isValueDetached()) {
                throw new Error("Copying detached entity " + ((IEntity) member).getDebugExceptionInfoString());
            }
            if (member instanceof IPrimitive<?>) {
                return member.getValue().toString();
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private static List<Path> getTemplateEntityMemberGraph(IEntity entity, List<Path> graph) {
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            IObject<?> member = entity.getMember(memberName);
            if (ObjectClassType.Primitive.equals(memberMeta.getObjectClassType())) {
                graph.add(new Path(member));
            } else if (memberMeta.isEntity()) {
                getTemplateEntityMemberGraph((IEntity) member, graph);
            } else {
                throw new Error("Template Entity must not contain collections: " + member.getPath().toString());
            }
        }
        return graph;
    }

    private static String pathToVarname(String path) {
        // convert from path with "/"-delimiter to varName with dotted notation
        // and remove ending T from the Root Object name
        String varName = path.replace("/", ".");
        if (varName.endsWith(".")) {
            varName = varName.substring(0, varName.length() - 1);
        }
        varName = varName.replaceFirst("T\\.", ".");
        return varName;
    }

    private static String varnameToPath(String varName) {
        // convert from varName with dotted notation to path with "/"-delimiter
        // and add T-ending to the Root Object names
        String path = varName.replaceFirst("\\.", "T.").replace(".", "/");
        return path + "/";
    }
}
