/*
 * UserPrefBooleanPaletteEntry.java
 *
 * Copyright (C) 2020 by RStudio, PBC
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.studio.client.application.ui;

import org.rstudio.core.client.js.JsObject;
import org.rstudio.core.client.widget.Toggle;
import org.rstudio.studio.client.RStudioGinjector;
import org.rstudio.studio.client.workbench.prefs.model.Prefs.PrefValue;
import org.rstudio.studio.client.workbench.prefs.model.UserPrefDefinition;
import org.rstudio.studio.client.workbench.prefs.model.UserPrefs;

import com.google.gwt.user.client.ui.Widget;

public class UserPrefBooleanPaletteEntry extends UserPrefPaletteEntry
{
   @SuppressWarnings("unchecked")
   public UserPrefBooleanPaletteEntry(String id, UserPrefDefinition pref)
   {
      super(id, pref);
      UserPrefs prefs = RStudioGinjector.INSTANCE.getUserPrefs();
      boolean initial = false;

      value_ = (PrefValue<Boolean>)prefs.getPrefValue(id);
      if (value_ == null)
      {
         JsObject userPrefs = prefs.getUserLayer();
         initial = userPrefs.getBool(id);
      }
      else
      {
         initial = value_.getGlobalValue();
      }

      toggle_ = new Toggle("", false);
      toggle_.setState(initial ? 
         Toggle.State.ON : Toggle.State.OFF, false);

      initialize();
   }

   @Override
   public void invoke()
   {
      boolean newValue = toggle_.getState() != Toggle.State.ON;
      if (value_ != null)
      {
         value_.setGlobalValue(newValue);
      }

      toggle_.setState(newValue ? Toggle.State.ON : Toggle.State.OFF,
         !RStudioGinjector.INSTANCE.getUserPrefs().reducedMotion().getValue());
   }

   @Override
   public Widget getInvoker()
   {
      return toggle_;
   }
   
   private final Toggle toggle_;
   private final PrefValue<Boolean> value_;
}
