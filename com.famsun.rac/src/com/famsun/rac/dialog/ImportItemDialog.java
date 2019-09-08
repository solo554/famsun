package com.famsun.rac.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

import com.famsun.rac.operations.NewItemAndDatasetOperation;
import com.famsun.rac.util.CommonUtils;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextField;

public class ImportItemDialog extends AbstractAIFDialog
	implements InterfaceAIFOperationListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private iTextField filePathJfield;
	private iTextField folderNameJfield;
	private JButton button_Browser;
	private JProgressBar prgsBar;
	private JButton button_OK;
	private AbstractButton button_Cancel;
	private File excelFile;
	private JLabel jlabel11;
	private JLabel jlabel21;
	private JLabel jlabel31;
	private JLabel jlabel33;
	protected Map<String,List<String>> partsMap;
	public TCSession tcsession;
	protected Frame parent;
	protected InterfaceAIFComponent[] pasteTargets;
	protected AIFDesktop desktop;

			
	public ImportItemDialog(Frame frame, InterfaceAIFComponent ainterfaceaifcomponent[]){
	     super(frame, true);
	     tcsession = null;
	     desktop = null;
	     parent = null;
	  
	     tcsession = (TCSession)ainterfaceaifcomponent[0].getSession();
	     pasteTargets = ainterfaceaifcomponent;
	     parent = frame;
	     if(frame instanceof AIFDesktop)
	    	 desktop = (AIFDesktop)frame;
	     initializeDialog();
     
     }
	
	private void initializeDialog() {
		// TODO Auto-generated method stub
		setTitle("窗口测试");//窗口名称
		
		//选择excel文件的面板
		PropertyLayout propertyLayout = new PropertyLayout();
		propertyLayout.setVerticalGap(15);
		propertyLayout.setTopMargin(5);
		propertyLayout.setBottomMargin(10);
		JPanel topPane = new JPanel(propertyLayout);
		
		jlabel11 = new JLabel("选择文件:");		
		topPane.add("1.1.left", jlabel11);
		filePathJfield = new iTextField(25,true);
		filePathJfield.setBorder(new EtchedBorder());
		topPane.add("1.2.left", filePathJfield);
		button_Browser = new JButton("选择Excel文件");	 
		button_Browser.setMargin(new Insets(0, 0, 0, 0));
		button_Browser.setToolTipText("importButton.TIP");
		button_Browser.setFocusPainted(false);
		button_Browser.setEnabled(true);
		topPane.add("1.3.left", button_Browser);
		button_Browser.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent actionevent) {
				
				excelFile = CommonUtils.chooseImportFile();// 从本机上获取excel表格
				if(excelFile!=null) {
					partsMap=CommonUtils.readImportExcel(excelFile);
					if (partsMap!=null){
						button_OK.setEnabled(true);					
						filePathJfield.setText(excelFile.getAbsolutePath());
					System.out.println(excelFile.getPath());						
					} 
					else {
						MessageBox  msgBox = new MessageBox("导入的Excel文件不符合要求！","提示",MessageBox.INFORMATION);
			  	    	msgBox.setModal(true);
			  	    	msgBox.setVisible(true); 
					}
	
				}
			}
	
	    });
		
		jlabel21 = new JLabel("文件夹名称:");		
		topPane.add("2.1.left", jlabel21);
		folderNameJfield = new iTextField(25,true);
		folderNameJfield.setBorder(new EtchedBorder());;
		topPane.add("2.2.left", folderNameJfield);
		
		jlabel31 = new JLabel("  ");
		topPane.add("3.1.center", jlabel31);
		prgsBar = new JProgressBar();
		prgsBar.setStringPainted(true);		
		prgsBar.setBorderPainted(false);
		prgsBar.setBackground(new Color(0, 210, 40));
		prgsBar.setForeground(new Color(188, 190, 194));
		prgsBar.setPreferredSize(new Dimension(330, 20));//.setBounds(0, 100, 100, 15);
		prgsBar.setMinimum(1);
		prgsBar.setMaximum(1500);
		topPane.add("3.2.center", prgsBar);
		jlabel33 = new JLabel("  ");
		topPane.add("3.3.left", jlabel33);
		
		
		button_OK = new JButton("导入");
	    //button_OK.setMnemonic(appReg.getString("ok.MNEMONIC").charAt(0));
	    button_OK.setEnabled(false);
	    button_OK.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionevent) {
				
				startImport(partsMap);

			} 	
	    });
	    
		
	    button_Cancel = new JButton("取消");
	    //button_Cancel.setMnemonic(appReg.getString("cancel.MNEMONIC").charAt(0));
		button_Cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent actionevent) {
	
				setVisible(false);
				dispose();
			}
		});
		
		// 按钮面板
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.CENTER);
		JPanel botPane = new JPanel(flowLayout);
		botPane.add(button_OK);
		botPane.add(new JLabel(" "));
		botPane.add(button_Cancel);		
		
		// 总面板
		VerticalLayout sumLayout = new VerticalLayout();
		sumLayout.setVerticalGap(10);
		sumLayout.setTopMargin(10);
		sumLayout.setBottomMargin(10);
		sumLayout.setLeftMargin(7);
		sumLayout.setRightMargin(7);
		JPanel pane = new JPanel(sumLayout);
		pane.add("top.bind", topPane);
		pane.add("bottom.nobind", botPane);
		getContentPane().add(pane);
		setSize(getWidth(), getHeight() + 50);
		
		pack();
		centerToScreen(1.0D, 1.0D);
	}

	

	private void startImport(Map<String, List<String>> partsMap) {
		try {
			NewItemAndDatasetOperation op = new NewItemAndDatasetOperation(tcsession,desktop,partsMap,pasteTargets);
			op.addOperationListener(this);
			op.executeOperation();
		}
		catch (Exception e) {
			System.out.println("error:"+e.getMessage());
		}

	}

	@Override
	public void endOperation() {
		// TODO Auto-generated method stub
		
		setVisible(false);
		dispose();
	}

	@Override
	public void startOperation(String arg0) {
		// TODO Auto-generated method stub
		button_OK.setEnabled(false);
	}

}
