package com.demo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;

public class App extends JFrame {

    private static final Multi<Long> publisher = Multi.createFrom()
            .items(() -> LongStream.range(0, Long.MAX_VALUE).boxed())
            .emitOn(Infrastructure.getDefaultWorkerPool());

    public static boolean isLongJob;

    private final IntegerTextField tfPush = new IntegerTextField();
    private final JButton btnPush = new JButton("Push");
    private final JButton btnPop = new JButton("Pop");
    private final JCheckBox chbLongJob = new JCheckBox("Long job");
    private final Box container = Box.createVerticalBox();
    private final List<SubscriberLine> subscriberLines = new ArrayList<>();

    public App() {
        initializeGUI();
        initializeListeners();
    }

    public static void main(String[] args) {
        App app = new App();

        app.setTitle("Flux Réactifs");
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setSize(new Dimension(500, 700));
        app.setLocationRelativeTo(null);
        app.setVisible(true);
    }

    private void initializeGUI() {
        setLayout(new GridBagLayout());

        Box top = Box.createHorizontalBox();
        top.add(Box.createHorizontalGlue());
        top.add(tfPush);
        top.add(Box.createHorizontalStrut(5));
        top.add(btnPush);
        top.add(Box.createHorizontalStrut(5));
        top.add(btnPop);
        top.add(Box.createHorizontalStrut(5));
        top.add(chbLongJob);
        top.add(Box.createHorizontalGlue());

        add(top, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        add(container, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0));
    }

    private void initializeListeners() {
        btnPush.addActionListener(e -> onBtnPushPressed());
        btnPop.addActionListener(e -> onBtnPopPressed());
        chbLongJob.addActionListener(e -> onChbLongJobPressed());
    }

    private void onBtnPopPressed() {
        if (subscriberLines.isEmpty()) {
            return;
        }

        SubscriberLine subscriberLine = subscriberLines.remove(subscriberLines.size() - 1);
        subscriberLine.cancel();
        container.remove(subscriberLine);
        container.revalidate();
        repaint();
    }

    private void onBtnPushPressed() {
        SubscriberLine subscriberLine = new SubscriberLine(publisher, tfPush.getLongValue());
        container.add(subscriberLine);
        container.revalidate();
        repaint();
        subscriberLines.add(subscriberLine);
    }

    private void onChbLongJobPressed() {
        isLongJob = chbLongJob.isSelected();
    }

}
