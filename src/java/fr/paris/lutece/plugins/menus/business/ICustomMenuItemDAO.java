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

import java.util.List;

/**
 * ICustomMenuItemDAO Interface
 */
public interface ICustomMenuItemDAO
{
	/**
	 * Insert a new record in the table.
	 * 
	 * @param customMenuItem
	 *                       instance of the CustomMenuItem object to insert
	 * @param plugin
	 *                       the Plugin
	 */
	void insert( CustomMenuItem customMenuItem, Plugin plugin );

	/**
	 * Update the record in the table
	 * 
	 * @param customMenuItem
	 *                       the reference of the CustomMenuItem
	 * @param plugin
	 *                       the Plugin
	 */
	void store( CustomMenuItem customMenuItem, Plugin plugin );

	/**
	 * Delete a record from the table
	 * 
	 * @param nKey
	 *               The identifier of the CustomMenuItem to delete
	 * @param plugin
	 *               the Plugin
	 */
	void delete( int nKey, Plugin plugin );

	/**
	 * Delete all items of a menu
	 * 
	 * @param nMenuId
	 *                The identifier of the menu
	 * @param plugin
	 *                the Plugin
	 */
	void deleteByMenuId( int nMenuId, Plugin plugin );

	/**
	 * Delete all subMenu item from all menu
	 * 
	 * @param nMenuId
	 *                The identifier of the menu to delete
	 * @param plugin
	 *                the Plugin
	 */
	void deleteSubMenuItemBySubMenuId( int nMenuSourceId, Plugin plugin );

	/**
	 * Load the data from the table
	 * 
	 * @param nKey
	 *               The identifier of the customMenuItem
	 * @param plugin
	 *               the Plugin
	 * @return The instance of the customMenuItem
	 */
	CustomMenuItem load( int nKey, Plugin plugin );

	/**
	 * Load the data of all the customMenuItem objects and returns them as a
	 * collection
	 * 
	 * @param plugin
	 *               the Plugin
	 * @return The collection which contains the data of all the customMenuItem
	 *         objects
	 */
	List < CustomMenuItem > selectAll( Plugin plugin );

	/**
	 * Load the data of all the customMenuItem objects for a specific menu and
	 * returns them as a list
	 * 
	 * @param nMenuId
	 *                The identifier of the menu
	 * @param plugin
	 *                the Plugin
	 * @return The list which contains the data of all the customMenuItem
	 *         objects for the menu
	 */
	List < CustomMenuItem > selectByMenuId( int nMenuId, Plugin plugin );

	/**
	 * Load the data of all the customMenuItem objects for a specific menu and
	 * returns their ids as a list
	 * 
	 * @param nMenuId
	 *                The identifier of the menu
	 * @param plugin
	 *                the Plugin
	 * @return The list which contains the id of all the customMenuItem
	 *         objects for the menu
	 */
	List < Integer > selectAllIdsByMenuId( int nMenuId, Plugin _plugin );

}
