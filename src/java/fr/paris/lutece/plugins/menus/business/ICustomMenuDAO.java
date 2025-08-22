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
 * ICustomMenuDAO Interface
 */
public interface ICustomMenuDAO
{
	/**
	 * Insert a new record in the table.
	 * 
	 * @param customMenu
	 *                   instance of the CustomMenu object to insert
	 * @param plugin
	 *                   the Plugin
	 */
	void insert( CustomMenu customMenu, Plugin plugin );

	/**
	 * Update the record in the table
	 * 
	 * @param customMenu
	 *                   the reference of the CustomMenu
	 * @param plugin
	 *                   the Plugin
	 */
	void store( CustomMenu customMenu, Plugin plugin );

	/**
	 * Delete a record from the table
	 * 
	 * @param nKey
	 *               The identifier of the CustomMenu to delete
	 * @param plugin
	 *               the Plugin
	 */
	void delete( int nKey, Plugin plugin );

	/**
	 * Load the data from the table
	 * 
	 * @param nKey
	 *               The identifier of the customMenu
	 * @param plugin
	 *               the Plugin
	 * @return The instance of the customMenu
	 */
	CustomMenu load( int nKey, Plugin plugin );

	/**
	 * Load the data of all the customMenu objects and returns them as a collection
	 * 
	 * @param plugin
	 *               the Plugin
	 * @return The collection which contains the data of all the customMenu objects
	 */
	List < CustomMenu > selectAll( Plugin plugin );

	/**
	 * Load the data of all the customMenu according to a list of customMenu Ids and
	 * returns them as a collection
	 * 
	 * @param plugin
	 *                the Plugin
	 * @param listIds
	 *                the Ids list
	 * @return The collection which contains the data of all the customMenu objects
	 */
	List < CustomMenu > selectMenusListByIds( Plugin _plugin, List < Integer > listIds );

	/**
	 * Load the data of all the customMenu idss and returns them as a collection
	 * 
	 * @param plugin
	 *               the Plugin
	 * @return The collection which contains the data of all the customMenu objects
	 */
	List < Integer > selectIdMenusList( Plugin _plugin );

	/**
	 * Load the data of all the customMenu according to filter Criteria and returns
	 * them as a collection
	 * 
	 * @param plugin
	 *                       the Plugin
	 * @param filterCriteria
	 *                       the filter Criteria
	 * @return The collection which contains the data of all the customMenu objects
	 */
	List < CustomMenu > selectMenusListByFilterCriteria( Plugin _plugin, String filterCriteria );

	/**
	 * Return if bookmark is unique or not
	 *
	 * @param plugin
	 *                   the Plugin
	 * @param customMenu
	 *                   instance of the CustomMenu object to insert
	 * @return True if bookmark is unique or false else.
	 */
	Integer countBookmark( Plugin plugin, CustomMenu customMenu );
}
