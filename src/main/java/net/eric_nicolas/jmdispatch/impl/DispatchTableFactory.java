package net.eric_nicolas.jmdispatch.impl;

import net.eric_nicolas.jmdispatch.DispatchTable;

public class DispatchTableFactory {

    public static DispatchTable build(int nTypes) {
        switch(nTypes) {
            case 1: return new DispatchTable1();
            case 2: return new DispatchTable2();
            default: return new DispatchTableN(nTypes);
        }
    }
}
