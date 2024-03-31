package de.nexus.modelserver;

import java.util.List;

public class EnablingFixContainer extends FixContainer {
    public EnablingFixContainer(String fixTitle, List<FixStatement> statements, boolean emptyMatchFix) {
        super(fixTitle, statements, emptyMatchFix);
    }
}
