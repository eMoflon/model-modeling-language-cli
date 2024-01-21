package de.nexus.mmlcli.constraint.adapter;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

public class DummyEMFProject implements IProject {
    private final String projectName;
    private final IPath path;

    public DummyEMFProject(String projectName, Path path) {
        this.projectName = projectName;
        this.path = IPath.fromPath(path);
    }

    @Override
    public void build(int kind, String builderName, Map<String, String> args, IProgressMonitor monitor) {

    }

    @Override
    public void build(int kind, IProgressMonitor monitor) {

    }

    @Override
    public void build(IBuildConfiguration config, int kind, IProgressMonitor monitor) {

    }

    @Override
    public void close(IProgressMonitor monitor) {

    }

    @Override
    public void create(IProjectDescription description, IProgressMonitor monitor) {

    }

    @Override
    public void create(IProgressMonitor monitor) {

    }

    @Override
    public void create(IProjectDescription description, int updateFlags, IProgressMonitor monitor) {

    }

    @Override
    public void delete(boolean deleteContent, boolean force, IProgressMonitor monitor) {

    }

    @Override
    public IBuildConfiguration getActiveBuildConfig() {
        return null;
    }

    @Override
    public IBuildConfiguration getBuildConfig(String configName) {
        return null;
    }

    @Override
    public IBuildConfiguration[] getBuildConfigs() {
        return new IBuildConfiguration[0];
    }

    @Override
    public IContentTypeMatcher getContentTypeMatcher() {
        return null;
    }

    @Override
    public IProjectDescription getDescription() {
        return null;
    }

    @Override
    public IFile getFile(String name) {
        return null;
    }

    @Override
    public IFolder getFolder(String name) {
        return null;
    }

    @Override
    public IProjectNature getNature(String natureId) {
        return null;
    }

    @Override
    public IPath getWorkingLocation(String id) {
        return null;
    }

    @Override
    public IProject[] getReferencedProjects() {
        return new IProject[0];
    }

    @Override
    public void clearCachedDynamicReferences() {

    }

    @Override
    public IProject[] getReferencingProjects() {
        return new IProject[0];
    }

    @Override
    public IBuildConfiguration[] getReferencedBuildConfigs(String configName, boolean includeMissing) {
        return new IBuildConfiguration[0];
    }

    @Override
    public boolean hasBuildConfig(String configName) {
        return false;
    }

    @Override
    public boolean hasNature(String natureId) {
        return false;
    }

    @Override
    public boolean isNatureEnabled(String natureId) {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void loadSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) {

    }

    @Override
    public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) {

    }

    @Override
    public void open(int updateFlags, IProgressMonitor monitor) {

    }

    @Override
    public void open(IProgressMonitor monitor) {

    }

    @Override
    public void saveSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) {

    }

    @Override
    public void setDescription(IProjectDescription description, IProgressMonitor monitor) {

    }

    @Override
    public void setDescription(IProjectDescription description, int updateFlags, IProgressMonitor monitor) {

    }

    @Override
    public String getDefaultLineSeparator() {
        return null;
    }

    @Override
    public boolean exists(IPath path) {
        return false;
    }

    @Override
    public IResource findMember(String path) {
        return null;
    }

    @Override
    public IResource findMember(String path, boolean includePhantoms) {
        return null;
    }

    @Override
    public IResource findMember(IPath path) {
        return null;
    }

    @Override
    public IResource findMember(IPath path, boolean includePhantoms) {
        return null;
    }

    @Override
    public String getDefaultCharset() {
        return null;
    }

    @Override
    public String getDefaultCharset(boolean checkImplicit) {
        return null;
    }

    @Override
    public IFile getFile(IPath path) {
        return null;
    }

    @Override
    public IFolder getFolder(IPath path) {
        return null;
    }

    @Override
    public IResource[] members() {
        return new IResource[0];
    }

    @Override
    public IResource[] members(boolean includePhantoms) {
        return new IResource[0];
    }

    @Override
    public IResource[] members(int memberFlags) {
        return new IResource[0];
    }

    @Override
    public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) {
        return new IFile[0];
    }

    @Override
    public void setDefaultCharset(String charset) {

    }

    @Override
    public void setDefaultCharset(String charset, IProgressMonitor monitor) {

    }

    @Override
    public IResourceFilterDescription createFilter(int type, FileInfoMatcherDescription matcherDescription, int updateFlags, IProgressMonitor monitor) {
        return null;
    }

    @Override
    public IResourceFilterDescription[] getFilters() {
        return new IResourceFilterDescription[0];
    }

    @Override
    public void accept(IResourceProxyVisitor visitor, int memberFlags) {

    }

    @Override
    public void accept(IResourceProxyVisitor visitor, int depth, int memberFlags) {

    }

    @Override
    public void accept(IResourceVisitor visitor) {

    }

    @Override
    public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) {

    }

    @Override
    public void accept(IResourceVisitor visitor, int depth, int memberFlags) {

    }

    @Override
    public void clearHistory(IProgressMonitor monitor) {

    }

    @Override
    public void copy(IPath destination, boolean force, IProgressMonitor monitor) {

    }

    @Override
    public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) {

    }

    @Override
    public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) {

    }

    @Override
    public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) {

    }

    @Override
    public IMarker createMarker(String type) {
        return null;
    }

    @Override
    public IResourceProxy createProxy() {
        return null;
    }

    @Override
    public void delete(boolean force, IProgressMonitor monitor) {

    }

    @Override
    public void delete(int updateFlags, IProgressMonitor monitor) {

    }

    @Override
    public void deleteMarkers(String type, boolean includeSubtypes, int depth) {

    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public IMarker findMarker(long id) {
        return null;
    }

    @Override
    public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) {
        return new IMarker[0];
    }

    @Override
    public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) {
        return 0;
    }

    @Override
    public String getFileExtension() {
        return null;
    }

    @Override
    public IPath getFullPath() {
        return null;
    }

    @Override
    public long getLocalTimeStamp() {
        return 0;
    }

    @Override
    public IPath getLocation() {
        return this.path;
    }

    @Override
    public URI getLocationURI() {
        return null;
    }

    @Override
    public IMarker getMarker(long id) {
        return null;
    }

    @Override
    public long getModificationStamp() {
        return 0;
    }

    @Override
    public String getName() {
        return this.projectName;
    }

    @Override
    public IPathVariableManager getPathVariableManager() {
        return null;
    }

    @Override
    public IContainer getParent() {
        return null;
    }

    @Override
    public Map<QualifiedName, String> getPersistentProperties() {
        return null;
    }

    @Override
    public String getPersistentProperty(QualifiedName key) {
        return null;
    }

    @Override
    public IProject getProject() {
        return null;
    }

    @Override
    public IPath getProjectRelativePath() {
        return null;
    }

    @Override
    public IPath getRawLocation() {
        return null;
    }

    @Override
    public URI getRawLocationURI() {
        return null;
    }

    @Override
    public ResourceAttributes getResourceAttributes() {
        return null;
    }

    @Override
    public Map<QualifiedName, Object> getSessionProperties() {
        return null;
    }

    @Override
    public Object getSessionProperty(QualifiedName key) {
        return null;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public IWorkspace getWorkspace() {
        return null;
    }

    @Override
    public boolean isAccessible() {
        return false;
    }

    @Override
    public boolean isDerived() {
        return false;
    }

    @Override
    public boolean isDerived(int options) {
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isHidden(int options) {
        return false;
    }

    @Override
    public boolean isLinked() {
        return false;
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

    @Override
    public boolean isLinked(int options) {
        return false;
    }

    @Override
    public boolean isLocal(int depth) {
        return false;
    }

    @Override
    public boolean isPhantom() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean isSynchronized(int depth) {
        return false;
    }

    @Override
    public boolean isTeamPrivateMember() {
        return false;
    }

    @Override
    public boolean isTeamPrivateMember(int options) {
        return false;
    }

    @Override
    public void move(IPath destination, boolean force, IProgressMonitor monitor) {

    }

    @Override
    public void move(IPath destination, int updateFlags, IProgressMonitor monitor) {

    }

    @Override
    public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) {

    }

    @Override
    public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) {

    }

    @Override
    public void refreshLocal(int depth, IProgressMonitor monitor) {

    }

    @Override
    public void revertModificationStamp(long value) {

    }

    @Override
    public void setDerived(boolean isDerived) {

    }

    @Override
    public void setDerived(boolean isDerived, IProgressMonitor monitor) {

    }

    @Override
    public void setHidden(boolean isHidden) {

    }

    @Override
    public void setLocal(boolean flag, int depth, IProgressMonitor monitor) {

    }

    @Override
    public long setLocalTimeStamp(long value) {
        return 0;
    }

    @Override
    public void setPersistentProperty(QualifiedName key, String value) {

    }

    @Override
    public void setReadOnly(boolean readOnly) {

    }

    @Override
    public void setResourceAttributes(ResourceAttributes attributes) {

    }

    @Override
    public void setSessionProperty(QualifiedName key, Object value) {

    }

    @Override
    public void setTeamPrivateMember(boolean isTeamPrivate) {

    }

    @Override
    public void touch(IProgressMonitor monitor) {

    }

    @Override
    public <T> T getAdapter(Class<T> aClass) {
        return null;
    }

    @Override
    public boolean contains(ISchedulingRule iSchedulingRule) {
        return false;
    }

    @Override
    public boolean isConflicting(ISchedulingRule iSchedulingRule) {
        return false;
    }
}
