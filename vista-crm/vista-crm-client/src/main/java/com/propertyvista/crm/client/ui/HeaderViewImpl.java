package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.Toolbar;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.i18n.CompiledLocale;

public class HeaderViewImpl extends FlowPanel implements HeaderView {

    public static String BACK_TO_CRM = "vista_Back2CRM";

    private static final I18n i18n = I18n.get(HeaderViewImpl.class);

    private HeaderPresenter presenter;

    private Button userButton;

    private Button exitAdminButton;

    private Button communicationButton;

    private Button adminButton;

    private HTML thisIsProduction;

    private HTML thisIsDemo;

    private MenuItem support;

    private Button languageButton;

    private ButtonMenuBar languageButtonMenu;

    private Button sideMenuButton;

    private static String brandedHeader;

    private static boolean useLogoImage;

    private LayoutType layoutType;

    private boolean loggedIn = false;

    //TODO Misha How can I do this properly ?
    @Deprecated
    public static void temporaryWayToSetTitle(String title, boolean logoImageAvalable) {
        brandedHeader = title;
        useLogoImage = logoImageAvalable;
    }

    public HeaderViewImpl() {
        setStyleName(SiteViewTheme.StyleName.SiteViewHeader.name());

        add(createLeftToolbar());

        add(createLogoContainer());

        add(createRightToolbar());

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    private IsWidget createLeftToolbar() {
        Toolbar toolbar = new Toolbar();
        sideMenuButton = new Button(CrmImages.INSTANCE.menu(), new Command() {
            @Override
            public void execute() {
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
            }
        });
        toolbar.addItem(sideMenuButton);
        return toolbar;
    }

    private Widget createLogoContainer() {
        SimplePanel logoContainer = new SimplePanel();
        logoContainer.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
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

    private IsWidget createRightToolbar() {
        Toolbar toolbar = new Toolbar();
        toolbar.getElement().getStyle().setPosition(Position.ABSOLUTE);
        toolbar.getElement().getStyle().setProperty("right", "0");

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

        userButton = new Button("");

        ButtonMenuBar userButtonMenu = new ButtonMenuBar();
        userButton.setMenu(userButtonMenu);

        userButtonMenu.addItem(new MenuItem(i18n.tr("Account"), new Command() {
            @Override
            public void execute() {
                presenter.showAccount();
            }
        }));
        userButtonMenu.addItem(new MenuItem(i18n.tr("Settings"), new Command() {
            @Override
            public void execute() {
                presenter.showProperties();
            }
        }));
        userButtonMenu.addItem(new MenuItem(i18n.tr("LogOut"), new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        }));
        userButtonMenu.addItem(support = new MenuItem(i18n.tr("Support"), new Command() {
            @Override
            public void execute() {
                presenter.getSatisfaction();
            }
        }));

        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED) {
            communicationButton = new Button(CrmImages.INSTANCE.alert(), new Command() {
                @Override
                public void execute() {
                    switch (layoutType) {
                    case phonePortrait:
                    case phoneLandscape:
                        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideComm));
                        break;
                    default:
                        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(communicationButton));
                        break;
                    }
                }
            });
        }

        exitAdminButton = new Button(i18n.tr("Exit Administration"));
        exitAdminButton.ensureDebugId("home");
        exitAdminButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showHome();
            }
        });

        adminButton = new Button(i18n.tr("Administration"));
        adminButton.ensureDebugId("administration");
        adminButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showSettings();
            }
        });

        languageButton = new Button("");

        languageButtonMenu = new ButtonMenuBar();
        languageButton.setMenu(languageButtonMenu);

        // from toolbar:

        toolbar.addItem(thisIsProduction);
        toolbar.addItem(thisIsDemo);

        toolbar.addItem(userButton);
        toolbar.addItem(exitAdminButton);
        toolbar.addItem(adminButton);
        toolbar.addItem(languageButton);
        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED) {
            toolbar.addItem(communicationButton);
        }
        return toolbar;
    }

    @Override
    public void setPresenter(final HeaderPresenter presenter) {
        this.presenter = presenter;
        calculateActionsState();
    }

    @Override
    public void onLogedOut() {
        this.loggedIn = false;
        exitAdminButton.setVisible(false);
        adminButton.setVisible(false);
        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED) {
            communicationButton.setVisible(false);
        }
        support.setVisible(false);

        userButton.setVisible(false);
        userButton.setTextLabel("");

        thisIsDemo.getElement().getStyle().setPosition(Position.ABSOLUTE);
        thisIsDemo.getElement().getStyle().setProperty("marginLeft", "auto");
        thisIsDemo.getElement().getStyle().setProperty("marginRight", "auto");
        thisIsDemo.getElement().getStyle().setProperty("left", "0px");
        thisIsDemo.getElement().getStyle().setProperty("width", "100%");

        calculateActionsState();
    }

    @Override
    public void onLogedIn(String userName) {
        this.loggedIn = true;
        exitAdminButton.setVisible(true);
        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED) {
            communicationButton.setVisible(true);
        }
        adminButton.setVisible(true);
        support.setVisible(true);

        userButton.setVisible(true);
        userButton.setTextLabel(userName);

        thisIsDemo.getElement().getStyle().setPosition(Position.RELATIVE);
        thisIsDemo.getElement().getStyle().setProperty("marginLeft", "1em");
        thisIsDemo.getElement().getStyle().setProperty("marginRight", "1em");
        thisIsDemo.getElement().getStyle().setProperty("left", "0px");
        thisIsDemo.getElement().getStyle().setProperty("width", null);

        calculateActionsState();
    }

    @Override
    public void setAvailableLocales(List<CompiledLocale> localeList) {
        languageButtonMenu.clearItems();
        for (final CompiledLocale compiledLocale : localeList) {
            languageButtonMenu.addItem(new MenuItem(compiledLocale.getNativeDisplayName(), new Command() {
                @Override
                public void execute() {
                    presenter.setLocale(compiledLocale);
                }
            }));
        }
        languageButton.setTextLabel(ClientLocaleUtils.getCurrentLocale().getNativeDisplayName());
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
            //TODO   messages.setText("Messages (" + String.valueOf(number) + ")");
        } else {
            //TODO  messages.setText("Messages");
        }
    }

    private void doLayout(LayoutType layoutType) {
        this.layoutType = layoutType;
        calculateActionsState();
    }

    private void calculateActionsState() {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            sideMenuButton.setVisible(loggedIn);
            break;
        default:
            sideMenuButton.setVisible(false);
            break;
        }

        if (presenter != null) {
            if (presenter.isAdminPlace()) {
                exitAdminButton.setVisible(true);
                adminButton.setVisible(false);
            } else {
                exitAdminButton.setVisible(false);
                adminButton.setVisible(true);
            }
        }
    }

}