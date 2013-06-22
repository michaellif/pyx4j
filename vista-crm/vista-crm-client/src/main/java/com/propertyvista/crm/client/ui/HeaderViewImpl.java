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

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.shared.i18n.CompiledLocale;

public class HeaderViewImpl extends HorizontalPanel implements HeaderView {

    public enum Theme {
        Gainsboro, VillageGreen, BlueCold, BrownWarm
    }

    public static String BACK_TO_CRM = "vista_Back2CRM";

    private static final I18n i18n = I18n.get(HeaderViewImpl.class);

    private Presenter presenter;

    private Anchor greetings;

    private Anchor logout;

    private Anchor login;

    private Anchor home;

    private Anchor settings;

    private HTML thisIsProduction;

    private HTML thisIsDemo;

    private MenuBar languageMenu;

    private MenuBar languages;

    private Anchor getSatisfaction;

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

        greetings = new Anchor(null);
        greetings.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.showAccount();
            }
        });
        greetings.ensureDebugId("account");

        logout = new Anchor(null);
        logout.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.logout();
            }
        });

        logout.ensureDebugId("logout");
        logout.setHTML(i18n.tr("LogOut"));
        logout.setVisible(false);

        login = new Anchor(null);
        login.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.login();
            }
        });

        login.ensureDebugId("login");
        login.setHTML(i18n.tr("Log In"));

        home = new Anchor(null);
        home.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showHome();
            }
        });

        home.ensureDebugId("home");
        home.setHTML(i18n.tr("Home"));

        settings = new Anchor(null);
        settings.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showSettings();
            }
        });

        settings.ensureDebugId("administration");
        settings.setHTML(i18n.tr("Administration"));

        languageMenu = new MenuBar();
        languageMenu.setAutoOpen(false);
        languageMenu.setAnimationEnabled(false);
        languageMenu.setFocusOnHoverEnabled(true);
        languages = new MenuBar(true);
        MenuItem item = new MenuItem(ClientNavigUtils.getCurrentLocale().toString(), languages);
        languageMenu.addItem(item);

        getSatisfaction = new Anchor(null);
        getSatisfaction.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.getSatisfaction();
            }
        });

        getSatisfaction.ensureDebugId("getSatisfaction");
        getSatisfaction.setHTML(i18n.tr("Support"));

        toolbar.add(thisIsProduction);
        toolbar.add(thisIsDemo);

        toolbar.add(greetings);
        toolbar.add(home);
        toolbar.add(settings);
        toolbar.add(login);
        toolbar.add(logout);
        toolbar.add(languageMenu);
        toolbar.add(getSatisfaction);

        return toolbar;
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        login.setVisible(false);
        home.setVisible(false);
        settings.setVisible(false);
        getSatisfaction.setVisible(false);
        greetings.setVisible(false);
        greetings.setHTML("");

        thisIsDemo.getElement().getStyle().setPosition(Position.ABSOLUTE);
        thisIsDemo.getElement().getStyle().setProperty("marginLeft", "auto");
        thisIsDemo.getElement().getStyle().setProperty("marginRight", "auto");
        thisIsDemo.getElement().getStyle().setProperty("left", "0px");
        thisIsDemo.getElement().getStyle().setProperty("width", "100%");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        home.setVisible(true);
        settings.setVisible(true);
        getSatisfaction.setVisible(true);
        greetings.setHTML(i18n.tr("Welcome {0}", userName));
        greetings.setVisible(true);

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
            Command changeLanguage = new Command() {
                @Override
                public void execute() {
                    presenter.setLocale(compiledLocale);
                }
            };
            MenuItem item = new MenuItem(compiledLocale.getNativeDisplayName(), changeLanguage);
            languages.addItem(item);
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

}