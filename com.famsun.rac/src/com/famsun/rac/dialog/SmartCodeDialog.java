package com.famsun.rac.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.teamcenter.rac.express.smartcodes.confnumgen.TcXSCConfigPanel;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.AbstractDialog;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.VerticalLayout;

public class SmartCodeDialog extends AbstractDialog
	implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TCSession tcsession;
	private NewItemDialog newitemdialog;

	private JScrollPane m_jspMaster;
	private TcXSCConfigPanel m_CNGConfPanel;
	private JButton button_OK;
	private JButton button_Cancel;
	private JTextField idTextfield;
	private JTextField idTextfield1;

	public SmartCodeDialog(Frame frame,TCSession session ,NewItemDialog dialog,String s, boolean modal) {
		super(frame, s, modal);
		// TODO Auto-generated constructor stub

		tcsession=null;
		newitemdialog=null;

		tcsession=session;
		newitemdialog=dialog;
		
		try {
	    	 initDialog();
	    }catch(TCException tcexception){

	    }
	}

	private void initDialog() throws TCException{
		// TODO Auto-generated method stub
		
		setTitle("编码申请");
        JPanel panel1 = new JPanel( new BorderLayout());	    
	    JPanel panel2 = new JPanel(new FlowLayout());
 
        idTextfield=newitemdialog.itemId_Field;
        idTextfield1=new JTextField(15);
        Document dt=idTextfield1.getDocument();
        dt.addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				if(idTextfield1.getText().length()>7 && m_CNGConfPanel.hasValidEntries()){
					button_OK.setEnabled(true);	
					
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				if(idTextfield1.getText().length()>7 && m_CNGConfPanel.hasValidEntries()){
					button_OK.setEnabled(true);	
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub

			}
        	
        });
	    
	    JLabel jlabel = new JLabel(Registry.getRegistry("com.teamcenter.rac.express.smartcodes.confnumgen.confnumgen").getString("itemID.LABEL"), Registry.getRegistry("com.teamcenter.rac.commands.newitem.newitem").getImageIcon("info.ICON"), 2);
	    jlabel.setForeground(Color.blue);
	    panel1.add(jlabel, "North");

	    m_jspMaster = new JScrollPane();
	    String selectedItemType="Item";
	    m_CNGConfPanel = new TcXSCConfigPanel(idTextfield1, this, tcsession, "Item", selectedItemType, "item_id");
	    m_CNGConfPanel.addPropertyChangeListener(this);

	    m_jspMaster.getViewport().add(m_CNGConfPanel);	    
	    panel1.add(m_jspMaster, "Center");

	    button_OK = new JButton("确定");	   
	    button_OK.setEnabled(false);			    
	    button_OK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionevent)
            {         
            	idTextfield.setText(idTextfield1.getText());
            	setVisible(false);
				dispose();
            }	           
        });
		
	    button_Cancel = new JButton("取消");
		button_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionevent) {
				setVisible(false);
				dispose();
			}
		});
	
		// 按钮面板
		panel2.add(button_OK);
		panel2.add(new JLabel(" "));
		panel2.add(button_Cancel);	

	
		//总面板
		VerticalLayout sumLayout = new VerticalLayout();
		sumLayout.setVerticalGap(10);
		sumLayout.setTopMargin(10);
		sumLayout.setBottomMargin(10);
		sumLayout.setLeftMargin(7);
		sumLayout.setRightMargin(7);
		JPanel panel = new JPanel(sumLayout);
		panel.add("top1.bind",panel1);
		panel.add("top2.bind",panel2);
		
	    getContentPane().add(panel);
		
		Dimension dimension = new Dimension(520, 350);
		this.setPreferredSize(dimension);

		pack();
		centerToScreen(1.0D, 0.5D, 0.0D, 0.5D);
	}
	

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// TODO Auto-generated method stub

		}
		
}
