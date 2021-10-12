package com.huwei.check;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;


/**
 * Created by huwei on 2021/10/8.
 */
public class AFrame  implements ActionListener {
    JFrame frame = new JFrame("算账辅助工具");//
    Container con = new Container();//
    JLabel label1 = new JLabel("大赢家扣点");
    JComboBox<String> comboBox = new JComboBox<>();
    JLabel label3 = new JLabel("点数金额");
    JComboBox<String> comboBox2 = new JComboBox<>();
    JLabel label2 = new JLabel("选择文件");
    JTextField text2 = new JTextField();// 文件的路径
    JTextArea text3 = new JTextArea();// 输出内容
    JButton button2 = new JButton("...");// 选择
    JFileChooser jfc = new JFileChooser();// 文件选择器
    JButton button3 = new JButton("算账");//
    JButton button4 = new JButton("导出");//

    File f = null;
    AFrame() {
        jfc.setCurrentDirectory(new File("./"));// 文件选择器的初始目录定为d盘

        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        Font f = new Font("隶书",Font.PLAIN,20);
        frame.setLocation(new Point((int) (lx / 2) - 150, (int) (ly / 2) - 150));// 设定窗口出现位置
        frame.setSize(750, 600);// 设定窗口大小
//        frame.setResizable(false);

        //大赢家设置
        label1.setBounds(10,10,100,30);
        label1.setFont(f);
        comboBox.addItem("10");
//        comboBox.addItem("15");
        comboBox.addItem("20");
        comboBox.setBounds(10,45,100,30);
        comboBox.setFont(f);
        con.add(label1);
        con.add(comboBox);

        //点数配置
        label3.setBounds(10,90,100,30);
        label3.setFont(f);
        comboBox2.addItem("0.1元");
        comboBox2.addItem("0.2元");
        comboBox2.addItem("0.5元");
        comboBox2.addItem("1元");
        comboBox2.addItem("2元");
        comboBox2.setBounds(10,125,100,30);
        comboBox2.setFont(f);
        con.add(label3);
        con.add(comboBox2);

//        frame.setContentPane(tabPane);// 设置布局
        label2.setBounds(10, 170, 100, 30);
        text2.setBounds(10, 205, 120, 30);
        button2.setBounds(130, 205, 50, 30);
        button2.setFont(f);
        text2.setFont(f);
        button3.setBounds(10, 240, 100, 30);
        button3.setFont(f);
        button4.setBounds(10, 280, 100, 30);
        button4.setFont(f);
        button2.addActionListener(this); // 添加事件处理
        button3.addActionListener(this); // 添加事件处理
        button4.addActionListener(this); // 添加事件处理
        label2.setFont(f);
        button2.setFont(f);
        con.add(button2);
        con.add(button3);
        con.add(button4);
//        button3.setFont(f);
        con.add(label2);
        con.add(text2);

        //数据展示
        text3.setBounds(200,10,800,800);
        text3.setFont(f);
        text3.setEditable(false);
        con.add(text3);
        frame.setVisible(true);// 窗口可见
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 使能关闭窗口，结束程序
//        tabPane.add("功能", con);// 添加布局1
        frame.add(con);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 绑定到选择文件，先择文件事件
        if (e.getSource().equals(button2)) {
            jfc.setFileSelectionMode(0);// 设定只能选择到文件
            int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
            if (state == 1) {
                return;// 撤销则返回
            } else {
                f = jfc.getSelectedFile();// f为选择到的文件
                text2.setText(f.getName());
            }
        }
        if (e.getSource().equals(button3)) {
            text3.setText("");
            if (f==null){
                JOptionPane.showMessageDialog(null,"未选择文件","提示",2);
            }else{
//                JOptionPane.showMessageDialog(null,"正在解析","提示",2);
                fileAnalysis();
            }
        }
        if (e.getSource().equals(button4)) {
            String text = text3.getText();
            if (text==null){
                JOptionPane.showMessageDialog(null,"没有计算结果","提示",2);
            }else{
                Date time = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                String format = formatter.format(time);
                File file = new File("./" + format + ".txt");
                try {
                    file.createNewFile();
                    FileWriter fileWritter = new FileWriter(file.getName(),false);
                    fileWritter.append(text);
                    fileWritter.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    JOptionPane.showMessageDialog(null,"导出失败","提示",2);
                }
                JOptionPane.showMessageDialog(null,"导出结束","提示",2);
            }
        }
    }

    private void fileAnalysis() {
        BigDecimal ii = new BigDecimal(comboBox2.getSelectedItem().toString().replace("元",""));
        Integer maxWin = new Integer(comboBox.getSelectedItem().toString());
        ExcelReader reader = ExcelUtil.getReader(f);
        List<List<Object>> read = reader.read();
        for (Object ss : read.get(0)) {
            if (!ss.equals("游戏: 悠闲山西麻将")){
                JOptionPane.showMessageDialog(null,"数据文件内容格式不正确","提示",2);
                return;
            }
        }

        Map<String, Integer> map = new HashMap<>();
        int size = read.get(0).size();
        text3.append("共"+size+"局游戏 \n");
        text3.append("用户名称\t\t点数\t金额\n");
        for (int i = 0; i < size; i++) {
            String value1 = (String)read.get(6).get(i);//第1个人
            String value2 = (String)read.get(7).get(i);//第2个人
            String value3 = (String)read.get(8).get(i);//第3个人
            String value4 = (String)read.get(9).get(i);//第4个人
            Map<String, Integer> map1 = new HashMap<>();
            String[] s1 = value1.split("  ");
            map1.put(s1[1],new Integer(s1[0]));
            String[] s2 = value2.split("  ");
            map1.put(s2[1],new Integer(s2[0]));
            String[] s3 = value3.split("  ");
            map1.put(s3[1],new Integer(s3[0]));
            String[] s4 = value4.split("  ");
            map1.put(s4[1],new Integer(s4[0]));

            //大赢家计算
            String max = s1[1];
            Integer maxInt = 0;
            String max2 = "";
            for (String s:map1.keySet()){
                Integer integer = map1.get(s);
                if (integer>maxInt){
                    max2 = "";
                    max = s;
                    maxInt = integer;
                }else if (integer.equals(maxInt)){
                    max2 = s;
                }
            }

            if (max2.equals("")){
                map1.put(max,maxInt-maxWin);
            }else{
                map1.put(max,maxInt-maxWin/2);
                map1.put(max2,maxInt-maxWin/2);
            }

            for (String s:map1.keySet()){
                if (map.containsKey(s)){
                    map.put(s,map.get(s)+map1.get(s));
                }else{
                    map.put(s,map1.get(s));
                }
            }
        }
        for (String s:map.keySet()){
            text3.append(s);
            text3.append(s.length()>8?"\t":"\t\t");
            text3.append(map.get(s)+"\t");
            text3.append(ii.multiply(new BigDecimal(map.get(s)))+"\n");
        }

        text3.append("总收入\t\t"+size*maxWin+"\t"+ii.multiply(new BigDecimal(size*maxWin)));

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new AFrame();
    }
}
