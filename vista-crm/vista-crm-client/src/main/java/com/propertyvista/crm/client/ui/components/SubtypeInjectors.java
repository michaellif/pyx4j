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

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaEntityFolder;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.domain.property.vendor.Contract;
import com.propertyvista.domain.property.vendor.Licence;
import com.propertyvista.domain.property.vendor.Maintenance;
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.domain.property.vendor.Warranty;
import com.propertyvista.domain.property.vendor.WarrantyItem;

public class SubtypeInjectors {

    protected static I18n i18n = I18n.get(SubtypeInjectors.class);

    public static void injectPhones(VistaDecoratorsFlowPanel main, IList<Phone> proto, CEntityEditor<?> parent) {
        injectPhones(main, proto, parent, true, false);
    }

    public static void injectPhones(VistaDecoratorsFlowPanel main, IList<Phone> proto, CEntityEditor<?> parent, final boolean showType,
            final boolean showDescription) {

        main.add(new CrmSectionSeparator(proto.getMeta().getCaption()));
        main.add(parent.inject(proto, new VistaEntityFolder<Phone>(Phone.class, i18n.tr("Phone"), parent.isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                if (showType) {
                    columns.add(new EntityFolderColumnDescriptor(proto().type(), "8em"));
                }
                columns.add(new EntityFolderColumnDescriptor(proto().number(), "11em"));
                columns.add(new EntityFolderColumnDescriptor(proto().extension(), "5em"));
                if (showDescription) {
                    columns.add(new EntityFolderColumnDescriptor(proto().description(), "20em"));
                }
                return columns;
            }
        }));
        main.add(new HTML());
    }

    public static void injectPropertyPhones(VistaDecoratorsFlowPanel main, IList<PropertyPhone> proto, CEntityEditor<?> parent) {

        main.add(new CrmSectionSeparator(proto.getMeta().getCaption()));
        main.add(parent.inject(proto, new VistaEntityFolder<PropertyPhone>(PropertyPhone.class, i18n.tr("Phone"), parent.isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().number(), "11em"));
                columns.add(new EntityFolderColumnDescriptor(proto().extension(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "20em"));
                columns.add(new EntityFolderColumnDescriptor(proto().designation(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().provider(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().visibility(), "7em"));
                return columns;
            }
        }));
        main.add(new HTML());
    }

    public static void injectEmails(VistaDecoratorsFlowPanel main, IList<Email> proto, CEntityEditor<?> parent) {

        main.add(new CrmSectionSeparator(proto.getMeta().getCaption()));
        main.add(parent.inject(proto, new VistaEntityFolder<Email>(Email.class, i18n.tr("Email"), parent.isEditable()) {
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
        main.add(parent.inject(proto.website()), 23);
        injectEmails(main, proto.emails(), parent);
// TODO : design representation for:
//        main.add(parent.inject(proto.contacts()), 15);
//        main.add(parent.inject(proto.logo()), 15);
    }

    public static void injectVendor(VistaDecoratorsFlowPanel main, Vendor proto, CEntityEditor<?> parent) {

        main.add(parent.inject(proto.type()), 15);
        injectCompany(main, proto, parent);
    }

    public static void injectLicence(VistaDecoratorsFlowPanel main, VistaDecoratorsSplitFlowPanel split, Licence proto, CEntityEditor<?> parent) {

        split.getLeftPanel().add(parent.inject(proto.number()), 15);

        split.getRightPanel().add(parent.inject(proto.expiration()), 8.2);
        split.getRightPanel().add(parent.inject(proto.renewal()), 8.2);
    }

    public static void injectContract(VistaDecoratorsFlowPanel main, VistaDecoratorsSplitFlowPanel split, Contract proto, CEntityEditor<?> parent) {

        split.getLeftPanel().add(parent.inject(proto.contractID()), 15);
        split.getLeftPanel().add(parent.inject(proto.contractor()), 20);
        split.getLeftPanel().add(parent.inject(proto.cost()), 10);

        split.getRightPanel().add(parent.inject(proto.start()), 8.2);
        split.getRightPanel().add(parent.inject(proto.end()), 8.2);

// TODO : design representation for:
//             main.add(parent.inject(proto.document()), 45);
    }

    public static void injectMaintenance(VistaDecoratorsFlowPanel main, VistaDecoratorsSplitFlowPanel split, Maintenance proto, CEntityEditor<?> parent) {

        injectContract(main, split, proto, parent);

        main.add(new CrmSectionSeparator("Maintenance Schedule"));
        main.add(split = new VistaDecoratorsSplitFlowPanel(main.isReadOnlyMode()));

        split.getLeftPanel().add(parent.inject(proto.lastService()), 8.2);
        split.getRightPanel().add(parent.inject(proto.nextService()), 8.2);
    }

    public static void injectWarranty(VistaDecoratorsFlowPanel main, VistaDecoratorsSplitFlowPanel split, Warranty proto, CEntityEditor<?> parent) {

        split.getLeftPanel().add(parent.inject(proto.title()), 15);
        split.getRightPanel().add(parent.inject(proto.type()), 11);

        main.add(new CrmSectionSeparator("Contract details"));
        main.add(split = new VistaDecoratorsSplitFlowPanel(main.isReadOnlyMode()));
        injectContract(main, split, proto, parent);

        main.add(new CrmSectionSeparator(proto.items().getMeta().getCaption()));
        main.add(parent.inject(proto.items(), new VistaEntityFolder<WarrantyItem>(WarrantyItem.class, i18n.tr("Warranty Item"), parent.isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "30em"));
                return columns;
            }
        }));
    }

    public static void injectMarketing(VistaDecoratorsFlowPanel main, Marketing proto, CEntityEditor<?> parent) {

        main.add(parent.inject(proto.name()), 15);
        main.add(parent.inject(proto.description()), 45);

        main.add(new CrmSectionSeparator(proto.adBlurbs().getMeta().getCaption()));
        main.add(parent.inject(proto.adBlurbs(),
                new VistaEntityFolder<AdvertisingBlurb>(AdvertisingBlurb.class, i18n.tr("Advertising Blurb"), parent.isEditable()) {
                    @Override
                    protected List<EntityFolderColumnDescriptor> columns() {
                        List<EntityFolderColumnDescriptor> columns;
                        columns = new ArrayList<EntityFolderColumnDescriptor>();
                        columns.add(new EntityFolderColumnDescriptor(proto().content(), "60em"));
                        return columns;
                    }
                }));
    }

    public static VistaEntityFolder<Concession> injectConcessions(boolean isEditable) {
        return new VistaEntityFolder<Concession>(Concession.class, "Concession", isEditable) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().approvedBy(), "20em"));
                columns.add(new EntityFolderColumnDescriptor(proto().status(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().expirationDate(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().expirationDate(), "8.2em"));
                return columns;
            }
        };
    }
}
