package com.heshun.dsm

import com.alibaba.fastjson.JSONException
import com.heshun.dsm.common.Constants
import com.heshun.dsm.entity.driver.DeviceDriverLoader
import com.heshun.dsm.entity.global.DataBuffer
import com.heshun.dsm.service.SystemHelper
import com.heshun.dsm.ui.ControlPanel
import com.heshun.dsm.ui.ControlPanel.OnClickListener
import com.heshun.dsm.ui.ControlPanel.OnStatusChangeListener
import com.heshun.dsm.ui.ListPanel
import java.awt.Label
import java.awt.TextArea
import java.awt.TextField
import java.io.IOException
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JProgressBar
import javax.swing.SwingUtilities
import com.heshun.dsm.util.ELog.getInstance as g

class GateWayServerMain {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ControlPanel(object : OnClickListener {

                override fun onInit(logArea: TextArea, countTag: Label, cacheArea: TextArea, portTag: TextField, deviceArea: JList<String>, progressBar: JProgressBar, progressTag: Label) {
                    g().setOutputSource(logArea)
                    val deviceWrapper = ListPanel(deviceArea)
                    try {
                        SystemHelper.loadSystemConfig()
                        SystemHelper.initMessageListener(object : OnStatusChangeListener {
                            override fun onConnectChange() {
                                countTag.text = "online:${SystemHelper.minaAcceptor.managedSessions.size}"
                                deviceWrapper.update()
                            }

                            override fun onDataChanged() {
                                SwingUtilities.invokeLater {
                                    val sb = StringBuilder("缓冲区数据:")
                                    with(DataBuffer.getInstance().buffer) {
                                        sb.append(size).append("\r\n")
                                        this.forEach { t, u ->
                                            sb.append(t).append(":").append(u.size).append("\r\n")
                                        }
                                    }
                                    cacheArea.text = sb.toString()
                                }
                            }

                            override fun onProgressChange(current: Int, total: Int) {
                                progressBar.value = (current.toFloat() / total.toFloat() * 100).toInt()
                                progressTag.text = "$current / $total"
                            }

                        })
                    } catch (e: IOException) {
                        g().err("开启Tcp监听失败，请检查 ${Constants.TCP_ACCEPTOR_PORT} 端口是否被占用")
                        e.printStackTrace()
                    } catch (e: JSONException) {
                        g().err("json解析错误，请检查配置文件格式是否正确")
                        e.printStackTrace()
                    }

                }

                /*
                 * 启动按钮
                 */
                override fun onStart() {

                    /*
                     * 开启udp广播自身ip,等待从机的tcp连接
                     */
                    SystemHelper.start()
                }

                /*
                 * 停止按钮
                 */
                override fun onStop() {
                    System.exit(0)
                }

                /*
                 * 清空缓存按钮
                 */
                override fun onFlush() {
                    //                SystemHelper.mQueryTask.flush();
                    with(JOptionPane.showInputDialog(null, "型号", "清除设备驱动缓存", 1)) {
                        if (!isEmpty()) {
                            val unload = DeviceDriverLoader.unload(this)
                            g().log("清除设备驱动 [$this] 缓存-->>  $unload ")
                        }
                    }

                }

                override fun onReset() {}

            }).open()
        }


    }
}
