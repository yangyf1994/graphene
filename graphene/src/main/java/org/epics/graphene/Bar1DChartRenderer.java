/**
 * Copyright (C) 2012 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.graphene;

import java.awt.*;

/**
 *
 * @author carcassi
 */
public class Bar1DChartRenderer extends Graph2DRenderer<Bar1DChartRendererUpdate> {

    public Bar1DChartRenderer(int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
    }

    @Override
    public Bar1DChartRendererUpdate newUpdate() {
        return new Bar1DChartRendererUpdate();
    }

    public void draw(Graphics2D graphics, Cell1DDataset dataset) {
        
        Color backgroundColor = Color.WHITE;
        Color dividerColor = new Color(196, 196, 196);
        Color lineColor = new Color(140, 140, 140);
        Color histogramColor = new Color(175, 175, 175);
        
        calculateRanges(dataset.getXRange(), dataset.getStatistics());
        drawBackground(graphics);
        drawAxis(graphics);

        // Compute bin limits
        int[] binLimitsPx = new int[dataset.getXCount() + 1];
        int[] binHeightsPx = new int[dataset.getXCount()];
        
        for (int i = 0; i < dataset.getXCount(); i++) {
            binLimitsPx[i] = (int) scaledX(dataset.getXBoundaries().getDouble(i));
            binHeightsPx[i] = (int) scaledY(dataset.getValue(i));
        }
        binLimitsPx[dataset.getXCount()] = (int) scaledX(dataset.getXBoundaries().getDouble(dataset.getXCount()));
        
        // Draw histogram area
        int plotStart = (int) scaledY(getYPlotRange().getMinimum().doubleValue());
        for (int i = 0; i < binHeightsPx.length; i++) {
            graphics.setColor(histogramColor);
            graphics.fillRect(binLimitsPx[i], binHeightsPx[i], binLimitsPx[i+1] - binLimitsPx[i], plotStart - binHeightsPx[i]);
            graphics.setColor(dividerColor);
            // Draw the divider only if the vertical size is more than 0
            if ((plotStart - binHeightsPx[i]) > 0) {
                graphics.drawLine(binLimitsPx[i], binHeightsPx[i], binLimitsPx[i], plotStart);
            }
        }
        
        // Draw horizontal reference lines
        graphics.setColor(backgroundColor);
        drawHorizontalReferenceLines(graphics);
        
        // Draw histogram contour
        int previousHeight = plotStart;
        for (int i = 0; i < binHeightsPx.length; i++) {
            graphics.setColor(lineColor);
            graphics.drawLine(binLimitsPx[i], previousHeight, binLimitsPx[i], binHeightsPx[i]);
            graphics.drawLine(binLimitsPx[i], binHeightsPx[i], binLimitsPx[i+1], binHeightsPx[i]);
            previousHeight = binHeightsPx[i];
        }
        if (previousHeight > 0)
            graphics.drawLine(binLimitsPx[binLimitsPx.length - 1], previousHeight, binLimitsPx[binLimitsPx.length - 1], plotStart);
        
    }

}
