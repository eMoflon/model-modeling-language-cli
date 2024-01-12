package de.nexus.mmlcli.constraint;

import de.nexus.mmlcli.constraint.adapter.EmfMetamodelSource;
import de.nexus.mmlcli.constraint.adapter.HiPEBuilder;
import de.nexus.mmlcli.constraint.entity.ConstraintDocumentEntity;
import de.nexus.mmlcli.constraint.entity.EntityReferenceResolver;
import org.eclipse.core.internal.preferences.OSGiPreferencesServiceManager;
import org.eclipse.core.internal.preferences.PreferencesService;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.internal.framework.EquinoxConfiguration;
import org.eclipse.osgi.internal.framework.EquinoxContainer;
import org.eclipse.osgi.internal.location.BasicLocation;
import org.eclipse.osgi.launch.Equinox;
import org.eclipse.osgi.launch.EquinoxFactory;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import picocli.CommandLine;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import static org.eclipse.osgi.internal.location.LocationHelper.buildURL;

@CommandLine.Command(name = "hipegen", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Builds hipe network")
public class HipeGenCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0")
    File ecorePath;
    @CommandLine.Parameters(index = "1")
    File sConstraintDocPath;

    @CommandLine.Parameters(index = "2")
    File workspacePath;

    @Override
    public Integer call() throws Exception {
        EmfMetamodelSource metamodelSource = new EmfMetamodelSource();
        metamodelSource.load(ecorePath.toString());

        String sConstraintDoc = Files.readString(sConstraintDocPath.toPath(), StandardCharsets.UTF_8);

        ConstraintDocumentEntity cDoc = ConstraintDocumentEntity.build(sConstraintDoc);

        EntityReferenceResolver.resolve(cDoc);

        HiPEBuilder hiPEBuilder = new HiPEBuilder(metamodelSource, cDoc, workspacePath.toPath());

        Equinox equinox = (Equinox) new EquinoxFactory().newFramework(new HashMap<>());
        EquinoxConfiguration equinoxConfig = new EquinoxContainer(new HashMap<>(), null).getConfiguration();

        InternalPlatform.start(equinox);

        BundleContext bundleContext = equinox.getBundleContext();

        bundleContext.registerService(EnvironmentInfo.class, equinoxConfig, new Hashtable<>());

        InternalPlatform.getDefault().start(bundleContext);

        ResourcesPlugin resourcesPlugin = new ResourcesPlugin();

        resourcesPlugin.start(bundleContext);


        try {
            // Load resources plugin class and the nested private WorkspaceInitCustomizer class
            Class<?> clazzRP = Class.forName("org.eclipse.core.resources.ResourcesPlugin");
            Class<?> clazz = clazzRP.getDeclaredClasses()[0];


            // get the private workspaceInitCustomizer field of the ResourcesPlugin and make it accessible
            Field field = clazzRP.getDeclaredField("workspaceInitCustomizer");
            field.setAccessible(true);

            // get the WorkspaceInitCustomizer that has been assigned during ResourcePlugin.start()
            Object workspaceInitCustomizer = field.get(resourcesPlugin);

            // get its addingService method and make it accessible
            Method method = clazz.getDeclaredMethod("addingService", ServiceReference.class);
            method.setAccessible(true);

            // build a custom location to the workspacePath without any additional information
            URL url = buildURL(workspacePath.getAbsolutePath(), true);
            Location loc = new BasicLocation(null, url, false, null, null, null, null);

            // register a new LocationService to trigger to WorkspaceInitCustomizer
            ServiceRegistration<?> registration = bundleContext.registerService(
                    Location.class.getName(),
                    loc,
                    null);

            ServiceReference<?> reference = registration.getReference();

            // define the path for the temporary plugin state
            IPath path = IPath.fromFile(workspacePath);

            // get the plugin and its declared private stateLocation field, make it accessible
            Plugin pgn = ResourcesPlugin.getPlugin();

            Field stateLoc = Plugin.class.getDeclaredField("stateLocation");
            stateLoc.setAccessible(true);

            // overwrite the stateLocation
            stateLoc.set(pgn, path);

            // register two PreferencesServices since they are required during the workspace creation
            bundleContext.registerService(IPreferencesService.class,
                    PreferencesService.getDefault(), new Hashtable<>());
            bundleContext.registerService(org.osgi.service.prefs.PreferencesService.class,
                    new OSGiPreferencesServiceManager(bundleContext), null);

            // invoke the workspace creation manually with the previously defined Location
            System.out.println("Invoke workspaceInitCustomizer...");
            Workspace result = (Workspace) method.invoke(workspaceInitCustomizer, reference);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }

        assert ResourcesPlugin.getWorkspace() != null;

        hiPEBuilder.build();

        return 0;
    }
}
