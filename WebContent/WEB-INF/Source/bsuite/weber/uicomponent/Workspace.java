package bsuite.weber.uicomponent;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntryCollection;

import com.ibm.xsp.component.UIIncludeComposite;
import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.UISelectItemsEx;
import com.ibm.xsp.component.xp.XspCommandButton;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspSelectOneMenu;
import com.ibm.xsp.component.xp.XspTable;
import com.ibm.xsp.component.xp.XspTableCell;
import com.ibm.xsp.component.xp.XspTableRow;
import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.extlib.component.outline.UIOutlineDropDownButton;
import com.ibm.xsp.extlib.tree.complex.ComplexContainerTreeNode;
import com.ibm.xsp.extlib.tree.complex.ComplexLeafTreeNode;
import com.ibm.xsp.util.ManagedBeanUtil;

import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.relationship.Association;
import bsuite.weber.security.Profile;
import bsuite.weber.tools.CompUtil;
import bsuite.weber.tools.JSFUtil;

public class Workspace extends BsuiteWorkFlow implements Serializable {
	private String BEAN_NAME = "employee";
	private UIComponent tabs;
	private UIComponent centity;
	private UIComponent rentity;
	private ViewEntryCollection getmydocs;
	private UIComponent features;

	@SuppressWarnings("unchecked")
	public UIComponent getCentity() {
		centity = new UIPanelEx();
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		String pid = (String) viewscope.get("personunid");
		String moduleName = (String) viewscope.get("moduleName");
		String entityName = (String) viewscope.get("entityName");
		if (moduleName != null & entityName != null) {
			createEditForm(moduleName, entityName);
		}
		return centity;
	}

	public void setCentity(UIComponent centity) {
		this.centity = centity;
	}

	public UIComponent getTabs() {
		tabs = new UIPanelEx();
		UIDojoTabContainer tabcont = (UIDojoTabContainer) CreateTabTable
				.createTT(tabs, true);
		tabcont.setId("MainTabContainer");
		ArrayList<String> mnames = (ArrayList<String>) JSFUtil
				.getBindingValue("#{security.modules}");
		if (mnames == null) {
			return tabs;
		}

		for (String x : mnames) {

			if (x.contains(" ")) {
				x = x.replace(" ", "_");
			}

			UIDojoTabPane pane2 = (UIDojoTabPane) CreateTabTable.createTabPane(
					tabcont, x);
			String onShow = "console.log(\"hiiiiii\");\nXSP.executeOnServer(\'view:_id1:getTabPane\', \"\", \"\",  \""
					+ x + "\");";

			pane2.setOnShow(onShow);

			createActionBar(pane2, x);

		}
		return tabs;
	}

	public void setTabs(UIComponent tabs) {
		this.tabs = tabs;
	}

	public void createView(String entityName) {

	}

	// When entity schema is defined we need to select which fields should be
	// added as columns in the view we create using that data we need to
	// add columns in the view we create for this entity

	public void createReadForm(String entityName) {
		Association asstn = new Association();
		if (!asstn.getParentEntity(entityName).equals("")) {
			// Create fields for associated entity and tie it to the bean
		}
		// Load the fields here
	}

	public void createEditForm(String moduleName, String entityName) {
		XspTable utable = new XspTable();
		utable.setId("tableCreate");
		Profile profile = (Profile) JSFUtil
				.getBindingValue("#{security.profile}");

		String eaccess = profile.getEntityAccessType(moduleName, entityName);

		// When the entity is Public R/W show all the fields, don check the
		// profile whether it is visible or not
		if (eaccess.equals("2")) {
			loadEditFields(profile.getAllFieldsNames(moduleName, entityName),
					profile.getAllFieldsNames(moduleName, entityName), utable,
					0);
		} else {
			loadEditFields(profile.getEditableFieldsNames(moduleName,
					entityName), profile.getVisibleFieldsNames(moduleName,
					entityName), utable, 0);
		}

		// loadEditFields(profile.getEditableFieldsNames(moduleName,
		// entityName), profile.getVisibleFieldsNames(moduleName,
		// entityName),utable,0);
		centity.getChildren().add(utable);

		if (eaccess.equals("2")) {
			viewScope.put("editEntity", true);
		} else {
			if (profile.isEntityUpdate(moduleName, entityName)) {
				viewScope.put("editEntity", true);
			} else {
				viewScope.put("editEntity", false);
			}
		}

		// If accesstype is Public Read/Write then no need to check about the
		// EntityDelete,Delete should be seen only by document owner
		if (!eaccess.equals("2")) {
			if (profile.isEntityDelete(moduleName, entityName)) {
				viewScope.put("deleteEntity", true);
			} else {
				viewScope.put("deleteEntity", false);
			}
		}
	}

	public void createFeatureButtons(UIComponent actionpanel,
			String moduleName, XspTable acttable, XspTableRow actrow1) {
		// For this entity what features are available that needs to be
		// populated by this method
		Profile profile = (Profile) JSFUtil
				.getBindingValue("#{security.profile}");

		ArrayList<String> features = profile
				.getVisibleFeaturesNames(moduleName);

		for (String x : features) {
			XspTableCell cell1 = CompUtil.createCell(actrow1, "cell" + x);
			String expression2 = "alert(\"Hello: \"" + x + ")";
			XspCommandButton button2 = CompUtil.createButton(x, "button2" + x);
			XspEventHandler ev2 = CompUtil.createEventHandler("onclick",
					"complete", expression2, false, null);
			button2.getChildren().add(ev2);
			cell1.getChildren().add(button2);

		}

	}

	private void loadReadFields(ArrayList<String> fields, XspTable table,
			int startRow) {
		// This function is used to populate the fields in the given table
		// fields:list of fields
		// table: table component where field label and field will be added
		// startRow: the starting row number of the table where the fields needs
		// to be added
		for (int i = 0; i < fields.size(); i++) {
			XspTableRow row1 = CompUtil.createRow(table, "row" + i);
			for (int j = 0; j < 2; j++) {
				XspTableCell cell = CompUtil.createCell(row1, "cell" + i + j);

				switch (j) {
				case 0:
					CompUtil.createLabel(cell, fields.get(i).toString(), true);
					break;
				case 1:

					CompUtil.createInput(cell, fields.get(i).toString(),
							BEAN_NAME);
					break;
				}
			}
		}
	}

	private void loadEditFields(ArrayList<String> editableFields,
			ArrayList<String> fields, XspTable table, int startRow) {
		// This function is used to populate the fields in the given table
		// fields:list of fields
		// table: table component where field label and field will be added
		// startRow: the starting row number of the table where the fields needs
		// to be added
		for (int i = 0; i < fields.size(); i++) {
			XspTableRow row1 = CompUtil.createRow(table, "row" + i);
			for (int j = 0; j < 2; j++) {
				XspTableCell cell = CompUtil.createCell(row1, "cell" + i + j);
				String fieldName = fields.get(i).toString();
				int e = 0;
				if (editableFields.contains(fieldName)) {
					e = 2;// Editable field
				} else {
					e = 1;// Readonly field
				}
				switch (j) {
				case 0:
					CompUtil.createLabel(cell, fields.get(i).toString(), true);
					break;
				case 1:

					CompUtil.createInput(cell, fieldName, BEAN_NAME, e);
					break;
				}
			}
		}
	}

	private void createActionBar(UIComponent com, String moduleName) {
		UIPanelEx actionpanel = new UIPanelEx();
		actionpanel.setId("actionTable" + moduleName);
		UIPanelEx entitypanel = new UIPanelEx();
		entitypanel.setId("MainEntityPanel" + moduleName);

		// Create 1 table with 1 row and 2 column
		XspTable viewtable = new XspTable();
		XspTableRow viewrow = CompUtil.createRow(viewtable, "row" + "0");
		XspTableCell viewcell1 = CompUtil.createCell(viewrow, "EntityPanel"
				+ moduleName);
		XspTableCell viewcell2 = CompUtil.createCell(viewrow, "readPanel"
				+ moduleName);
		viewcell2.setValign("top");
		entitypanel.getChildren().add(viewtable);
		// For dropdown button

		HashMap<String, ArrayList<String>> entity = (HashMap<String, ArrayList<String>>) JSFUtil
				.getBindingValue("#{security.modulesEntities}");
		Profile profile = (Profile) JSFUtil
				.getBindingValue("#{security.profile}");
		// this is added to match the modulename which is store in the hashmap
		if (moduleName.contains("_")) {
			moduleName = moduleName.replace("_", " ");
		}

		// Read&Write Entities of the current module
		ArrayList<String> rwentities = profile.getPublicRWEntities(moduleName);
		ArrayList<String> creatableentites = entity.get(moduleName);

		// If the createable entities array doesnt contains publi entities then
		// add to the list
		if (rwentities != null) {
			for (String a : rwentities) {
				if (!creatableentites.contains(a)) {
					creatableentites.add(a);
				}
			}
		}

		// to get readable entities for the given moduleName
		ArrayList<String> readentities = profile
				.getPublicReadEntities(moduleName);

		// this is converted back becoz moduleName with underscore is use for
		// component id and also for partial refresh
		if (moduleName.contains(" ")) {
			moduleName = moduleName.replace(" ", "_");
		}

		ArrayList<String> readableEntities = profile
				.getReadableEntitiesNames(moduleName);

		// If the readabe entities array doesnt contains publi entities then add
		// to the list
		if (rwentities != null) {
			for (String a : rwentities) {
				if (!readableEntities.contains(a)) {
					readableEntities.add(a);
				}
			}
		}
		// If the readabe entities array doesnt contains public readonly
		// entities then add to the list
		if (readentities != null) {
			for (String a : readentities) {
				if (!readableEntities.contains(a)) {
					readableEntities.add(a);
				}
			}
		}

		XspTable acttable = new XspTable();
		acttable.setId("actTable");
		XspTableRow actrow1 = CompUtil.createRow(acttable, "row" + "0");
		if (creatableentites != null) {

			UIOutlineDropDownButton result = new UIOutlineDropDownButton();
			result.setId("dropDownButton1");
			ComplexContainerTreeNode treeNodes = new ComplexContainerTreeNode();
			treeNodes.setComponent(result);
			treeNodes.setLabel("Create");
			for (String ent : creatableentites) {
				ComplexLeafTreeNode children = new ComplexLeafTreeNode();
				children.setComponent(result);
				children.setLabel(ent);

				children.setSubmitValue(moduleName + "+" + ent);
				treeNodes.addChild(children);
			}

			String onItemClickExpr = "#{javascript:var choice=context.getSubmittedValue()\nvar array=choice.split(\"+\");\nprintln(\"Selected Choice \",choice);\nviewScope.moduleName=array[0];\nviewScope.entityName=array[1];\nsessionScope.documentId=\"\";\nloadCreateEntity(viewScope.moduleName,viewScope.entityName);\n\n\n\n\n}";

			String refreshId = "EntityPanel" + moduleName;
			XspEventHandler ev2 = CompUtil.createEventHandler("onItemClick",
					"partial", onItemClickExpr, true, refreshId);
			ev2.setOnStart("showWait()");
			ev2.setOnComplete("hideWait()");
			result.getChildren().add(ev2);

			result.addNode(treeNodes);

			XspTableCell cell = CompUtil.createCell(actrow1, "cell" + "00");

			cell.getChildren().add(result);

			actionpanel.getChildren().add(acttable);

		}

		if (readableEntities != null) {

			UIOutlineDropDownButton result1 = new UIOutlineDropDownButton();
			result1.setId("dropDownButton2");
			ComplexContainerTreeNode treeNodes1 = new ComplexContainerTreeNode();
			treeNodes1.setComponent(result1);
			treeNodes1.setLabel("View");
			for (String readent : readableEntities) {
				ComplexLeafTreeNode children1 = new ComplexLeafTreeNode();
				children1.setComponent(result1);
				children1.setLabel(readent);
				children1.setSubmitValue(moduleName + "+" + readent);
				treeNodes1.addChild(children1);
			}

			String onItemClickExpr = "#{javascript:var choice=context.getSubmittedValue()\nvar array=choice.split(\"+\");\nprintln(\"Selected Read Choice \",choice);\nviewScope.moduleName=array[0];\nviewScope.entityName=array[1];\nloadViewEntity(viewScope.moduleName,viewScope.entityName);\n\n\n\n\n}";

			String refreshId = "EntityPanel" + moduleName;
			XspEventHandler ev3 = CompUtil.createEventHandler("onItemClick",
					"partial", onItemClickExpr, true, refreshId);
			ev3.setOnStart("showWait()");
			ev3.setOnComplete("hideWait()");
			result1.getChildren().add(ev3);

			result1.addNode(treeNodes1);

			XspTableCell cell1 = CompUtil.createCell(actrow1, "cell" + "01");

			cell1.getChildren().add(result1);

		}

		createFeatureButtons(actionpanel, moduleName, acttable, actrow1);

		com.getChildren().add(actionpanel);
		com.getChildren().add(entitypanel);

	}

	private void createEntityActionBar(UIComponent comp, String moduleName,
			String entityName, int c, int r, int d) {
		// c, r, d for entity is creatable, editable, deletable based on this we
		// will load buttons
		UIPanelEx actionpanel = new UIPanelEx();
		actionpanel.setId("actionTable" + moduleName + entityName);
		UIPanelEx actionpanel2 = new UIPanelEx();
		actionpanel2.setId("cEntityPanel" + entityName);

		// Create Button
		if (c == 1) {
			String expression3 = "#{javascript:sessionScope.documentId=\"\";viewScope.moduleName=\""
					+ moduleName
					+ "\";viewScope.entityName=\""
					+ entityName
					+ "\";loadCreateEntity(\""
					+ moduleName
					+ "\",\""
					+ entityName + "\");}";
			XspCommandButton button3 = CompUtil.createButton("Create",
					"button3");
			XspEventHandler ev3 = CompUtil.createEventHandler("onclick",
					"complete", expression3, true, null);
			button3.getChildren().add(ev3);
			actionpanel.getChildren().add(button3);
		}
		if (d == 1) {
			// Delete Button
			String expression4 = "#{javascript:}";
			XspCommandButton button4 = CompUtil.createButton("Delete",
					"button4");
			XspEventHandler ev4 = CompUtil.createEventHandler("onclick",
					"complete", expression4, true, null);
			button4.getChildren().add(ev4);
			actionpanel.getChildren().add(button4);
		}

		if (entityName.equals("Employee")) {// If entity readable
			String expression = "#{javascript:loadTestControl(\""
					+ actionpanel.getId()
					+ "\",\"/cc_EntityView.xsp\",\"moduledivx" + moduleName
					+ entityName + "\",\"" + moduleName + "\",\"" + entityName
					+ "\")}";
			XspEventHandler ev4 = CompUtil.createEventHandler("onClientLoad",
					"norefresh", expression, true, null);
			actionpanel.getChildren().add(ev4);
		}

		comp.getChildren().add(actionpanel);

	}

	public void resetUI(UIComponent com, FacesContext context, String page) {
		CompUtil.create(com, context, page);
	}

	public void resetBean() {

		Object user = ManagedBeanUtil.getBean(context, BEAN_NAME);
		try {
			Class<?> c = user.getClass();
			Method m = c.getMethod("reset", null);
			m.invoke(user, null);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public UIComponent getRentity() {
		rentity = new UIPanelEx();
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		String pid = (String) viewscope.get("personunid");
		String moduleName = (String) viewscope.get("moduleName");
		String entityName = (String) viewscope.get("entityName");
		if (moduleName != null & entityName != null) {
			createReadForm(moduleName, entityName);
		}
		return rentity;
	}

	public void setRentity(UIComponent rentity) {
		this.rentity = rentity;
	}

	public void createReadForm(String moduleName, String entityName) {
		XspTable utable = new XspTable();
		utable.setId("tableRead");
		Profile profile = (Profile) JSFUtil
				.getBindingValue("#{security.profile}");
		String eaccess = profile.getEntityAccessType(moduleName, entityName);

		// When the entity is Public R/W show all the fields, don check the
		// profile whether it is visible or not
		if (eaccess.equals("2")) {
			loadViewFields(profile.getAllFieldsNames(moduleName, entityName),
					utable, 0);
		} else {
			loadViewFields(profile
					.getVisibleFieldsNames(moduleName, entityName), utable, 0);
		}
		rentity.getChildren().add(utable);

		// When the entity is Public R/W show Edit button, don't check the
		// profile whether it is visible or not
		if (eaccess.equals("2")) {
			viewScope.put("editEntity", true);
		} else {
			if (profile.isEntityUpdate(moduleName, entityName)) {
				viewScope.put("editEntity", true);
			} else {
				viewScope.put("editEntity", false);
			}
		}

		// If accesstype is Public Read/Write then no need to check about the
		// EntityDelete,Delete should be seen only by document owner
		if (!eaccess.equals("2")) {

			if (profile.isEntityDelete(moduleName, entityName)) {
				viewScope.put("deleteEntity", true);
			} else {
				viewScope.put("deleteEntity", false);
			}

		}

	}

	private void loadViewFields(ArrayList<String> fields, XspTable table,
			int startRow) {
		// This function is used to populate the fields in the given table
		// fields:list of fields
		// table: table component where field label and field will be added
		// startRow: the starting row number of the table where the fields needs
		// to be added
		for (int i = 0; i < fields.size(); i++) {
			XspTableRow row1 = CompUtil.createRow(table, "row" + i);
			for (int j = 0; j < 2; j++) {
				XspTableCell cell = CompUtil.createCell(row1, "cell" + i + j);

				switch (j) {
				case 0:
					CompUtil.createLabel(cell, fields.get(i).toString(), true);
					break;
				case 1:

					CompUtil.createInput(cell, fields.get(i).toString(),
							BEAN_NAME, 1);
					break;
				}
			}
		}
	}

	public ViewEntryCollection getGetmydocs() {
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		String moduleName = (String) viewscope.get("moduleName");
		String entityName = (String) viewscope.get("entityName");
		viewscope.put("fields", "Form,CreatedBy");
		Profile profile = (Profile) JSFUtil
				.getBindingValue("#{security.profile}");

		if (moduleName.contains("_")) {
			moduleName = moduleName.replace("_", " ");
		}

		// get the module name and treat it as database name
		String dbname = moduleName.toLowerCase().replace(" ", "");
		dbname = dbname + ".nsf";
		// ViewName can get from entityName
		String viewName = entityName;
		try {
			Database db = session.getDatabase("", bsuitepath + dbname);
			View allView = db.getView(viewName);

			// check the AccessType for the given EntityName

			Profile profile1 = (Profile) JSFUtil
					.getBindingValue("#{security.profile}");
			String eaccess = profile1.getEntityAccessType(moduleName,
					entityName);
			if (eaccess.equals("1")) {// private
				// to get the query string from the security.searchString
				String querystr = (String) JSFUtil
						.getBindingValue("#{role.searchString}");
				allView.FTSearch(querystr);
			}

			return allView.getAllEntries();
		} catch (Exception e) {
		}
		return null;
	}

	public void setGetmydocs(ViewEntryCollection getmydocs) {
		this.getmydocs = getmydocs;
	}

	public UIComponent getFeatures() {

		return features;
	}

	public void setFeatures(UIComponent features) {
		this.features = features;
	}

	private void loadTab(String moduleName, String entityName) {

	}

}
