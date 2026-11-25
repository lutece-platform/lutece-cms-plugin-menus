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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.cdi.mvc.event.MvcEvent.ControllerInvocationType;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The back office controller of the custom menus checks the security token of all its actions
 */
public class MenusSecurityTokenTest extends LuteceTestCase
{
    private static final String PATH = "/jsp/admin/plugins/menus/ManageCustomMenus.jsp";
    private static final String [ ] ACTIONS = {
            "createCustomMenu", "modifyCustomMenu", "removeCustomMenu", "createCustomMenuItems", "modifyCustomMenuItem", "removeCustomMenuItem",
            "searchItems", "changeMenuItemsOrder", "doModifyNativeMenuSettings"
    };

    private static List<Method> methods( )
    {
        List<Method> listMethods = new ArrayList<>( );
        for ( Class<?> c = CustomMenusJspBean.class; c != null && c != Object.class; c = c.getSuperclass( ) )
        {
            listMethods.addAll( List.of( c.getDeclaredMethods( ) ) );
        }
        return listMethods;
    }

    @Test
    public void testControllerEnablesTheToken( )
    {
        assertTrue( CustomMenusJspBean.class.getAnnotation( Controller.class ).securityTokenEnabled( ) );
    }

    /**
     * Every action of the controller is in the list checked by this test (a new action must be added to the CSRF checklist)
     */
    @Test
    public void testInventoryOfTheActions( )
    {
        Set<String> setActions = new HashSet<>( );
        for ( Method method : methods( ) )
        {
            if ( method.isAnnotationPresent( Action.class ) )
            {
                setActions.add( method.getAnnotation( Action.class ).value( ) );
            }
        }
        assertEquals( Set.of( ACTIONS ), setActions );
    }

    /**
     * The token checked by an action is stored under its securityTokenAction (or its name): a view must generate it under the same key
     */
    @Test
    public void testEveryActionGetsItsTokenFromAView( )
    {
        Set<String> setViewKeys = new HashSet<>( );
        for ( Method method : methods( ) )
        {
            View view = method.getAnnotation( View.class );
            if ( view != null )
            {
                setViewKeys.add( view.securityTokenAction( ).isEmpty( ) ? view.value( ) : view.securityTokenAction( ) );
            }
        }
        for ( Method method : methods( ) )
        {
            Action action = method.getAnnotation( Action.class );
            if ( action != null )
            {
                String strKey = action.securityTokenAction( ).isEmpty( ) ? action.value( ) : action.securityTokenAction( );
                assertTrue( setViewKeys.contains( strKey ), action.value( ) + " expects a token stored under " + strKey + ", no view generates it: " + setViewKeys );
                assertFalse( action.securityTokenDisabled( ), action.value( ) );
            }
        }
    }

    /**
     * The core disables the token of an action when the controller has a method of the same name without @Action nor @View: no such method may
     * hide an action
     */
    @Test
    public void testNoMethodDisablesTheTokenOfAnAction( )
    {
        Set<String> setActions = new HashSet<>( List.of( ACTIONS ) );
        for ( Method method : methods( ) )
        {
            if ( method.getName( ).startsWith( "do" ) && !method.isAnnotationPresent( Action.class ) && !method.isAnnotationPresent( View.class ) )
            {
                assertFalse( setActions.contains( method.getName( ) ), method.getName( ) + " disables the security token of the action of the same name" );
            }
        }
    }

    /**
     * Every action posted without a token is refused by the filter
     */
    @Test
    public void testActionsWithoutTokenAreRefused( )
    {
        SecurityTokenHandler handler = CDI.current( ).select( SecurityTokenHandler.class ).get( );
        handler.registerAdminControllers( null );
        for ( String strAction : ACTIONS )
        {
            MockHttpServletRequest request = new MockHttpServletRequest( );
            request.setMethod( "POST" );
            request.setServletPath( PATH );
            request.addParameter( "action", strAction );

            assertFalse( handler.shouldNotFilter( request ), strAction + " is not checked by the filter" );
            assertFalse( handler.validate( request, handler.resolveTokenAction( request, strAction ) ), strAction + " accepted without token" );
        }
    }

    /**
     * The token of a page of the controller is accepted by its actions, once (the removal confirmed from the list, the depth form...)
     */
    @Test
    public void testTokenOfAPageIsAcceptedOnce( )
    {
        SecurityTokenHandler handler = CDI.current( ).select( SecurityTokenHandler.class ).get( );
        handler.registerAdminControllers( null );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        String strToken = CDI.current( ).select( ISecurityTokenService.class ).get( ).getToken( request, CustomMenusJspBean.SECURITY_TOKEN_ACTION );
        request.setMethod( "POST" );
        request.setServletPath( PATH );
        request.addParameter( "action", "removeCustomMenu" );
        request.addParameter( SecurityTokenHandler.MARK_CSRF_TOKEN, strToken );

        assertTrue( handler.validate( request, handler.resolveTokenAction( request, "removeCustomMenu" ) ) );
        assertFalse( handler.validate( request, handler.resolveTokenAction( request, "removeCustomMenu" ) ) );
    }

    /**
     * The list of the menus carries the token of the page in its forms (the depth forms post an action)
     *
     * @throws Exception
     *             if the view fails
     */
    @Test
    public void testManagePageFormsCarryTheToken( ) throws Exception
    {
        // the admin macros of the commons (pageContainer...) are not active in the test container until the macros are set up again
        AppTemplateService.initMacros( );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( "super_admin", new RBACRole( "super_admin", "super_admin" ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, CustomMenusJspBean.RIGHT_MANAGE_CUSTOM_MENUS );
        CustomMenusJspBean bean = CDI.current( ).select( CustomMenusJspBean.class ).get( );
        bean.init( request, CustomMenusJspBean.RIGHT_MANAGE_CUSTOM_MENUS );
        SecurityTokenHandler handler = CDI.current( ).select( SecurityTokenHandler.class ).get( );
        handler.handle( request );
        Method view = CustomMenusJspBean.class.getMethod( "getManageCustomMenus", HttpServletRequest.class, Models.class );
        handler.handleToken( request, ControllerInvocationType.VIEW, view );
        String strToken = handler.resolveTokenValue( request );
        assertNotNull( strToken );

        String strHtml = bean.getManageCustomMenus( request, CDI.current( ).select( Models.class ).get( ) );

        assertTrue( strHtml.contains( "id='form-depth-main'" ) || strHtml.contains( "id=\"form-depth-main\"" ), strHtml );
        assertTrue( strHtml.contains( "name=\"_csrftoken\" value=\"" + strToken + "\"" ), strHtml );
    }
}
