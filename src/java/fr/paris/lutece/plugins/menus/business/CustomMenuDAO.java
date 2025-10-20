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
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * This class provides Data Access methods for CustomMenu objects
 */
@ApplicationScoped
public final class CustomMenuDAO implements ICustomMenuDAO
{
	// Constants
	private static final String SQL_QUERY_SELECT = "SELECT id_menu, name, bookmark, type, description FROM menus_custom_menu WHERE id_menu = ?";
	private static final String SQL_QUERY_INSERT = "INSERT INTO menus_custom_menu ( name, bookmark, type, description ) VALUES ( ?, ?, ?, ? )";
	private static final String SQL_QUERY_DELETE = "DELETE FROM menus_custom_menu WHERE id_menu = ?";
	private static final String SQL_QUERY_UPDATE = "UPDATE menus_custom_menu SET name = ?, bookmark = ?, type = ?, description = ? WHERE id_menu = ?";
	private static final String SQL_QUERY_SELECTALL = "SELECT id_menu, name, bookmark, type, description FROM menus_custom_menu";
	private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_menu FROM menus_custom_menu";
	private static final String SQL_QUERY_SELECT_BY_ID = "SELECT id_menu, name, bookmark, type, description FROM menus_custom_menu WHERE id_menu in ";
	private static final String SQL_QUERY_COUNT_BOOKMARK = "SELECT count(bookmark) FROM menus_custom_menu WHERE bookmark= ?";
	private static final String SQL_WHERE = "WHERE";
	private static final String SQL_OR = "OR";
	private static final String SQL_LIKE = "LIKE";
	private static final String SQL_COLUMN_NAME = "name";

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void insert( CustomMenu customMenu, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
		{
			int nIndex = 1;
			daoUtil.setString( nIndex ++ , customMenu.getName( ) );
			daoUtil.setString( nIndex ++ , customMenu.getBookmark( ) );
			daoUtil.setString( nIndex ++ , customMenu.getType( ) );
			daoUtil.setString( nIndex ++ , customMenu.getDescription( ) );

			daoUtil.executeUpdate( );
			if( daoUtil.nextGeneratedKey( ) )
			{
				customMenu.setId( daoUtil.getGeneratedKeyInt( 1 ) );
			}
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public CustomMenu load( int nKey, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
		{
			daoUtil.setInt( 1, nKey );
			daoUtil.executeQuery( );
			CustomMenu customMenu = null;

			if( daoUtil.next( ) )
			{
				customMenu = new CustomMenu( );
				int nIndex = 1;

				customMenu.setId( daoUtil.getInt( nIndex ++ ) );
				customMenu.setName( daoUtil.getString( nIndex ++ ) );
				customMenu.setBookmark( daoUtil.getString( nIndex ++ ) );
				customMenu.setType( daoUtil.getString( nIndex ++ ) );
				customMenu.setDescription( daoUtil.getString( nIndex ++ ) );
			}

			return customMenu;
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
	public void store( CustomMenu customMenu, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
		{
			int nIndex = 1;

			daoUtil.setString( nIndex ++ , customMenu.getName( ) );
			daoUtil.setString( nIndex ++ , customMenu.getBookmark( ) );
			daoUtil.setString( nIndex ++ , customMenu.getType( ) );
			daoUtil.setString( nIndex ++ , customMenu.getDescription( ) );
			daoUtil.setInt( nIndex, customMenu.getId( ) );

			daoUtil.executeUpdate( );
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public List < CustomMenu > selectAll( Plugin plugin )
	{
		List < CustomMenu > customMenuList = new ArrayList <>( );
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
		{
			daoUtil.executeQuery( );

			while( daoUtil.next( ) )
			{
				CustomMenu customMenu = new CustomMenu( );
				int nIndex = 1;

				customMenu.setId( daoUtil.getInt( nIndex ++ ) );
				customMenu.setName( daoUtil.getString( nIndex ++ ) );
				customMenu.setBookmark( daoUtil.getString( nIndex ++ ) );
				customMenu.setType( daoUtil.getString( nIndex ++ ) );
				customMenu.setDescription( daoUtil.getString( nIndex ++ ) );

				customMenuList.add( customMenu );
			}

			return customMenuList;
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public List < CustomMenu > selectMenusListByIds( Plugin plugin, List < Integer > listIds )
	{
		List < CustomMenu > MenusList = new ArrayList <>( );

		StringBuilder builder = new StringBuilder( );

		if( ! listIds.isEmpty( ) )
		{
			for( int i = 0 ; i < listIds.size( ) ; i ++ )
			{
				builder.append( "?," );
			}

			final String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
			final String stmt = SQL_QUERY_SELECT_BY_ID + "(" + placeHolders + ")";

			try( final DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
			{
				int index = 1;
				for( Integer n : listIds )
				{
					daoUtil.setInt( index ++ , n );
				}

				daoUtil.executeQuery( );
				while( daoUtil.next( ) )
				{
					MenusList.add( getCustomMenu( daoUtil ) );
				}
			}
		}
		return MenusList;
	}

	/**
	 * get menu from DAO
	 *
	 * @param daoUtil
	 * @return the menu
	 */
	private CustomMenu getCustomMenu( DAOUtil daoUtil )
	{
		CustomMenu menu = new CustomMenu( );
		int nIndex = 1;

		menu.setId( daoUtil.getInt( nIndex ++ ) );
		menu.setName( daoUtil.getString( nIndex ++ ) );
		menu.setBookmark( daoUtil.getString( nIndex ++ ) );
		menu.setType( daoUtil.getString( nIndex ++ ) );
		menu.setDescription( daoUtil.getString( nIndex ++ ) );

		return menu;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public List < Integer > selectIdMenusList( Plugin plugin )
	{
		List < Integer > menuList = new ArrayList < Integer >( );
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
		{
			daoUtil.executeQuery( );

			while( daoUtil.next( ) )
			{
				menuList.add( daoUtil.getInt( 1 ) );
			}
		}
		return menuList;
	}

	@Override
	public List < CustomMenu > selectMenusListByFilterCriteria( Plugin plugin, String strFilterCriteria )
	{

		if( StringUtils.isBlank( strFilterCriteria ) )
		{
			return selectAll( plugin );
		}

		List < CustomMenu > MenusList = new ArrayList <>( );

		String strCleanFilterCriteria = strFilterCriteria.trim( );

		String strStatement = prepareStatement( strCleanFilterCriteria );

		try( final DAOUtil daoUtil = new DAOUtil( strStatement, plugin ) )
		{
			int index = 1;
			daoUtil.setString( index ++ , strCleanFilterCriteria );

			for( String criteria : strCleanFilterCriteria.split( " " ) )
			{
				daoUtil.setString( index ++ , '%' + criteria + '%' );
			}

			daoUtil.executeQuery( );

			while( daoUtil.next( ) )
			{
				MenusList.add( getCustomMenu( daoUtil ) );
			}
		}

		return MenusList;
	}

	private String prepareStatement( String strFilterCriteria )
	{
		StringBuilder strStatement = new StringBuilder( );
		strStatement.append( SQL_QUERY_SELECTALL );
		strStatement.append( ' ' + SQL_WHERE );
		strStatement.append( ' ' + SQL_COLUMN_NAME + "= ?" );

		for( String criteria : strFilterCriteria.split( " " ) )
		{
			strStatement.append( ' ' + SQL_OR );
			strStatement.append( ' ' + SQL_COLUMN_NAME );
			strStatement.append( ' ' + SQL_LIKE );
			strStatement.append( " ?" );
		}

		return strStatement.toString( );
	}

	@Override
	public Integer countBookmark( Plugin plugin, CustomMenu customMenu )
	{
		int nCount = 0;

		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_BOOKMARK, plugin ) )
		{
			daoUtil.setString( 1, customMenu.getBookmark( ) );
			daoUtil.executeQuery( );

			if( daoUtil.next( ) )
			{
				nCount = daoUtil.getInt( 1 );
			}
		}

		return nCount;
	}

}
