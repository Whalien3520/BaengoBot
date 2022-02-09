import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.Collections;

public class Bingo {
    GraphicsEnvironment ge;
    BufferedImage image;
    String[] arr;
    Graphics g;
    int failCell;
    int[] numRows;

    Bingo (String[] r) {
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        arr=Arrays.copyOfRange(r, 1, r.length);
        Collections.shuffle(Arrays.asList(arr));
        numRows = new int[25];
    }

    public int failCell() {return failCell;}

    public boolean writeImage() {
        try {ImageIO.write(image, "png", new File("output.png"));}
        catch (IOException e) {return false;}
        return true;
    }

    public boolean getRushia(String s) {
        try {image = ImageIO.read(new File(s));}
        catch (IOException e) {return false;}
        g=image.getGraphics();
        g.setColor(Color.BLACK);
        return true;
    }

    public boolean getFont(String s) {
        Font font;
        try {font = Font.createFont(Font.TRUETYPE_FONT, new File(s)).deriveFont(200f);}
        catch (IOException | FontFormatException e) {return false;}
        ge.registerFont(font);
        g.setFont(new Font(font.getName(), font.getStyle(), 200));
        return true;
    }

    public boolean generateBoard() {
        for(int i = 0; i < 5; i++)
            for(int j=0; j < 5; j++) {
                if(arr[i*5+j].equals("")) {
                    failCell=i*5+j;
                    return false;
                }
                g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 200));
                String[] cell = arr[i*5+j].split(" ");
                if(!setFontSize(cell, i*5+j)) {
                    failCell = i*5+j;
                    return false;
                }
                AttributedString attCell = new AttributedString(arr[i*5+j]);
                attCell.addAttribute(TextAttribute.FONT, g.getFont());
                AttributedCharacterIterator attIter = attCell.getIterator();
                Graphics2D g2d = (Graphics2D)g;
                FontRenderContext frc = g2d.getFontRenderContext();
                LineBreakMeasurer lbm = new LineBreakMeasurer(attIter, frc);
                float breakWidth = 160;
                float drawPosY = 160 * j + (160 - (numRows[i*5 + j] - 1) * g2d.getFontMetrics().getAscent()) / 2;
                lbm.setPosition(0);
                boolean offset = false;
                while(lbm.getPosition() < arr[i*5+j].length())
                {
                    TextLayout layout = lbm.nextLayout(breakWidth);
                    double drawPosX = 160.0 * i + (layout.isLeftToRight() ? 0 : breakWidth - layout.getAdvance());
                    double xOffset = (160 - layout.getBounds().getWidth()) / 2 - Math.abs((layout.getPixelBounds(null, (float)drawPosX, drawPosY).getX()) - drawPosX);
                    drawPosX += xOffset;
                    drawPosY += layout.getAscent();
                    if(!offset) {
                        drawPosY -= Math.abs((layout.getPixelBounds(null, (float) drawPosX, drawPosY).getY()) - drawPosY);
                        offset=true;
                    }
                    layout.draw(g2d, (float)drawPosX, drawPosY);
                }
            }
        return true;
    }

    private boolean setFontSize(String[] s, int c) {
        int ret=200;
        double width, nWidth;
        for (String str : s) {
            width = g.getFontMetrics().stringWidth(str);
            while (width > 160) {
                width = g.getFontMetrics().stringWidth(str);
                nWidth = g.getFontMetrics().stringWidth(new String(new char[str.length()-1]).replace("\0", " "));
                ret *= nWidth / width;
                g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), ret));
            }
        }
        while (ret > 1) {
            g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), ret));
            if(fits(s, c))
                return true;
            else
                ret--;
        }
        return false;
    }

    private boolean fits(String[] arr, int c) {
        int rows=1;
        StringBuilder cur= new StringBuilder(arr[0]);
        FontMetrics metrics = g.getFontMetrics();
        for(int i = 1; i < arr.length; i++) {
            if(metrics.stringWidth(cur + " " + arr[i]) < 160)
                cur.append(" ").append(arr[i]);
            else {
                cur = new StringBuilder(arr[i]);
                rows++;
            }
            if(rows * (metrics.getAscent()+metrics.getDescent()) >= 160)
                return false;
        }
        numRows[c]=rows;
        return true;
    }
}