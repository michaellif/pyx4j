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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.client.DownloadFrame;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleName;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.DecorationData;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.domain.tenant.TenantIn.Status;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;
import com.propertyvista.portal.ptapp.client.ui.components.BuildingPicture;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.ptapp.client.ui.steps.charges.ChargesViewForm;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;

public class SummaryViewForm extends CEntityEditor<SummaryDTO> {

    private static I18n i18n = I18n.get(SummaryViewForm.class);

    public final static String DEFAULT_STYLE_PREFIX = "SummaryViewForm";

    public static enum StyleSuffix implements IStyleName {
        DigitalSignature, DigitalSignatureLabel, DigitalSignatureEdit
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private SummaryViewPresenter presenter;

    private TenantsTable tenantsTable;

    public SummaryViewForm() {
        super(SummaryDTO.class, new VistaViewersComponentFactory());
    }

    public SummaryViewPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(SummaryViewPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        main.add(alignWidth(new VistaHeaderBar(i18n.tr("Apartment"))));

        main.add(new ApartmentView());

        main.add(alignWidth(new VistaHeaderBar(i18n.tr("Lease Term"))));
        main.add(new LeaseTermView());

        main.add(alignWidth(createHeaderWithEditLink(i18n.tr("Tenants"), new PtSiteMap.Tenants())));
        main.add(tenantsTable = new TenantsTable());
        main.add(inject(proto().tenantList().tenants(), new TenantInLeaseFolder()));

        //TODO
//        main.add(alignWidth(createHeaderWithEditLink(i18n.tr("Info"), new PtSiteMap.Info())));
//        main.add(inject(proto().tenantsWithInfo().tenants(), createTenantView()));
//
//        main.add(alignWidth(createHeaderWithEditLink(i18n.tr("Financial"), new PtSiteMap.Financial())));
//        main.add(inject(proto().tenantFinancials(), createFinancialView()));

        main.add(alignWidth(new VistaHeaderBar(i18n.tr("Lease Terms"))));
        main.add(new LeaseTermsCheck());

        main.add(inject(proto().charges(), new ChargesViewForm(this)));

        main.add(alignWidth(new VistaHeaderBar(i18n.tr("Digital Signature"))));
        main.add(new SignatureView());

        // last step - add building picture on the right:
        HorizontalPanel content = new HorizontalPanel();
        main.setWidth("700px");
        content.add(main);
        VerticalPanel rightSite = new VerticalPanel();
        rightSite.add(new BuildingPicture());
        rightSite.add(new DemoReportButtons());
        content.add(rightSite);
        return content;
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

    private Widget createHeaderWithEditLink(String captionTxt, final AppPlace link) {

        Button edit = new Button(i18n.tr("Edit"));
        edit.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        edit.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        edit.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (link.getClass().equals(PtSiteMap.Info.class) || link.getClass().equals(PtSiteMap.Financial.class)) {
                    if (!getValue().tenantList().tenants().isEmpty()) {
                        link.putArg(PtSiteMap.STEP_ARG_NAME, getValue().tenantList().tenants().get(0).getPrimaryKey().toString());
                    }
                }

                getPresenter().goToPlace(link);
            }
        });

        return new VistaHeaderBar(captionTxt, edit);
    }

    private static Widget alignWidth(Widget e) {
        e.setWidth("100%");
        return e;
    }

    /*
     * Selected Apartment information view implementation
     */
    private class ApartmentView extends HorizontalPanel {

        public ApartmentView() {

            alignWidth(this);

            FlowPanel main = new FlowPanel();
            main.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            main.getElement().getStyle().setPaddingRight(1, Unit.EM);
            add(main);
            setCellWidth(main, "100%");

            Map<String, String> tableLayout = new LinkedHashMap<String, String>();
            tableLayout.put("Type", "30%");
            tableLayout.put("Unit", "20%");
            tableLayout.put("Bedrooms", "10%");
            tableLayout.put("Bathrooms", "10%");
            tableLayout.put("Available", "15%");

            // fill header:
            for (Entry<String, String> e : tableLayout.entrySet()) {
                HTML label = new HTML(i18n.tr(e.getKey()));
                label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                label.setWidth(e.getValue());
                main.add(label);
            }

            Widget sp = new VistaLineSeparator(100, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setPaddingRight(2, Unit.EM);
            sp.getElement().getStyle().setPosition(Position.RELATIVE);
            sp.getElement().getStyle().setLeft(-1, Unit.EM);
            main.add(sp);

            // add table content panel:
            FlowPanel content;
            main.add(content = new FlowPanel());

            addCell(tableLayout, content, "Type", inject(proto().selectedUnit().name()).asWidget());
            addCell(tableLayout, content, "Unit", inject(proto().selectedUnit().suiteNumber()).asWidget());
            addCell(tableLayout, content, "Bedrooms", inject(proto().selectedUnit().bedrooms()).asWidget());
            addCell(tableLayout, content, "Bathrooms", inject(proto().selectedUnit().bathrooms()).asWidget());
            addCell(tableLayout, content, "Available", inject(proto().selectedUnit().leaseFrom()).asWidget());
        }

        private void addCell(Map<String, String> tableLayout, FlowPanel content, String cellName, Widget cellContent) {
            cellContent.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            cellContent.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);
            cellContent.setWidth(tableLayout.get(cellName));
            content.add(cellContent);
        }
    }

    /*
     * Selected Apartment information view implementation
     */
    private class LeaseTermView extends HorizontalPanel {

        public LeaseTermView() {

            alignWidth(this);

            // add lease term/price:
            FlowPanel content = new FlowPanel();
            content.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            content.getElement().getStyle().setPaddingLeft(1, Unit.EM);

            Widget label = inject(proto().selectedUnit().unitRent()).asWidget();
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(DecorationUtils.inline(label, "auto"));
            label = new HTML("&nbsp;/ " + i18n.tr("month"));
            label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            content.add(DecorationUtils.inline(label));

            add(content);
            setCellWidth(content, "30%");

            // add static lease terms blah-blah:
            HTML availabilityAndPricing = new HTML(PortalResources.INSTANCE.availabilityAndPricing().getText());
            availabilityAndPricing.getElement().getStyle().setPaddingRight(1, Unit.EM);
            add(availabilityAndPricing);
            setCellWidth(availabilityAndPricing, "70%");
        }
    }

    /*
     * Tenants information table implementation
     */
    static private class TenantsTable extends HorizontalPanel {

        private static final Map<String, String> COLUMNS = new LinkedHashMap<String, String>();
        static {
            COLUMNS.put("Name", "29%");
            COLUMNS.put("Date of Birth", "15%");
            COLUMNS.put("Email", "30%");
            COLUMNS.put("Relationship", "15%");
            COLUMNS.put("Status", "11%");
        }

        public TenantsTable() {

            alignWidth(this);

            FlowPanel main = new FlowPanel();
            main.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            main.getElement().getStyle().setPaddingRight(1, Unit.EM);
            add(main);
            setCellWidth(main, "100%");

            // fill header:
            for (Entry<String, String> e : COLUMNS.entrySet()) {
                HTML label = new HTML(i18n.tr(e.getKey()));
                label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                label.setWidth(e.getValue());
                main.add(label);
            }

            Widget sp = new VistaLineSeparator(100, Unit.PCT, 0.5, Unit.EM, 0.5, Unit.EM);
            sp.getElement().getStyle().setPadding(0, Unit.EM);
            sp.getElement().getStyle().setPaddingRight(2, Unit.EM);
            sp.getElement().getStyle().setPosition(Position.RELATIVE);
            sp.getElement().getStyle().setLeft(-1, Unit.EM);
            main.add(sp);
        }

    }

    class TenantInLeaseFolder extends VistaTableFolder<TenantInLease> {

        public TenantInLeaseFolder() {
            super(TenantInLease.class);
        }

        @Override
        public CEditableComponent<?, ?> create(IObject<?> member) {
            if (member instanceof TenantInLease) {
                return new TenantInLeaseEditor(TenantsTable.COLUMNS);
            } else {
                return super.create(member);
            }
        }

        @Override
        protected List<EntityFolderColumnDescriptor> columns() {
            return null;
        }

    }

    class TenantInLeaseEditor extends CEntityEditor<TenantInLease> {

        public TenantInLeaseEditor(Map<String, String> tableLayout) {
            super(TenantInLease.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel content = new FlowPanel();
            content.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            content.getElement().getStyle().setPaddingRight(1, Unit.EM);
            addCell(TenantsTable.COLUMNS, content, "Name", DecorationUtils.formFullName(this, proto().tenant().person()));
            addCell(TenantsTable.COLUMNS, content, "Date of Birth", inject(proto().tenant().person().birthDate()).asWidget());
            addCell(TenantsTable.COLUMNS, content, "Email", inject(proto().tenant().person().email()).asWidget());
            //TODO
            // if (first) {
            //      addCell(tableLayout, content, "Relationship", new HTML("&nbsp;"));
            //  } else {
            addCell(TenantsTable.COLUMNS, content, "Relationship", inject(proto().relationship()).asWidget());
            //  }
            addCell(TenantsTable.COLUMNS, content, "Status", inject(proto().status()).asWidget());
            return content;
        }

        private void addCell(Map<String, String> tableLayout, FlowPanel content, String cellName, Widget cellContent) {
            cellContent.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            cellContent.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);
            cellContent.setWidth(tableLayout.get(cellName));
            content.add(cellContent);
        }
    }

    static class TenantInfoFolder extends VistaTableFolder<TenantInLease> {

        public TenantInfoFolder() {
            super(TenantInLease.class);
        }

        @Override
        protected IFolderDecorator<TenantInLease> createDecorator() {
            return new BoxReadOnlyFolderDecorator<TenantInLease>();
        }

        @Override
        protected List<EntityFolderColumnDescriptor> columns() {
            return null;
        }
    }

//    /*
//     * Tenants detailed information view implementation
//     */
//    public CEntityFolder<TenantInLease> createTenantView() {
//        return new CEntityFolder<TenantInLease>(TenantInLease.class) {
//
//            @Override
//            protected CEntityFolderBoxEditor<TenantInLease> createItem(boolean first) {
//                return new SummaryViewTenantInfo();
//            }
//
//            @Override
//            protected IFolderDecorator<TenantInLease> createDecorator() {
//                return new BoxReadOnlyFolderDecorator<TenantInLease>();
//            }
//        };
//    }
//
//    /*
//     * Financial detailed information view implementation
//     */
//    public CEntityFolder<SummaryTenantFinancialDTO> createFinancialView() {
//        return new CEntityFolder<SummaryTenantFinancialDTO>(SummaryTenantFinancialDTO.class) {
//
//            @Override
//            protected CEntityFolderBoxEditor<SummaryTenantFinancialDTO> createItem(boolean first) {
//                return new SummaryViewTenantFinancial();
//            }
//
//            @Override
//            protected IFolderDecorator<SummaryTenantFinancialDTO> createDecorator() {
//                return new BoxReadOnlyFolderDecorator<SummaryTenantFinancialDTO>();
//            }
//        };
//    }

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
            bind(check, proto().agree());
            VistaWidgetDecorator agree = new VistaWidgetDecorator(check, new DecorationData(0, Unit.EM, 0, Unit.EM));
            agree.asWidget().getElement().getStyle().setMarginLeft(40, Unit.PCT);
            agree.asWidget().getElement().getStyle().setMarginTop(0.5, Unit.EM);
            add(agree);

            check.addValueValidator(new EditableValueValidator<Boolean>() {

                @Override
                public boolean isValid(CEditableComponent<Boolean, ?> component, Boolean value) {
                    return value == Boolean.TRUE;
                }

                @Override
                public String getValidationMessage(CEditableComponent<Boolean, ?> component, Boolean value) {
                    return i18n.tr("You should agree to terms");
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
            bind(edit, proto().fullName());

            DecorationData dd = new DecorationData(16d, HasHorizontalAlignment.ALIGN_LEFT, 16);
            dd.labelStyleName = DEFAULT_STYLE_PREFIX + StyleSuffix.DigitalSignatureLabel.name();
            //            dd.componentStyle = DEFAULT_STYLE_PREFIX + StyleSuffix.DigitalSignatureEdit.name();
            VistaWidgetDecorator signature = new VistaWidgetDecorator(edit, dd);
            signature.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DigitalSignature.name());
            signature.getElement().getStyle().setMarginTop(1, Unit.EM);
            signature.getElement().getStyle().setPaddingTop(1, Unit.EM);
            signature.getElement().getStyle().setPaddingLeft(1.5, Unit.EM);
            signature.setHeight("3em");
            add(alignWidth(signature));

            // validation:
            edit.addValueValidator(new EditableValueValidator<String>() {

                @Override
                public boolean isValid(CEditableComponent<String, ?> component, String value) {
                    return isSignatureValid(value);
                }

                @Override
                public String getValidationMessage(CEditableComponent<String, ?> component, String value) {
                    return i18n.tr("Digital Signature string should match your name");
                }
            });
        }
    }

    private boolean isSignatureValid(String signature) {
        if (CommonsStringUtils.isEmpty(signature)) {
            return false;
        }
        for (TenantInLease pti : getValue().tenantList().tenants()) {
            if (pti.status().getValue() == Status.Applicant) {
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
