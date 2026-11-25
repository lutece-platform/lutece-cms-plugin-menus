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

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;
import jakarta.validation.constraints.Pattern;

/**
 * The bookmark of a custom menu cannot be a marker the core puts in the model of the pages
 */
public class CustomMenuBookmarkTest extends LuteceTestCase
{
    private static final String MESSAGE_FORBIDDEN = "#i18n{menus.validation.customMenu.bookmark.forbiddenName}";

    /**
     * Checks a bookmark against the patterns of the field with the « forbidden name » message (no Bean Validation
     * provider in the test container: the patterns are read from the annotations)
     *
     * @param strBookmark
     *            the bookmark
     * @return true if a forbidden-name pattern refuses it
     * @throws Exception
     *             if the field cannot be read
     */
    private static boolean isForbidden( String strBookmark ) throws Exception
    {
        Field field = CustomMenu.class.getDeclaredField( "_strBookmark" );
        boolean bForbidden = false;
        int nPatterns = 0;

        for ( Pattern pattern : field.getAnnotationsByType( Pattern.class ) )
        {
            if ( MESSAGE_FORBIDDEN.equals( pattern.message( ) ) )
            {
                nPatterns++;
                bForbidden |= !java.util.regex.Pattern.matches( pattern.regexp( ), strBookmark );
            }
        }
        assertTrue( nPatterns > 0, "forbidden-name pattern" );

        return bForbidden;
    }

    /**
     * The columns and the page markers of the core are refused
     */
    @Test
    public void testCoreMarkersAreRefused( ) throws Exception
    {
        for ( String strBookmark : new String [ ] {
                "page_content_col1", "page_content_col2", "page_content_col12", "page_main_menu", "page_tree_menu", "page_name", "page_path",
                "page_title", "page_id", "page_content", "page_header", "page_footer", "page_init_menu", "page_tools_menu", "customMenu",
                "page_tree_menu_main"
        } )
        {
            assertTrue( isForbidden( strBookmark ), strBookmark );
        }
    }

    /**
     * Ordinary bookmarks, even starting like a core marker, are accepted
     */
    @Test
    public void testOrdinaryBookmarksAreAccepted( ) throws Exception
    {
        for ( String strBookmark : new String [ ] {
                "my_menu", "page_content_col", "page_content_colx", "page_name_menu", "footer_links", "page_main_menu_custom"
        } )
        {
            assertFalse( isForbidden( strBookmark ), strBookmark );
        }
    }
}
