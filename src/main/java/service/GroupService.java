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

    public JSONObject getGroupById(int name) {
        Group gr = db.getGroupById(name);
        JSONObject groupsJson = new JSONObject();
        groupsJson.put("name", gr.getName());
        groupsJson.put("description", gr.getDescription());
        groupsJson.put("id", gr.getId());
        return groupsJson;
    }

    public JSONObject getAllGroups() {
        List<Group> groups = db.getAllGroups();
        JSONArray groupsArr = new JSONArray();
        JSONObject groupsJson = new JSONObject();
        int i = 0;
        while(!groups.isEmpty()){
            JSONObject groupJson = new JSONObject();
            groupJson.put("name", groups.get(i).getName());
            groupJson.put("description", groups.get(i).getDescription());
            groupJson.put("id", groups.get(i).getId());
            groups.remove(i);
            groupsArr.add(groupJson);
        }
        groupsJson.put("result", groupsArr);
        return groupsJson;
    }

    public void createGroup(JSONObject groupJson) throws SQLException {
        String name = (String) groupJson.get("name");
        String about = (String) groupJson.get("description");
        db.createGroup(name, about);
    }

    public void updateGroup(int id, JSONObject groupJson) throws SQLException {
        db.updateGroup(id, (String) groupJson.get("name"),(String) groupJson.get("description"));
    }

    public void deleteGroup(int id) throws SQLException {
        db.deleteGroup(id);
    }
}
