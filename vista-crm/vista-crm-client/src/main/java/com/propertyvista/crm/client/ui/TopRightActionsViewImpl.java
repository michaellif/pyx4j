package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.shared.CompiledLocale;

public class TopRightActionsViewImpl extends FlowPanel implements TopRightActionsView {

    public enum Theme {
        Gainsboro, VillageGreen, BlueCold, BrownWarm
    }

    public static String BACK_TO_CRM = "vista_Back2CRM";

    private static final I18n i18n = I18n.get(TopRightActionsViewImpl.class);

    private Presenter presenter;

    private final Anchor greetings;

    private final Anchor logout;

    private final Anchor login;

    private final Anchor home;

    private final Anchor settings;

    private final HTML thisIsProduction;

    private final HTML thisIsDemo;

    private final HorizontalPanel locales;

    private CompiledLocale language;

    MenuBar languageMenu;

    MenuBar languages;

/* Everything related to Search, Alerts and Messages is commented out and awaits implementation */

//    private final Image message;

//    private final Image alert;

//    private final SearchBox search;

    private final Anchor getSatisfaction;

    private final Theme otherTheme = Theme.BrownWarm;

    public TopRightActionsViewImpl() {
        setStyleName(CrmSitePanelTheme.StyleName.SiteViewAction.name());
        setSize("100%", "100%");

        HorizontalPanel container = new HorizontalPanel();
        container.getElement().getStyle().setFloat(Style.Float.RIGHT);
        container.setSize("30%", "100%");

        thisIsProduction = new HTML("This is PRODUCTION SUPPORT!");
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
        greetings.asWidget().getElement().getStyle().setDisplay(Display.INLINE);
        greetings.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
        greetings.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

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
        logout.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        login = new Anchor(null);
        login.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.login();
            }
        });

        login.ensureDebugId("login");
        login.setHTML(i18n.tr("Log In"));
        login.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        home = new Anchor(null);
        home.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showHome();
            }
        });

        home.ensureDebugId("home");
        home.setHTML(i18n.tr("Home"));
        home.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        settings = new Anchor(null);
        settings.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showSettings();
            }
        });

        settings.ensureDebugId("administration");
        settings.setHTML(i18n.tr("Administration"));
        settings.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        locales = new HorizontalPanel();
        locales.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);
        locales.setVisible(false);

        languageMenu = new MenuBar();
        languageMenu.setAutoOpen(false);
        languageMenu.setAnimationEnabled(false);
        languageMenu.setFocusOnHoverEnabled(true);
        languages = new MenuBar(true);
        MenuItem item = new MenuItem(ClientNavigUtils.getCurrentLocale().toString(), languages);
        languageMenu.addItem(item);
        languageMenu.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

//        alert = new Image(CrmImages.INSTANCE.alert());
//        alert.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                presenter.showAlerts();
//            }
//        });
//        alert.getElement().getStyle().setCursor(Cursor.POINTER);
//
//        message = new Image(CrmImages.INSTANCE.message());
//        message.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                presenter.showMessages();
//            }
//        });
//        message.getElement().getStyle().setCursor(Cursor.POINTER);
//
//        search = new SearchBox();

        getSatisfaction = new Anchor(null);
        getSatisfaction.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.getSatisfaction();
            }
        });

        getSatisfaction.ensureDebugId("getSatisfaction");
        getSatisfaction.setHTML(i18n.tr("Help"));
        getSatisfaction.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        /**
         * the following set of wrappers keep login/logout group relatively steady when
         * the elements right of it disappear
         */
//        SimplePanel searchwr = new SimplePanel();
//        searchwr.getElement().setAttribute("style", "min-width:12em");
//        searchwr.add(search);
//
//        SimplePanel messagewr = new SimplePanel();
//        messagewr.getElement().setAttribute("style", "min-width:3em");
//        messagewr.add(message);
//
//        SimplePanel alertwr = new SimplePanel();
//        alertwr.getElement().setAttribute("style", "min-width:3em");
//        alertwr.add(alert);

        container.add(thisIsProduction);
        container.add(thisIsDemo);
        container.add(greetings);
        container.add(home);
        container.add(settings);
        container.add(login);
        container.add(logout);
//        container.add(searchwr);
//        container.add(messagewr);
//        container.add(alertwr);
        container.add(languageMenu);
        container.add(getSatisfaction);
        add(container);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;

// removed by VISTA-1995:
//        // style settings link:
//        if (presenter.isSettingsPlace()) {
//            settings.setValue(i18n.tr("Home"));
//            settings.asWidget().addStyleName(BACK_TO_CRM);
//        } else {
//            settings.setValue(i18n.tr("Administration"));
//            settings.asWidget().removeStyleName(BACK_TO_CRM);
//        }
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        login.setVisible(false);
//        alert.setVisible(false);
//        message.setVisible(false);
//        search.setVisible(false);
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
//        alert.setVisible(true);
//        message.setVisible(true);
//        search.setVisible(true);
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