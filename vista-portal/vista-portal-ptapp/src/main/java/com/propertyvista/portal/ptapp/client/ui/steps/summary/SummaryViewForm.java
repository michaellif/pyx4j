/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.summary;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.essentials.client.DownloadFrame;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.editors.dto.FinancialViewForm;
import com.propertyvista.common.client.ui.components.editors.dto.InfoViewForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.portal.ptapp.client.ui.steps.apartment.ConcessionsFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.apartment.FeatureExFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.apartment.FeatureFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.charges.ChargesViewForm;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoSummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.services.steps.SummaryService;

public class SummaryViewForm extends CEntityDecoratableForm<SummaryDTO> {

    private static final I18n i18n = I18n.get(SummaryViewForm.class);

    private SummaryViewPresenter presenter;

    private final TwoColumnFlexFormPanel consessionPanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel includedPanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel excludedPanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel chargedPanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel petsPanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel parkingPanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel storagePanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel otherPanel = new TwoColumnFlexFormPanel();

    private final TwoColumnFlexFormPanel addonsPanel = new TwoColumnFlexFormPanel();

    private final boolean viewMode;

    public SummaryViewForm() {
        this(false);
    }

    public SummaryViewForm(boolean viewMode) {
        super(SummaryDTO.class, new VistaEditorsComponentFactory());
        setViewable(true);
        if (viewMode) {
            setEditable(false);
        }

        this.viewMode = viewMode;
    }

    public SummaryViewPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(SummaryViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;

        main.setH1(++row, 0, 1, i18n.tr("Premises"));
        main.setWidget(++row, 0, inject(proto().apartmentSummary(), new ApartmentView()));

        main.setH1(++row, 0, 1, i18n.tr("Lease Term/Rent"));
        main.setWidget(++row, 0, new LeaseTermView());

        main.setH1(++row, 0, 1, i18n.tr("Tenants"), createEditLink(new PtSiteMap.Tenants()));
        main.setWidget(++row, 0, inject(proto().tenantList().tenants(), new TenantViewFolder()));

        main.setH1(++row, 0, 1, i18n.tr("Information"), createEditLink(new PtSiteMap.Info()));
        main.setWidget(++row, 0, inject(proto().tenantsWithInfo(), createTenantView()));

        main.setH1(++row, 0, 1, i18n.tr("Financial"), createEditLink(new PtSiteMap.Financial()));
        main.setWidget(++row, 0, inject(proto().tenantFinancials(), createFinancialView()));

// TODO : Charges and Payment steps are closed (removed) so far...        
        if (false) {
            main.setWidget(++row, 0, inject(proto().charges(), new ChargesViewForm(true)));
        }

        main.setBR(++row, 0, 1);

        main.setH1(++row, 0, 1, i18n.tr("Lease Terms"));
        main.setWidget(++row, 0, inject(proto().leaseTerms(), new LeaseTemsFolder(!viewMode)));

        main.setH1(++row, 0, 1, i18n.tr("Digital Signature(s)"));
        main.setWidget(++row, 0, inject(proto().application().signatures(), new SignatureFolder(!viewMode)));

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        //hide/show various panels depend on populated data:
        consessionPanel.setVisible(!getValue().selectedUnit().concessions().isEmpty());
        chargedPanel.setVisible(!getValue().selectedUnit().agreedUtilities().isEmpty());

        petsPanel.setVisible(!getValue().selectedUnit().agreedPets().isEmpty());
        parkingPanel.setVisible(!getValue().selectedUnit().agreedParking().isEmpty());
        storagePanel.setVisible(!getValue().selectedUnit().agreedStorage().isEmpty());
        otherPanel.setVisible(!getValue().selectedUnit().agreedOther().isEmpty());
        addonsPanel.setVisible(!getValue().selectedUnit().agreedPets().isEmpty() | !getValue().selectedUnit().agreedParking().isEmpty()
                | !getValue().selectedUnit().agreedStorage().isEmpty() | !getValue().selectedUnit().agreedOther().isEmpty());
    }

    public class DemoReportButtons extends FlowPanel {

        public DemoReportButtons() {
            getElement().getStyle().setMargin(2, Unit.EM);

            Button download = new Button(i18n.tr("Print/Download the Summary"));
            download.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    SummaryService srv = GWT.create(SummaryService.class);
                    srv.downloadSummary(new DefaultAsyncCallback<String>() {

                        @Override
                        public void onSuccess(String result) {
                            //TODO implement this in IE
                            new DownloadFrame(GWT.getModuleBaseURL() + result);
                        }
                    }, null);

                }
            });
            add(download);
        }
    }

    private Widget createEditLink(final AppPlace link) {
        if (viewMode) {
            return new HTML();
        }

        Anchor edit = new Anchor(i18n.tr("Edit"));
        edit.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        edit.getElement().getStyle().setProperty("lineHeight", "2em");
        edit.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        edit.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (link.getClass().equals(PtSiteMap.Info.class) || link.getClass().equals(PtSiteMap.Financial.class)) {
                    if (!getValue().tenantList().tenants().isEmpty()) {
                        link.queryArg(PtSiteMap.STEP_ARG_NAME, getValue().tenantList().tenants().get(0).getPrimaryKey().toString());
                    }
                }

                getPresenter().goToPlace(link);
            }
        });

        return edit;
    }

    private class ApartmentView extends VistaTableFolder<ApartmentInfoSummaryDTO> {
        public ApartmentView() {
            super(ApartmentInfoSummaryDTO.class, false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList( //@formatter:off
                  new EntityFolderColumnDescriptor(proto().floorplan(), "10em"),
                  new EntityFolderColumnDescriptor(proto().address(), "30em"),
                  new EntityFolderColumnDescriptor(proto().bedrooms(), "10em"),
                  new EntityFolderColumnDescriptor(proto().dens(), "10em"),
                  new EntityFolderColumnDescriptor(proto().landlordName(), "10em"));
            //@formatter:on
        }
    }

    /*
     * Selected Apartment information view implementation
     */
    private class LeaseTermView extends TwoColumnFlexFormPanel {

        public LeaseTermView() {
            int row = -1;

            TwoColumnFlexFormPanel leasePanel = new TwoColumnFlexFormPanel();
            leasePanel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().selectedUnit().leaseFrom()), 8).build());
            leasePanel.setWidget(0, 1, new FormDecoratorBuilder(inject(proto().selectedUnit().leaseTo()), 8).build());
            leasePanel.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().selectedUnit().unitRent()), 8).build());
            setWidget(++row, 0, leasePanel);

            consessionPanel.setH2(0, 0, 1, i18n.tr("Promotions, Discounts and Concessions"));
            consessionPanel.setWidget(1, 0, inject(proto().selectedUnit().concessions(), new ConcessionsFolder()));
            setWidget(++row, 0, consessionPanel);

            chargedPanel.setH2(0, 0, 1, i18n.tr("Utilities"));
            chargedPanel.setWidget(1, 0, inject(proto().selectedUnit().agreedUtilities(), new FeatureFolder(ARCode.Type.Utility, null, false)));
            setWidget(++row, 0, chargedPanel);

            int addonsRow = -1;
            addonsPanel.setH2(++addonsRow, 0, 1, i18n.tr("Add-Ons"));

            petsPanel.setH3(0, 0, 1, i18n.tr("Pets"));
            petsPanel.setWidget(1, 0, inject(proto().selectedUnit().agreedPets(), new FeatureExFolder(ARCode.Type.Pet, null, false)));
            addonsPanel.setWidget(++addonsRow, 0, petsPanel);

            parkingPanel.setH3(0, 0, 1, i18n.tr("Parking"));
            parkingPanel.setWidget(1, 0, inject(proto().selectedUnit().agreedParking(), new FeatureExFolder(ARCode.Type.Parking, null, false)));
            addonsPanel.setWidget(++addonsRow, 0, parkingPanel);

            storagePanel.setH3(0, 0, 1, i18n.tr("Storage"));
            storagePanel.setWidget(1, 0, inject(proto().selectedUnit().agreedStorage(), new FeatureFolder(ARCode.Type.Locker, null, false)));
            addonsPanel.setWidget(++addonsRow, 0, storagePanel);

            otherPanel.setH3(0, 0, 1, i18n.tr("Other"));
            otherPanel.setWidget(1, 0, inject(proto().selectedUnit().agreedOther(), new FeatureFolder(ARCode.Type.AddOn, null, false)));
            addonsPanel.setWidget(++addonsRow, 0, otherPanel);
            setWidget(++row, 0, addonsPanel);

            setWidth("100%");
        }
    }

    /*
     * Tenants detailed information view implementation
     */
    private CEntityFolder<TenantInfoDTO> createTenantView() {
        return new VistaBoxFolder<TenantInfoDTO>(TenantInfoDTO.class, false) {

            @Override
            public CComponent<?> create(IObject<?> member) {
                if (member instanceof TenantInfoDTO) {
                    return new InfoViewForm(true);
                }
                return super.create(member);
            }

            @Override
            public IFolderItemDecorator<TenantInfoDTO> createItemDecorator() {
                BoxFolderItemDecorator<TenantInfoDTO> decorator = (BoxFolderItemDecorator<TenantInfoDTO>) super.createItemDecorator();
                decorator.setExpended(false);
                return decorator;
            }

        };
    }

    /*
     * Financial detailed information view implementation
     */
    private CEntityFolder<TenantFinancialDTO> createFinancialView() {
        return new VistaBoxFolder<TenantFinancialDTO>(TenantFinancialDTO.class, false) {

            @Override
            public CComponent<?> create(IObject<?> member) {
                if (member instanceof TenantFinancialDTO) {
                    return new FinancialViewForm(true);
                }
                return super.create(member);
            }

            @Override
            public IFolderItemDecorator<TenantFinancialDTO> createItemDecorator() {
                BoxFolderItemDecorator<TenantFinancialDTO> decorator = (BoxFolderItemDecorator<TenantFinancialDTO>) super.createItemDecorator();
                decorator.setExpended(false);
                return decorator;
            }
        };
    }

    //TODO this function is a temporary Hack to make it Work. Remove!
    @Override
    public boolean isValid() {
        if ((getValue() != null) && !getValue().signed().isBooleanTrue()) {
            return (get(proto().application().signatures()).isValid() && get(proto().leaseTerms()).isValid());
        } else {
            return true;
        }
    }
}
