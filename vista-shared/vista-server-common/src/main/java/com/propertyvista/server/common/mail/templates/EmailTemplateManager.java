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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.server.common.mail.templates.model.ApplicationT;
import com.propertyvista.server.common.mail.templates.model.CompanyT;
import com.propertyvista.server.common.mail.templates.model.NewPasswordT;

public class EmailTemplateManager {

    /*
     * PasswordRetrievalCrm
     * - {userName}
     * - {passwordResetUrl}
     * PasswordRetrievalTenant
     * ApplicationCreatedApplicant
     * - {applicant.firstname}
     * - {application.reference}
     * - {pmc.prospectportalsite.com}
     * - {PMC COMPANY NAME}
     * - {PMC CHOSEN CONTACT PERSON... could be the Superintendent, the office, the admin, etc}
     * ApplicationCreatedCoApplicant
     * - {co-applicant.firstname}
     * - {application.reference}
     * - {pmc.prospecportalsite.com}
     * - {PMC CHOSEN Phone NUMBER}
     * - {PMC COMPANY NAME}
     * - {PMC CHOSEN CONTACT PERSON... could be the Superintendent, the office, the admin, etc}
     * ApplicationCreatedGuarantor
     * - {guarantor.firstname}
     * - {application.reference}
     * - {pmc.prospecportalsite.com}
     * - {PMC CHOSEN Phone NUMBER}
     * - {PMC COMPANY NAME}
     * - {PMC CHOSEN CONTACT PERSON... could be the Superintendent, the office, the admin, etc}
     * ApplicationApproved
     * - {applicant.firstName}
     * - {lease.startDateWeekday}
     * - {lease.startDate}
     * - {pmc.website}
     * - {pmc.tenantportalsite.com}
     * - {pmc.companyName}
     * - {pmc.contanctPerson}
     */

    private static final String template1 = "Building Information:\n" + "Property Code: ${BuildingT/propertyCode/}\n" + "Website: ${BuildingT/website/}\n"
            + "E-mail Address: ${BuildingT/email/}\n";

    private static final String template2 = "Congratulations ${ApplicationT/applicant/firstName/}!\n"
            + "The Application Reference Number is: ${ApplicationT/refNumber/}\n" + "Sincerely,\n" + "${CompanyT/name/} Team\n"
            + "${CompanyT/contacts/administrator/name/firstName/} ${CompanyT/contacts/administrator/name/lastName/}\n";

    public static void main(String[] args) {
        System.out.println("EmailTemplateManager started...");
        for (String path : getTemplateDataObjectSelection(EmailTemplateType.ApplicationCreatedApplicant)) {
            System.out.println("===> " + path);
        }

        // TODO load tenant object
        TenantInLease til = EntityFactory.create(TenantInLease.class);
        til.tenant().user().name().setValue("Donald Duck");
        til.tenant().person().name().firstName().setValue("Donald");
        til.application().belongsTo().setPrimaryKey(new Key(123));
        til.lease().leaseFrom().setValue(new LogicalDate());

        ArrayList<IEntity> data = new ArrayList<IEntity>();
        for (IEntity tObj : getTemplateDataObjects(EmailTemplateType.ApplicationCreatedApplicant)) {
            data.add(EmailTemplateRootObjectLoader.loadRootObject(tObj, til));
        }
        System.out.println(parseTemplate(template2, data));
    }

    public static List<IEntity> getTemplateDataObjects(EmailTemplateType template) {
        List<IEntity> values = new Vector<IEntity>();
        switch (template) {
        case PasswordRetrievalCrm:
        case PasswordRetrievalTenant:
            values.add(EntityFactory.create(NewPasswordT.class));
            break;
        case ApplicationCreatedApplicant:
        case ApplicationCreatedCoApplicant:
        case ApplicationCreatedGuarantor:
        case ApplicationApproved:
        case ApplicationDeclined:
            values.add(EntityFactory.create(ApplicationT.class));
            values.add(EntityFactory.create(CompanyT.class));
            break;
        default:
            throw new Error("We missed it");
        }
        return values;
    }

    /**
     * Used for template editing
     */
    public static Set<String> getTemplateDataObjectSelection(EmailTemplateType template) {
        List<IEntity> dataObjects = getTemplateDataObjects(template);
        Set<String> selection = new HashSet<String>();
        for (IEntity obj : dataObjects) {
            for (Path path : getTemplateEntityMemberGraph(obj, new ArrayList<Path>())) {
                selection.add(pathToVarname(path.toString()));
            }
        }
        return selection;
    }

    public static String parseTemplate(String htmlTemplate, Collection<IEntity> data) {
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
                if (value == null) {
                    throw new IllegalArgumentException("Missing value of ${" + varName + "}");
                } else {
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
        // convert from path with "/"-delimiter to varName with dotted notation encolsed into ${}
        // and remove ending T from the Root Object name
        String varName = path.replace("/", ".");
        if (varName.endsWith(".")) {
            varName = varName.substring(0, varName.length() - 1);
        }
        varName = varName.replaceFirst("T\\.", ".");
        return "${" + varName + "}";
    }

    private static String varnameToPath(String varName) {
        // convert from varName with dotted notation encolsed into ${} to path with "/"-delimiter
        // and add T-ending to the Root Object names
        String path = varName.replaceFirst("\\.", "T.").replace(".", "/");
        if (path.startsWith("${")) {
            path = path.substring(2);
        }
        if (path.endsWith("}")) {
            path = path.substring(0, path.length() - 1);
        }
        return path + "/";
    }
}
