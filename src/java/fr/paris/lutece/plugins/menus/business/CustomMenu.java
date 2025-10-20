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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * This is the business class for the object CustomMenu
 */
public class CustomMenu implements Serializable
{
	private static final long serialVersionUID = 2L;

	// Variables declarations
	private int _nId;
	@NotEmpty( message = "#i18n{menus.validation.customMenu.name.notEmpty}" )
	@Size( max = 100, message = "#i18n{menus.validation.customMenu.name.size}" )
	private String _strName;
	@Pattern( regexp = "^[a-zA-Z0-9_]*$", message = "#i18n{menus.validation.customMenu.bookmark.pattern}" )
	@Pattern( regexp = "^(?!customMenu$|customMenuMainPage$|customMenuInternalPage$|customMenuSideBar$|page_tree_menu_main$|page_tree_menu_tree$|page_tree_menu_tree_all_pages$).+$", message = "#i18n{menus.validation.customMenu.bookmark.forbiddenName}" )
	@Size( max = 100, message = "#i18n{menus.validation.customMenu.bookmark.size}" )
	@NotEmpty( message = "#i18n{menus.validation.customMenu.bookmark.notEmpty}" )
	private String _strBookmark;
	@Pattern( regexp = "main|internal|sidebar|submenu", message = "#i18n{menus.validation.customMenu.type.pattern}" )
	private String _strType;
	@Size( max = 255, message = "#i18n{menus.validation.customMenu.description.size}" )
	private String _strDescription;
	private List < CustomMenuItem > _listItems;

	// Type constants
	public static final String TYPE_MAIN = "menus.constant_custom_menu.type.main";
	public static final String TYPE_SUBMENU = "menus.constant_custom_menu.type.customMenu";
	public static final String TYPE_INTERNAL = "menus.constant_custom_menu.type.internal";
	public static final String TYPE_SIDEBAR = "menus.constant_custom_menu.type.sidebar";

	/**
	 * Constructor
	 */
	public CustomMenu( )
	{
		_listItems = new ArrayList < CustomMenuItem >( );
		_strBookmark = "";
	}

	/**
	 * Returns the Id
	 * 
	 * @return The Id
	 */
	public int getId( )
	{
		return _nId;
	}

	/**
	 * Sets the Id
	 * 
	 * @param nId
	 *            The Id
	 */
	public void setId( int nId )
	{
		_nId = nId;
	}

	/**
	 * Returns the Name
	 * 
	 * @return The Name
	 */
	public String getName( )
	{
		return _strName;
	}

	/**
	 * Sets the Name
	 * 
	 * @param strName
	 *                The Name
	 */
	public void setName( String strName )
	{
		_strName = strName;
	}

	/**
	 * Returns the Bookmark
	 * 
	 * @return The Bookmark
	 */
	public String getBookmark( )
	{
		return _strBookmark;
	}

	/**
	 * Sets the Bookmark
	 * 
	 * @param strBookmark
	 *                    The Bookmark
	 */
	public void setBookmark( String strBookmark )
	{
		_strBookmark = strBookmark;
	}

	/**
	 * Returns the Type
	 * 
	 * @return The Type
	 */
	public String getType( )
	{
		return _strType;
	}

	/**
	 * Sets the Type
	 * 
	 * @param strType
	 *                The Type
	 */
	public void setType( String strType )
	{
		_strType = strType;
	}

	/**
	 * Returns the Description
	 * 
	 * @return The Description
	 */
	public String getDescription( )
	{
		return _strDescription;
	}

	/**
	 * Sets the description
	 * 
	 * @param string Description
	 *               The Description
	 */
	public void setDescription( String strDescription )
	{
		_strDescription = strDescription;
	}

	/**
	 * Returns the list of items
	 * 
	 * @return The list of items
	 */
	public List < CustomMenuItem > getListItems( )
	{
		return _listItems;
	}

	/**
	 * Sets the list of items
	 * 
	 * @param listItems
	 *                  The list of items
	 */
	public void setListItems( List < CustomMenuItem > listItems )
	{
		_listItems = listItems;
	}

	/**
	 * Add an item to the menu
	 * 
	 * @param item
	 *             The item to add
	 */
	public void addItem( CustomMenuItem item )
	{
		_listItems.add( item );
	}

	/**
	 * Remove an item from the menu
	 * 
	 * @param item
	 *             The item to remove
	 */
	public void removeItem( CustomMenuItem item )
	{
		_listItems.remove( item );
	}
}
