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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.client.ui.flex.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.domain.Company;
import com.propertyvista.domain.Email;
import com.propertyvista.domain.Phone;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.asset.Equipment;
import com.propertyvista.domain.property.vendor.Contract;
import com.propertyvista.domain.property.vendor.Licence;
import com.propertyvista.domain.property.vendor.Maintenance;
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.domain.property.vendor.Warranty;
import com.propertyvista.domain.property.vendor.WarrantyItem;

public class SubtypeInjectors {

    protected static I18n i18n = I18nFactory.getI18n(SubtypeInjectors.class);

    public static void injectPhones(VistaDecoratorsFlowPanel main, IList<Phone> proto, CEntityEditor<?> parent) {

        main.add(new CrmHeader2Decorator(proto.getMeta().getCaption()));
        main.add(parent.inject(proto, new CrmEntityFolder<Phone>(Phone.class, i18n.tr("Phone"), parent.isEditable()) {
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
        main.add(new HTML());
    }

    public static void injectEmails(VistaDecoratorsFlowPanel main, IList<Email> proto, CEntityEditor<?> parent) {

        main.add(new CrmHeader2Decorator(proto.getMeta().getCaption()));
        main.add(parent.inject(proto, new CrmEntityFolder<Email>(Email.class, i18n.tr("Email"), parent.isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().address(), "25em"));
                return columns;
            }
        }));
        main.add(new HTML());
    }

    public static void injectCompany(VistaDecoratorsFlowPanel main, Company proto, CEntityEditor<?> parent) {

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

    public static void injectVendor(VistaDecoratorsFlowPanel main, Vendor proto, CEntityEditor<?> parent) {

        main.add(parent.inject(proto.type()), 15);
        injectCompany(main, proto, parent);
    }

    public static void injectLicence(VistaDecoratorsFlowPanel main, Licence proto, CEntityEditor<?> parent) {

        main.add(parent.inject(proto.number()), 15);
        main.add(parent.inject(proto.expiration()), 8.2);
        main.add(parent.inject(proto.renewal()), 8.2);
    }

    public static void injectContract(VistaDecoratorsFlowPanel main, Contract proto, CEntityEditor<?> parent) {

        main.add(parent.inject(proto.contractID()), 15);
//        injectVendor(main, proto.contractor(), parent);
        main.add(parent.inject(proto.contractor()), 15);
        main.add(parent.inject(proto.cost()), 15);
        main.add(parent.inject(proto.start()), 8.2);
        main.add(parent.inject(proto.end()), 8.2);
// TODO : design representation for:
//             main.add(parent.inject(proto.document()), 45);
    }

    public static void injectMaintenance(VistaDecoratorsFlowPanel main, Maintenance proto, CEntityEditor<?> parent) {

        injectContract(main, proto, parent);
        main.add(parent.inject(proto.lastService()), 8.2);
        main.add(parent.inject(proto.nextService()), 8.2);
    }

    public static void injectWarranty(VistaDecoratorsFlowPanel main, Warranty proto, CEntityEditor<?> parent) {

        main.add(parent.inject(proto.title()), 15);
        main.add(parent.inject(proto.type()), 15);
        injectContract(main, proto, parent);

        main.add(new CrmHeader2Decorator(proto.items().getMeta().getCaption()));
        main.add(parent.inject(proto.items(), new CrmEntityFolder<WarrantyItem>(WarrantyItem.class, i18n.tr("Warranty Item"), parent.isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "20em"));
                return columns;
            }
        }));
    }

    public static void injectEquipment(VistaDecoratorsFlowPanel main, Equipment proto, CEntityEditor<?> parent) {

        main.add(parent.inject(proto.type()), 15);
        main.add(parent.inject(proto.description()), 15);
        main.add(parent.inject(proto.make()), 15);
        main.add(parent.inject(proto.model()), 15);
        main.add(parent.inject(proto.build()), 8.2);

        main.add(new CrmHeaderDecorator(i18n.tr(proto.licence().getMeta().getCaption())));
        SubtypeInjectors.injectLicence(main, proto.licence(), parent);

        main.add(new CrmHeaderDecorator(i18n.tr(proto.warranty().getMeta().getCaption())));
        SubtypeInjectors.injectWarranty(main, proto.warranty(), parent);

        main.add(new CrmHeaderDecorator(i18n.tr(proto.maitenance().getMeta().getCaption())));
        SubtypeInjectors.injectMaintenance(main, proto.maitenance(), parent);

        main.add(parent.inject(proto.notes()), 25);
    }

    public static void injectMarketing(VistaDecoratorsFlowPanel main, Marketing proto, CEntityEditor<?> parent) {

        main.add(parent.inject(proto.name()), 15);
        main.add(parent.inject(proto.description()), 25);

        main.add(new CrmHeader2Decorator(proto.addBlurbs().getMeta().getCaption()));
        main.add(parent.inject(proto.addBlurbs(),
                new CrmEntityFolder<AdvertisingBlurb>(AdvertisingBlurb.class, i18n.tr("Advertising Blurb"), parent.isEditable()) {
                    @Override
                    protected List<EntityFolderColumnDescriptor> columns() {
                        List<EntityFolderColumnDescriptor> columns;
                        columns = new ArrayList<EntityFolderColumnDescriptor>();
                        columns.add(new EntityFolderColumnDescriptor(proto().content(), "60em"));
                        return columns;
                    }
                }));
    }

}
