package com.famsun.rac.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

import com.famsun.rac.operations.NewDatasetOperation;
import com.famsun.rac.operations.NewItemOperation;
import com.famsun.rac.util.CommonUtils;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextField;

public class CopyOfImportItemDialog extends AbstractAIFDialog
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

	
	
	class NewDataset implements InterfaceAIFOperationListener {
		protected String id;
		protected List<String> info;
		private NewItemOperation newItemOp;
		private NewDatasetOperation newDatasetOp;
		
		public NewDataset(String id,List<String> info,NewItemOperation newItemOp){
		this.id=id;
		this.info=info;
		this.newItemOp=newItemOp;
		}
		
		@Override
		public void startOperation(String arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void endOperation() {
			newItemOp.removeOperationListener(this);
			// TODO Auto-generated method stub
			 if(!newItemOp.isAbortRequested() && newItemOp.getSuccessFlag()){
	        	 
				try{
					//属性表赋值
					
					TCComponentItemRevision newItemRevision=newItemOp.getNewItem().getLatestItemRevision();	        	
					TCComponentForm itemRevForm=(TCComponentForm)newItemRevision.getRelatedComponent("IMAN_master_form_rev");
					String drawingNo=info.get(2);
					String partType=info.get(3);
					String[] itemRevFormNames=new String[] {"m2MY_DrawingNo","m2MY_Mass","m2MY_Material","m2MY_Memo","m2MY_Stock","m2MY_Type"};	    
					String[] itemRevFormValues=new String[] {drawingNo,"","","","",partType};

			        TCProperty[]  formProperties = itemRevForm.getFormTCProperties(itemRevFormNames);
			 
			        for(int k=0;k<formProperties.length;k++){
					         formProperties[k].setStringValueData(itemRevFormValues[k]);    			
			        }
			        itemRevForm.setTCProperties(formProperties);
					} catch(Exception e) {  
			    	    System.out.println("error");
					}
				}
			 
		 		if (info.get(4)!=null){
		 			//创建并导入数据集
		 			
		 			try {	
				 		String datasetName=id+"/A";
				 		String datasetType=CommonUtils.getDatasetType(info.get(4));
				 		String toolType=CommonUtils.getSeToolType(info.get(4));
				 		String refType=CommonUtils.getRefType(info.get(4));
	 				    TCComponentItem newItem=newItemOp.getNewItem();
	 				    TCComponentItemRevision tcNewItemRevision=newItem.getLatestItemRevision();	        	
	  					InterfaceAIFComponent[] pasteToItemRev =new InterfaceAIFComponent[1];
	  					pasteToItemRev[0]=(InterfaceAIFComponent)tcNewItemRevision;				
	  					newDatasetOp = new NewDatasetOperation(tcsession, desktop, datasetName, "", datasetType, toolType, info.get(4), "Binary", refType, true, pasteToItemRev);		       						      				
	  					tcsession.queueOperation(newDatasetOp);  
	     					
	          			}
	          	        catch(Exception e){
	          	        	System.out.println("error");
	          	        }          	        	          	        
	                 } 
		 		}
		
	}
			
	public CopyOfImportItemDialog(Frame frame, InterfaceAIFComponent ainterfaceaifcomponent[]){
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
					} else {
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
				
				setVisible(false);
				dispose();
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
		// TODO Auto-generated method stub
		Map<String, List<String>> hashmap = partsMap;
		Set<Entry<String,List<String>>> set = hashmap.entrySet();
		Iterator<Map.Entry<String,List<String>>> itera_Entry = set.iterator();
		
		//迭代器
		while(itera_Entry.hasNext()) {
			Map.Entry<String, List<String>> mapEntry = itera_Entry.next();
			String itemId = mapEntry.getKey();
			List<String> itemProperties = mapEntry.getValue();
			if(CommonUtils.hasItem(itemId)) {
				continue;
				
			} else {
				createItemAndDataset(itemId,itemProperties);
			}

		}
		
	}

	private boolean createItemAndDataset(String id,List<String> info) {
		// TODO Auto-generated method stub
		boolean flag = false;
		String itemId=id;
		List<String> itemInfo=info;
		NewItemOperation newItemOp=null;
		try {		
			newItemOp = new NewItemOperation(tcsession,desktop,itemInfo.get(0),itemId,itemInfo.get(1),null,"Item",null,pasteTargets);
			newItemOp.addOperationListener(new NewDataset(itemId,info,newItemOp));
		    tcsession.queueOperation(newItemOp);
     	    
        } catch(Exception e) {    	   
    	   System.out.println("error");
    	   MessageBox.post(e);
    	   flag=false;
    	   return flag;
        }		
		 if (newItemOp.getSuccessFlag())
			 flag=true;
		 

  		 return flag;
	}


	@Override
	public void endOperation() {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void startOperation(String arg0) {
		// TODO Auto-generated method stub
		button_OK.setEnabled(false);
	}

}
