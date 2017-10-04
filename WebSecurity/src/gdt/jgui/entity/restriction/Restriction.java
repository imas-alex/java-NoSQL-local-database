package gdt.jgui.entity.restriction;

import gdt.data.store.Entigrator;

public interface Restriction {
public boolean deny(Entigrator entigrator,String locator$);
public boolean isForced();
}
