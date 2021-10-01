package com.demo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
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
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.subscription.Cancellable;

public class App extends JFrame {

    private static final Multi<Long> multi = Multi.createFrom().items(() -> LongStream.range(0, Long.MAX_VALUE).boxed())
            .emitOn(Infrastructure.getDefaultWorkerPool());
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
        subscriberLine.subscription.cancel();
        container.remove(subscriberLine);
        container.revalidate();
        repaint();
    }

    private static class SubscriberLine extends JPanel implements Subscriber<Long> {
        private final JLabel labelBandwidth = new JLabel();
        private final JLabel labelTotal = new JLabel();
        private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(-1, -1, 100, 1));

        private long c;
        private long total;

        private Subscription subscription;
        private long timestamp = System.currentTimeMillis();

        public SubscriberLine() {
            initializeGUI();
            multi.subscribe().withSubscriber(this);
            spinner.addChangeListener(e -> request());
        }

        private void initializeGUI() {
            setLayout(new GridBagLayout());
            add(labelBandwidth, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START,
                    GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
            add(labelTotal, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START,
                    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
            add(spinner, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                    GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
        }

        private void request() {
            int value = (int) spinner.getValue();
            if (value < 0)
                this.subscription.request(1);
            else if (value != 0)
                this.subscription.request(value);
        }

        @Override
        public void onSubscribe(Subscription s) {
            this.subscription = s;
            request();
        }

        @Override
        public void onNext(Long t) {
            long now = System.currentTimeMillis();
            c++;
            total++;

            // while (now - timeStamp < 1000) {
            // now = System.currentTimeMillis();
            // }

            // System.out.println(Thread.currentThread());

            DecimalFormat df = new DecimalFormat("##,##,##,##,##,##,##0");
            labelTotal.setText("Total : " + df.format(total));

            if (now - timestamp > 1000) {
                labelBandwidth.setText("Items/s : " + df.format(c));
                c = 0;
                timestamp = now;
            }

            int value = (int) spinner.getValue();
            if (value < 0)
                this.subscription.request(1);
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onComplete() {
        }
    }
}