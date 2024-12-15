package tasks;

import java.util.ArrayList;


public class Epic extends tasks.Task {
    private ArrayList<Integer> listSubtask = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public ArrayList<Integer> getListSubtask() {
        return listSubtask;
    }

    public void setListSubtask(ArrayList<Integer> listSubtask) {
        this.listSubtask = listSubtask;
    }
}
