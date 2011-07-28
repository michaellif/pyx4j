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

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.crm.client.ui.decorations.CrmHeader1Decorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeader2Decorator;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.financial.Concession;
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

        main.add(new CrmHeader2Decorator("Maintenance Schedule"));
        main.add(split = new VistaDecoratorsSplitFlowPanel(main.isReadOnlyMode()));

        split.getLeftPanel().add(parent.inject(proto.lastService()), 8.2);
        split.getRightPanel().add(parent.inject(proto.nextService()), 8.2);
    }

    public static void injectWarranty(VistaDecoratorsFlowPanel main, VistaDecoratorsSplitFlowPanel split, Warranty proto, CEntityEditor<?> parent) {

        split.getLeftPanel().add(parent.inject(proto.title()), 15);
        split.getRightPanel().add(parent.inject(proto.type()), 11);

        main.add(new CrmHeader2Decorator("Contract details"));
        main.add(split = new VistaDecoratorsSplitFlowPanel(main.isReadOnlyMode()));
        injectContract(main, split, proto, parent);

        main.add(new CrmHeader2Decorator(proto.items().getMeta().getCaption()));
        main.add(parent.inject(proto.items(), new CrmEntityFolder<WarrantyItem>(WarrantyItem.class, i18n.tr("Warranty Item"), parent.isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "30em"));
                return columns;
            }
        }));
    }

    public static void injectEquipment(VistaDecoratorsFlowPanel main, VistaDecoratorsSplitFlowPanel split, Equipment proto, CEntityEditor<?> parent) {

        split.getLeftPanel().add(parent.inject(proto.type()), 15);
        split.getLeftPanel().add(parent.inject(proto.make()), 15);
        split.getLeftPanel().add(parent.inject(proto.model()), 15);
        split.getRightPanel().add(parent.inject(proto.build()), 8.2);
        split.getRightPanel().add(parent.inject(proto.description()), 20);

        main.add(new CrmHeader1Decorator(i18n.tr(proto.licence().getMeta().getCaption())));
        main.add(split = new VistaDecoratorsSplitFlowPanel(main.isReadOnlyMode()));
        SubtypeInjectors.injectLicence(main, split, proto.licence(), parent);

        main.add(new CrmHeader1Decorator(i18n.tr(proto.warranty().getMeta().getCaption())));
        main.add(split = new VistaDecoratorsSplitFlowPanel(main.isReadOnlyMode()));
        SubtypeInjectors.injectWarranty(main, split, proto.warranty(), parent);

        main.add(new CrmHeader1Decorator(i18n.tr(proto.maintenance().getMeta().getCaption())));
        main.add(split = new VistaDecoratorsSplitFlowPanel(main.isReadOnlyMode()));
        SubtypeInjectors.injectMaintenance(main, split, proto.maintenance(), parent);

        main.add(new VistaLineSeparator());
        main.add(parent.inject(proto.notes()), 23);
    }

    public static void injectMarketing(VistaDecoratorsFlowPanel main, Marketing proto, CEntityEditor<?> parent) {

        main.add(parent.inject(proto.name()), 15);
        main.add(parent.inject(proto.description()), 45);

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

    public static CrmEntityFolder<Concession> injectConcessions(boolean isEditable) {
        return injectConcessions(isEditable, null);
    }

    public static CrmEntityFolder<Concession> injectConcessions(boolean isEditable, CEntityForm<?> parent) {
        return new CrmEntityFolder<Concession>(Concession.class, "Concession", isEditable, parent) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().value(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().approvedBy(), "20em"));
                columns.add(new EntityFolderColumnDescriptor(proto().status(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().start(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().end(), "8.2em"));
                return columns;
            }
        };
    }
}
