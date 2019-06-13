package com.heshun.dsm.ui;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.mina.core.session.IoSession;

import com.heshun.dsm.service.SystemHelper;
import com.heshun.dsm.util.SessionUtils;

public class ListPanel extends JFrame {

    private static final long serialVersionUID = 1L;

    private long clickTime = 0;
    private JPopupMenu jpm;
    private DefaultListModel<String> dlm = new DefaultListModel<>();// 动态内容
    private JList<String> jList;// 定义列表框
    private int currentPoint;// 当前高亮的item
    // private IoSession currentSession;
    private Map<Long, IoSession> sessions;
    private Long[] mapIndex;// 辅助定位session

    public ListPanel(JList<String> jList) throws HeadlessException {
        this.jList = jList;

        jpm = new JPopupMenu();
        // 初始化右键Menu
        initPopMenu();

        initJListPanel();
    }

    public void update() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                dlm.clear();
                StringBuffer sb;
                sessions = SystemHelper.minaAcceptor.getManagedSessions();
                int index = 0;
                mapIndex = new Long[sessions.size()];
                for (Entry<Long, IoSession> entry : sessions.entrySet()) {
                    sb = new StringBuffer();
                    IoSession session = entry.getValue();
                    int logtype = session.getAttribute("logotype") == null ? -1 : (int) session
                            .getAttribute("logotype");
                    sb.append(entry.getKey()).append("#").append(SessionUtils.getIpFromSession(session)).append(":")
                            .append(SessionUtils.getPortFromSession(session)).append("-->")
                            .append(logtype);
                    dlm.addElement(sb.toString());
                    mapIndex[index++] = entry.getKey();
                }
            }
        });
    }

    /**
     * JList入口
     *
     * @return
     */
    public void initJListPanel() {
        jList.setModel(dlm);

        jList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // 右击事件
                if (e.getButton() == 3) {
                    currentPoint = jList.locationToIndex(e.getPoint());
                    jList.setSelectedIndex(currentPoint);// 本条高亮
                    jpm.show(jList, e.getX(), e.getY());
                }
                // 双击事件
                if (System.currentTimeMillis() - clickTime < 500) {
                    int index;
                    index = jList.locationToIndex(e.getPoint());
                    new FrameCenter(sessions.get(mapIndex[currentPoint])).detailFrame().setVisible(true);
                    System.out.println("" + dlm.get(index));
                }
                clickTime = System.currentTimeMillis();
            }
        });

    }

    /**
     * 初始化右键菜单
     */
    public void initPopMenu() {
        JMenuItem item = new JMenuItem("查看设备");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println(String.format("查看%s的设备", dlm.getElementAt(currentPoint)));
            }
        });
        jpm.add(item);

        item = new JMenuItem("踢出管理机");
        // 踢出事件
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getModifiers() == 16) {// 左击
                    int action_id = JOptionPane.showConfirmDialog(null, "确定踢出?", "警告", JOptionPane.WARNING_MESSAGE);// i=0/1
                    if (action_id == 0) {// 确定后执行
                        dlm.remove(currentPoint);
                        jList.setModel(dlm);
                        IoSession s = sessions.get(mapIndex[currentPoint]);// 关闭连接
                        s.getWriteRequestQueue().clear(s);
                        s.closeNow();
                        System.out.println(e.getActionCommand());
                    }
                }
            }
        });
        jpm.add(item);// 加入设备

        item = new JMenuItem("添加设备");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getModifiers() == 16) {
                    System.out.println(e.getActionCommand());
                }
            }
        });
        jpm.add(item);
    }
}