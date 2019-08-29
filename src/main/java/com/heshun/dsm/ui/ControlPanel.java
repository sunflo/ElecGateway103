package com.heshun.dsm.ui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import com.heshun.dsm.common.Constants;
import com.heshun.dsm.util.ELog;

import static java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY;

public class ControlPanel {
    // static ControlPanel window = null;
    private JFrame mFrame;
    private JPanel mPanel = new JPanel();
    // java.awt.Label contentLable = new java.awt.Label();
    private Button mBtnStart = new Button();
    private Button mBtnStop = new Button();
    private Button mBtnClear = new Button();
    private Button mBtnLock = new Button();
    private Button mBtnFlush = new Button();
    private TextArea mTvLogs = new TextArea("", 0, 0, SCROLLBARS_VERTICAL_ONLY);
    // private TextArea mTvDevices = new TextArea();
    private TextArea mTvDatas = new TextArea();
    //
    private JList<String> mJList = new JList<>();
    private JScrollPane mScrollPane = new JScrollPane(mJList);
    private TextField mTfPort = new TextField();

    private JProgressBar mProgressBar = new JProgressBar();
    private Label mProgressCount = new Label();

    private Label mLbCount = new Label();
    private OnClickListener clickListener;

    // private boolean isStart = false;

    private boolean isLock = false;

    public void open() {
        try {
            mFrame.setTitle(String.format("DSM数据采集网关  %s ====>%s", Constants.GATEWAY_VERSION, new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm").format(new Date())));
            mFrame.setResizable(false);
            mFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the application
     */
    public ControlPanel(OnClickListener clickListener) {
        this.clickListener = clickListener;
        initialize();
        clickListener.onInit(mTvLogs, mLbCount, mTvDatas, mTfPort, mJList, mProgressBar, mProgressCount);
    }

    /**
     * Initialize the contents of the frame
     */
    private void initialize() {

        mFrame = new JFrame() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void processWindowEvent(WindowEvent e) {
                if (e.getID() != WindowEvent.WINDOW_CLOSING)
                    super.processWindowEvent(e);
            }
        };
        mFrame.getContentPane().setForeground(new Color(255, 255, 255));

        mFrame.setBounds(700, 400, 700, 510);// 窗口居中
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Call panel
        mPanel.setName("Call");
        mPanel.setLayout(null);
        mFrame.add(mPanel);
        mPanel.setBounds(500, 0, 500, 0);
        mPanel.setBackground(new Color(250, 250, 250));

        mBtnStart.setLabel("start");
        mBtnStart.setBackground(new Color(64, 147, 77));
        mPanel.add(mBtnStart);
        mBtnStart.setBounds(10, 20, 50, 30);

        mBtnStop.setLabel("stop");
        mBtnStop.setBackground(new Color(176, 196, 222));
        mPanel.add(mBtnStop);
        mBtnStop.setBounds(10, 60, 50, 30);

        mBtnClear.setLabel("clear");
        mBtnClear.setBackground(new Color(176, 196, 222));
        mPanel.add(mBtnClear);
        mBtnClear.setBounds(10, 100, 50, 30);

        mBtnLock.setLabel("lock");
        mBtnLock.setBackground(new Color(176, 196, 222));
        mPanel.add(mBtnLock);
        mBtnLock.setBounds(10, 140, 50, 30);

        mBtnFlush.setLabel("flush");
        mBtnFlush.setBackground(new Color(176, 196, 222));
        mPanel.add(mBtnFlush);
        mBtnFlush.setBounds(10, 180, 50, 30);

        mTvLogs.setBounds(70, 10, 600, 340);
        mPanel.add(mTvLogs);

        // mTvDevices.setBounds(70, 360, 380, 100);
        // mPanel.add(mTvDevices);

        /**
         * 改动
         */
        mScrollPane.setBounds(70, 360, 380, 100);
        mPanel.add(mScrollPane);

        mTvDatas.setBounds(460, 360, 210, 100);
        mPanel.add(mTvDatas);

        // mTfPort.setBounds(10, 430, 50, 30);
        // mTfPort.setText("9114");
        // mPanel.add(mTfPort);

        mLbCount.setBounds(620, 455, 100, 30);
        mLbCount.setText("online:0");
        mPanel.add(mLbCount);
        //
        mProgressCount.setBounds(530, 465, 630, 10);
        mProgressCount.setText("0/0");
        mPanel.add(mProgressCount);
        //
        mProgressBar.setBounds(70, 470, 450, 5);
        mPanel.add(mProgressBar);
        //
        JavaPhoneMouse aJavaPhoneMouse = new JavaPhoneMouse();
        mBtnStart.addMouseListener(aJavaPhoneMouse);
        mBtnStop.addMouseListener(aJavaPhoneMouse);
        mBtnClear.addMouseListener(aJavaPhoneMouse);
        mBtnLock.addMouseListener(aJavaPhoneMouse);
        mBtnFlush.addMouseListener(aJavaPhoneMouse);

    }

    // 事件監聽
    class JavaPhoneMouse extends java.awt.event.MouseAdapter {
        public void mouseClicked(java.awt.event.MouseEvent event) {
            Object object = event.getSource();
            if (object == mBtnStart) {
                clickListener.onStart();

            }
            if (object == mBtnStop) {
                clickListener.onStop();
            }
            if (object == mBtnClear) {
                mTvLogs.setText("");
            }
            if (object == mBtnLock) {

                isLock = !isLock;
                mBtnLock.setLabel(isLock ? "unLock" : "lock");
                if (isLock) {
                    ELog.getInstance().setOutputSource(null);
                } else {
                    ELog.getInstance().setOutputSource(mTvLogs);
                }
            }
            if (object == mBtnFlush) {
                clickListener.onFlush();
            }
        }
    }

    public interface OnClickListener {

        void onInit(TextArea contentText, Label countLabel, TextArea mTvDatas, TextField mTfPort, JList<String> mJList, final JProgressBar pb,
                    final Label pl);

        void onStart();

        void onStop();

        void onFlush();

        void onReset();

    }

    public interface OnStatusChangeListener {
        void onConnectChange();

        void onDataChanged();

        void onProgressChange(int current, int total);
    }
}