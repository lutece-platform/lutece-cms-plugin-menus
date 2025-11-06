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
package fr.paris.lutece.plugins.menus.web.validator;

import fr.paris.lutece.plugins.menus.business.CustomMenuItem;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

/**
 * Custom Item Form Validation
 */
public class ValidatorCustomItemForm
{

	private static final String MENU_ITEM_TYPE_XPAGE = "xpage";
	private static final String MENU_ITEM_TYPE_PAGE = "page";
	private static final String MENU_ITEM_TYPE_EXTERNAL_URL = "external_url";
	private static final String MENU_ITEM_TYPE_MENU = "menu";

	// Messages
	private final String MESSAGE_TYPE_NOT_EMPTY = "menus.validation.customMenuItem.type.notEmpty";
	private final String MESSAGE_TYPE_NOT_VALID = "menus.validation.customMenuItem.type.pattern";
	private final String MESSAGE_PARENT_MENU_NOT_VALID = "menus.validation.customMenuItem.parentMenu.notValid";
	private final String MESSAGE_SUBMENU_NOT_EMPTY = "menus.validation.customMenuItem.subMenu.notEmpty";
	private final String MESSAGE_URL_NOT_EMPTY = "menus.validation.customMenuItem.url.notEmpty";
	private final String MESSAGE_LABEL_NOT_EMPTY = "menus.validation.customMenuItem.label.notEmpty";
	private final String MESSAGE_DYNAMIC_PAGE_LABEL_NOT_EMPTY = "menus.validation.customMenuItem.pageLabel.notEmpty";

	private List < ErrorMessage > _listErrors = new ArrayList <>( );

	/**
	 * Add an error message
	 * 
	 * @param strMessageKey
	 *                      The message
	 * @param locale
	 *                      The locale
	 */
	protected void addError( String strMessageKey, Locale locale )
	{
		_listErrors.add( new MVCMessage( I18nService.getLocalizedString( strMessageKey, locale ) ) );
	}

	/**
	 * validate Custom Menu item form
	 * 
	 * @param menuItems
	 *                  Item to test
	 * @param locale
	 *                  The locale
	 */
	public boolean isValid( CustomMenuItem menuItems, Locale locale )
	{

		_listErrors.clear( );
		String strType = menuItems.getType( );

		if( ! isValidMenuParent( menuItems ) )
		{
			addError( MESSAGE_PARENT_MENU_NOT_VALID, locale );
			return false;
		}

		if( strType != null )
		{

			switch( strType )
			{

				case MENU_ITEM_TYPE_XPAGE :
				case MENU_ITEM_TYPE_PAGE :
				case MENU_ITEM_TYPE_EXTERNAL_URL :
					if( ! isValidLabel( menuItems, locale ) )
					{
						if( Strings.CS.equals( menuItems.getType( ), MENU_ITEM_TYPE_PAGE )
								&& ! menuItems.isLabelDynamic( ) )
						{
							addError( MESSAGE_DYNAMIC_PAGE_LABEL_NOT_EMPTY, locale );
						}
						else
						{
							addError( MESSAGE_LABEL_NOT_EMPTY, locale );
						}
					}
					if( ! isValidUrl( menuItems, locale ) )
					{
						addError( MESSAGE_URL_NOT_EMPTY, locale );
					}

					return isValidLabel( menuItems, locale ) && isValidUrl( menuItems, locale );
				case MENU_ITEM_TYPE_MENU :
					if( ! isValidMenu( menuItems, locale ) )
					{
						addError( MESSAGE_SUBMENU_NOT_EMPTY, locale );
					}
					if( ! isValidLabel( menuItems, locale ) )
					{
						addError( MESSAGE_LABEL_NOT_EMPTY, locale );
					}
					if( ! isValidUrl( menuItems, locale ) )
					{
						addError( MESSAGE_URL_NOT_EMPTY, locale );
					}

					return isValidLabel( menuItems, locale ) && isValidUrl( menuItems, locale )
							&& isValidMenu( menuItems, locale );
				default :
					addError( MESSAGE_TYPE_NOT_VALID, locale );
					return false;
			}

		}

		addError( MESSAGE_TYPE_NOT_EMPTY, locale );
		return true;
	}

	private boolean isValidMenuParent( CustomMenuItem menuItems )
	{
		return menuItems.getParentMenuId( ) > 0;
	}

	private boolean isValidMenu( CustomMenuItem menuItems, Locale locale )
	{
		return ! StringUtils.isBlank( menuItems.getSourceItemId( ) );
	}

	private boolean isValidUrl( CustomMenuItem menuItems, Locale locale )
	{
		return ! StringUtils.isBlank( menuItems.getUrl( ) );
	}

	private boolean isValidLabel( CustomMenuItem menuItems, Locale locale )
	{

		if( ( StringUtils.isBlank( menuItems.getLabel( ) )
				&& ! Strings.CS.equals( menuItems.getType( ), MENU_ITEM_TYPE_PAGE ) )
				|| ( StringUtils.isBlank( menuItems.getLabel( ) )
						&& Strings.CS.equals( menuItems.getType( ), MENU_ITEM_TYPE_PAGE )
						&& ! menuItems.isLabelDynamic( ) ) )
		{
			return false;
		}

		return true;
	}

	public List < ErrorMessage > getListErrors( )
	{
		return _listErrors;
	}
}