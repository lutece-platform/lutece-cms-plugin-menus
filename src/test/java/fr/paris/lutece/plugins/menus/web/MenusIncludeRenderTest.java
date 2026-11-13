/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.menus.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.menus.business.CustomMenu;
import fr.paris.lutece.plugins.menus.business.CustomMenuHome;
import fr.paris.lutece.plugins.menus.business.CustomMenuItem;
import fr.paris.lutece.plugins.menus.business.CustomMenuItemHome;
import fr.paris.lutece.plugins.menus.business.Menus;
import fr.paris.lutece.plugins.menus.business.MenusHome;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.FreeMarkerTemplateService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.TemplateHTMLOutputModel;

/**
 * Renders the menus the page includes put in the model of the portal page, with HTML auto-escaping (LUT-33153): each
 * menu is HTML rendered by the plugin, put in the model as markup, so the page template writes it as HTML; the labels
 * and URLs typed in the back office are escaped inside it, and a URL with a scheme other than http, https, mailto or tel
 * is not written in a link. The tree menus of the pages (MainTreeMenuInclude, MainTreeMenuIncludeAllPages,
 * TreeMenuPageInclude) are marked the same way; their cache service does not start in the test container.
 */
public class MenusIncludeRenderTest extends LuteceTestCase
{
    private static final String BOOKMARK = "menus_render_test";
    private static final String LABEL = "Plan <b>\"accueil\"</b>";
    private static final String LABEL_ESCAPED = "Plan &lt;b&gt;&quot;accueil&quot;&lt;/b&gt;";
    private static final String URL = "https://example.org/plan?a=1&b=2";
    private static final String URL_ESCAPED = "href=\"https://example.org/plan?a=1&amp;b=2\"";
    private static final String URL_REFUSED = "javascript:void(0)";
    /** The function of the front office theme the custom menus call, auto-included by the theme of the site. */
    private static final String SAFE_URL = "skin/themes/macros/utilities/functions/safeUrl.ftl";

    private final List<Integer> _listCreated = new ArrayList<>( );
    private final List<Integer> _listMenusCreated = new ArrayList<>( );
    private boolean _bSafeUrlAdded;

    @BeforeEach
    protected void addSafeUrl( )
    {
        if ( !FreeMarkerTemplateService.getInstance( ).getAutoIncludes( ).contains( SAFE_URL ) )
        {
            FreeMarkerTemplateService.getInstance( ).addAutoInclude( SAFE_URL );
            _bSafeUrlAdded = true;
        }
    }

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        for ( Integer nId : _listCreated )
        {
            CustomMenuItemHome.removeByMenuId( nId );
            CustomMenuHome.remove( nId );
        }
        for ( Integer nId : _listMenusCreated )
        {
            MenusHome.remove( nId );
        }
        if ( _bSafeUrlAdded )
        {
            FreeMarkerTemplateService.getInstance( ).removeAutoInclude( SAFE_URL );
        }
        super.tearDown( );
    }

    /**
     * Creates a custom menu with an external link and a link whose URL is refused.
     *
     * @param strType
     *            the type of the menu
     * @param strBookmark
     *            the bookmark of the menu
     */
    private void createMenu( String strType, String strBookmark )
    {
        CustomMenu menu = new CustomMenu( );
        menu.setName( "Render test" );
        menu.setBookmark( strBookmark );
        menu.setType( strType );
        menu.setDescription( "" );
        CustomMenuHome.create( menu );
        _listCreated.add( menu.getId( ) );

        CustomMenuItem link = new CustomMenuItem( );
        link.setParentMenuId( menu.getId( ) );
        link.setType( "external_url" );
        link.setLabel( LABEL );
        link.setUrl( URL );
        link.setIsBlank( true );
        link.setOrder( 1 );
        CustomMenuItemHome.create( link );

        CustomMenuItem refused = new CustomMenuItem( );
        refused.setParentMenuId( menu.getId( ) );
        refused.setType( "external_url" );
        refused.setLabel( "Refused" );
        refused.setUrl( URL_REFUSED );
        refused.setOrder( 2 );
        CustomMenuItemHome.create( refused );
    }

    /**
     * Fills the model of a portal page with CustomMenuInclude, and returns the HTML of a marker.
     *
     * @param strMarker
     *            the marker
     * @return the HTML of the marker
     * @throws Exception
     *             if the markup cannot be read
     */
    private static String fillAndRead( String strMarker ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        new CustomMenuInclude( ).fillTemplate( model, null, 0, new MockHttpServletRequest( ) );

        Object value = model.get( strMarker );
        assertTrue( value instanceof TemplateHTMLOutputModel, strMarker + " : " + value );
        String strHtml = HTMLOutputFormat.INSTANCE.getMarkupString( (TemplateHTMLOutputModel) value );

        String strPage = AppTemplateService.getTemplateFromStringFtl( "<nav>${" + strMarker + "}</nav>", null, model ).getHtml( );
        assertTrue( strPage.contains( "<nav>" + strHtml.substring( 0, Math.min( 40, strHtml.length( ) ) ) ), strPage );

        return strHtml;
    }

    /**
     * Checks the links of the menu: tags kept, label and URL escaped, refused URL not written.
     *
     * @param strHtml
     *            the HTML of the menu
     */
    private static void assertLinks( String strHtml )
    {
        assertTrue( strHtml.contains( "<a class=\"nav-link\"" ), strHtml );
        assertTrue( strHtml.contains( LABEL_ESCAPED ), strHtml );
        assertTrue( strHtml.contains( URL_ESCAPED ), strHtml );
        assertFalse( strHtml.contains( "javascript:" ), strHtml );
        assertFalse( strHtml.contains( "<b>" ), strHtml );
    }

    /**
     * The menu written by its bookmark is markup: its tags are kept, the data inside it is escaped.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testBookmarkMenuIsMarkup( ) throws Exception
    {
        createMenu( "submenu", BOOKMARK );

        String strHtml = fillAndRead( BOOKMARK );

        assertTrue( strHtml.contains( "<ul class=\"custom-submenu-list\">" ), strHtml );
        assertTrue( strHtml.contains( "target=\"_blank\"" ), strHtml );
        assertLinks( strHtml );
    }

    /**
     * The main menu, written by customMenuMainPage and by the former marker page_main_menu_html, is markup.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testMainMenuIsMarkup( ) throws Exception
    {
        createMenu( "main", BOOKMARK + "_main" );

        assertLinks( fillAndRead( "customMenuMainPage" ) );
        assertLinks( fillAndRead( "page_main_menu_html" ) );
    }

    /**
     * The sidebar menu is markup.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testSidebarMenuIsMarkup( ) throws Exception
    {
        createMenu( "sidebar", BOOKMARK + "_sidebar" );

        assertLinks( fillAndRead( "customMenuSideBar" ) );
    }

    /**
     * Creates a menu of the menus_menus table.
     *
     * @param strType
     *            the type of the menu
     * @param strMarker
     *            the marker of the menu
     */
    private void createMenus( String strType, String strMarker )
    {
        Menus menus = new Menus( );
        menus.setMenuName( "Render test" );
        menus.setTypeMenu( strType );
        menus.setMenuMarker( strMarker );
        MenusHome.create( menus );
        _listMenusCreated.add( menus.getIdMenu( ) );
    }

    /**
     * The accessibility anchor is markup, and the name of the XPage read from the request is URL-encoded in its link.
     *
     * @throws Exception
     *             if the markup cannot be read
     */
    @Test
    public void testAccessibilityAnchorIsMarkup( ) throws Exception
    {
        createMenus( "accessibility_anchor_menu", "menus_anchor_test" );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "page", "a\"b<c d" );

        Map<String, Object> model = new HashMap<>( );
        new AccessibilityAnchorMenuInclude( ).fillTemplate( model, null, 0, request );

        Object value = model.get( "menus_anchor_test" );
        assertTrue( value instanceof TemplateHTMLOutputModel, String.valueOf( value ) );
        String strHtml = HTMLOutputFormat.INSTANCE.getMarkupString( (TemplateHTMLOutputModel) value );
        assertTrue( strHtml.contains( "?page=a%22b%3Cc%20d#header\"" ), strHtml );
        assertTrue( strHtml.contains( "<a href=" ), strHtml );
    }

    /**
     * The list of the XPages is markup.
     *
     * @throws Exception
     *             if the markup cannot be read
     */
    @Test
    public void testXPageListIsMarkup( ) throws Exception
    {
        createMenus( "xpage_menu", "menus_xpages_test" );

        Map<String, Object> model = new HashMap<>( );
        new XPageMenuInclude( ).fillTemplate( model, null, 0, new MockHttpServletRequest( ) );

        Object value = model.get( "menus_xpages_test" );
        assertTrue( value instanceof TemplateHTMLOutputModel, String.valueOf( value ) );
        String strHtml = HTMLOutputFormat.INSTANCE.getMarkupString( (TemplateHTMLOutputModel) value );
        assertTrue( strHtml.contains( "<ul>" ), strHtml );
        assertFalse( strHtml.contains( "&lt;" ), strHtml );
    }

    /**
     * A menu left as a String is escaped: the reason the includes put markup in the model.
     */
    @Test
    public void testAStringMenuIsEscaped( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( BOOKMARK, "<ul><li>Menu</li></ul>" );

        String strPage = AppTemplateService.getTemplateFromStringFtl( "${" + BOOKMARK + "}", null, model ).getHtml( );

        assertTrue( strPage.startsWith( "&lt;ul&gt;" ), strPage );
    }
}
