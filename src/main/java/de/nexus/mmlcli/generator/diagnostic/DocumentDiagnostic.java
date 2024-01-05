package de.nexus.mmlcli.generator.diagnostic;

public class DocumentDiagnostic {
    private String message;
    private DocumentRange range;
    private int severity;
    private String code;
    private String source;

    public String getMessage() {
        return message;
    }

    public DocumentRange getRange() {
        return range;
    }

    public int getSeverity() {
        return severity;
    }

    public String getCode() {
        return code;
    }

    public String getSource() {
        return source;
    }
}
