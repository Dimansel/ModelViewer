package org.dimansel.modelviewer;

import org.dimansel.math3d.Vertex3D;
import org.dimansel.projection3d.*;
import org.dimansel.shader3d.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.util.Arrays;

class Screen extends JPanel {
    Camera cam;
    private Model model;
    private int width, height;
    boolean mouseGrabbed = false;

    Screen(int width, int height) {
        setSize(width, height);
        setBackground(Color.BLACK);
        this.width = width;
        this.height = height;

        init();
    }

    private void init() {
        cam = new Camera(width, height, 70, 0, 100);
        IShader shader = new PhongShader(new Color(128, 142, 255));

        Reader reader;
        try {
            reader = new FileReader(MainWindow.path);
        } catch (FileNotFoundException e) {
            System.out.println("No path is specified or it's not found. Loading default model...");
            reader = new InputStreamReader(getClass().getResourceAsStream("wt_teapot.obj"));
        }

        model = OBJModelLoader.load(reader, shader);
        if (model == null) {
            System.out.print("Invalid file!");
            System.exit(1);
        }
        model.position = new Vertex3D(0, 0, 5);
    }

    void projectVertices() {
        model.projectVertices(cam, cam.pos);
    }

    private void renderModels(Graphics g) {
        BufferedImage frameBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] data = ((DataBufferInt)frameBuffer.getRaster().getDataBuffer()).getData();
        double[] zbuffer = new double[width*height];
        Arrays.fill(zbuffer, 100);
        model.Render(data, zbuffer, width);

        g.drawImage(frameBuffer, 0, 0, this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        renderModels(g);
    }
}
