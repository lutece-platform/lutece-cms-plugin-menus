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
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.style.PageTemplateHome;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The menus do not show a page restricted to a front office role to a visitor who does not have that role
 */
public class MenusPageVisibilityTest extends LuteceTestCase
{
    private static final String ROLE = "menus_visibility_test_role";

    private final List<Integer> _listPages = new ArrayList<>( );
    private Object _savedEnable;
    private Object _savedAuthentication;
    private Page _publicPage;
    private Page _reservedPage;
    private Page _reservedChild;

    @BeforeEach
    protected void createPages( )
    {
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
     * The JSON tree of the XPage treemenupages leaves out, for an anonymous visitor, a page reserved to a role and its
     * sub-pages
     *
     * @throws Exception
     *             if the authentication cannot be set
     */
    @Test
    public void testTreeMenuPagesHidesReservedPages( ) throws Exception
    {
        enableAuthenticationWithAnonymousVisitor( );

        String strJson = CDI.current( ).select( XPageTreeMenuPages.class ).get( ).getMenuTree( new MockHttpServletRequest( ) ).toString( );

        assertTrue( strJson.contains( "\"name\":\"menus-visibility-public\"" ), strJson );
        assertFalse( strJson.contains( "menus-visibility-reserved" ), strJson );
        assertFalse( strJson.contains( "page_id=" + _reservedPage.getId( ) + "\"" ), strJson );
        assertFalse( strJson.contains( "page_id=" + _reservedChild.getId( ) + "\"" ), strJson );
    }

    /**
     * Without authentication, every page is in the tree
     */
    @Test
    public void testTreeMenuPagesWithoutAuthenticationListsEveryPage( )
    {
        String strJson = CDI.current( ).select( XPageTreeMenuPages.class ).get( ).getMenuTree( new MockHttpServletRequest( ) ).toString( );

        assertTrue( strJson.contains( "\"name\":\"menus-visibility-public\"" ), strJson );
        assertTrue( strJson.contains( "\"name\":\"menus-visibility-reserved\"" ), strJson );
        assertTrue( strJson.contains( "\"name\":\"menus-visibility-reserved-child\"" ), strJson );
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
