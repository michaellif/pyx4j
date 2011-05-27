/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-27
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.domain.Company;
import com.propertyvista.domain.Email;
import com.propertyvista.domain.Phone;
import com.propertyvista.domain.property.vendor.Licence;
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.domain.property.vendor.Warranty;
import com.propertyvista.domain.property.vendor.WarrantyItem;

public class SubtypeInjectors {

    public static void injectPhones(VistaDecoratorsFlowPanel main, IList<Phone> proto, CEntityEditableComponent<?> parent) {

        main.add(parent.inject(proto, new CrmEntityFolder<Phone>(Phone.class, "Phone", parent.isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().number(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().extension(), "5em"));
                return columns;
            }
        }));
    }

    public static void injectEmails(VistaDecoratorsFlowPanel main, IList<Email> proto, CEntityEditableComponent<?> parent) {

        main.add(parent.inject(proto, new CrmEntityFolder<Email>(Email.class, "Email", parent.isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().address(), "20em"));
                return columns;
            }
        }));
    }

    public static void injectWarranty(VistaDecoratorsFlowPanel main, Warranty proto, CEntityEditableComponent<?> parent) {

        main.add(parent.inject(proto.title()), 15);
        main.add(parent.inject(proto.type()), 10);
        injectCompany(main, proto.providedBy(), parent);
        main.add(parent.inject(proto.start()), 8.2);
        main.add(parent.inject(proto.end()), 8.2);

        main.add(parent.inject(proto.items(), new CrmEntityFolder<WarrantyItem>(WarrantyItem.class, "Warranty Item", parent.isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "20em"));
                return columns;
            }
        }));

// TODO : design representation for:
//        main.add(parent.inject(proto.document()), 45);
    }

    public static void injectCompany(VistaDecoratorsFlowPanel main, Company proto, CEntityEditableComponent<?> parent) {

        main.add(parent.inject(proto.name()), 15);
// TODO : design representation for:
//        main.add(parent.inject(proto.addresses()), 15);
        injectPhones(main, proto.phones(), parent);
        main.add(parent.inject(proto.website()), 25);
        injectEmails(main, proto.emails(), parent);
// TODO : design representation for:
//        main.add(parent.inject(proto.contacts()), 15);
//        main.add(parent.inject(proto.logo()), 15);
    }

    public static void injectVendor(VistaDecoratorsFlowPanel main, Vendor proto, CEntityEditableComponent<?> parent) {

        main.add(parent.inject(proto.type()), 10);
        injectCompany(main, proto, parent);
    }

    public static void injectLicence(VistaDecoratorsFlowPanel main, Licence proto, CEntityEditableComponent<?> parent) {

        main.add(parent.inject(proto.number()), 20);
        main.add(parent.inject(proto.expiration()), 8.2);
        main.add(parent.inject(proto.renewal()), 8.2);
    }
}
