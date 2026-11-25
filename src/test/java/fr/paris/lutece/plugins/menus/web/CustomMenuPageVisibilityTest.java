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

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Timestamp;
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
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.style.PageTemplateHome;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.FreeMarkerTemplateService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.TemplateHTMLOutputModel;

/**
 * A custom menu does not show its « page » items whose page is restricted to a front office role to a visitor who does
 * not have that role
 */
public class CustomMenuPageVisibilityTest extends LuteceTestCase
{
    private static final String ROLE = "menus_visibility_test_role";

    private final List<Integer> _listPages = new ArrayList<>( );
    private Object _savedEnable;
    private Object _savedAuthentication;
    private Page _publicPage;
    private Page _reservedPage;
    private Page _reservedChild;
    private CustomMenu _menu;

    /** The function of the front office theme the custom menus call, auto-included by the theme of the site. */
    private static final String SAFE_URL = "skin/themes/macros/utilities/functions/safeUrl.ftl";

    private boolean _bSafeUrlAdded;

    @BeforeEach
    protected void createPages( )
    {
        if ( !FreeMarkerTemplateService.getInstance( ).getAutoIncludes( ).contains( SAFE_URL ) )
        {
            FreeMarkerTemplateService.getInstance( ).addAutoInclude( SAFE_URL );
            _bSafeUrlAdded = true;
        }
        _publicPage = createPage( PortalService.getRootPageId( ), "menus-visibility-public", Page.ROLE_NONE );
        _reservedPage = createPage( PortalService.getRootPageId( ), "menus-visibility-reserved", ROLE );
        _reservedChild = createPage( _reservedPage.getId( ), "menus-visibility-reserved-child", Page.ROLE_NONE );
    }

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        if ( _savedEnable != null )
        {
            setSecurityField( "_bEnable", _savedEnable );
            setSecurityField( "_authenticationService", _savedAuthentication );
        }
        if ( _bSafeUrlAdded )
        {
            FreeMarkerTemplateService.getInstance( ).removeAutoInclude( SAFE_URL );
        }
        if ( _menu != null )
        {
            CustomMenuItemHome.removeByMenuId( _menu.getId( ) );
            CustomMenuHome.remove( _menu.getId( ) );
        }
        for ( int i = _listPages.size( ) - 1; i >= 0; i-- )
        {
            PageHome.remove( _listPages.get( i ) );
        }
        super.tearDown( );
    }

    private Page createPage( int nParentId, String strName, String strRole )
    {
        Page page = new Page( );
        page.setParentPageId( nParentId );
        page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( ).get( 0 ).getId( ) );
        page.setName( strName );
        page.setDescription( strName + " description" );
        page.setMetaKeywords( "" );
        page.setMetaDescription( "" );
        page.setNodeStatus( 1 );
        page.setRole( strRole );
        page.setDateUpdate( new Timestamp( System.currentTimeMillis( ) ) );
        PageHome.create( page );
        _listPages.add( page.getId( ) );

        return page;
    }

    /**
     * Creates a menu of type « internal » with an item for each test page, its label dynamic
     *
     * @return the menu
     */
    private CustomMenu createMenu( )
    {
        CustomMenu menu = new CustomMenu( );
        menu.setName( "Visibility test" );
        menu.setBookmark( "menus_visibility_test" );
        menu.setType( "internal" );
        menu.setDescription( "" );
        CustomMenuHome.create( menu );
        _menu = menu;

        for ( Page page : new Page [ ] {
                _publicPage, _reservedPage, _reservedChild
        } )
        {
            CustomMenuItem item = new CustomMenuItem( );
            item.setParentMenuId( menu.getId( ) );
            item.setType( "page" );
            item.setSourceItemId( String.valueOf( page.getId( ) ) );
            item.setIsLabelDynamic( true );
            item.setLabel( page.getName( ) );
            item.setUrl( "page_id=" + page.getId( ) );
            CustomMenuItemHome.create( item );
        }

        return menu;
    }

    private static String render( int nMode ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        new CustomMenuInclude( ).fillTemplate( model, null, nMode, new MockHttpServletRequest( ) );

        return HTMLOutputFormat.INSTANCE.getMarkupString( (TemplateHTMLOutputModel) model.get( "menus_visibility_test" ) );
    }

    /**
     * An anonymous visitor does not get the items of the pages reserved to a role
     *
     * @throws Exception
     *             if the authentication cannot be set
     */
    @Test
    public void testReservedPageItemsAreHidden( ) throws Exception
    {
        createMenu( );
        enableAuthenticationWithAnonymousVisitor( );

        String strHtml = render( 0 );

        assertTrue( strHtml.contains( "page_id=" + _publicPage.getId( ) + "\"" ), strHtml );
        assertFalse( strHtml.contains( "page_id=" + _reservedPage.getId( ) + "\"" ), strHtml );
        // the visibility of a page does not depend on the one of its parent page in the core
        assertTrue( strHtml.contains( "page_id=" + _reservedChild.getId( ) + "\"" ), strHtml );
    }

    /**
     * Without authentication, every page item is shown
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testItemsWithoutAuthentication( ) throws Exception
    {
        createMenu( );

        String strHtml = render( 0 );

        assertTrue( strHtml.contains( "page_id=" + _publicPage.getId( ) + "\"" ), strHtml );
        assertTrue( strHtml.contains( "page_id=" + _reservedPage.getId( ) + "\"" ), strHtml );
    }

    /**
     * Authentication on, with a portal authentication where nobody is signed in
     */
    private void enableAuthenticationWithAnonymousVisitor( ) throws Exception
    {
        _savedEnable = getSecurityField( "_bEnable" );
        _savedAuthentication = getSecurityField( "_authenticationService" );
        Object authentication = Proxy.newProxyInstance( getClass( ).getClassLoader( ), new Class<?> [ ] {
                LuteceAuthentication.class
        }, ( proxy, method, args ) -> method.getReturnType( ) == boolean.class ? Boolean.FALSE : null );
        setSecurityField( "_authenticationService", authentication );
        setSecurityField( "_bEnable", Boolean.TRUE );
    }

    private static Object getSecurityField( String strName ) throws Exception
    {
        Field field = SecurityService.class.getDeclaredField( strName );
        field.setAccessible( true );

        return field.get( null );
    }

    private static void setSecurityField( String strName, Object value ) throws Exception
    {
        Field field = SecurityService.class.getDeclaredField( strName );
        field.setAccessible( true );
        field.set( null, value );
    }
}
