/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.flexdock.plaf.resources.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;



import org.flexdock.plaf.resources.ColorResourceHandler;
import org.flexdock.plaf.resources.ResourceHandler;

/**
 * @author Christopher Butler
 */
public class SquareBevelBorderResource extends ResourceHandler {

    private static final ColorUIResource DEFAULT_COLOR = new ColorUIResource(Color.WHITE);

    @Override
    public Object getResource(String data) {
        // pattern should be "lineWidth, lightColor, darkColor"
        String[] args = getArgs(data);
        int lineWidth = args.length>0? getInt(args[0]): 1;
        ColorUIResource lightColor = args.length>1? getColor(args[1]): DEFAULT_COLOR;
        ColorUIResource darkColor = args.length>2? getColor(args[2]): DEFAULT_COLOR;

        return new SquareBevelBorder(lineWidth, lightColor, darkColor);
    }

    private int getInt(String data) {
        try {
            return Integer.parseInt(data);
        } catch(Exception e) {
            System.err.println("Exception: " +e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    private ColorUIResource getColor(String data) {
        ColorUIResource color = ColorResourceHandler.parseHexColor(data);
        return data==null? DEFAULT_COLOR: color;
    }

    public static class SquareBevelBorder implements Border {
        private int lineWidth;
        private Color light;
        private Color dark;
        private Insets insets;

        public SquareBevelBorder(int lineWidth, Color light, Color dark) {
            this.lineWidth = lineWidth;
            this.light = light;
            this.dark = dark;
            insets = new Insets(lineWidth, lineWidth, lineWidth, lineWidth);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color saved = g.getColor();

            width-=1;
            height-=1;

            for(int i=0; i<lineWidth; i++) {
                g.setColor(dark);
                g.drawLine(width-i, i, width-i, height-i);
                g.drawLine(i, height-i, width-i, height-i);

                g.setColor(light);
                g.drawLine(i, i, width-i, i);
                g.drawLine(i, i, i, height-i);
            }

            g.setColor(saved);

        }
    }
}