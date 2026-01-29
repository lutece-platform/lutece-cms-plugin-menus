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

import jakarta.enterprise.inject.spi.CDI;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.menus.service.CustomMenuService;

/**
 * This is the business class for the object CustomMenuItem
 */
public class CustomMenuItem implements Serializable
{
	private static final long serialVersionUID = 2L;

	// Variables declarations
	private int _nId;
	private int _nParentMenuId;
	private String _strSourceItemId;
	private boolean _bIsLabelDynamic;
	private boolean _bIsBlank;
	@Size( max = 50, message = "#i18n{menus.validation.customMenuItem.label.size}" )
	private String _strLabel;
	private String _strType;
	@Size( max = 500, message = "#i18n{menus.validation.customMenuItem.url.size}" )
	private String _strUrl;
	private CustomMenu _subMenu;
	private int _nOrder;

	// Type constants
	public static final String TYPE_XPAGE = "menus.constant_custom_menu_item.type.xpage";
	public static final String TYPE_PAGE = "menus.constant_custom_menu_item.type.page";
	public static final String TYPE_EXTERNAL_URL = "menus.constant_custom_menu_item.type.externalUrl";
	public static final String TYPE_MENU = "menus.constant_custom_menu_item.type.submenu";

	private CustomMenuService _customMenuService = CDI.current( ).select( CustomMenuService.class ).get( );

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
	 * Returns the ParentMenuId
	 * 
	 * @return The ParentMenuId
	 */
	public int getParentMenuId( )
	{
		return _nParentMenuId;
	}

	/**
	 * Sets the ParentMenuId
	 * 
	 * @param nParentMenuId
	 *                      The ParentMenuId
	 */
	public void setParentMenuId( int nParentMenuId )
	{
		_nParentMenuId = nParentMenuId;
	}

	/**
	 * Returns the SourceItemId
	 * 
	 * @return The SourceItemId
	 */
	public String getSourceItemId( )
	{
		return _strSourceItemId;
	}

	/**
	 * Sets the SourceItemId
	 * 
	 * @param strSourceItemId
	 *                        The SourceItemId
	 */
	public void setSourceItemId( String strSourceItemId )
	{
		_strSourceItemId = strSourceItemId;
	}

	/**
	 * Returns the IsLabelDynamic
	 * 
	 * @return The IsLabelDynamic
	 */
	public boolean isLabelDynamic( )
	{
		return _bIsLabelDynamic;
	}

	/**
	 * Sets the IsLabelDynamic
	 * 
	 * @param bIsLabelDynamic
	 *                        The IsLabelDynamic
	 */
	public void setIsLabelDynamic( boolean bIsLabelDynamic )
	{
		_bIsLabelDynamic = bIsLabelDynamic;
	}

	/**
	 * Returns the IsBlank
	 * 
	 * @return The IsBlank
	 */
	public boolean isBlank( )
	{
		return _bIsBlank;
	}

	/**
	 * Sets the IsBlank
	 * 
	 * @param bIsBlank
	 *                 The IsBlank
	 */
	public void setIsBlank( boolean bIsBlank )
	{
		_bIsBlank = bIsBlank;
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
	 * Returns the Label
	 * 
	 * @return The Label
	 */
	public String getLabel( )
	{
		// If an item is a page referenced and option labelDynamic is at true
		if( this._bIsLabelDynamic && ! StringUtils.isBlank( this._strSourceItemId ) )
		{

			String strLabel = _customMenuService.getLabelPageById( this._strSourceItemId );

			if( strLabel != null )
			{
				int dashIndex = strLabel.indexOf( "-" );
				if( dashIndex != - 1 )
				{
					strLabel = strLabel.substring( 0, dashIndex );
				}
			}
			else
			{
				strLabel = "";
			}

			return strLabel;
		}

		return _strLabel;
	}

	/**
	 * Sets the Label
	 * 
	 * @param strLabel
	 *                 The Label
	 */
	public void setLabel( String strLabel )
	{
		_strLabel = strLabel;
	}

	/**
	 * Returns the Url
	 * 
	 * @return The Url
	 */
	public String getUrl( )
	{
		return _strUrl;
	}

	/**
	 * Sets the Url
	 * 
	 * @param strUrl
	 *               The Url
	 */
	public void setUrl( String strUrl )
	{
		_strUrl = strUrl;
	}

	/**
	 * Returns the Order
	 * 
	 * @return The Order
	 */
	public int getOrder( )
	{
		return _nOrder;
	}

	/**
	 * Sets the Order
	 * 
	 * @param nOrder
	 *               The Order
	 */
	public void setOrder( int nOrder )
	{
		_nOrder = nOrder;
	}

	/**
	 * Returns the subMenu
	 * 
	 * @return The subMenu
	 */
	public CustomMenu getSubMenu( )
	{
		return _subMenu;

	}

	/**
	 * Sets the subMenu
	 * 
	 * @param subMenu
	 *                The subMenu
	 */
	public void setSubMenu( CustomMenu subMenu )
	{
		this._subMenu = subMenu;

	}
}
