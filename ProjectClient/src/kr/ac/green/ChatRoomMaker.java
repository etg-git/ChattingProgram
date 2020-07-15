package kr.ac.green;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ChatRoomMaker extends JDialog implements Serializable{
   private String title;
   private String subject;
   private String userNum;

   private JLabel lblTitle;
   private JTextField tfTitle;
   private JLabel lblSubject;
   private JLabel lblUser;
   private JCheckBox cbPassword;
   private JPasswordField pw;

   private JButton btnOk;
   private JButton btnCancel;

   private JComboBox<String> cbSubject;
   private JComboBox<String> cbUser;

   private String subjects[] = { "�ϻ��ȭ", "������", "����", "��ȭ", "��̻�Ȱ" };
   private String userCount[] = { "2��", "3��", "4��", "5��", "6��", "7��", "8��", "9��"};

   private Socket sock;
   private ObjectOutputStream oos;
   private ObjectInputStream ois;
   private String myId;

   private int maxNumbers;
   public ChatRoomMaker(Queue queue, Socket sock, ObjectOutputStream oos, ObjectInputStream ois, String myId) {
      super(queue, "�� �����", true);
      this.sock = sock;
      this.oos = oos;
      this.ois = ois;
      this.myId = myId;
      init();
      setDisplay();
      addListener();
      showFrame();
   }   
   
   public JTextField getTfTitle() {
      return tfTitle;
   }

   public void setTfTitle(JTextField tfTitle) {
      this.tfTitle = tfTitle;
   }

   public JPasswordField getPw() {
      return pw;
   }

   public void setPw(JPasswordField pw) {
      this.pw = pw;
   }

   public JComboBox<String> getCbSubject() {
      return cbSubject;
   }

   public void setCbSubject(JComboBox<String> cbSubject) {
      this.cbSubject = cbSubject;
   }

   public JComboBox<String> getCbUser() {
      return cbUser;
   }

   public void setCbUser(JComboBox<String> cbUser) {
      this.cbUser = cbUser;
   }

   
   public int getMaxNumbers() {
      return maxNumbers;
   }

   public void setMaxNumbers(int maxNumbers) {
      this.maxNumbers = maxNumbers;
   }

   private void init() {
      lblTitle = new JLabel("����");
      lblTitle.setBorder((new LineBorder(Color.BLACK, 2)));
      lblTitle.setHorizontalAlignment(JLabel.CENTER);
      lblTitle.setPreferredSize(new Dimension(60, 23));
      tfTitle = new JTextField(20);

      lblSubject = new JLabel("����");
      lblSubject.setBorder((new LineBorder(Color.BLACK, 2)));
      lblSubject.setHorizontalAlignment(JLabel.CENTER);
      lblSubject.setPreferredSize(new Dimension(60, 23));
      cbSubject = new JComboBox<String>(subjects);
      lblUser = new JLabel("�ο� ��");
      lblUser.setBorder((new LineBorder(Color.BLACK, 2)));
      lblUser.setHorizontalAlignment(JLabel.CENTER);
      lblUser.setPreferredSize(new Dimension(60, 23));
      cbUser = new JComboBox<String>(userCount);

      cbPassword = new JCheckBox("��й�ȣ");
      pw = new JPasswordField(19);
      pw.setEditable(false);

      btnOk = new JButton("Ȯ��");
      btnCancel = new JButton("���");

   }

   private void setDisplay() {

      JPanel pnlMain = new JPanel(new BorderLayout());
      pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));

      // ���û��� �г�
      JPanel pnlChoice = new JPanel(new GridLayout(0, 1));
      // ���� �κ�
      JPanel pnlTitle = new JPanel(new FlowLayout());
      pnlTitle.add(lblTitle);
      pnlTitle.add(tfTitle);
      // ����, �ο��κ�
      JPanel pnlCombo = new JPanel(new FlowLayout());
      JPanel pnlSubject = new JPanel();
      pnlSubject.add(lblSubject);
      pnlCombo.add(pnlSubject);
      pnlCombo.add(cbSubject);
      JPanel pnlUser = new JPanel();
      pnlUser.add(lblUser);
      pnlCombo.add(pnlUser);
      pnlCombo.add(cbUser);
      // ��й�ȣ ����
      JPanel pnlPassword = new JPanel(new FlowLayout());
      pnlPassword.add(cbPassword);
      pnlPassword.add(pw);

      pnlChoice.add(pnlTitle);
      pnlChoice.add(pnlCombo);
      pnlChoice.add(pnlPassword);
     
      // ��ư �г�
      JPanel pnlButton = new JPanel(new FlowLayout());
      pnlButton.add(btnOk);
      pnlButton.add(btnCancel);

      pnlMain.add(pnlChoice, BorderLayout.NORTH);
      pnlMain.add(pnlButton, BorderLayout.CENTER);

      add(pnlMain, BorderLayout.CENTER);

   }

   private void addListener() {
      cbPassword.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            if (cbPassword.isSelected()) {
               pw.setEditable(true);
            } else {
               pw.setEditable(false);
            }
         }
      });
      btnCancel.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            dispose();
         }
      });

      btnOk.addActionListener((ae) -> {
         boolean flag = true;
         if (tfTitle.getText().equals("")) {
            flag = false;
            JOptionPane.showMessageDialog(ChatRoomMaker.this, "������ �Է��ϼ���");
         }
         if (flag) {
            maxNumbers = 0;
            try {
               maxNumbers = Integer.parseInt(String.valueOf(cbUser.getSelectedItem().toString().charAt(0)));
            } catch (NumberFormatException e) {
               maxNumbers = Integer.parseInt(cbUser.getSelectedItem().toString().substring(0, 2));
            }
            try {
               oos.writeObject(new SendData(
                     OperationCodeClient.CREATE_ROOM_REQUEST, new RoomInfo(tfTitle.getText(),
                           cbSubject.getSelectedItem().toString(), maxNumbers, 1, 1, pw.getText()),
                     myId));
               oos.flush();
               oos.reset();
               
               dispose();
            } catch (Exception e1) {
               e1.printStackTrace();
            }
         }
      });
   }

   private void showFrame() {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      pack();
      setLocationRelativeTo(null);
      setResizable(false);
      setVisible(true);
   }
}