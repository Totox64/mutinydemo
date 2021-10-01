package com.demo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.smallrye.mutiny.Multi;

public class SubscriberLine extends JPanel implements Subscriber<Long> {

    private static final DecimalFormat DF = new DecimalFormat("##,##,##,##,##,##,##0");

    private final JLabel labelBandwidth = new JLabel();
    private final JLabel labelTotal = new JLabel();
    private final IntegerTextField tfNbRequests = new IntegerTextField();
    private final JButton btnOk = new JButton("Ok");

    private Subscription subscription;
    private long timestamp = System.currentTimeMillis();
    private long total;
    private long bandwidth;
    private long nbRequests;

    public SubscriberLine(Multi<Long> multi, long nbRequests) {
        this.nbRequests = nbRequests;
        initializeGUI();
        initializeListeners();
        tfNbRequests.setValue(nbRequests);
        multi.subscribe().withSubscriber(this);
    }

    private void autoRequest() {
        if (nbRequests < 0) {
            this.subscription.request(1);
        } else if (nbRequests != 0) {
            this.subscription.request(nbRequests);
        }
    }

    public void cancel() {
        subscription.cancel();
    }

    private void initializeGUI() {
        setLayout(new GridBagLayout());
        tfNbRequests.setPreferredSize(new Dimension(60, 30));
        add(labelBandwidth, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
        add(labelTotal, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
        add(tfNbRequests, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
        add(btnOk, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(5, 5, 0, 0), 0, 0));
    }

    private void initializeListeners() {
        btnOk.addActionListener(e -> onBtnOkPressed());
    }

    private void onBtnOkPressed() {
        nbRequests = tfNbRequests.getLongValue();
        autoRequest();
    }

    @Override
    public void onComplete() {
        // Does nothing
    }

    @Override
    public void onError(Throwable t) {
        // Does nothing
    }

    @Override
    public void onNext(Long t) {
        bandwidth++;
        total++;

        if (App.isLongJob) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        labelTotal.setText("Total : " + DF.format(total));

        long now = System.currentTimeMillis();
        if (now - timestamp > 1000) {
            labelBandwidth.setText("Items/s : " + DF.format(bandwidth));
            bandwidth = 0;
            timestamp = now;
        }

        if (nbRequests < 0) {
            this.subscription.request(1);
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        autoRequest();
    }

}