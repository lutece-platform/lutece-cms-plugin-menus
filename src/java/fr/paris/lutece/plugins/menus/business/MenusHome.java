/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import java.util.Collection;

/**
 * This class provides instances management methods (create, find, ...) for Menus objects
 */
public final class MenusHome
{
    // Static variable pointed at the DAO instance
    private static IMenusDAO _dao = SpringContextService.getBean( "menus.menusDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "menus" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private MenusHome( )
    {
    }

    /**
     * Create an instance of the menus class
     * 
     * @param menus
     *            The instance of the Menus which contains the informations to store
     * @param plugin
     *            the Plugin
     * @return The instance of menus which has been created with its primary key.
     */
    public static Menus create( Menus menus, Plugin plugin )
    {
        _dao.insert( menus, plugin );

        return menus;
    }

    /**
     * Create an instance of the menus class
     * 
     * @param menus
     *            The instance of the Menus which contains the informations to store
     * @return The instance of menus which has been created with its primary key.
     */
    public static Menus create( Menus menus )
    {
        _dao.insert( menus, _plugin );

        return menus;
    }

    /**
     * Update of the menus which is specified in parameter
     * 
     * @param menus
     *            The instance of the Menus which contains the data to store
     * @param plugin
     *            the Plugin
     * @return The instance of the menus which has been updated
     */
    public static Menus update( Menus menus, Plugin plugin )
    {
        _dao.store( menus, plugin );

        return menus;
    }

    /**
     * Update of the menus which is specified in parameter
     * 
     * @param menus
     *            The instance of the Menus which contains the data to store
     * @return The instance of the menus which has been updated
     */
    public static Menus update( Menus menus )
    {
        _dao.store( menus, _plugin );

        return menus;
    }

    /**
     * Remove the menus whose identifier is specified in parameter
     * 
     * @param nMenusId
     *            The menus Id
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nMenusId, Plugin plugin )
    {
        _dao.delete( nMenusId, plugin );
    }

    /**
     * Remove the menus whose identifier is specified in parameter
     * 
     * @param nMenusId
     *            The menus Id
     */
    public static void remove( int nMenusId )
    {
        _dao.delete( nMenusId, _plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a menus whose identifier is specified in parameter
     * 
     * @param nKey
     *            The menus primary key
     * @param plugin
     *            the Plugin
     * @return an instance of Menus
     */
    public static Menus findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Returns an instance of a menus whose identifier is specified in parameter
     * 
     * @param nKey
     *            The menus primary key
     * @return an instance of Menus
     */
    public static Menus findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the menus objects and returns them in form of a collection
     * 
     * @param plugin
     *            the Plugin
     * @return the collection which contains the data of all the menus objects
     */
    public static Collection<Menus> findAll( Plugin plugin )
    {
        return _dao.selectAll( plugin );
    }

    /**
     * Load the data of all the menus objects and returns them in form of a collection
     * 
     * @return the collection which contains the data of all the menus objects
     */
    public static Collection<Menus> findAll( )
    {
        return _dao.selectAll( _plugin );
    }

    /**
     * Search the number of menus
     *
     * @param plugin
     *            the Plugin
     * @return int the number of menus
     */
    public static int getNbMenus( Plugin plugin )
    {
        return _dao.selectNbMenus( plugin );
    }

    /**
     * Search the number of menus
     *
     * @return int the number of menus
     */
    public static int getNbMenus( )
    {
        return _dao.selectNbMenus( _plugin );
    }
}
