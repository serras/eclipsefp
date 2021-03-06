package net.sf.eclipsefp.haskell.core.hlint;

import java.util.Map;
import net.sf.eclipsefp.haskell.core.HaskellCorePlugin;
import net.sf.eclipsefp.haskell.core.util.ResourceUtil;
import net.sf.eclipsefp.haskell.hlint.Severity;
import net.sf.eclipsefp.haskell.hlint.Suggestion;
import net.sf.eclipsefp.haskell.util.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * HLint builder: calls HLint, parses the results and shows
 * them as file markers in the editor.
 *
 * @author Alejandro Serrano
 */
public class HLintBuilder extends IncrementalProjectBuilder {

  public static String BUILDER_ID = HLintBuilder.class.getName();

  public HLintBuilder() {
    // Do nothing
  }

  @Override
  protected IProject[] build( final int kind, final Map<String,String> args,
      final IProgressMonitor monitor ) throws CoreException {
    if( kind == INCREMENTAL_BUILD || kind == AUTO_BUILD ) {
      // Get delta
      final IResourceDelta delta = getDelta( getProject() );
      if( delta == null ) {
        return null;
      }
            delta.accept( new DeltaVisitor() );
    } else if( kind == CLEAN_BUILD ) {
          clean( monitor );

    } else if( kind == FULL_BUILD ) {

        clean( monitor );
        getProject().accept( new FullBuildVisitor() );
    }
    // Complete code here
    return null;
  }

  @Override
  protected void clean( final IProgressMonitor monitor ) throws CoreException {
    getProject().accept( new CleanVisitor() );
  }

  @Override
  public ISchedulingRule getRule( final int kind, final Map<String, String> args ) {
    // prevent other project operations, but operations elsewhere in the workspace are fine
    return getProject();
  }

  /**
   * return a IFile representing the resource if needed, or null if not needed
   * @param resource the resource
   * @return the IFile object to analyze, or null if nothing to do
   */
  static IFile getFileToVisit( final IResource resource ) {
    if  ( resource instanceof IFile
        && hasCorrectExtension( resource.getProjectRelativePath() )
        && ResourceUtil.isInSourceFolder( ( IFile )resource )
        && !resource.isDerived() ){
      return (IFile)resource;
    }
    return null;
  }

  static boolean mustBeVisited( final IResource resource ) {
    return getFileToVisit( resource )!=null;
  }


  static boolean hasCorrectExtension( final IPath path ) {
    if( path == null ) {
      return false;
    }
    String extension = path.getFileExtension();
    if( extension == null ) {
      return false;
    }
    return extension.equals( FileUtil.EXTENSION_HS );
  }

  /**
   * show always the full text?
   */
  private static boolean alwaysFull=false;

  public static void setAlwaysFull( final boolean alwaysFull ) {
    HLintBuilder.alwaysFull = alwaysFull;
  }

  static void createMarker( final IResource resource, final Suggestion s )
      throws CoreException {
    if( s.getSeverity() != Severity.IGNORE ) {
      IMarker marker = resource
          .createMarker( HaskellCorePlugin.ID_HLINT_MARKER );


      marker.setAttribute( IMarker.MESSAGE, s.getMarkerText(alwaysFull) );
      marker.setAttribute( IMarker.SEVERITY,getMarkerSeverity(s.getSeverity()) );
      marker.setAttribute( IMarker.LINE_NUMBER, s.getLocation().getLine() );
      marker.setAttribute( HaskellCorePlugin.ATT_HLINT_SUGGESTION, s.toString() );
    }
  }


  private static int getMarkerSeverity(final Severity sev){
    switch( sev ) {
      case ERROR:
        return IMarker.SEVERITY_WARNING;
      case WARNING:
        return IMarker.SEVERITY_WARNING;
      default:
        return IMarker.SEVERITY_INFO;
    }
  }
}
