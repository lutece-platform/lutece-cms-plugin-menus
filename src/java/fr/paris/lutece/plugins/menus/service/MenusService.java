/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
package fr.paris.lutece.plugins.menus.service;

import fr.paris.lutece.plugins.menus.business.Menus;
import fr.paris.lutece.portal.service.util.AppPathService;


/**
 * This Service manages contactListt actions (create, delete, modify ...)
 * and notify listeners.
 * @author lenaini
 */
public class MenusService
{
    private static MenusService _singleton = new MenusService(  );
    public static final int MODE_SITE = 0;
    public static final int MODE_ADMIN = 1;
    public static final String MARKER_SITE_PATH = "site_path";

    /**
    * Initializes the Menus service
    *
    */
    public void init(  )
    {
        Menus.init(  );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static MenusService getInstance(  )
    {
        return _singleton;
    }

    /**
     * Define the site path : Portal Url when mode isn't admin mode, otherwise AdminPortalUrl
     * @param nMode the mode define by the request
     * @return site path depending on the mode
     */
    public String getSitePath( int nMode )
    {
        String strSitePath = AppPathService.getAdminPortalUrl(  );

        if ( nMode != MODE_ADMIN )
        {
            strSitePath = AppPathService.getPortalUrl(  );
        }

        return strSitePath;
    }
}
