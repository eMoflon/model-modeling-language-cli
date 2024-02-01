package de.nexus.mmlcli.constraint.adapter.codegen;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public abstract class TemporaryFileObject extends SimpleJavaFileObject {
    private final String sourceCode;

    public TemporaryFileObject(String name, String sourceCode) {
        super(URI.create("file:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
        this.sourceCode = sourceCode;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceCode;
    }

    @Override
    public OutputStream openOutputStream() {
        throw new IllegalStateException();
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(sourceCode.getBytes());
    }
}
