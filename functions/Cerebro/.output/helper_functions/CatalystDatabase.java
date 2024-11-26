package helper_functions;

import com.zc.api.APIConstants;
import com.zc.common.ZCProject;
import com.zc.component.object.ZCObject;
import com.zc.component.object.ZCRowObject;
import com.zc.component.object.ZCTable;
import com.zc.component.zcql.ZCQL;

import java.util.Map;

public class CatalystDatabase {

    public static void main(String[] args) throws Exception {

        ZCProject.initProject();

        ZCProject adminProject = ZCProject.initProject("admin", APIConstants.ZCUserScope.ADMIN);
        ZCQL.getInstance(adminProject).executeQuery("select * from test");
        //Create a base Object Instance
        ZCObject object = ZCObject.getInstance();
        //Get a Table Instance referring the tableID on base object
        ZCTable tab = object.getTable("4548000000085169");
        //Create a row instance
        ZCRowObject row = ZCRowObject.getInstance();
        //Set the required column values using set() method on the row instance
        row.set("Name", "George Smith");
        row.set("Age", 25);
        //Add the single row to table by calling insertRow() method
        tab.insertRow(row);
    }
}

