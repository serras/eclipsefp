package net.sf.eclipsefp.haskell.browser.views.hoogle;

import java.util.ArrayList;
import java.util.Map;
import net.sf.eclipsefp.haskell.browser.items.Declaration;
import net.sf.eclipsefp.haskell.browser.items.HoogleResult;
import net.sf.eclipsefp.haskell.browser.items.HoogleResultConstructor;
import net.sf.eclipsefp.haskell.browser.items.HoogleResultDeclaration;
import net.sf.eclipsefp.haskell.browser.util.ImageCache;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;


public class HoogleLabelProvider implements ILabelProvider {

  public Image getImage( final Object element ) {

    HoogleResult result = null;
    if (element instanceof HoogleResult) {
      result = (HoogleResult)element;
    } else {
      Map.Entry<String, Object> entry = (Map.Entry<String, Object>)element;
      if (entry.getValue() instanceof ArrayList) {
        result = ( (ArrayList<HoogleResult>)entry.getValue() ).get( 0 );
      } else {
        // If we came here, it means that we are in an element defined in several places
        return ImageCache.MODULE_CONTENT;
      }
    }

    switch(result.getType()) {
      case PACKAGE:
        return ImageCache.PACKAGE;
      case MODULE:
        return ImageCache.MODULE;
      case CONSTRUCTOR:
        return ImageCache.CONSTRUCTOR;
      case DECLARATION:
        Declaration decl = ((HoogleResultDeclaration)result).getDeclaration();
        return ImageCache.getImageForDeclaration( decl.getType() );
    }

    return null;
  }

  public String getText( final Object element ) {

    HoogleResult result = null;
    if (element instanceof HoogleResult) {
      result = (HoogleResult)element;
      switch(result.getType()) {
        case PACKAGE:
        case MODULE:
          return result.getName();
        case DECLARATION:
          Declaration decl = ((HoogleResultDeclaration)result).getDeclaration();
          return decl.getShownName();
        case CONSTRUCTOR:
          HoogleResultConstructor con = (HoogleResultConstructor)result;
          return con.getConstructor().getShownName();
      }
    } else {
      Map.Entry<String, Object> entry = (Map.Entry<String, Object>)element;
      if (entry.getValue() instanceof ArrayList) {
        // We are in the root of an element defined in several places
        return entry.getKey();
      } else {
        // We are in an instance of an element defined several times,
        // so we need to show the name of the module
        result = (HoogleResult)entry.getValue();
        switch(result.getType()) {
          case PACKAGE:
          case MODULE:
            return null; // This should not happen
          case DECLARATION:
            HoogleResultDeclaration decl = (HoogleResultDeclaration)result;
            return decl.getModule();
          case CONSTRUCTOR:
            HoogleResultConstructor con = (HoogleResultConstructor)result;
            return con.getModule();
        }
      }
    }



    return null;
  }

  // Listeners: not used
  public void addListener( final ILabelProviderListener listener ) {
    // Do nothing
  }

  public void dispose() {
    // Do nothing
  }

  public boolean isLabelProperty( final Object element, final String property ) {
    // Do nothing
    return false;
  }

  public void removeListener( final ILabelProviderListener listener ) {
    // Do nothing
  }
}