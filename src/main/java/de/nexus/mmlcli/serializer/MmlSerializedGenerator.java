package de.nexus.mmlcli.serializer;

import de.nexus.mmlcli.entities.model.ModelEntity;
import de.nexus.mmlcli.entities.model.PackageEntity;
import de.nexus.mmlcli.generator.DeserializedDocument;
import de.nexus.mmlcli.generator.SerializedDocument;
import org.eclipse.emf.ecore.EPackage;

import java.net.URI;

public class MmlSerializedGenerator {
    public static PackageEntity buildEntities(EPackage pckg) {
        EcoreIdResolver resolver = new EcoreIdResolver();
        return PackageEntity.fromEPackage(pckg, resolver);
    }

    public static String serializeEntities(PackageEntity pckgEntity, URI uri) {
        ModelEntity model = ModelEntity.fromPackageEntity(pckgEntity);
        DeserializedDocument dDoc = DeserializedDocument.build(model);
        SerializedDocument sDoc = new SerializedDocument(uri, dDoc);
        return sDoc.serialize();
    }
}
