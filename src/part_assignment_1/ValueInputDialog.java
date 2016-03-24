package part_assignment_1;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import part_assignment_1.Interface;

public class ValueInputDialog extends JDialog{
	
	public int a, b, c, d;
	public boolean status = false;
	
	public ValueInputDialog(int minL, int maxL, boolean showSub_1){
		setTitle("Input parameters");
        setModal(true);
        setSize(300, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel jp;
        if (showSub_1)
        	jp = new JPanel(new GridLayout(3, 1));
        else
        	jp = new JPanel(new GridLayout(2, 1));
        JTextField textA = new JTextField();
        JTextField textB = new JTextField();
        JTextField textC = new JTextField();
        JTextField textD = new JTextField();
        jp.add(new JLabel("[Current Grey Depth] minL: " + minL + " , maxL: " + maxL));
        if (showSub_1) {
	        JPanel sub_1 = new JPanel(new GridLayout(1,4));
	        sub_1.add(new JLabel("a"));
	        sub_1.add(textA);
	        sub_1.add(new JLabel("b"));
	        sub_1.add(textB);
	        jp.add(sub_1);
        }
        
        JPanel sub_2 = new JPanel(new GridLayout(1,4));
        sub_2.add(new JLabel("c"));
        sub_2.add(textC);
        sub_2.add(new JLabel("d"));
        sub_2.add(textD);
        jp.add(sub_2);
        JButton jb = new JButton("Confirm");
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try{
	            	if (showSub_1){
	            		a = Integer.valueOf(textA.getText());
	            		b = Integer.valueOf(textB.getText());
	            	}
	                c = Integer.valueOf(textC.getText());
	                d = Integer.valueOf(textD.getText());
            	}catch(NumberFormatException ne){
            		JOptionPane.showMessageDialog(null, "Please enter valid numbers!");
            		dispose();
            		return;
            	}
            	status = true;
                dispose();
            }
        });
         
        add(jp);
        add(jb,BorderLayout.SOUTH);
        setVisible(true);
	}
	
}
