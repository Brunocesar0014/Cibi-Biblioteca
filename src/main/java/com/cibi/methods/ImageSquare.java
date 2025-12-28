package com.cibi.methods;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class ImageSquare extends JComponent {

    private Icon icon;

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        repaint();   // Redesenha quando a imagem muda
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        
        if (icon != null) {
            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height);

            // Centraliza o quadrado
            int x = width / 2 - size / 2;
            int y = height / 2 - size / 2;

            // Ajusta a imagem proporcionalmente ao quadrado
            Dimension imgSize = getAutoSize(icon, size);

            // Cria imagem para redimensionar com qualidade
            BufferedImage buffer = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2_img = buffer.createGraphics();
            g2_img.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2_img.drawImage(toImage(icon), 0, 0, imgSize.width, imgSize.height, null);
            g2_img.dispose();

            // Desenha no componente
            Graphics2D g2 = (Graphics2D) grphcs;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(buffer, x, y, null);
        }
    }

    private Dimension getAutoSize(Icon image, int size) {
        int iw = image.getIconWidth();
        int ih = image.getIconHeight();

        double scale = (double) size / Math.max(iw, ih);

        int newW = (int) (iw * scale);
        int newH = (int) (ih * scale);

        return new Dimension(newW, newH);
    }

    private Image toImage(Icon icon) {
        return ((ImageIcon) icon).getImage();
    }
}
