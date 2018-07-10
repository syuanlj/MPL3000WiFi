package com.fujitsu.mpl3000wifi;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fujitsu.mpl3000.Printer;
import com.fujitsu.mpl3000.WifiCommunication;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    Button btnConn = null;
    Button btnPrint = null;
    Button btn_test = null;
    Button btnClose = null;
    Button btn_opencasher = null;
    EditText edtContext = null;

    EditText txt_ip = null;
    int connFlag = 0;
    revMsgThread revThred = null;
    //checkPrintThread cheThread = null;
    private static final int WFPRINTER_REVMSG = 0x06;

    private Printer mpl3000 = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConn =  findViewById(R.id.btn_conn);
        btnConn.setOnClickListener(new ClickEvent());
        btnPrint = findViewById(R.id.btnSend);
        btnPrint.setOnClickListener(new ClickEvent());
        btn_test = findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new ClickEvent());
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new ClickEvent());
        edtContext = findViewById(R.id.txt_content);
        txt_ip = this.findViewById(R.id.txt_ip);
        mpl3000 = new Printer(mHandler);
        btn_opencasher = (Button) this.findViewById(R.id.btn_opencasher);
        btn_opencasher.setOnClickListener(new ClickEvent());

        btnConn.setEnabled(true);
        btnPrint.setEnabled(false);
        btn_test.setEnabled(false);
        btnClose.setEnabled(false);
        btn_opencasher.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mpl3000.close();
    }

    class ClickEvent implements View.OnClickListener {
        public void onClick(View v) {
            if (v == btnConn) {
                if (connFlag == 0) {   //避免连续点击此按钮创建多个连接线程
                    connFlag = 1;
                    Log.d("wifi调试", "点击\"连接\"");
                    String strAddressIp = txt_ip.getText().toString();
                    mpl3000.initSocket(strAddressIp);
                }
            } else if (v == btnPrint) {
                String msg = edtContext.getText().toString();
                if (msg.length() > 0) {
                    mpl3000.send_str(msg);
                }
            } else if (v == btnClose) {
                mpl3000.close();
            } else if (v == btn_opencasher) {
//                byte[] tcmd = new byte[5];
//                tcmd[0] = 0x1B;
//                tcmd[1] = 0x70;
//                tcmd[2] = 0x00;
//                tcmd[3] = 0x40;
//                tcmd[4] = 0x50;
//                wfComm.sndByte(tcmd);
            } else if (v == btn_test) {
                printer3000();


            }
        }
    }


    public void printer3000() {

        mpl3000.goto_mark_left();//走做黑标定位
        mpl3000.page_creat(72.0, 90.0, 1);
        mpl3000.set_font_style(Printer.FontName.FontLocal_GBK24x24, Printer.StyleNormal);

        mpl3000.draw_line(1.0, 7.0, 71.0, 7, 2);
        mpl3000.draw_line(1, 20, 71, 20, 2);
        mpl3000.draw_line(48, 34, 71, 34, 2);
        mpl3000.draw_line(1, 41, 71, 41, 2);
        mpl3000.draw_line(1, 50, 71, 50, 2);
        mpl3000.draw_line(1, 58, 71, 58, 2);
        mpl3000.draw_line(1, 30, 48, 30, 2);
        mpl3000.draw_line(1, 80, 71, 80, 2);
        mpl3000.draw_line(48, 7, 48, 41, 2);
        mpl3000.draw_line(15, 50, 15, 58, 2);
        mpl3000.draw_line(43, 50, 43, 58, 2);
        mpl3000.draw_line(57, 50, 57, 58, 2);
        mpl3000.draw_line(41, 58, 41, 80, 2);
        mpl3000.draw_line(1, 7, 1, 80, 2);
        mpl3000.draw_line(71, 7, 71, 80, 2);
        mpl3000.draw_text(3, 23, 28, "武汉快运测试分拨", 0);
        mpl3000.draw_text(3, 33, 28, "快运武汉测试网点", 0);
        mpl3000.draw_text(2.25, 43, 28, "湖北省武汉市武昌区", 0);
        mpl3000.draw_text(42, 62.25, 28, "快运武汉测试网点", 0);
        mpl3000.draw_text(42, 68.5, 28, "2017/10/10", 0);
        mpl3000.draw_text(42, 74.5, 28, "12:22:39", 0);
        mpl3000.draw_text(9, 10, 28, "569999701", 0);
        mpl3000.draw_text(52, 11, 28, "次数" , 0);
        mpl3000.draw_text(52, 24, 28, "送货上门", 0);
        mpl3000.draw_text(49, 36, 24, "1.0kg/1.00m3", 0);
        mpl3000.draw_barcode1d(3, 60, Printer.BarcodeCODABAR, 2, 80, "5699997010001", false);
        mpl3000.draw_text(7, 72, 32, "5699997010001", 0);

        mpl3000.page_print(Printer.MarkNone);

    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case WifiCommunication.WFPRINTER_CONNECTED:
                    connFlag = 0;
                    Toast.makeText(getApplicationContext(), "Connect the WIFI-printer successful",
                            Toast.LENGTH_SHORT).show();
                    btnPrint.setEnabled(true);
                    btn_test.setEnabled(true);
                    btnClose.setEnabled(true);
                    btn_opencasher.setEnabled(true);
                    btnConn.setEnabled(false);

                    revThred = new revMsgThread();
                    revThred.start();
                    break;
                case WifiCommunication.WFPRINTER_DISCONNECTED:
                    Toast.makeText(getApplicationContext(), "Disconnect the WIFI-printer successful",
                            Toast.LENGTH_SHORT).show();
                    btnConn.setEnabled(true);
                    btnPrint.setEnabled(false);
                    btn_test.setEnabled(false);
                    btnClose.setEnabled(false);
                    btn_opencasher.setEnabled(false);
                    revThred.interrupt();
                    break;
                case WifiCommunication.SEND_FAILED:
                    connFlag = 0;
                    Toast.makeText(getApplicationContext(), "Send Data Failed,please reconnect",
                            Toast.LENGTH_SHORT).show();
                    btnConn.setEnabled(true);
                    btnPrint.setEnabled(false);
                    btn_test.setEnabled(false);
                    btnClose.setEnabled(false);
                    btn_opencasher.setEnabled(false);
                    revThred.interrupt();
                    break;
                case WifiCommunication.WFPRINTER_CONNECTEDERR:
                    connFlag = 0;
                    Toast.makeText(getApplicationContext(), "Connect the WIFI-printer error",
                            Toast.LENGTH_SHORT).show();
                    break;
                case WFPRINTER_REVMSG:
                    int  revData =  Integer.parseInt(message.obj.toString());
                    String MessageError = "";
//                    if (revData!=0)
                    if (revData==2) MessageError = "纸仓缺纸";
                    if (revData==3) MessageError = "纸仓盖开";
                    if (revData==1) MessageError = "机芯过热";
//                    if (((revData >> 6) & 0x01) == 0x01)
                    if (!MessageError.equals(""))
                        Toast.makeText(getApplicationContext(), MessageError, Toast.LENGTH_SHORT).show();
                    Log.e("状态返回：", MessageError);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    //打印机线程，连接上打印机时创建，关闭打印机时退出
    class revMsgThread extends Thread {
        @Override
        public void run() {
            try {
                Message msg = new Message();
                int revData = -1;
                int i=0;
                while (true) {
                    revData=mpl3000.get_state();//非阻塞单个字节接收数据，如需改成非阻塞接收字符串请参考手册
                    Log.e("状态返回：", revData + "");
                    if (revData != -1) {

                        msg = mHandler.obtainMessage(WFPRINTER_REVMSG);
                        msg.obj = revData;
                        mHandler.sendMessage(msg);
//                        break;
                    }else {
                        if (i==4)break;
                        i++;
                    }
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d("wifi调试", "退出线程");
            }
        }
    }
}
