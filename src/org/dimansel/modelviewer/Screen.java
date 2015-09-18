package org.dimansel.modelviewer;

import org.dimansel.math3d.Vertex3D;
import org.dimansel.projection3d.Camera;
import org.dimansel.projection3d.Model;
import org.dimansel.projection3d.OBJModelLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.ArrayList;

public class Screen extends JPanel {
    protected Camera cam;
    protected ArrayList<Model> models;
    private int width, height;
    protected boolean mouseGrabbed = false;

    public Screen(int width, int height) {
        setSize(width, height);
        setBackground(Color.BLACK);
        this.width = width;
        this.height = height;

        init();
    }

    private void init() {
        models = new ArrayList<>();
        cam = new Camera(width, height, 70, 0.1, 100);

        Model model1 = OBJModelLoader.load("D:\\JetBrains\\Models\\suzanne.obj", 1);
        model1.color = new Color(35, 255, 0);
        model1.position = new Vertex3D(0, 0, 5);
        models.add(model1);
    }

    protected void projectVertices() {
        for (Model m : models) {
            m.projectVertices(cam, cam.pos);
        }
    }

    protected void setShadingMode() {
        for (Model m : models) {
            m.gouraudShading = !m.gouraudShading;
        }
    }

    private void renderModels(Graphics g) {
        BufferedImage frameBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] data = ((DataBufferInt)frameBuffer.getRaster().getDataBuffer()).getData();
        double[] zbuffer = new double[width*height];
        Arrays.fill(zbuffer, 100);

        for (Model m : models) {
            m.Render(data, zbuffer, width);
        }

        g.drawImage(frameBuffer, 0, 0, this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        renderModels(g);
    }
}
