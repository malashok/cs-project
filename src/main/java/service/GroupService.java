package service;


import db.Database;
import db.Group;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class GroupService {
    public Database db;

    public GroupService(Database db) {
        this.db = db;
    }

//    public JSONObject get_group_by_name(String name) {
//        List<Group> groups = db.пуе("name", name);
//        JSONObject groups_json = new JSONObject();
//        groups_json.put("group_name", groups.get(0).getName());
//        groups_json.put("about", groups.get(0).getAbout());
//        groups_json.put("group_id", groups.get(0).getId());
//        return groups_json;
//    }

    public JSONObject getAllGroups() {
        List<Group> groups = db.getAllGroups();
        JSONArray groups_array = new JSONArray();
        JSONObject groups_json = new JSONObject();
        int i = 0;
        while(!groups.isEmpty()){
            JSONObject group_json = new JSONObject();
            group_json.put("name", groups.get(i).getName());
            group_json.put("about", groups.get(i).getDescription());
            group_json.put("group_id", groups.get(i).getId());
            groups.remove(i);
            groups_array.add(group_json);
        }
        groups_json.put("result", groups_array);
        return groups_json;
    }

    public void createGroup(JSONObject group_json) throws SQLException {
        String name = (String) group_json.get("name");
        String about = (String) group_json.get("about");
        db.createGroup(name, about);
    }

    public void updateGroup(int id, JSONObject group_json) throws SQLException {
        db.updateGroup(id, (String) group_json.get("name"),(String) group_json.get("description"));
    }

    public void deleteGroup(int id) throws SQLException {
        db.deleteGroup(id);
    }
}
