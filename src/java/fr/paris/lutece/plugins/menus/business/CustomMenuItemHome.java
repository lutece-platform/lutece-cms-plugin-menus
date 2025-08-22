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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for CustomMenuItem objects
 */
public final class CustomMenuItemHome
{
    // Static variable pointed at the DAO instance
    private static ICustomMenuItemDAO _dao = SpringContextService.getBean( "menus.customMenuItemDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "menus" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private CustomMenuItemHome( )
    {
    }

    /**
     * Create an instance of the customMenuItem class
     * 
     * @param customMenuItem
     *            The instance of the CustomMenuItem which contains the informations to store
     * @return The instance of customMenuItem which has been created with its primary key.
     */
    public static CustomMenuItem create( CustomMenuItem customMenuItem )
    {
        _dao.insert( customMenuItem, _plugin );

        return customMenuItem;
    }

    /**
     * Update of the customMenuItem which is specified in parameter
     * 
     * @param customMenuItem
     *            The instance of the CustomMenuItem which contains the data to store
     * @return The instance of the customMenuItem which has been updated
     */
    public static CustomMenuItem update( CustomMenuItem customMenuItem )
    {
        _dao.store( customMenuItem, _plugin );

        return customMenuItem;
    }

    /**
     * Remove the customMenuItem whose identifier is specified in parameter
     * 
     * @param nKey
     *            The customMenuItem Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Remove all items of a menu
     * 
     * @param nMenuId
     *            The menu Id
     */
    public static void removeByMenuId( int nMenuId )
    {
        _dao.deleteByMenuId( nMenuId, _plugin );
    }

    /**
     * Returns an instance of a customMenuItem whose identifier is specified in parameter
     * 
     * @param nKey
     *            The customMenuItem primary key
     * @return an instance of CustomMenuItem
     */
    public static CustomMenuItem findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the customMenuItem objects and returns them in form of a collection
     * 
     * @return the collection which contains the data of all the customMenuItem objects
     */
    public static List<CustomMenuItem> getCustomMenuItemsList( )
    {
        return _dao.selectAll( _plugin );
    }

    /**
     * Load the data of all the customMenuItem objects for a specific menu and returns them in form of a collection
     * 
     * @param nMenuId
     *            The menu identifier
     * @return the collection which contains the data of all the customMenuItem objects for the menu
     */
    public static List<CustomMenuItem> getCustomMenuItemsListByMenuId( int nMenuId )
    {
        return _dao.selectByMenuId( nMenuId, _plugin );
    }
    
    /**
     * Load the data of all the customMenuItem objects for a specific menu and returns them in form of a collection
     * 
     * @param nMenuId
     *            The menu identifier
     * @return the collection which contains the data of all the customMenuItem objects for the menu
     */
    public static List<Integer> getCustomMenuItemsIdsListByMenuId( int nMenuId )
    {
        return _dao.selectAllIdsByMenuId( nMenuId, _plugin );
    }
}
