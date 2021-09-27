package com.demo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import io.smallrye.mutiny.Multi;

public class App extends JFrame {

    private static final Multi<Long> mutli = Multi.createFrom()
            .items(() -> LongStream.range(0, Long.MAX_VALUE).boxed());
    // .ticks().every(Duration.ofMillis(100));

    private final JButton btnPush = new JButton("Push");
    private final JButton btnPop = new JButton("Pop");
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
        top.add(btnPush);
        top.add(Box.createHorizontalStrut(5));
        top.add(btnPop);
        top.add(Box.createHorizontalGlue());

        add(top, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 5), 0, 0));
        add(container, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0));
    }

    private void initializeListeners() {
        btnPush.addActionListener(e -> onBtnPushPressed());
        btnPop.addActionListener(e -> onBtnPopPressed());
    }

    private void onBtnPushPressed() {
        SubscriberLine subscriberLine = new SubscriberLine();
        container.add(subscriberLine);
        container.revalidate();
        repaint();
        subscriberLines.add(subscriberLine);
    }

    private void onBtnPopPressed() {
        if (subscriberLines.isEmpty())
            return;
        SubscriberLine subscriberLine = subscriberLines.remove(subscriberLines.size() - 1);
        container.remove(subscriberLine);
        container.revalidate();
        repaint();
    }

    private static class SubscriberLine extends JPanel {

        private final JLabel label = new JLabel("Text");
        private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(5, -1, 100, 1));

        private final AtomicLong c = new AtomicLong();

        public SubscriberLine() {
            initializeGUI();
            mutli.subscribe().with(item -> c.incrementAndGet());
        }

        private void initializeGUI() {
            setLayout(new GridBagLayout());

            add(label, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
            add(spinner, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
        }

    }

}
