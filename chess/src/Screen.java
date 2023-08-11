import java.awt.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class Screen extends JFrame implements ActionListener{
    public static void createWindow(){
        JFrame frame = new JFrame("Focking Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Box components = Box.createVerticalBox();
        frame.add(components);

        JLabel textLabel = new JLabel("hello there", SwingConstants.CENTER);
        textLabel.setPreferredSize(new Dimension(400,400));
        frame.getContentPane().add(textLabel,BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
    static JLabel i;
    @Override
    public void actionPerformed(ActionEvent e) {
       Screen s = new Screen();
        System.out.println(e);
        JButton b1 = new JButton("save dialog");
        b1.addActionListener(s);
        JPanel p = new JPanel();
        p.add(b1);
        p.add(i);
    }
}
