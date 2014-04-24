/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for Menus objects
 */
public final class MenusDAO implements IMenusDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_menu ) FROM menus";
    private static final String SQL_QUERY_SELECT = "SELECT id_menu, menu_name, type_menu, id_page_root, menu_marker FROM menus WHERE id_menu = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO menus ( id_menu, menu_name, type_menu, id_page_root, menu_marker ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM menus WHERE id_menu = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE menus SET id_menu = ?, menu_name = ?, type_menu = ?, id_page_root = ?, menu_marker = ? WHERE id_menu = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_menu, menu_name, type_menu, id_page_root, menu_marker FROM menus";
    private static final String SQL_QUERY_SELECT_NB_MENUS = " SELECT count(*) FROM menus";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     * @param menus instance of the Menus object to insert
     * @param plugin The plugin
     */
    public void insert( Menus menus, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        menus.setIdMenu( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, menus.getIdMenu(  ) );
        daoUtil.setString( 2, menus.getMenuName(  ) );
        daoUtil.setString( 3, menus.getMenuType(  ) );
        daoUtil.setInt( 4, menus.getIdPageRoot(  ) );
        daoUtil.setString( 5, menus.getMenuMarker(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of the menus from the table
     * @param nId The identifier of the menus
     * @param plugin The plugin
     * @return the instance of the Menus
     */
    public Menus load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        Menus menus = null;

        if ( daoUtil.next(  ) )
        {
            menus = new Menus(  );

            menus.setIdMenu( daoUtil.getInt( 1 ) );
            menus.setMenuName( daoUtil.getString( 2 ) );
            menus.setTypeMenu( daoUtil.getString( 3 ) );
            menus.setIdPageRoot( daoUtil.getInt( 4 ) );
            menus.setMenuMarker( daoUtil.getString( 5 ) );
        }

        daoUtil.free(  );

        return menus;
    }

    /**
     * Delete a record from the table
     * @param nMenusId The identifier of the menus
     * @param plugin The plugin
     */
    public void delete( int nMenusId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nMenusId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     * @param menus The reference of the menus
     * @param plugin The plugin
     */
    public void store( Menus menus, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, menus.getIdMenu(  ) );
        daoUtil.setString( 2, menus.getMenuName(  ) );
        daoUtil.setString( 3, menus.getMenuType(  ) );
        daoUtil.setInt( 4, menus.getIdPageRoot(  ) );
        daoUtil.setString( 5, menus.getMenuMarker(  ) );
        daoUtil.setInt( 6, menus.getIdMenu(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the menuss and returns them as a collection
     * @param plugin The plugin
     * @return The Collection which contains the data of all the menuss
     */
    public Collection<Menus> selectAll( Plugin plugin )
    {
        Collection<Menus> menusList = new ArrayList<Menus>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Menus menus = new Menus(  );

            menus.setIdMenu( daoUtil.getInt( 1 ) );
            menus.setMenuName( daoUtil.getString( 2 ) );
            menus.setTypeMenu( daoUtil.getString( 3 ) );
            menus.setIdPageRoot( daoUtil.getInt( 4 ) );
            menus.setMenuMarker( daoUtil.getString( 5 ) );

            menusList.add( menus );
        }

        daoUtil.free(  );

        return menusList;
    }

    /**
     * Returns the menus count
     * @return nCount
     */
    public int selectNbMenus( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_NB_MENUS, plugin );
        daoUtil.executeQuery(  );

        int nCount = 0;

        if ( daoUtil.next(  ) )
        {
            nCount = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nCount;
    }
}
