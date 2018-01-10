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

/**
 * This is the business class for the object Menus
 */
public class Menus
{
    // Variables declarations
    private int _nIdMenu;
    private int _nIdPageRoot;
    private String _strMenuName;
    private String _strMenuType;
    private String _strMenuMarker;

    /**
     * Initialize the Menus
     */
    public static void init( )
    {
        // Nothing to do
    }

    /**
     * Returns the IdMenu
     * 
     * @return The IdMenu
     */
    public int getIdMenu( )
    {
        return _nIdMenu;
    }

    /**
     * Sets the IdMenu
     * 
     * @param nIdMenu
     *            The IdMenu
     */
    public void setIdMenu( int nIdMenu )
    {
        _nIdMenu = nIdMenu;
    }

    /**
     * Returns the MenuName
     * 
     * @return The MenuName
     */
    public String getMenuName( )
    {
        return _strMenuName;
    }

    /**
     * Sets the MenuName
     * 
     * @param strMenuName
     *            The MenuName
     */
    public void setMenuName( String strMenuName )
    {
        _strMenuName = strMenuName;
    }

    /**
     * Returns the TypeMenu
     * 
     * @return The TypeMenu
     */
    public String getMenuType( )
    {
        return _strMenuType;
    }

    /**
     * Sets the TypeMenu
     * 
     * @param strTypeMenu
     *            The TypeMenu
     */
    public void setTypeMenu( String strTypeMenu )
    {
        _strMenuType = strTypeMenu;
    }

    /**
     * Returns the IdPageRoot
     * 
     * @return The IdPageRoot
     */
    public int getIdPageRoot( )
    {
        return _nIdPageRoot;
    }

    /**
     * Sets the IdPageRoot
     * 
     * @param nIdPageRoot
     *            The IdPageRoot
     */
    public void setIdPageRoot( int nIdPageRoot )
    {
        _nIdPageRoot = nIdPageRoot;
    }

    /**
     * Returns the MenuMarker
     * 
     * @return The MenuMarker
     */
    public String getMenuMarker( )
    {
        return _strMenuMarker;
    }

    /**
     * Sets the MenuMarker
     * 
     * @param strMenuMarker
     *            The MenuMarker
     */
    public void setMenuMarker( String strMenuMarker )
    {
        _strMenuMarker = strMenuMarker;
    }
}
