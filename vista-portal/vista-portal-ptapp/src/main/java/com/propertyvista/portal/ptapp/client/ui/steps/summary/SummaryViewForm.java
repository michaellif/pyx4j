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
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.client.DownloadFrame;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.editors.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.editors.dto.FinancialViewForm;
import com.propertyvista.common.client.ui.components.editors.dto.InfoViewForm;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;
import com.propertyvista.portal.ptapp.client.ui.components.UtilityFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.apartment.FeatureExFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.apartment.FeatureFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.charges.ChargesViewForm;
import com.propertyvista.portal.ptapp.client.ui.steps.tenants.TenantFolder;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;

public class SummaryViewForm extends CEntityDecoratableEditor<SummaryDTO> {

    private static I18n i18n = I18n.get(SummaryViewForm.class);

    public final static String DEFAULT_STYLE_PREFIX = "SummaryViewForm";

    public static enum StyleSuffix implements IStyleName {
        DigitalSignature, DigitalSignatureLabel, DigitalSignatureEdit
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private SummaryViewPresenter presenter;

    //@formatter:off
    // parts of lease term panel
    private final FormFlexPanel consessionPanel = new FormFlexPanel();
    private final FormFlexPanel includedPanel = new FormFlexPanel();
    private final FormFlexPanel excludedPanel = new FormFlexPanel();
    private final FormFlexPanel chargedPanel = new FormFlexPanel();
    private final FormFlexPanel petsPanel = new FormFlexPanel();
    private final FormFlexPanel parkingPanel = new FormFlexPanel();
    private final FormFlexPanel storagePanel = new FormFlexPanel();
    private final FormFlexPanel otherPanel = new FormFlexPanel();
    private final FormFlexPanel addonsPanel = new FormFlexPanel();
    //@formatter:on

    public SummaryViewForm() {
        super(SummaryDTO.class, new VistaViewersComponentFactory());
        setEditable(false);
    }

    public SummaryViewPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(SummaryViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;

        main.setH1(++row, 0, 1, i18n.tr("Premises"));
        main.setWidget(++row, 0, new ApartmentView());

        main.setH1(++row, 0, 1, i18n.tr("Lease Term/Rent"));
        main.setWidget(++row, 0, new LeaseTermView());

        main.setH1(++row, 0, 1, i18n.tr("Tenants"), createEditLink(new PtSiteMap.Tenants()));
        main.setWidget(++row, 0, inject(proto().tenantList().tenants(), new TenantFolder(false)));

        main.setH1(++row, 0, 1, i18n.tr("Full info"), createEditLink(new PtSiteMap.Info()));
        main.setWidget(++row, 0, inject(proto().tenantsWithInfo(), createTenantView()));

        main.setH1(++row, 0, 1, i18n.tr("Financial"), createEditLink(new PtSiteMap.Financial()));
        main.setWidget(++row, 0, inject(proto().tenantFinancials(), createFinancialView()));

        main.setH1(++row, 0, 1, i18n.tr("Lease Terms"));
        main.setWidget(++row, 0, new LeaseTermsCheck());

        main.setWidget(++row, 0, inject(proto().charges(), new ChargesViewForm(this)));

        main.setH1(++row, 0, 1, i18n.tr("Digital Signature"));
        main.setWidget(++row, 0, new SignatureView());

        return main;
    }

    @Override
    public void populate(SummaryDTO entity) {
        super.populate(entity);

        //hide/show various panels depend on populated data:
        consessionPanel.setVisible(!entity.selectedUnit().concessions().isEmpty());
        includedPanel.setVisible(!entity.selectedUnit().includedUtilities().isEmpty());
        excludedPanel.setVisible(!entity.selectedUnit().externalUtilities().isEmpty());
        chargedPanel.setVisible(!entity.selectedUnit().agreedUtilities().isEmpty());

        petsPanel.setVisible(!entity.selectedUnit().agreedPets().isEmpty());
        parkingPanel.setVisible(!entity.selectedUnit().agreedParking().isEmpty());
        storagePanel.setVisible(!entity.selectedUnit().agreedStorage().isEmpty());
        otherPanel.setVisible(!entity.selectedUnit().agreedOther().isEmpty());
        addonsPanel.setVisible(!entity.selectedUnit().agreedPets().isEmpty() | !entity.selectedUnit().agreedParking().isEmpty()
                | !entity.selectedUnit().agreedStorage().isEmpty() | !entity.selectedUnit().agreedOther().isEmpty());
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
            add(alignWidth(download));
        }
    }

    private Widget createEditLink(final AppPlace link) {
        Anchor edit = new Anchor(i18n.tr("Edit"));
        edit.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        edit.getElement().getStyle().setProperty("lineHeight", "2em");
        edit.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        edit.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (link.getClass().equals(PtSiteMap.Info.class) || link.getClass().equals(PtSiteMap.Financial.class)) {
                    if (!getValue().tenantList().tenants().isEmpty()) {
                        link.arg(PtSiteMap.STEP_ARG_NAME, getValue().tenantList().tenants().get(0).getPrimaryKey().toString());
                    }
                }

                getPresenter().goToPlace(link);
            }
        });

        return edit;
    }

    private static Widget alignWidth(Widget e) {
        e.setWidth("100%");
        return e;
    }

    private static class TableCell {
        public String caption;

        public String width;

        public Widget widget;

        public TableCell(String caption, String width, Widget widget) {
            this.caption = caption;
            this.width = width;
            this.widget = widget;
        }

    }

    /*
     * Selected Apartment information view implementation
     */
    private class ApartmentView extends HorizontalPanel {
        public ApartmentView() {

            alignWidth(this);

            FormFlexPanel main = new FormFlexPanel();

            main.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            main.getElement().getStyle().setPaddingRight(1, Unit.EM);
            add(main);
            setCellWidth(main, "100%");

            List<TableCell> cells = Arrays.asList(
                    //
                    new TableCell("Suite Name", "20%", inject(proto().selectedUnit().floorplan()).asWidget()),
                    new TableCell("Address", "40%", inject(proto().selectedUnit().address().street2()).asWidget()),
                    new TableCell("Bedrooms #", "10%", inject(proto().selectedUnit().bedrooms()).asWidget()),
                    new TableCell("Den #", "10%", inject(proto().selectedUnit().dens()).asWidget()),
                    new TableCell("Landlord Name", "20%", inject(proto().selectedUnit().landlordName()).asWidget()));

            int col = -1;
            for (TableCell c : cells) {
                HTML label = new HTML(i18n.tr(c.caption).replaceAll(" ", "&nbsp"));
                label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                label.setWidth(c.width);
                main.setWidget(0, ++col, label);
                main.setWidget(2, col, c.widget);
                main.getFlexCellFormatter().setWidth(0, col, c.width);
            }
            Widget sp = new VistaLineSeparator(100, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setPaddingRight(2, Unit.EM);
            sp.getElement().getStyle().setPosition(Position.RELATIVE);
            sp.getElement().getStyle().setLeft(-1, Unit.EM);
            main.setWidget(1, 0, sp);
            main.getFlexCellFormatter().setColSpan(1, 0, cells.size());
        }
    }

    /*
     * Selected Apartment information view implementation
     */
    private class LeaseTermView extends FormFlexPanel {

        public LeaseTermView() {
            super();
            alignWidth(this);
            int row = -1;
            setWidget(++row, 0, new DecoratorBuilder(inject(proto().selectedUnit().leaseFrom()), 8).build());
            setWidget(++row, 0, new DecoratorBuilder(inject(proto().selectedUnit().leaseTo()), 8).build());
            setWidget(++row, 0, new DecoratorBuilder(inject(proto().selectedUnit().unitRent()), 8).build());

            includedPanel.setH2(0, 0, 1, i18n.tr("Included Utilities"));
            includedPanel.setWidget(1, 0, inject(proto().selectedUnit().includedUtilities(), new UtilityFolder()));
            setWidget(++row, 0, includedPanel);

            excludedPanel.setH2(0, 0, 1, i18n.tr("Excluded Utilities"));
            excludedPanel.setWidget(1, 0, inject(proto().selectedUnit().externalUtilities(), new UtilityFolder()));
            setWidget(++row, 0, excludedPanel);

            chargedPanel.setH2(0, 0, 1, i18n.tr("Billed Utilities"));
            chargedPanel.setWidget(1, 0, inject(proto().selectedUnit().agreedUtilities(), new FeatureFolder(Feature.Type.utility, null, false)));
            setWidget(++row, 0, chargedPanel);

            int addonsRow = -1;
            addonsPanel.setH2(++addonsRow, 0, 1, i18n.tr("Add-Ons"));

            petsPanel.setH3(0, 0, 1, i18n.tr("Pets"));
            petsPanel.setWidget(1, 0, inject(proto().selectedUnit().agreedPets(), new FeatureExFolder(Feature.Type.pet, null, false)));
            addonsPanel.setWidget(++addonsRow, 0, petsPanel);

            parkingPanel.setH3(0, 0, 1, i18n.tr("Parking"));
            parkingPanel.setWidget(1, 0, inject(proto().selectedUnit().agreedParking(), new FeatureExFolder(Feature.Type.parking, null, false)));
            addonsPanel.setWidget(++addonsRow, 0, parkingPanel);

            storagePanel.setH3(0, 0, 1, i18n.tr("Storage"));
            storagePanel.setWidget(1, 0, inject(proto().selectedUnit().agreedStorage(), new FeatureFolder(Feature.Type.locker, null, false)));
            addonsPanel.setWidget(++addonsRow, 0, storagePanel);

            otherPanel.setH3(0, 0, 1, i18n.tr("Other"));
            otherPanel.setWidget(1, 0, inject(proto().selectedUnit().agreedOther(), new FeatureFolder(Feature.Type.addOn, null, false)));
            addonsPanel.setWidget(++addonsRow, 0, otherPanel);
            setWidget(++row, 0, addonsPanel);
        }
    }

    /*
     * Tenants detailed information view implementation
     */
    private CEntityFolder<TenantInfoDTO> createTenantView() {
        return new VistaBoxFolder<TenantInfoDTO>(TenantInfoDTO.class, false) {

            @Override
            public CComponent<?, ?> create(IObject<?> member) {
                if (member instanceof TenantInfoDTO) {
                    return new InfoViewForm(new VistaViewersComponentFactory());
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
            public CComponent<?, ?> create(IObject<?> member) {
                if (member instanceof TenantFinancialDTO) {
                    return new FinancialViewForm(new VistaViewersComponentFactory());
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

    /*
     * Lease Terms view implementation
     */
    private class LeaseTermsCheck extends FlowPanel {

        public LeaseTermsCheck() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            alignWidth(this);

            // add terms content:
            CLabel leaseTermContent = new CLabel();
            leaseTermContent.setAllowHtml(true);
            leaseTermContent.setWordWrap(true);
            bind(leaseTermContent, proto().leaseTerms().text());

            ScrollPanel leaseTerms = new ScrollPanel(leaseTermContent.asWidget());
            leaseTerms.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            leaseTerms.getElement().getStyle().setBorderWidth(1, Unit.PX);
            leaseTerms.getElement().getStyle().setBorderColor("#bbb");

            leaseTerms.getElement().getStyle().setBackgroundColor("white");
            leaseTerms.getElement().getStyle().setColor("black");

            leaseTerms.getElement().getStyle().setPaddingLeft(0.5, Unit.EM);
            leaseTerms.setHeight("20em");
            add(leaseTerms);

            // "I Agree" check-box:
            CCheckBox check = new CCheckBox();
            bind(check, proto().application().signature().agree());
            VistaWidgetDecorator agree = new VistaWidgetDecorator(check, new DecorationData(0, Unit.EM, 0, Unit.EM));
            agree.asWidget().getElement().getStyle().setMarginLeft(40, Unit.PCT);
            agree.asWidget().getElement().getStyle().setMarginTop(0.5, Unit.EM);
            add(agree);
            check.inheritContainerAccessRules(false);
            check.addValueValidator(new EditableValueValidator<Boolean>() {

                @Override
                public boolean isValid(CComponent<Boolean, ?> component, Boolean value) {
                    return value == Boolean.TRUE;
                }

                @Override
                public String getValidationMessage(CComponent<Boolean, ?> component, Boolean value) {
                    return i18n.tr("You Must Agree To The Terms And Conditions To Continue");
                }
            });
        }
    }

    /*
     * Digital Signature view implementation
     */
    private class SignatureView extends FlowPanel {

        public SignatureView() {

            getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            alignWidth(this);

            HTML signatureTerms = new HTML(PortalResources.INSTANCE.digitalSignature().getText());
            add(signatureTerms);

            // signature composure:
            CTextField edit = new CTextField();
            bind(edit, proto().application().signature().fullName());

            DecorationData dd = new DecorationData(16d, HasHorizontalAlignment.ALIGN_LEFT, 16);
            dd.labelStyleName = DEFAULT_STYLE_PREFIX + StyleSuffix.DigitalSignatureLabel.name();
            //            dd.componentStyle = DEFAULT_STYLE_PREFIX + StyleSuffix.DigitalSignatureEdit.name();
            VistaWidgetDecorator signature = new VistaWidgetDecorator(edit, dd);
            signature.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DigitalSignature.name());
            signature.getElement().getStyle().setMarginTop(1, Unit.EM);
            signature.getElement().getStyle().setPaddingTop(1, Unit.EM);
            signature.getElement().getStyle().setPaddingLeft(1.5, Unit.EM);
            signature.setHeight("3em");
            edit.inheritContainerAccessRules(false);
            add(alignWidth(signature));

            // validation:
            edit.addValueValidator(new EditableValueValidator<String>() {

                @Override
                public boolean isValid(CComponent<String, ?> component, String value) {
                    return isSignatureValid(value);
                }

                @Override
                public String getValidationMessage(CComponent<String, ?> component, String value) {
                    return i18n.tr("Digital Signature Must Match Your Name On File");
                }
            });

            CDateLabel dl = new CDateLabel();
            dl.setDateFormat(proto().application().signature().timestamp().getMeta().getFormat());
            add(inject(proto().application().signature().timestamp(), dl));
            add(inject(proto().application().signature().ipAddress(), new CLabel()));
        }
    }

    private boolean isSignatureValid(String signature) {
        if (CommonsStringUtils.isEmpty(signature)) {
            return false;
        }
        for (TenantInLeaseDTO pti : getValue().tenantList().tenants()) {
            if (pti.role().getValue() == TenantInLease.Role.Applicant) {
                return isCombinationMatch(signature, pti.tenant().person().name().firstName(), pti.tenant().person().name().lastName(), pti.tenant().person()
                        .name().middleName());
            }
        }
        return false;
    }

    private boolean isCombinationMatch(String signature, IPrimitive<String> value1, IPrimitive<String> value2, IPrimitive<String> value3) {
        signature = signature.trim().toLowerCase().replaceAll("\\s+", " ");
        String s1 = CommonsStringUtils.nvl(value1.getValue()).trim().toLowerCase();
        String s2 = CommonsStringUtils.nvl(value2.getValue()).trim().toLowerCase();
        String s3 = CommonsStringUtils.nvl(value3.getValue()).trim().toLowerCase();
        if ((signature.equals(CommonsStringUtils.nvl_concat(s1, s2, " ")) || (signature.equals(CommonsStringUtils.nvl_concat(s2, s1, " "))))) {
            return true;
        }
        if ((signature.equals(CommonsStringUtils.nvl_concat(CommonsStringUtils.nvl_concat(s1, s3, " "), s2, " ")))) {
            return true;
        }
        if ((signature.equals(CommonsStringUtils.nvl_concat(CommonsStringUtils.nvl_concat(s2, s3, " "), s1, " ")))) {
            return true;
        }
        return false;
    }
}
