package de.nexus.modelserver;

import java.util.List;

public class DisablingFixContainer extends FixContainer {
    public DisablingFixContainer(String fixTitle, List<FixStatement> statements, boolean emptyMatchFix) {
        super(fixTitle, statements, emptyMatchFix);
    }
}
