package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Toolbar;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.i18n.CompiledLocale;

public class HeaderViewImpl extends HorizontalPanel implements HeaderView {

    public enum Theme {
        Gainsboro, VillageGreen, BlueCold, BrownWarm
    }

    public static String BACK_TO_CRM = "vista_Back2CRM";

    private static final I18n i18n = I18n.get(HeaderViewImpl.class);

    private Presenter presenter;

    private MenuBar customer;

    private MenuItem customerName;

    private Anchor home;

    private Anchor messages;

    private Anchor settings;

    private HTML thisIsProduction;

    private HTML thisIsDemo;

    private MenuBar language;

    private MenuBar languages;

    private Anchor support;

    private static String brandedHeader;

    private static boolean useLogoImage;

    //TODO Misha How can I do this properly ?
    @Deprecated
    public static void temporaryWayToSetTitle(String title, boolean logoImageAvalable) {
        brandedHeader = title;
        useLogoImage = logoImageAvalable;
    }

    public HeaderViewImpl() {
        setStyleName(SiteViewTheme.StyleName.SiteViewHeader.name());

        Widget w;

        add(w = createLogoContainer());
        setCellHorizontalAlignment(w, HasHorizontalAlignment.ALIGN_LEFT);

        add(w = createActionsContainer());
        setCellHorizontalAlignment(w, HasHorizontalAlignment.ALIGN_RIGHT);
    }

    private Widget createLogoContainer() {
        SimplePanel logoContainer = new SimplePanel();
        HasClickHandlers logoElement;

        if (useLogoImage) {
            Image logoImage = new Image(MediaUtils.createCrmLogoUrl());
            logoImage.getElement().getStyle().setProperty("maxHeight", "50px");
            logoImage.getElement().getStyle().setMarginLeft(20, Unit.PX);
            logoImage.getElement().getStyle().setFloat(Style.Float.LEFT);
            logoImage.getElement().getStyle().setCursor(Cursor.POINTER);
            logoImage.setTitle(i18n.tr("Home"));

            logoContainer.setWidget(logoImage);
            logoElement = logoImage;
        } else {
            HTML logoHtml = new HTML("<h1>" + (brandedHeader != null ? new SafeHtmlBuilder().appendEscaped(brandedHeader).toSafeHtml().asString() : "")
                    + "</h1>");
            logoHtml.getElement().getStyle().setCursor(Cursor.POINTER);

            logoContainer.setWidget(logoHtml);
            logoElement = logoHtml;
        }

        logoElement.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.navigToLanding();
            }
        });

        return logoContainer;
    }

    private Widget createActionsContainer() {
        Toolbar toolbar = new Toolbar();
        toolbar.addStyleName(SiteViewTheme.StyleName.SiteViewAction.name());

        thisIsProduction = new HTML("PRODUCTION SUPPORT!");
        thisIsProduction.getElement().getStyle().setColor("red");
        thisIsProduction.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        thisIsProduction.getElement().getStyle().setFontSize(30, Unit.PX);
        thisIsProduction.getElement().getStyle().setMarginLeft(1, Unit.EM);
        thisIsProduction.getElement().getStyle().setMarginRight(1, Unit.EM);
        thisIsProduction.getElement().getStyle().setProperty("textAlign", "center");
        thisIsProduction.setVisible(false);

        thisIsDemo = new HTML(i18n.tr("Demo Environment"));
        thisIsDemo.getElement().getStyle().setColor("green");
        thisIsDemo.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        thisIsDemo.getElement().getStyle().setFontSize(30, Unit.PX);
        thisIsDemo.getElement().getStyle().setProperty("textAlign", "center");
        thisIsDemo.setVisible(false);

        customer = new MenuBar();
        customer.setAutoOpen(false);
        customer.setAnimationEnabled(false);
        customer.setFocusOnHoverEnabled(true);

        MenuBar customerMenu = new MenuBar(true);
        customer.addItem(customerName = new MenuItem("", customerMenu));
        customerMenu.addItem(new MenuItem(i18n.tr("Account"), new Command() {
            @Override
            public void execute() {
                presenter.showAccount();
            }
        }));
        customerMenu.addItem(new MenuItem(i18n.tr("Settings"), new Command() {
            @Override
            public void execute() {
                presenter.showProperties();
            }
        }));
        customerMenu.addItem(new MenuItem(i18n.tr("LogOut"), new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        }));

        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED) {
            messages = new Anchor(i18n.tr("Messages"), true);
            messages.ensureDebugId("messages");
            messages.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.showMessages(messages.getAbsoluteLeft(), messages.getAbsoluteTop());
                }
            });
        }

        home = new Anchor(i18n.tr("Home"), true);
        home.ensureDebugId("home");
        home.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showHome();
            }
        });

        settings = new Anchor(i18n.tr("Administration"), true);
        settings.ensureDebugId("administration");
        settings.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showSettings();
            }
        });

        language = new MenuBar();
        language.setAutoOpen(false);
        language.setAnimationEnabled(false);
        language.setFocusOnHoverEnabled(true);
        language.addItem(new MenuItem(ClientNavigUtils.getCurrentLocale().toString(), languages = new MenuBar(true)));

        support = new Anchor(i18n.tr("Support"), true);
        support.ensureDebugId("support");
        support.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.getSatisfaction();
            }
        });

        // from toolbar:

        toolbar.addItem(thisIsProduction);
        toolbar.addItem(thisIsDemo);

        toolbar.addItem(customer);
        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED) {
            toolbar.addItem(messages);
        }
        toolbar.addItem(home);
        toolbar.addItem(settings);
        toolbar.addItem(language);
        toolbar.addItem(support);

        return toolbar.asWidget();
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        home.setVisible(false);
        settings.setVisible(false);
        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED) {
            messages.setVisible(false);
        }
        support.setVisible(false);

        customer.setVisible(false);
        customerName.setText("");

        thisIsDemo.getElement().getStyle().setPosition(Position.ABSOLUTE);
        thisIsDemo.getElement().getStyle().setProperty("marginLeft", "auto");
        thisIsDemo.getElement().getStyle().setProperty("marginRight", "auto");
        thisIsDemo.getElement().getStyle().setProperty("left", "0px");
        thisIsDemo.getElement().getStyle().setProperty("width", "100%");
    }

    @Override
    public void onLogedIn(String userName) {
        home.setVisible(true);
        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED) {
            messages.setVisible(true);
        }
        settings.setVisible(true);
        support.setVisible(true);

        customer.setVisible(true);
        customerName.setText(userName);

        thisIsDemo.getElement().getStyle().setPosition(Position.RELATIVE);
        thisIsDemo.getElement().getStyle().setProperty("marginLeft", "1em");
        thisIsDemo.getElement().getStyle().setProperty("marginRight", "1em");
        thisIsDemo.getElement().getStyle().setProperty("left", "0px");
        thisIsDemo.getElement().getStyle().setProperty("width", null);
    }

    @Override
    public void setAvailableLocales(List<CompiledLocale> localeList) {
        languages.clearItems();
        for (final CompiledLocale compiledLocale : localeList) {
            languages.addItem(new MenuItem(compiledLocale.getNativeDisplayName(), new Command() {
                @Override
                public void execute() {
                    presenter.setLocale(compiledLocale);
                }
            }));
        }
    }

    @Override
    public void setDisplayThisIsProductionWarning(boolean displayThisIsProductionWarning) {
        thisIsProduction.setVisible(displayThisIsProductionWarning);
    }

    @Override
    public void setDisplayThisIsDemoWarning(boolean displayThisIsDemoWarning) {
        thisIsDemo.setVisible(displayThisIsDemoWarning);
    }

    @Override
    public void setNumberOfMessages(int number) {
        //messages.setVisible((ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED && SecurityController
        //        .checkBehavior(VistaBasicBehavior.CRM)));
        if (number > 0) {
            messages.setText("Messages (" + String.valueOf(number) + ")");
        } else {
            messages.setText("Messages");
        }
    }
}