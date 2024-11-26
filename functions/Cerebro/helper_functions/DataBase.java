package helper_functions;

import com.zc.common.ZCProject;

public class DataBase {
    public static void main(String[] args) {
        try {
            ZCProject project = ZCProject.getDefaultProject();
            System.out.println("Project initialized successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
