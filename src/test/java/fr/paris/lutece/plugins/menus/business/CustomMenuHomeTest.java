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
package fr.paris.lutece.plugins.menus.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;

/**
 * Removing a custom menu removes the sub-menu items pointing to it, and nothing else
 */
public class CustomMenuHomeTest extends LuteceTestCase
{
    private final List<Integer> _listCreated = new ArrayList<>( );

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        for ( Integer nId : _listCreated )
        {
            CustomMenuHome.remove( nId );
        }
        super.tearDown( );
    }

    private CustomMenu createMenu( String strBookmark )
    {
        CustomMenu menu = new CustomMenu( );
        menu.setName( "Home test" );
        menu.setBookmark( strBookmark );
        menu.setType( "submenu" );
        menu.setDescription( "" );
        CustomMenuHome.create( menu );
        _listCreated.add( menu.getId( ) );

        return menu;
    }

    private static CustomMenuItem createItem( int nParentMenuId, String strType, String strSourceItemId )
    {
        CustomMenuItem item = new CustomMenuItem( );
        item.setParentMenuId( nParentMenuId );
        item.setType( strType );
        item.setSourceItemId( strSourceItemId );
        item.setLabel( strType );
        item.setUrl( "" );
        CustomMenuItemHome.create( item );

        return item;
    }

    /**
     * The items of another menu whose source id equals the id of the removed menu are kept, unless they are sub-menu
     * items: a « page » item holds a page id, an « xpage » item an XPage name.
     */
    @Test
    public void testRemoveKeepsItemsOfOtherTypesWithTheSameSourceId( )
    {
        CustomMenu removed = createMenu( "menus_home_test_removed" );
        CustomMenu other = createMenu( "menus_home_test_other" );
        String strId = String.valueOf( removed.getId( ) );

        CustomMenuItem page = createItem( other.getId( ), "page", strId );
        CustomMenuItem xpage = createItem( other.getId( ), "xpage", strId );
        CustomMenuItem url = createItem( other.getId( ), "external_url", strId );
        CustomMenuItem subMenu = createItem( other.getId( ), "menu", strId );

        CustomMenuHome.remove( removed.getId( ) );
        _listCreated.remove( Integer.valueOf( removed.getId( ) ) );

        assertNotNull( CustomMenuItemHome.findByPrimaryKey( page.getId( ) ), "page item pointing to the page of the same id" );
        assertNotNull( CustomMenuItemHome.findByPrimaryKey( xpage.getId( ) ), "xpage item" );
        assertNotNull( CustomMenuItemHome.findByPrimaryKey( url.getId( ) ), "external url item" );
        assertNull( CustomMenuItemHome.findByPrimaryKey( subMenu.getId( ) ), "sub-menu item pointing to the removed menu" );
        assertEquals( 3, CustomMenuItemHome.getCustomMenuItemsListByMenuId( other.getId( ) ).size( ) );
    }
}
