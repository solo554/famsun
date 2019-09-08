package com.famsun.rac.util;

import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.*;

import com.teamcenter.rac.util.FileUtility;
import com.teamcenter.services.rac.core.DataManagementService;

@SuppressWarnings("restriction")
public class CommonUtils {

	  public static final TCSession geTcSession()
	    {
	        return (TCSession)AIFUtility.getCurrentApplication().getSession();
	    }

	    public static String[] getPreferenceStringArray(String preferenceName)
	    {
	        TCPreferenceService prefService = geTcSession().getPreferenceService();
	        return prefService.getStringArray(4, preferenceName);
	    }

	    public static final void addRelation(TCComponent primary, TCComponent secondary, String relationType)
	        throws TCException
	    {
	        TCSession session = geTcSession();
	        DataManagementService managementService = DataManagementService.getService(session);
	        com.teamcenter.services.rac.core._2006_03.DataManagement.Relationship relations[] = new com.teamcenter.services.rac.core._2006_03.DataManagement.Relationship[1];
	        relations[0] = new com.teamcenter.services.rac.core._2006_03.DataManagement.Relationship();
	        relations[0].clientId = (new Integer(1)).toString();
	        relations[0].primaryObject = primary;
	        relations[0].secondaryObject = secondary;
	        relations[0].relationType = relationType;
	        com.teamcenter.services.rac.core._2006_03.DataManagement.CreateRelationsResponse response = managementService.createRelations(relations);
	        if(response.serviceData.sizeOfPartialErrors() > 0)
	            SoaUtil.checkPartialErrors(response.serviceData);
	    }

	    public static final void removeAllFilesFromDataset(TCComponentDataset dataset)
	        throws TCException
	    {
	        if(dataset == null)
	            return;
	        NamedReferenceContext contexts[] = dataset.getDatasetDefinitionComponent().getNamedReferenceContexts();
	        for(int i = 0; i < contexts.length; i++)
	        {
	            NamedReferenceContext context = contexts[i];
	            String reference = context.getNamedReference();
	            dataset.removeNamedReference(reference);
	        }

	    }

	    public static final void importFileToDataset(TCComponentDataset dataset, File file, String fileType, String refType)
	        throws TCException
	    {
	        String as1[] = {
	            file.getPath()
	        };
	        String as2[] = {
	            fileType
	        };
	        String as3[] = {
	            "Plain"
	        };
	        String as4[] = {
	            refType
	        };
	        dataset.setFiles(as1, as2, as3, as4);
	    }



	    public static final TCComponentDataset createDataset(String name, String description, String type)
	        throws Exception
	    {
	        TCComponentDataset dataset = null;
	        TCComponentDatasetType datasetType = (TCComponentDatasetType)geTcSession().getTypeComponent(type);
	        if(datasetType == null) {
	            throw new Exception((new StringBuilder("\u65E0\u6CD5\u83B7\u53D6\u540D\u4E3A")).append(type).append("\u7684\u6570\u636E\u96C6\u7C7B\u578B\uFF01").toString());
	        } else {
	            dataset = datasetType.create(name, description, type);
	            return dataset;
	        }
	    }

	    public static final String getPreferenceString(String preferenceName)
	    {
	        TCPreferenceService prefService = geTcSession().getPreferenceService();
	        return prefService.getString(4, preferenceName);
	    }

	    public static final String getPreferenceString(String preferenceName, String defaultValue)
	    {
	        TCPreferenceService prefService = geTcSession().getPreferenceService();
	        return prefService.getString(4, preferenceName, defaultValue);
	    }

	    public static boolean isDBARole()
	    {
	        try
	        {
	            TCComponentRole role = geTcSession().getCurrentRole();
	            String roleName = role.getProperty("role_name");
	            return "DBA".equalsIgnoreCase(roleName);
	        }
	        catch(TCException e)
	        {
	            e.printStackTrace();
	        }
	        return false;
	    }

	    public static final TCComponentFolder createFolder(String name, String description, String type)
	        throws Exception
	    {
	        TCComponentFolder folder = null;
	        TCComponentFolderType folderType = (TCComponentFolderType)geTcSession().getTypeComponent(type);
	        if(folderType == null)
	        {
	            throw new Exception((new StringBuilder("\u65E0\u6CD5\u83B7\u53D6\u540D\u4E3A")).append(type).append("\u7684\u6587\u4EF6\u5939\u7C7B\u578B\uFF01").toString());
	        } else
	        {
	            folder = folderType.create(name, description, type);
	            return folder;
	        }
	    }

	    public static final TCComponent[] queryComponents(TCSession session, String queryName, String keys[], String values[])
	        throws TCException
	    {
	        TCComponent results[] = new TCComponent[0];
	        TCComponentQueryType querytype = (TCComponentQueryType)session.getTypeComponent("ImanQuery");
	        TCComponentQuery query = (TCComponentQuery)querytype.find(queryName);
	        querytype.clearCache();
	        if(query == null)
	            throw new RuntimeException((new StringBuilder("\u672A\u5B9A\u4E49\u7684\u67E5\u8BE2[")).append(queryName).append("]").toString());
	        TCTextService textService = session.getTextService();
	        String keytexts[] = new String[keys.length];
	        for(int i = 0; i < keys.length; i++)
	            keytexts[i] = textService.getTextValue(keys[i]);

	        String valuetexts[] = new String[values.length];
	        for(int i = 0; i < values.length; i++)
	            valuetexts[i] = textService.getTextValue(values[i]);

	        results = query.execute(keytexts, valuetexts);
	        query.clearCache();
	        return results;
	    }

	    public static boolean hasItem(String itemID)
	    {
	        try
	        {
	            TCComponentItemType itemType = (TCComponentItemType)geTcSession().getTypeComponent("Item");
	            TCComponentItem item = itemType.find(itemID);
	            return item != null;
	        }
	        catch(TCException e)
	        {
	            e.printStackTrace();
	        }
	        return false;
	    }



	    public static final TCComponentItem findItem(String itemId)
	    {
	        try
	        {
	            TCComponentItemType itemType = (TCComponentItemType)geTcSession().getTypeComponent("Item");
	            return itemType.find(itemId);
	        }
	        catch(TCException e)
	        {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    public static final TCComponentItemRevision findItemRevision(String itemId, String revId)
	    {
	        try
	        {
	            TCComponentItemRevisionType revType = (TCComponentItemRevisionType)geTcSession().getTypeComponent("ItemRevision");
	            return revType.findRevision(itemId, revId);
	        }
	        catch(TCException e)
	        {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    public static final void centerShell(Shell paramShell)
	    {
	        if(paramShell == null)
	            return;
	        Display display = paramShell.getDisplay();
	        Rectangle rectangle = display.getClientArea();
	        Monitor arrayOfMonitor[] = display.getMonitors();
	        Monitor primaryMonitor = display.getPrimaryMonitor();
	        if(primaryMonitor != null && arrayOfMonitor != null && arrayOfMonitor.length > 1)
	            rectangle = primaryMonitor.getClientArea();
	        Point point = paramShell.getSize();
	        paramShell.setLocation((rectangle.width - point.x) / 2, (rectangle.height - point.y) / 2);
	    }

	    public static final String getSuffix(String fileName)
	    {
	        int index = fileName.lastIndexOf('.');
	        if(index != -1)
	            return fileName.substring(index);
	        else
	            return "";
	    }

	    public static final String getPrefix(String fileName)
	    {
	        int index = fileName.lastIndexOf('.');
	        if(index != -1)
	            return fileName.substring(0, index);
	        else
	            return fileName;
	    }

	    public static final void deleteFolder(File folder)
	    {
	        if(folder.exists() && folder.isDirectory())
	            if(folder.listFiles().length == 0)
	            {
	                folder.delete();
	            } else
	            {
	                File subFiles[] = folder.listFiles();
	                File afile[];
	                int j = (afile = subFiles).length;
	                for(int i = 0; i < j; i++)
	                {
	                    File subFile = afile[i];
	                    if(subFile.isDirectory())
	                        deleteFolder(subFile);
	                    else
	                        subFile.delete();
	                }

	                folder.delete();
	            }
	    }

	    public static final boolean openByPass()
	    {
	        TCUserService userService = geTcSession().getUserService();
	        try
	        {
	            userService.call("openByPass", new Object[] {
	                Integer.valueOf(1)
	            });
	        }
	        catch(TCException e)
	        {
	            e.printStackTrace();
	            return false;
	        }
	        return true;
	    }

	    public static final void closeByPass()
	    {
	        TCUserService userService = geTcSession().getUserService();
	        try
	        {
	            userService.call("closeByPass", new Object[] {
	                Integer.valueOf(1)
	            });
	        }
	        catch(TCException e)
	        {
	            e.printStackTrace();
	        }
	    }
	
		
	public static TCComponentGroup getDefaultLoginGroup(TCComponent comp) throws TCException {
		if (comp instanceof TCComponentUser)
			return ((TCComponentUser) comp).getLoginGroup();
		else {
			TCComponentUser user = (TCComponentUser) comp.getReferenceProperty("owning_user");
			return user.getLoginGroup();
		}
	}

	
	
	public static TCComponentItemRevision findLatestReleasedRevision(TCComponentItem item) {
		if (item == null)
			return null;
		
		try {
			TCComponentItemRevision latestRev = item.getLatestItemRevision();
			if (getReleaseStatus(latestRev) == null) {
				latestRev = latestRev.basedOn();
			}
			return latestRev;
		} 
		catch (TCException e) {
			e.printStackTrace();
		}

		return null;
	}


	public static TCComponentItemRevision[] getAllRevisions(TCComponent component) {

		TCComponentItemRevision[] revs = new TCComponentItemRevision[0];

		TCComponentItem item = null;

		try {
			if (component instanceof TCComponentItem) {
				item = (TCComponentItem) component;
			}
			else if (component instanceof TCComponentItemRevision) {
				item = ((TCComponentItemRevision) component).getItem();
			}
		} 
		catch (TCException e1) {
			e1.printStackTrace();
		}

		if (item != null) {
			try {
				TCComponent[] components = item.getRelatedComponents("revision_list");

				revs = new TCComponentItemRevision[components.length];

				for (int i = 0; i < components.length; i++) {
					revs[i] = (TCComponentItemRevision) components[i];
				}
			} 
			catch (TCException e) {
				e.printStackTrace();
			}
		}

		return revs;
	}


	public static TCComponentRevisionRule findRevisionRule(String revRuleName) {

		TCComponentRevisionRule revRule = null;

		try {
			TCComponentRevisionRuleType revRuleType = (TCComponentRevisionRuleType) geTcSession().getTypeComponent("RevisionRule");

			TCComponent[] revRules = revRuleType.extent();

			for (TCComponent rule : revRules) {

				if (rule.toString().equals(revRuleName)) {
					revRule = (TCComponentRevisionRule) rule;
					break;
				}
			}
		} 
		catch (TCException e) {
			e.printStackTrace();
		}

		return revRule;
	}


	


	public static TCComponent getRelatedComponentByType(TCComponent component, String relationType, String componentType) {

		if (component == null) {
			return null;
		}

		try {
			TCComponent[] components = component.getRelatedComponents(relationType);

			for (int i = 0; i < components.length; i++) {
				if (components[i].getType().equals(componentType)) {
					return components[i];
				}
			}

			return null;
		} 
		catch (TCException e) {
			e.printStackTrace();
		}

		return null;
	}


	public static TCComponent[] getRelatedComponentsByType(TCComponent component, String relationType, String componentType) {

		if (component == null) {
			return null;
		}

		try {
			TCComponent[] components = component.getRelatedComponents(relationType);
			Vector<TCComponent> retVector = new Vector<TCComponent>();

			for (int i = 0; i < components.length; i++) {
				if (components[i].getType().equals(componentType)) {
					retVector.add(components[i]);
				}
			}

			return retVector.toArray(new TCComponent[retVector.size()]);
		} 
		catch (TCException e) {
			e.printStackTrace();
		}

		return new TCComponent[0];
	}
	
	
	public static TCComponent getRelatedComponentByName(TCComponent component, String relationType, String name) {

		if (component == null) {
			return null;
		}

		try {
			TCComponent[] components = component.getRelatedComponents(relationType);

			for (int i = 0; i < components.length; i++) {
				if (components[i].getProperty("object_name").equals(name)) {
					return components[i];
				}
			}

			return null;
		} 
		catch (TCException e) {
			e.printStackTrace();
		}

		return null;
	}


	public static TCComponentForm getIRMForm(TCComponentItemRevision rev) {

		if (rev == null) {
			return null;
		}

		TCComponent form = null;

		try {
			form = rev.getRelatedComponent("IMAN_master_form_rev");
		} 
		catch (TCException e) {
			e.printStackTrace();
		}

		if (form == null) {
			return null;
		}
		else {
			return (TCComponentForm) form;
		}
	}


	public static TCComponentDataset createDataset(String dsName, String dsDesc, String dsType, String[] filePath, String[] dsRef) throws TCException {

		TCComponentDataset dataset = null;

		TCComponentDatasetType datasetType = (TCComponentDatasetType) geTcSession().getTypeComponent("Dataset");

		dataset = datasetType.create(dsName, dsDesc, dsType);
		dataset.setFiles(filePath, dsRef);

		return dataset;
	}
	
	
	
	
	
	


	public static void setComponentProperties(TCComponent comp, Hashtable<String, String> valueMap) {

		if (comp == null) {
			return;
		}

		try {
			comp.lock();

			Enumeration<String> keys = valueMap.keys();
			Vector<TCProperty> propertyVector = new Vector<TCProperty>();

			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = valueMap.get(key);

				TCProperty property = comp.getTCProperty(key);

				if (property != null) {
					property.setStringValueData(value);
					propertyVector.add(property);
				}
			}

			TCProperty[] properties = propertyVector.toArray(new TCProperty[propertyVector.size()]);

			comp.setTCProperties(properties);
			comp.save();
			comp.unlock();
		} 
		catch (TCException e) {
			e.printStackTrace();
		}
	}


	public static Vector<TCComponentBOMLine> getSingleLevelChildrenBomLines(TCComponentBOMLine rootLine, String itemType, boolean includeRootLine) {

		Vector<TCComponentBOMLine> bomLineVector = new Vector<TCComponentBOMLine>();

		if (includeRootLine == true) {

			if (itemType.equals("") == false) {
				try {
					if (rootLine.getItem().getType().equals(itemType) == true) {
						bomLineVector.add(rootLine);
					}
				} 
				catch (TCException e) {
					e.printStackTrace();
				}
			}
			else {
				bomLineVector.add(rootLine);
			}
		}

		try {
			AIFComponentContext[] contexts = rootLine.getChildren();

			if (contexts != null) {
				for (AIFComponentContext context : contexts) {

					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();

					if (itemType.equals("") == false) {
						try {
							if (bomLine.getItem().getType().equals(itemType) == true) {
								bomLineVector.add(bomLine);
							}
						} 
						catch (TCException e) {
							e.printStackTrace();
						}
					}
					else {
						bomLineVector.add(bomLine);
					}
				}
			}
		} 
		catch (TCException e) {
			e.printStackTrace();
		}

		return bomLineVector;
	}

	
	public static Vector<TCComponentBOMLine> getAllChildrenBomLines(TCComponentBOMLine rootLine, String itemType, boolean includeRootLine) {

		Vector<TCComponentBOMLine> bomLineVector = new Vector<TCComponentBOMLine>();

		if (includeRootLine == true) {

			if (itemType.equals("") == false) {
				try {
					if (rootLine.getItem().getType().equals(itemType) == true) {
						bomLineVector.add(rootLine);
					}
				} 
				catch (TCException e) {
					e.printStackTrace();
				}
			}
			else {
				bomLineVector.add(rootLine);
			}
		}

		try {
			AIFComponentContext[] contexts = rootLine.getChildren();

			if (contexts != null) {
				for (AIFComponentContext context : contexts) {
					getSubBomLines((TCComponentBOMLine) context.getComponent(), itemType, bomLineVector);
				}
			}
		} 
		catch (TCException e) {
			e.printStackTrace();
		}

		return bomLineVector;
	}


	public static void getSubBomLines(TCComponentBOMLine bomLine, String itemType, Vector<TCComponentBOMLine> bomlineVector) {

		if (itemType.equals("") == false) {
			try {
				if (bomLine.getItem().getType().equals(itemType) == true) {
					bomlineVector.add(bomLine);
				}
			} 
			catch (TCException e) {
				e.printStackTrace();
			}
		}
		else {
			bomlineVector.add(bomLine);
		}

		try {
			AIFComponentContext[] contexts = bomLine.getChildren();

			if (contexts != null) {
				for (int i = 0; i < contexts.length; i++) {
					getSubBomLines((TCComponentBOMLine) contexts[i].getComponent(), itemType, bomlineVector);
				}
			}
		} 
		catch (TCException e) {
			e.printStackTrace();
		}
	}
	

	
	
	
	
	

	public static File getDatasetFile(String dsTypeName, String dsName, String fileName, String downloadDir, String newFileName) throws Exception {

		TCComponentDataset dataset = null;
		TCComponentDatasetType datasetType = (TCComponentDatasetType) geTcSession().getTypeComponent(dsTypeName);

		if (datasetType == null) {
			throw new Exception("未找到名为'" + dsTypeName + "'的数据集类型！");
		}

		dataset = datasetType.find(dsName);

		if (dataset == null) {
			throw new Exception("未找到名为'" + dsName + "'的" + dsTypeName + "数据集！");				
		}

		File file = null;

		TCComponentTcFile[] tcFiles = dataset.getTcFiles();

		if (fileName.equals("") == true) {

			if (tcFiles.length > 0) {

				if (downloadDir.equals("") == true) {
					downloadDir = getPortalTempPath();
				}

				if (newFileName.equals("") == true) {

					file = tcFiles[0].getFile(downloadDir);
				}
				else {
					file = tcFiles[0].getFile(downloadDir, newFileName);
				}
			}
		}
		else {
			for (TCComponentTcFile tcFile : tcFiles) {

				if (tcFile.getProperty("file_name").equals(fileName) == true) {

					if (downloadDir.equals("") == true) {
						downloadDir = getPortalTempPath();
					}

					if (newFileName.equals("") == true) {

						file = tcFile.getFile(downloadDir);
					}
					else {
						file = tcFile.getFile(downloadDir, newFileName);
					}

					break;
				}
			}			
		}

		return file;
	}
	
	public static String getPortalTempPath() {
		return System.getenv("TPR") + "\\temp";
	}

	public static String getUserId(TCComponent user) {
		return TCComponentUser.getUserIdFromFormattedString(user.toString());
	}


	public static String getUserName(TCComponent user) {
		return TCComponentUser.getUserNameFromFormattedString(user.toString());
	}


	public static String getUserId() {
		return getUserId(CommonUtils.geTcSession().getUser());
	}


	public static String getUserName() {
		return getUserName(CommonUtils.geTcSession().getUser());
	}


	public static String getPreferenceValue(String key) {
		return geTcSession().getPreferenceService().getString(TCPreferenceService.TC_preference_all, key);
	}

	
	public static String getPreferenceValueD(String key, String defalutValue) {
		return geTcSession().getPreferenceService().getString(TCPreferenceService.TC_preference_all, key, defalutValue);
	}
	
	
	public static String[] getPreferenceValueArray(String key) {
		return geTcSession().getPreferenceService().getStringArray(TCPreferenceService.TC_preference_all, key);
	}




	public static String getTempPath() {

		String tmpPath = System.getProperty("java.io.tmpdir");

		if (tmpPath.endsWith("\\")) {
			tmpPath = tmpPath.substring(0, tmpPath.length() - 1);
		}

		return tmpPath;
	}



	public static boolean checkWritePrivilege(TCComponent component) {

		if (component == null) {
			return false;
		}

		try {
			return geTcSession().getTCAccessControlService().checkPrivilege(component, "WRITE");
		} 
		catch (TCException e) {
			e.printStackTrace();
			return false;
		}
	}


	public static TCComponent getReleaseStatus(TCComponent comp) {
		
		if (comp == null) {
			return null;
		}

		try {
			TCComponent last_release_status = comp.getReferenceProperty("last_release_status");
			return last_release_status;
		} 
		catch (TCException e) {
			e.printStackTrace();
			return null;
		}
	}


	public static String getReleaseStatusName(TCComponentReleaseStatus status) {
		try {
			if (status == null) {
				return "";
			}
			
			return status.getTCProperty("object_name").getStringValue();
		} 
		catch (TCException e) {
			e.printStackTrace();
			return "";
		}
	}


	public static String getTrimedReleaseStatusName(String realStatus) {
		if (realStatus == null) {
			return "";
		}

		String[] sps = realStatus.split("_");
		if (sps.length == 2)
			return sps[1];
		else
			return realStatus;
	}
	
	
	public static TCComponent[] findComponentsByQuery(String queryName, Hashtable<String, String> valueMap, boolean allowEmptyValueSearch) {

		TCComponent[] components = null;

		try {
			TCComponentQueryType queryType = (TCComponentQueryType) geTcSession().getTypeComponent("ImanQuery");
			TCComponentQuery query = (TCComponentQuery) queryType.find(queryName);

			if (query == null) {
				return new TCComponent[0];
			}

			Vector<String> namesVector = new Vector<String>();
			Vector<String> valuesVector = new Vector<String>();

			boolean hasValue = false;

			TCQueryClause[] clauses = query.describe();

			for (TCQueryClause queryClause : clauses) {

				String name = queryClause.getUserEntryNameDisplay();
				String value = valueMap.get(name);
				System.out.println("name: " + name + ", value: " + value);
				if (name.equals("") == true) {
					continue;
				}
				else if (value != null && value.equals("") == false) {
					namesVector.add(name);
					valuesVector.add(value);
					hasValue = true;
				}
				else if (queryClause.getDefaultValue().equals("") == false) {
					namesVector.add(name);
					valuesVector.add(queryClause.getDefaultValue());
				}
			}

			if (namesVector.size() == 0) {
				return new TCComponent[0];
			}

			if (hasValue == false && allowEmptyValueSearch == false) {
				return new TCComponent[0];
			}

			String[] names = namesVector.toArray(new String[namesVector.size()]);
			String[] values = valuesVector.toArray(new String[valuesVector.size()]);
			System.out.println("names: " + Arrays.asList(names) + ", values: " + Arrays.asList(values));
			
			components = query.execute(names, values);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return new TCComponent[0];
		}

		return components;
	}
	
	
	public static TCComponent[] findComponentsByQuery(TCComponentQuery query, Hashtable<String, String> valueMap, boolean allowEmptyValueSearch) {

		TCComponent[] components = null;

		try {
			Vector<String> namesVector = new Vector<String>();
			Vector<String> valuesVector = new Vector<String>();

			boolean hasValue = false;

			TCQueryClause[] clauses = query.describe();

			for (TCQueryClause queryClause : clauses) {

				String name = queryClause.getUserEntryNameDisplay();
				String value = valueMap.get(name);

				if (name.equals("") == true) {
					continue;
				}
				else if (value != null && value.equals("") == false) {
					namesVector.add(name);
					valuesVector.add(value);
					hasValue = true;
				}
				else if (queryClause.getDefaultValue().equals("") == false) {
					namesVector.add(name);
					valuesVector.add(queryClause.getDefaultValue());
				}
			}

			if (namesVector.size() == 0) {
				return new TCComponent[0];
			}

			if (hasValue == false && allowEmptyValueSearch == false) {
				return new TCComponent[0];
			}

			String[] names = namesVector.toArray(new String[namesVector.size()]);
			String[] values = valuesVector.toArray(new String[valuesVector.size()]);
			System.out.println("names: " + Arrays.asList(names) + ", values: " + Arrays.asList(values));
			
			components = query.execute(names, values);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return new TCComponent[0];
		}

		return components;
	}
	
	
	public static TCComponentUser findUser(String userId) {
		try {
			TCComponentUserType userType = (TCComponentUserType) geTcSession().getTypeComponent("User");
			return userType.find(userId);
		} 
		catch (TCException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static TCComponentGroup findGroup(String groupId) {
		try {
			TCComponentGroupType userType = (TCComponentGroupType) geTcSession().getTypeComponent("Group");
			return userType.find(groupId);
		} catch (TCException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public static String getDatasetVolumeName(TCComponentDataset dataset) {
		
		String volumeName = "";
		
		try {
			TCComponentTcFile[] tcFiles = dataset.getTcFiles();
			
			if (tcFiles.length > 0) {
				
				TCComponent volume = tcFiles[0].getReferenceProperty("volume_tag");
				
				if (volume instanceof TCComponentVolume) {
					volumeName = volume.getProperty("volume_name");
				}
			}
		} 
		catch (TCException e) {
			e.printStackTrace();
		}
		
		return volumeName;
	}


	

	public static int Str2Int(String str, int defaultValue) {

		int ret = defaultValue;

		if (str.equals("") == false) {
			try {
				ret = Integer.parseInt(str);
			} 
			catch (Exception e) {}			
		}

		return ret;
	}


	public static BigDecimal Str2BigDecimal(String str, BigDecimal defaultValue) {

		BigDecimal ret = defaultValue;

		if (str.equals("") == false) {
			try {
				ret = new BigDecimal(str);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}
	

	public static double Str2Double(String str, double defaultValue) {

		double ret = defaultValue;

		if (str.equals("") == false) {
			try {
				ret = Double.parseDouble(str);
			} 
			catch (Exception e) {}			
		}

		return ret;
	}
	

	public static String date2String(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}


	public static String date2String(Date date, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.format(date);
	}


	public static String getFileName(String filePath) {
		return new File(filePath).getName();
	}


	public static String getFileExtName(String filePath) {

		String fileName = getFileName(filePath);

		if (fileName.lastIndexOf(".") == -1) {
			return "";
		}
		else {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		}
	}


	public static String getTextMapping(String s) {
		String ret = s;

		try {
			ret = geTcSession().getTextService().getTextValue(s);
		} 
		catch (TCException e) {
			e.printStackTrace();
		}
		
		if (ret == null || "".equals(ret))
			ret = s;

		return ret;
	}
	
	
	public static void copyFile(File sourceFile,File targetFile) throws IOException {

		FileInputStream input = new FileInputStream(sourceFile); 
		BufferedInputStream inBuff = new BufferedInputStream(input); 

		FileOutputStream output = new FileOutputStream(targetFile); 
		BufferedOutputStream outBuff = new BufferedOutputStream(output); 

		byte[] b = new byte[1024 * 5]; 
		int len;

		while ((len =inBuff.read(b)) != -1) { 
			outBuff.write(b, 0, len); 
		} 

		outBuff.flush(); 

		inBuff.close(); 
		outBuff.close(); 
		output.close(); 
		input.close(); 
	}


	//去除非法字符
	public static String replaceIllegalChars(String str, char replaceWithChr) {

		String replaceWithStr = String.valueOf(replaceWithChr);

		String tmpStr = str.replaceAll("/", replaceWithStr);
		String ret = "";

		String regEx="[`*|:\\<>/?\"]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(tmpStr);      
		ret = matcher.replaceAll(replaceWithStr);

		char[] temp = ret.toCharArray();

		for (int i = 0; i < temp.length; i++) {
			if (temp[i] == '\\') {
				temp[i] = replaceWithChr;
			}
		}

		ret = String.valueOf(temp);

		return ret;
	}


	public static String[] getStartupParameters() {
		String[] args = EclipseEnvironmentInfo.getDefault().getNonFrameworkArgs();
		return args;
	}
	
	
	public static String deleteTailZero(String input) {
		String result = input;

		if (result.indexOf(".") >= 0) {
			while (true) {
				if (result.endsWith("0"))
					result = result.substring(0, result.length() - 1);
				else if (result.endsWith("."))
					result = result.substring(0, result.length() - 1);
				else
					break;
			}
		}
		return result;
	}
	

	public static String formatNumber(String num) {
		
		if (num.equals("") == true) {
			return "0";
		}
		
		DecimalFormat df = new DecimalFormat("###0.000");

		String formatNum = "0";
		try {
			formatNum = df.format(Double.parseDouble(num));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return deleteTailZero(formatNum);
	}
	

	/**
	 * 获取一年前日子
	 * 
	 * @return
	 */
	public static Date beforOneYear() {
		Date myDate = new Date();
		long myTime = (myDate.getTime() / 1000) - 60 * 60 * 24 * 365;
		myDate.setTime(myTime * 1000);
		return myDate;
	}

	
	public static String formateDate(Date date) {
		TCDateFormat format = new TCDateFormat(geTcSession());
		return format.format(date);
	}
	
	
	public static String getExtension(String filename) {
		return getExtension(filename, "");
	}

	
	public static String getExtension(String filename, String defExt) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');

			if ((i > -1) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1);
			}
		}
		return defExt;
	}  
	
	
	public static String getFileNameNoEx(String filename) {

		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');

			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}

		return filename;
	}
	
	public static String getSeToolType(String filename){
	    String extName=getExtension(filename);
	    String flag="";        
		if (extName.equals("par")){
			flag="SEpart";
		}
		else 
		if(extName.equals("psm")){
			flag="SEdraft";
		}
		else 
		if(extName.equals("asm")){
			flag="SEassembly";
		}
		else 
		if(extName.equals("dft")){
			flag="SEsheetMetal";
		}
	     		
		return flag;
	
	 }
	
	public static String getDatasetType(String filename){
	    String extName=getExtension(filename);
	    String flag="";        
		if (extName.equals("par")){
			flag="SE Part";
		}
		else 
		if(extName.equals("psm")){
			flag="SE SheetMetal";
		}
		else 
		if(extName.equals("asm")){
			flag="SE Assembly";
		}
		else 
		if(extName.equals("dft")){
			flag="SE Draft";
		}
	     		
	return flag;
	
	 }
	
	public static String getRefType(String filename){
	    String extName=getExtension(filename);
	    String flag="";        
		if (extName.equals("par")){
			flag="SE-part";
		}
		else 
		if(extName.equals("psm")){
			flag="SE-sheetMetal";
		}
		else 
		if(extName.equals("asm")){
			flag="SE-assembly";
		}
		else 
		if(extName.equals("dft")){
			flag="SE-draft";
		}
	     		
	return flag;
	
	 }

	
	public static TCComponentFolder createFolder(TCSession session, String name, String desc, String type) throws Exception {
		TCComponentFolderType folderTypeComponent = (TCComponentFolderType) session.getTypeComponent(type);
		TCComponent[] newFolder = new TCComponent[] { folderTypeComponent.create(name, desc, type) };
		if (newFolder == null || newFolder.length == 0)
			return null;
		return (TCComponentFolder) newFolder[0];
	}

	
	public static TCComponentFolder copyFolder(TCComponentFolder copiedFld, TCComponentFolder parentFld, String specifiedDesc) throws Exception {
		if (copiedFld == null)
			return null;

		String name = copiedFld.getProperty("object_name");
		String desc = copiedFld.getProperty("object_desc");
		
		if (specifiedDesc != null)
			desc = specifiedDesc;
			
		TCComponentFolder newFld = createFolder(copiedFld.getSession(), name, desc, copiedFld.getType());
		
		if (newFld == null)
			return null;
		if (parentFld != null) {
			parentFld.add("contents", newFld);
		}
		
		AIFComponentContext[] childs = copiedFld.getChildren();
		if (childs == null || childs.length == 0)
			return newFld;
		
		for (int i = 0; i < childs.length; i++) {
			InterfaceAIFComponent iac = childs[i].getComponent();
			if (iac instanceof TCComponentFolder) {
				copyFolder((TCComponentFolder) iac, newFld, specifiedDesc);
			}
		}
		return newFld;
	}
	
	/**
	 * 
	 * @param component
	 * @return
	 */
	public static boolean isInWorking(TCComponent component) {
		String stage = "";
		if (component == null)
			return false;
		try {
			stage = component.getProperty("process_stage_list");
		} catch (TCException e) {
			e.printStackTrace();
		}
		return (stage != null && stage.trim().length() > 1);
	}
	
	public static boolean isReleased(TCComponent com) {
		try {
			TCComponent[] relStatus = com
					.getReferenceListProperty("release_status_list");
			if (relStatus!=null && relStatus.length>0) {
				return true ;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return false ;
	}
	
	public static TCComponentTask getObjectRelTask(TCComponent component) {
		if (component == null)
			return null;
		try {
			TCComponent[] stage = component.getReferenceListProperty("process_stage_list");
			if (stage != null && stage.length>0) {
				return ((TCComponentTask)stage[0]).getRoot();
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	// the following are some helper functions to manipulate dataset and files
	public static String getDownloadDir() {
		String tempDsExportDirPath = FileUtility.getExportDir() + File.separator + getUserId() + "_" + new Date().getTime();

		try {
			File tempDsExportDir = new File(tempDsExportDirPath);
			tempDsExportDir.mkdirs();
			System.out.println("tempDsExportDir : " + tempDsExportDir);			
		} catch (Exception e) {
			System.out.println(e);
		}

		return tempDsExportDirPath;
	}

	public static File[] downloadFile(TCComponentDataset dataset, String downloadDir) throws TCException {
		TCComponentTcFile[] tcFiles = dataset.getTcFiles();
		if (tcFiles == null || tcFiles.length == 0)
			return new File[0];
		else {
			ArrayList<File> fileList = new ArrayList<File>();
			for (TCComponentTcFile tcFile : tcFiles) {
				File file = tcFile.getFile(downloadDir);
				fileList.add(file);
			}
			return fileList.toArray(new File[fileList.size()]);
		}
	}

		
	public static String reverseGroup(String group) {

		String ret = "";

		if (!"".equals(group)) {

			for (String groupName : group.split("[.]")) {

				if (ret.equals("")) {
					ret = groupName;
				} else {
					ret = groupName + "." + ret;
				}
			}
		}

		return ret;
	}
	
	public static boolean isGroupMatched(String group1, String group2) {
		String rgroup1 = reverseGroup(group1);
		String rgroup2 = reverseGroup(group2);

		String[] ras1 = rgroup1.split("[.]");
		String[] ras2 = rgroup2.split("[.]");

		if (ras1.length >= 2 && ras2.length >= 2) {
			boolean is_equal = true;
			for (int i = 0; i < 2; i++) {
				if (!ras1[i].equals(ras2[i])) {
					is_equal = false;
					break;
				}
			}
			return is_equal;
		}
		else if (ras1.length == ras2.length) {
			boolean is_equal = true;
			for (int i = 0; i < ras1.length; i++) {
				if (!ras1[i].equals(ras2[i])) {
					is_equal = false;
					break;
				}
			}
			return is_equal;
		}
		
		return false;
	}
	
	
	


	public static String[] readTxtFile(String filePath){
		String[] tempArray = null;
        try 
        {
        	
            //String encoding="GBK";
            File file=new File(filePath);//
            if(file.isFile() && file.exists())
            { //判断文件是否存在
                //InputStreamReader read = new InputStreamReader( new FileInputStream(file),encoding);//考虑到编码格式
            	InputStreamReader read = new InputStreamReader( new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                   
                    tempArray= lineTxt.split("@");                   
                }
                read.close();
            }
            else
            {
            System.out.println("找不到指定的文件");
            }
           
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return tempArray;
    }
	
	 
	 

		public static File chooseImportFile()
		//打开本地excel表格
		{
			JFileChooser fc = impFileChooser;
			if (fc == null) {
				fc = impFileChooser = new JFileChooser();
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						return !f.isFile() || f.getName().endsWith(".xlsx")|| f.getName().endsWith(".xls");
					}

					@Override
					public String getDescription() {
						return "MS Excel Files (*.xlsx)";
					}

				});
			}
			
			fc.rescanCurrentDirectory();
			//if (JFileChooser.APPROVE_OPTION != fc.showOpenDialog(super.getParent()))
			AIFDesktop aifdesktop =AIFUtility.getActiveDesktop();
			Frame  frame=aifdesktop.getFrame();
			if (JFileChooser.APPROVE_OPTION != fc.showOpenDialog(frame))
				return null;
			File impFile=fc.getSelectedFile();
			System.out.println("004....."+impFile.getAbsolutePath());
			return impFile;

		
		}
		
		public static File chooseImportFile(String str)
		//选择SE文件
		{
			JFileChooser fc = impFileChooser;
			if (fc == null) {
				fc = impFileChooser = new JFileChooser();
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						return !f.isFile() || f.getName().endsWith(".par")|| f.getName().endsWith(".psm")|| f.getName().endsWith(".asm")|| f.getName().endsWith(".dft");
					}

					@Override
					public String getDescription() {
						return "所有SolidEdge文档 (...)";
					}

				});
			}
			
			fc.rescanCurrentDirectory();
			//if (JFileChooser.APPROVE_OPTION != fc.showOpenDialog(super.getParent()))
			AIFDesktop aifdesktop =AIFUtility.getActiveDesktop();
			Frame  frame=aifdesktop.getFrame();
			if (JFileChooser.APPROVE_OPTION != fc.showOpenDialog(frame))
				return null;
			File impFile=fc.getSelectedFile();
			System.out.println("004....."+impFile.getAbsolutePath());
			return impFile;

		
		}
		
		public static Map<String,List<String>> readImportExcel(File file){
			String[] nameArray=new String[]{"itemId","itemName","itemRev","drawingNo","partType","filePath"};
			
			Map<String,List<String>> map=new HashMap<String,List<String>>();
			FileInputStream fis=null;
			XSSFWorkbook workBook=null;
			if (file.exists()){
				try {
					fis =new FileInputStream(file);
					workBook=new XSSFWorkbook(fis);
					XSSFSheet sheet1=workBook.getSheetAt(0);
					XSSFRow row1=sheet1.getRow(0);
					for (int i=0; i<5; i++){
						XSSFCell xcell=row1.getCell(i);
						if(!getStringValueFromCell(xcell).equals(nameArray[i])){
							System.out.println("列标题\""+xcell.getStringCellValue()+"\"不符合要求!");
							return null;
						}
					}
					int rowsOfSheet = sheet1.getPhysicalNumberOfRows();
					for(int r=1;r<rowsOfSheet;r++){
						List<String> list=new ArrayList<String>();
						XSSFRow xrow=sheet1.getRow(r);						
						if (xrow == null){
							continue;
						} else {
							String itemid=null;
							for(int c=0;c<6;c++){
								XSSFCell xcell=xrow.getCell(c);
								String cellValue=getStringValueFromCell(xcell);
								if (cellValue=="" || cellValue==null)
									continue;
								if (c==0){
									itemid=cellValue;
								} else {
									list.add(cellValue);	
								}								
							}
							map.put(itemid, list);
						}
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
			} else {
				System.out.println("文件不存在！");
			}
			
			return map;
		}
		
		public static String getStringValueFromCell(XSSFCell cell){

			String cellValue="";
			if (cell==null){
				return cellValue;

			} else if(cell.getCellType()==HSSFCell.CELL_TYPE_STRING){
				cellValue=cell.getStringCellValue().trim();
				if(cellValue.equals("")||cellValue.length()<=0)
					cellValue="";
			} else if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
				cellValue=String.valueOf(cell.getNumericCellValue());
			} else {
				cellValue="";
			}
			return cellValue;
		}

   
    private static JFileChooser impFileChooser = null;

    

    
    
 
}

