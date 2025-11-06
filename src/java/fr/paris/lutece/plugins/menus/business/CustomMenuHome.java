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

import org.apache.commons.lang3.Strings;

/**
 * This class provides instances management methods (create, find, ...) for
 * CustomMenu objects
 */
public final class CustomMenuHome
{
	// Static variable pointed at the DAO instance
	private static ICustomMenuDAO _dao = SpringContextService.getBean( "menus.customMenuDAO" );
	private static ICustomMenuItemDAO _daoItem = SpringContextService.getBean( "menus.customMenuItemDAO" );
	private static Plugin _plugin = PluginService.getPlugin( "menus" );

	/**
	 * Private constructor - this class need not be instantiated
	 */
	private CustomMenuHome( )
	{
	}

	/**
	 * Create an instance of the customMenu class
	 * 
	 * @param customMenu
	 *                   The instance of the CustomMenu which contains the
	 *                   informations to store
	 * @return The instance of customMenu which has been created with its primary
	 *         key.
	 */
	public static CustomMenu create( CustomMenu customMenu )
	{
		_dao.insert( customMenu, _plugin );

		return customMenu;
	}

	/**
	 * Update of the customMenu which is specified in parameter
	 * 
	 * @param customMenu
	 *                   The instance of the CustomMenu which contains the data to
	 *                   store
	 * @return The instance of the customMenu which has been updated
	 */
	public static CustomMenu update( CustomMenu customMenu )
	{
		_dao.store( customMenu, _plugin );

		return customMenu;
	}

	/**
	 * Remove the customMenu whose identifier is specified in parameter
	 * 
	 * @param nKey
	 *             The customMenu Id
	 */
	public static void remove( int nKey )
	{
		// Remove all items of the menu first
		_daoItem.deleteByMenuId( nKey, _plugin );

		// Remove this menu in other menu (subMenu item)
		_daoItem.deleteSubMenuItemBySubMenuId( nKey, _plugin );

		// Then remove the menu
		_dao.delete( nKey, _plugin );
	}

	/**
	 * Returns an instance of a customMenu whose identifier is specified in
	 * parameter
	 * 
	 * @param nKey
	 *             The customMenu primary key
	 * @return an instance of CustomMenu
	 */
	public static CustomMenu findByPrimaryKey( int nKey )
	{
		return _dao.load( nKey, _plugin );

	}

	/**
	 * Load the data of all the customMenu objects and returns them in form of a
	 * collection
	 * 
	 * @return the collection which contains the data of all the customMenu objects
	 */
	public static List < CustomMenu > getCustomMenusList( )
	{
		return _dao.selectAll( _plugin );
	}

	/**
	 * Load the id of all the customMenu objects and returns them as a list
	 * 
	 * @return the list which contains the id of all the customMenu objects
	 */
	public static List < Integer > getIdMenusList( )
	{
		return _dao.selectIdMenusList( _plugin );
	}

	/**
	 * Load the id of all the customMenu objects containing in listIds and returns
	 * them as a list
	 * 
	 * @param nKey
	 *             The customMenu primary key
	 * @return the list which contains the id of all the customMenu objects
	 */
	public static List < CustomMenu > getMenusListByIds( List < Integer > listIds )
	{
		return _dao.selectMenusListByIds( _plugin, listIds );
	}

	/**
	 * Load a filtered list of customMenu objects and returns them as a list
	 * 
	 * @param strFilterCriteria
	 *                          Filter criteria
	 * @return the filtered list of customMenu objects
	 */
	public static List < CustomMenu > findAllWithCriteria( String strFilterCriteria )
	{
		return _dao.selectMenusListByFilterCriteria( _plugin, strFilterCriteria );
	}

	/**
	 * Return if bookmark is unique or not
	 * 
	 * @param customMenu
	 *                    instance of the CustomMenu object to insert
	 * @param bIsCreation
	 *                    is creation mode or not
	 * @return True if bookmark is unique or false else.
	 */
	public static Boolean isUniqueBookmark( CustomMenu customMenu, Boolean bIsCreation )
	{

		Integer nCount = _dao.countBookmark( _plugin, customMenu );

		if( bIsCreation )
		{
			return nCount == 0;
		}
		else
		{
			CustomMenu oldCustomMenu = _dao.load( customMenu.getId( ), _plugin );
			return nCount == 0
					|| ( nCount == 1 &&  Strings.CS.equals( oldCustomMenu.getBookmark( ), customMenu.getBookmark( ) ) );
		}
	}
}
