/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

/**
 *
 * @author carcassi
 */
public final class Hist1DT1 extends MockHistogram1D {

    public Hist1DT1() {
        setMinValueRange(0.0);
        setMaxValueRange(2.0);
        setMinCountRange(0);
        setMaxCountRange(550);
        setBinValueBoundary(new double[] {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0,
            1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0});
        setBinCount(new int[] {30, 14, 150, 160, 180, 230, 220, 350, 400, 450, 500,
            350, 230, 180, 220, 170, 130, 80, 30, 40});
        
    }
    
}