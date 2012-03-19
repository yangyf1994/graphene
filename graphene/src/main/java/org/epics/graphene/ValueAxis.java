/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.text.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author carcassi
 */
public class ValueAxis {
    
    private double minValue;
    private double maxValue;
    private double[] tickValues;
    private String[] tickStrings;

    public ValueAxis(double minValue, double maxValue, double[] tickValues, String[] tickStrings) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.tickValues = tickValues;
        this.tickStrings = tickStrings;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double[] getTickValues() {
        return tickValues;
    }

    public String[] getTickLabels() {
        return tickStrings;
    }

    public static ValueAxis createAutoAxis(double minValue, double maxValue, int maxTicks) {
        return createAutoAxis(minValue, maxValue, maxTicks, Double.MIN_VALUE);
    }
    
    private static final DecimalFormat defaultFormat = new DecimalFormat("0.###");
    
    private static final DecimalFormat decimal0 = new DecimalFormat("0");
    private static final DecimalFormat decimal1 = new DecimalFormat("0.0");
    private static final DecimalFormat decimal2 = new DecimalFormat("0.00");
    
    private static final Map<Integer, DecimalFormat> formats = new ConcurrentHashMap<Integer, DecimalFormat>();
    
    static DecimalFormat formatWithFixedSignificantDigits(int significantDigits) {
        DecimalFormat result = formats.get(significantDigits);
        if (result == null) {
            StringBuilder pattern = new StringBuilder("0");
            for (int i = 0; i < significantDigits; i++) {
                if (i == 0) {
                    pattern.append(".");
                }
                pattern.append("0");
            }
            result = new DecimalFormat(pattern.toString());
            formats.put(significantDigits, result);
        }
        return result;
    }
    
    public static ValueAxis createAutoAxis(double minValue, double maxValue, int maxTicks, double minIncrement) {
        double increment = incrementForRange(minValue, maxValue, maxTicks, minIncrement);
        double[] ticks = createTicks(minValue, maxValue, increment);
        int rangeOrder = (int) orderOfMagnitude(minValue, maxValue);
        int incrementOrder = (int) orderOfMagnitude(increment);
        int nDigits = rangeOrder - incrementOrder;
        
        // The format will decide how many significant digit to show
        DecimalFormat format = defaultFormat;
        // The normalization and the exponent will need to agree and
        // decide what order of magnitude to format the number as
        double normalization = 1.0;
        String exponent = null;
        if (rangeOrder >= -3 && rangeOrder <= 3) {
            if (incrementOrder < 0) {
                format = formatWithFixedSignificantDigits(-incrementOrder);
            } else {
                format = formatWithFixedSignificantDigits(0);
            }
        } else if (rangeOrder < -3) {
            format = formatWithFixedSignificantDigits(nDigits);
            normalization = Math.pow(10.0, rangeOrder);
            exponent = Integer.toString(rangeOrder);
        }
        
        String[] labels = new String[ticks.length];
        for (int i = 0; i < ticks.length; i++) {
            double value = ticks[i];
            labels[i] = format(value, format, exponent, normalization);
        }
        return new ValueAxis(minValue, maxValue, ticks, labels);
    }
    
    static String format(double number, DecimalFormat format, String exponent, double normalization) {
        if (exponent != null) {
            return format.format(number/normalization) + "e" + exponent;
        } else {
            return format.format(number/normalization);
        }
    }
    
    static double orderOfMagnitude(double value) {
        return Math.floor(Math.log10(value));
    }
    
    static double orderOfMagnitude(double min, double max) {
        return orderOfMagnitude(Math.max(Math.abs(max), Math.abs(min)));
    }
    
    static double incrementForRange(double min, double max, int nTicks, double minIncrement) {
        double magnitude = Math.pow(10.0, orderOfMagnitude(min, max));
        if (magnitude < minIncrement) {
            return Double.NaN;
        }
        int ticks = countTicks(min, max, magnitude);
        if (ticks > nTicks) {
            if (ticks / 2 < nTicks) {
                int newTicks = countTicks(min, max, magnitude * 5);
                if (newTicks > 2 && newTicks <= nTicks) {
                    return magnitude * 5;
                }
            }
            
            if (ticks / 5 < nTicks) {
                int newTicks = countTicks(min, max, magnitude * 2);
                if (newTicks > 2 && newTicks <= nTicks) {
                    return magnitude * 2;
                }
            }
            
            return Double.NaN;
        } else {
            double increment = magnitude;
            // Refine if there is still space to refine
            while (countTicks(min, max, increment / 2) <= nTicks) {
                if (increment / 10 >= minIncrement && countTicks(min, max, increment / 10) <= nTicks) {
                    increment /= 10;
                } else if (increment / 5 >= minIncrement && countTicks(min, max, increment / 5) <= nTicks) {
                    return increment / 5;
                } else if(increment / 2 >= minIncrement) {
                    return increment / 2;
                } else {
                    return increment;
                }
            }
            return increment;
        }
    }
    
    /**
     * Determines how many ticks would there be in that range using that increment.
     * 
     * @param min value range start
     * @param max value range end
     * @param increment space between ticks
     * @return number of ticks in the range
     */
    static int countTicks(double min, double max, double increment) {
        int start = (int) Math.ceil(min / increment);
        int end = (int) Math.floor(max / increment);
        return end - start + 1;
    }
    
    /**
     * Create values for the axis tick given the range and the increment.
     * 
     * @param min value range start
     * @param max value range end
     * @param increment space between ticks
     * @return values for the ticks
     */
    static double[] createTicks(double min, double max, double increment) {
        int start = (int) Math.ceil(min / increment);
        int end = (int) Math.floor(max / increment);
        double[] ticks = new double[end-start+1];
        for (int i = 0; i < ticks.length; i++) {
            ticks[i] = (i + start) * increment;
        }
        return ticks;
    }
    
}
