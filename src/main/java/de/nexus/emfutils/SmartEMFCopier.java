package de.nexus.emfutils;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.emoflon.smartemf.runtime.SmartObject;
import org.emoflon.smartemf.runtime.collections.LinkedSmartESet;

import java.util.*;

class SmartEMFCopier extends LinkedHashMap<SmartObject, SmartObject> {

    /**
     * Whether proxies should be resolved during copying.
     */
    protected boolean resolveProxies = true;

    /**
     * Whether non-copied references should be used during copying.
     */
    protected boolean useOriginalReferences = true;

    public SmartEMFCopier() {
        super();
    }

    public SmartEMFCopier(boolean resolveProxies, boolean useOriginalReferences) {
        this.resolveProxies = resolveProxies;
        this.useOriginalReferences = useOriginalReferences;
    }

    /**
     * Creates an instance that resolves proxies or not as specified.
     *
     * @param resolveProxies whether proxies should be resolved while copying.
     */
    public SmartEMFCopier(boolean resolveProxies) {
        this.resolveProxies = resolveProxies;
    }

    public <T> Collection<T> copyAll(Collection<? extends T> eObjects) {
        Collection<T> result = new ArrayList<T>(eObjects.size());
        for (Object object : eObjects) {
            @SuppressWarnings("unchecked") T t = (T) copy((SmartObject) object);
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Returns a copy of the given eObject.
     *
     * @param eObject the object to copy.
     * @return the copy.
     */
    public SmartObject copy(SmartObject eObject) {
        if (eObject == null) {
            return null;
        } else {
            SmartObject copyEObject = createCopy(eObject);
            if (copyEObject != null) {
                put(eObject, copyEObject);
                EClass eClass = eObject.eClass();
                for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i) {
                    EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);
                    if (eStructuralFeature.isChangeable() && !eStructuralFeature.isDerived()) {
                        if (eStructuralFeature instanceof EAttribute) {
                            copyAttribute((EAttribute) eStructuralFeature, eObject, copyEObject);
                        } else {
                            EReference eReference = (EReference) eStructuralFeature;
                            if (eReference.isContainment()) {
                                copyContainment(eReference, eObject, copyEObject);
                            }
                        }
                    }
                }

                copyProxyURI(eObject, copyEObject);
            }

            return copyEObject;
        }
    }

    /**
     * Copies the proxy URI from the original to the copy, if present.
     *
     * @param eObject     the object being copied.
     * @param copyEObject the copy being initialized.
     */
    protected void copyProxyURI(SmartObject eObject, SmartObject copyEObject) {
        if (eObject.eIsProxy()) {
            ((InternalEObject) copyEObject).eSetProxyURI(((InternalEObject) eObject).eProxyURI());
        }
    }

    /**
     * Returns a new instance of the object's target class.
     *
     * @param eObject the object to copy.
     * @return a new instance of the target class.
     * @see #getTarget(SmartObject)
     * @see EcoreUtil#create(EClass)
     */
    protected SmartObject createCopy(SmartObject eObject) {
        EClass eClass = getTarget(eObject);
        return eClass == null ? null : (SmartObject) EcoreUtil.create(eClass);
    }

    /**
     * Returns the target class used to create a copy instance for the given instance object.
     *
     * @param eObject the object to be copied.
     * @return the target class used to create a copy instance.
     * @since 2.10
     */
    protected EClass getTarget(SmartObject eObject) {
        return getTarget(eObject.eClass());
    }

    /**
     * Returns the target class used to create a copy instance for objects of the given source class.
     *
     * @param eClass the source class.
     * @return the target class used to create a copy instance.
     * @see #getTarget(EStructuralFeature, SmartObject, SmartObject)
     */
    protected EClass getTarget(EClass eClass) {
        return eClass;
    }

    /**
     * Returns a setting for the feature and copy instance to be populated with the original object's source feature's value.
     *
     * @param eStructuralFeature the source feature.
     * @return the target feature used to populate a copy instance.
     * @see #getTarget(EStructuralFeature)
     * @see #getTarget(SmartObject)
     * @since 2.10
     */
    protected EStructuralFeature.Setting getTarget(EStructuralFeature eStructuralFeature, SmartObject eObject, SmartObject copyEObject) {
        EStructuralFeature targetEStructuralFeature = getTarget(eStructuralFeature);
        return targetEStructuralFeature == null ? null : copyEObject.eSetting(targetEStructuralFeature);
    }

    /**
     * Returns the target feature used to populate a copy instance from the given source feature.
     *
     * @param eStructuralFeature the source feature.
     * @return the target feature used to populate a copy instance.
     * @see #getTarget(EClass)
     */
    protected EStructuralFeature getTarget(EStructuralFeature eStructuralFeature) {
        return eStructuralFeature;
    }

    /**
     * Called to handle the copying of a containment feature;
     * this adds a list of copies or sets a single copy as appropriate for the multiplicity.
     *
     * @param eReference  the feature to copy.
     * @param eObject     the object from which to copy.
     * @param copyEObject the object to copy to.
     */
    protected void copyContainment(EReference eReference, SmartObject eObject, SmartObject copyEObject) {
        if (eObject.eIsSet(eReference)) {
            EStructuralFeature.Setting setting = getTarget(eReference, eObject, copyEObject);
            if (setting != null) {
                Object value = eObject.eGet(eReference);
                if (eReference.isMany()) {
                    @SuppressWarnings("unchecked")
                    List<EObject> target = (List<EObject>) value;
                    setting.set(copyAll(target));
                } else {
                    setting.set(copy((SmartObject) value));
                }
            }
        }
    }

    /**
     * Called to handle the copying of an attribute;
     * this adds a list of values or sets a single value as appropriate for the multiplicity.
     *
     * @param eAttribute  the attribute to copy.
     * @param eObject     the object from which to copy.
     * @param copyEObject the object to copy to.
     */
    protected void copyAttribute(EAttribute eAttribute, SmartObject eObject, SmartObject copyEObject) {
        if (eObject.eIsSet(eAttribute)) {
            if (FeatureMapUtil.isFeatureMap(eAttribute)) {
                FeatureMap featureMap = (FeatureMap) eObject.eGet(eAttribute);
                copyFeatureMap(featureMap);
            } else {
                EStructuralFeature.Setting setting = getTarget(eAttribute, eObject, copyEObject);
                if (setting != null) {
                    copyAttributeValue(eAttribute, eObject, eObject.eGet(eAttribute), setting);
                }
            }
        }
    }

    /**
     * Call to handle copying the contained objects within a feature map.
     *
     * @param featureMap the feature map the copy.
     * @since 2.10
     */
    protected void copyFeatureMap(FeatureMap featureMap) {
        for (int i = 0, size = featureMap.size(); i < size; ++i) {
            EStructuralFeature feature = featureMap.getEStructuralFeature(i);
            if (feature instanceof EReference && ((EReference) feature).isContainment()) {
                Object value = featureMap.getValue(i);
                if (value != null) {
                    // The containment references are hooked up later during copyReferences.
                    //
                    copy((SmartObject) value);
                }
            }
        }
    }

    /**
     * Called to handle copying of an attribute's value to the target setting.
     *
     * @param eAttribute the attribute of the source object corresponding to the value.
     * @param eObject    the object being copied.
     * @param value      the value to be copied.
     * @param setting    the feature-value pair that is the target of of the copy.
     * @since 2.10
     */
    protected void copyAttributeValue(EAttribute eAttribute, SmartObject eObject, Object value, EStructuralFeature.Setting setting) {
        setting.set(value);
    }

    /**
     * Hooks up cross references; it delegates to {@link #copyReference copyReference}.
     */
    public void copyReferences() {
        for (Map.Entry<SmartObject, SmartObject> entry : entrySet()) {
            SmartObject eObject = entry.getKey();
            SmartObject copyEObject = entry.getValue();
            EClass eClass = eObject.eClass();
            for (int j = 0, size = eClass.getFeatureCount(); j < size; ++j) {
                EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(j);
                if (eStructuralFeature.isChangeable() && !eStructuralFeature.isDerived()) {
                    if (eStructuralFeature instanceof EReference eReference) {
                        if (!eReference.isContainment() && !eReference.isContainer()) {
                            copyReference(eReference, eObject, copyEObject);
                        }
                    } else if (FeatureMapUtil.isFeatureMap(eStructuralFeature)) {
                        FeatureMap copyFeatureMap = (FeatureMap) getTarget(eStructuralFeature, eObject, copyEObject);
                        if (copyFeatureMap != null) {
                            FeatureMap featureMap = (FeatureMap) eObject.eGet(eStructuralFeature);
                            int copyFeatureMapSize = copyFeatureMap.size();
                            for (int k = 0, featureMapSize = featureMap.size(); k < featureMapSize; ++k) {
                                EStructuralFeature feature = featureMap.getEStructuralFeature(k);
                                if (feature instanceof EReference) {
                                    Object referencedEObject = featureMap.getValue(k);
                                    Object copyReferencedEObject = get(referencedEObject);
                                    if (copyReferencedEObject == null && referencedEObject != null) {
                                        EReference reference = (EReference) feature;
                                        if (!useOriginalReferences || reference.isContainment() || reference.getEOpposite() != null) {
                                            continue;
                                        }
                                        copyReferencedEObject = referencedEObject;
                                    }

                                    // If we can't add it, it must already be in the list so find it and move it to the end.
                                    //
                                    if (!copyFeatureMap.add(feature, copyReferencedEObject)) {
                                        for (int l = 0; l < copyFeatureMapSize; ++l) {
                                            if (copyFeatureMap.getEStructuralFeature(l) == feature && copyFeatureMap.getValue(l) == copyReferencedEObject) {
                                                copyFeatureMap.move(copyFeatureMap.size() - 1, l);
                                                --copyFeatureMapSize;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    copyFeatureMap.add(getTarget(featureMap.getEStructuralFeature(k)), featureMap.getValue(k));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called to handle the copying of a cross reference;
     * this adds values or sets a single value as appropriate for the multiplicity
     * while omitting any bidirectional reference that isn't in the copy map.
     *
     * @param eReference  the reference to copy.
     * @param eObject     the object from which to copy.
     * @param copyEObject the object to copy to.
     */
    protected void copyReference(EReference eReference, SmartObject eObject, SmartObject copyEObject) {
        if (eObject.eIsSet(eReference)) {
            EStructuralFeature.Setting setting = getTarget(eReference, eObject, copyEObject);
            if (setting != null) {
                Object value = eObject.eGet(eReference, resolveProxies);
                Object copyValue = setting.get(resolveProxies);
                if (eReference.isMany()) {
                    @SuppressWarnings("unchecked") LinkedSmartESet<SmartObject> source = (LinkedSmartESet<SmartObject>) value;
                    @SuppressWarnings("unchecked") LinkedSmartESet<SmartObject> target = (LinkedSmartESet<SmartObject>) copyValue;
                    if (source.isEmpty()) {
                        target.clear();
                    } else {
                        boolean isBidirectional = eReference.getEOpposite() != null;
                        int index = 0;
                        for (Iterator<SmartObject> k = resolveProxies ? source.iterator() : source.basicIterator(); k.hasNext(); ) {
                            SmartObject referencedEObject = k.next();
                            SmartObject copyReferencedEObject = get(referencedEObject);
                            if (copyReferencedEObject == null) {
                                if (useOriginalReferences && !isBidirectional) {
                                    target.addUnique(index, referencedEObject);
                                    ++index;
                                }
                            } else {
                                if (isBidirectional) {
                                    if (!target.contains(copyReferencedEObject)) {
                                        target.addUnique(index, copyReferencedEObject);
                                    }
                                } else {
                                    target.addUnique(index, copyReferencedEObject);
                                }
                                ++index;
                            }
                        }
                    }
                } else {
                    if (value == null) {
                        setting.set(null);
                    } else {
                        Object copyReferencedEObject = get(value);
                        if (copyReferencedEObject == null) {
                            if (useOriginalReferences && eReference.getEOpposite() == null) {
                                setting.set(value);
                            }
                        } else {
                            setting.set(copyReferencedEObject);
                        }
                    }
                }
            }
        }
    }
}
