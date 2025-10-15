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
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for CustomMenuItem objects
 */
public final class CustomMenuItemDAO implements ICustomMenuItemDAO
{
	// Constants
	private static final String SQL_QUERY_SELECT = "SELECT id_item, id_parent_menu, id_source_item, is_label_dynamic, is_blank, label, type, url, item_order FROM menus_custom_menu_items WHERE id_item = ?";
	private static final String SQL_QUERY_INSERT = "INSERT INTO menus_custom_menu_items ( id_parent_menu, id_source_item, is_label_dynamic, is_blank, label, type, url, item_order ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
	private static final String SQL_QUERY_SELECT_MAX_ORDER = "SELECT max(item_order) FROM menus_custom_menu_items WHERE id_parent_menu= ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM menus_custom_menu_items WHERE id_item = ?";
	private static final String SQL_QUERY_DELETE_BY_MENU = "DELETE FROM menus_custom_menu_items WHERE id_parent_menu = ?";
	private static final String SQL_QUERY_DELETE_ALL_SUBMENU_BY_ID_SUBMENU = "DELETE FROM menus_custom_menu_items WHERE id_source_item = ?";
	private static final String SQL_QUERY_UPDATE = "UPDATE menus_custom_menu_items SET id_parent_menu = ?, id_source_item = ?, is_label_dynamic = ?, is_blank = ?, label = ?, type = ?, url = ?, item_order = ? WHERE id_item = ?";
	private static final String SQL_QUERY_SELECTALL = "SELECT id_item, id_parent_menu, id_source_item, is_label_dynamic, label, type, url, item_order FROM menus_custom_menu_items";
	private static final String SQL_QUERY_SELECTALL_BY_MENU = "SELECT id_item, id_parent_menu, id_source_item, is_label_dynamic, is_blank, label, type, url, item_order FROM menus_custom_menu_items WHERE id_parent_menu = ? ORDER BY item_order";
	private static final String SQL_QUERY_SELECTALL_ID_BY_MENU_ID = "SELECT id_item FROM menus_custom_menu_items WHERE id_parent_menu = ? ORDER BY item_order";

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void insert( CustomMenuItem customMenuItem, Plugin plugin )
	{
		Integer order = selectMaxOrderGroupByMenuParent( customMenuItem, plugin );

		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
		{
			int nIndex = 1;
			daoUtil.setInt( nIndex ++ , customMenuItem.getParentMenuId( ) );
			daoUtil.setString( nIndex ++ , customMenuItem.getSourceItemId( ) );
			daoUtil.setBoolean( nIndex ++ , customMenuItem.isLabelDynamic( ) );
			daoUtil.setBoolean( nIndex ++ , customMenuItem.isBlank( ) );
			daoUtil.setString( nIndex ++ , customMenuItem.getLabel( ) );
			daoUtil.setString( nIndex ++ , customMenuItem.getType( ) );
			daoUtil.setString( nIndex ++ , customMenuItem.getUrl( ) );
			daoUtil.setInt( nIndex ++ , order );

			daoUtil.executeUpdate( );
			if( daoUtil.nextGeneratedKey( ) )
			{
				customMenuItem.setId( daoUtil.getGeneratedKeyInt( 1 ) );
			}
		}
	}

	private Integer selectMaxOrderGroupByMenuParent( CustomMenuItem customMenuItem, Plugin plugin )
	{

		Integer nOrder = 1;

		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_ORDER, plugin ) )
		{
			daoUtil.setInt( 1, customMenuItem.getParentMenuId( ) );

			daoUtil.executeQuery( );
			if( daoUtil.next( ) )
			{
				nOrder = daoUtil.getInt( 1 ) + 1;
			}
		}
		return nOrder;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public CustomMenuItem load( int nKey, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
		{
			daoUtil.setInt( 1, nKey );
			daoUtil.executeQuery( );
			CustomMenuItem customMenuItem = null;

			if( daoUtil.next( ) )
			{
				customMenuItem = new CustomMenuItem( );
				int nIndex = 1;

				customMenuItem.setId( daoUtil.getInt( nIndex ++ ) );
				customMenuItem.setParentMenuId( daoUtil.getInt( nIndex ++ ) );
				customMenuItem.setSourceItemId( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setIsLabelDynamic( daoUtil.getBoolean( nIndex ++ ) );
				customMenuItem.setIsBlank( daoUtil.getBoolean( nIndex ++ ) );
				customMenuItem.setLabel( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setType( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setUrl( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setOrder( daoUtil.getInt( nIndex ++ ) );
			}

			return customMenuItem;
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void delete( int nKey, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
		{
			daoUtil.setInt( 1, nKey );
			daoUtil.executeUpdate( );
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void deleteByMenuId( int nMenuId, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_MENU, plugin ) )
		{
			daoUtil.setInt( 1, nMenuId );
			daoUtil.executeUpdate( );
		}
	}

	@Override
	public void deleteSubMenuItemBySubMenuId( int nMenuSourceId, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL_SUBMENU_BY_ID_SUBMENU, plugin ) )
		{
			daoUtil.setInt( 1, nMenuSourceId );
			daoUtil.executeUpdate( );
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void store( CustomMenuItem customMenuItem, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
		{

			int nIndex = 1;
			daoUtil.setInt( nIndex ++ , customMenuItem.getParentMenuId( ) );
			daoUtil.setString( nIndex ++ , customMenuItem.getSourceItemId( ) );
			daoUtil.setBoolean( nIndex ++ , customMenuItem.isLabelDynamic( ) );
			daoUtil.setBoolean( nIndex ++ , customMenuItem.isBlank( ) );
			daoUtil.setString( nIndex ++ , customMenuItem.getLabel( ) );
			daoUtil.setString( nIndex ++ , customMenuItem.getType( ) );
			daoUtil.setString( nIndex ++ , customMenuItem.getUrl( ) );
			daoUtil.setInt( nIndex ++ , customMenuItem.getOrder( ) );
			daoUtil.setInt( nIndex, customMenuItem.getId( ) );

			daoUtil.executeUpdate( );
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public List < CustomMenuItem > selectAll( Plugin plugin )
	{
		List < CustomMenuItem > customMenuItemList = new ArrayList <>( );
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
		{
			daoUtil.executeQuery( );

			while( daoUtil.next( ) )
			{
				CustomMenuItem customMenuItem = new CustomMenuItem( );
				int nIndex = 1;

				customMenuItem.setId( daoUtil.getInt( nIndex ++ ) );
				customMenuItem.setParentMenuId( daoUtil.getInt( nIndex ++ ) );
				customMenuItem.setSourceItemId( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setIsLabelDynamic( daoUtil.getBoolean( nIndex ++ ) );
				customMenuItem.setIsBlank( daoUtil.getBoolean( nIndex ++ ) );
				customMenuItem.setLabel( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setType( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setUrl( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setOrder( daoUtil.getInt( nIndex ++ ) );

				customMenuItemList.add( customMenuItem );
			}

			return customMenuItemList;
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public List < CustomMenuItem > selectByMenuId( int nMenuId, Plugin plugin )
	{
		List < CustomMenuItem > customMenuItemList = new ArrayList <>( );
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_MENU, plugin ) )
		{
			daoUtil.setInt( 1, nMenuId );
			daoUtil.executeQuery( );

			while( daoUtil.next( ) )
			{
				CustomMenuItem customMenuItem = new CustomMenuItem( );
				int nIndex = 1;

				customMenuItem.setId( daoUtil.getInt( nIndex ++ ) );
				customMenuItem.setParentMenuId( daoUtil.getInt( nIndex ++ ) );
				customMenuItem.setSourceItemId( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setIsLabelDynamic( daoUtil.getBoolean( nIndex ++ ) );
				customMenuItem.setIsBlank( daoUtil.getBoolean( nIndex ++ ) );
				customMenuItem.setLabel( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setType( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setUrl( daoUtil.getString( nIndex ++ ) );
				customMenuItem.setOrder( daoUtil.getInt( nIndex ++ ) );

				customMenuItemList.add( customMenuItem );
			}

			return customMenuItemList;
		}
	}

	public List < Integer > selectAllIdsByMenuId( int nMenuId, Plugin plugin )
	{
		List < Integer > customMenuItemIdList = new ArrayList <>( );

		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID_BY_MENU_ID, plugin ) )
		{
			daoUtil.setInt( 1, nMenuId );
			daoUtil.executeQuery( );

			while( daoUtil.next( ) )
			{
				customMenuItemIdList.add( daoUtil.getInt( 1 ) );
			}
		}

		return customMenuItemIdList;
	}

}
