package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DailyPlanner extends Planner {
    private Map<String, String> dailyPlannerTask;
    private List<String> timesList; // time array
    private int interval;  //minutes interval
    private int startHour;
    private int endHour;
    private int NumAgendas;

    /**
     * initialize DailyPlanner
     *
     * @param plannerName: the name of the planner
     * @param startTime:   the start time of the current planner, "HH:MM"
     * @param endTime:     the end time of the current planner, "HH:MM"
     * @param Interval:    time interval between each calendar time, in minutes
     */
    public DailyPlanner(String plannerName, String startTime, String endTime, int Interval) {
        super();
        initializePlannerVars(plannerName, startTime, endTime, Interval);
    }

    public DailyPlanner(int numPlannersLoaded, String plannerName, String startTime, String endTime, int Interval) {
        super(numPlannersLoaded);
        initializePlannerVars(plannerName, startTime, endTime, Interval);
    }

    private void initializePlannerVars(String plannerName, String startTime, String endTime, int Interval) {
        this.plannerName = plannerName;
        this.interval = Interval;
        this.startHour = Integer.parseInt(startTime.substring(0, 2));
        this.endHour = Integer.parseInt(endTime.substring(0, 2));
        this.timesList = new ArrayList<>();
        this.dailyPlannerTask = new HashMap<>();
        this.NumAgendas = 0;
        String timeFormat;
        int m = 0;
        for (int h = this.startHour; h < this.endHour; h++) {
            timeFormat = String.format("%02d:%02d", h, m);
            timesList.add(timeFormat);
            h = h + interval;
        }

        //add all time to Hashmap with empty agenda
        for (String time : timesList) {
            dailyPlannerTask.put(time, "N/A");
        }
    }

    /**
     * Show the planner type is daily planner
     *
     * @return a string represent this planner is daily planner
     */
    public String getType() {
        return "daily";
    }


    /**
     * Set a name for the daily planner representing in String.
     */
    public void setPlannerName(String PlannerName) {
        this.plannerName = PlannerName;
    }


    /**
     * Show the current time based on the system time
     *
     * @return a string representation of the current time
     */
    public String CurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }


    /**
     * Get the number of agendas the daily planner has
     *
     * @return a integer representation of the number of agendas
     */
    public int getNumAgendas() {
        return this.NumAgendas;
    }


    /**
     * Show the current planner
     *
     * @return a string represent this planner's content
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Status: ").append(this.privacyStatus).append("\n");

        String timeInfo = String.format("Start time -> %d:%d, End time -> %d:%d. \n",
                this.startHour, 0, this.endHour, 0);
        String plannerInfo = this.plannerName + "\n" + "ID: " + super.getID() + "\n" + timeInfo + "\nTasks: \n";
        sb.append(plannerInfo);
        for (String time : timesList) {
            sb.append(time);
            sb.append(":");
            sb.append(this.dailyPlannerTask.get(time));
            sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * add agenda to current planner, if the user does not give a time.
     *
     * @param time: the timeslot of the new agenda.
     * @return true iff the agenda is correctly added to current planner
     */
    @Override
    public Boolean add(String time, String agenda) {

        this.NumAgendas++;
        int newStartHour = Integer.parseInt(time.substring(0, 2));

        if (newStartHour < this.startHour) {
            int gap = startHour - newStartHour;
            for (int i = 0; i < gap; i++) {
                int currH = startHour - i - 1;
                String hour = String.format("%02d", currH);
                this.dailyPlannerTask.put(hour + ":00", "N/A");
                this.timesList.add(0, hour + ":00");
            }
            this.dailyPlannerTask.put(String.format("%02d", newStartHour) + ":00", agenda);
            this.startHour = newStartHour;
        } else if (newStartHour > this.endHour) {
            int gap = newStartHour - endHour;
            for (int i = 0; i < gap; i++) {
                int currH = endHour + i + 1;
                String hour = String.format("%02d", currH);
                this.dailyPlannerTask.put(hour + ":00", "N/A");
                this.timesList.add(hour + ":00");
            }
            this.dailyPlannerTask.put(String.format("%02d", newStartHour) + ":00", agenda);
            this.endHour = newStartHour;
        } else {
            String hour = String.format("%02d", newStartHour);
            this.dailyPlannerTask.put(hour + ":00", agenda);
        }
        System.out.println(this.dailyPlannerTask);

        this.NumAgendas++;
        return true;

    }


    /**
     * edit agenda to current planner (given index)
     *
     * @param time   index of the agenda user wish to edit
     * @param agenda content of the agenda user wish to edit
     * @return true iff the agenda is correctly edited on current planner
     */
    @Override
    public Boolean edit(String time, String agenda) {
        return this.add(time, agenda);
    }


    /**
     * @param TaskName   the task name the user wants to change status
     * @param TaskStatus the status the user wants to change
     * @return true iff the planner is correctly changed to the right status
     */
    @Override
    public Boolean ChangeTaskStatus(String TaskName, String TaskStatus) {
        return false; // This is a daily planner which does not need to change status.
    }


    /**
     * delete agenda to current planner
     *
     * @param time index of the agenda user wish to delete
     * @return true iff the agenda is correctly deleted from current planner
     */
    public boolean delete(String time) {
        // delete everything on that time slot, i.e. no option to delete one thing
        // check if is legal time frame
        return edit(time, "N/A");
    }


    /**
     * for Phase 2
     * take the new agenda start time to the closest minutes based on the interval
     *
     * @param NewStartMins the start time for a agenda
     * @param Interval     the time interval set by the user
     * @return the closest time the user may reach
     */
    public int getClosestMins(int NewStartMins, int Interval) {
        //new list of all possible mins given ineterval, ie. 0, 5, 10, 15... for interval=5
        List<Integer> numbers = new ArrayList<>(0);
        numbers.add(0);
        for (int i = 0; i < (60 - Interval); i = i + Interval) {
            numbers.add(i);
        }
        int distance = Math.abs(numbers.get(0) - NewStartMins);
        int idx = 0;
        for (int c = 1; c < numbers.size(); c++) {
            int cdistance = Math.abs(numbers.get(c) - NewStartMins);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        return numbers.get(idx);

    }

    /**
     * get the remaining tasks the user need to do
     *
     * @return a int iff the agenda is not passed.
     */
    public String remainTasks() {
        //get the current time from the system.
        String current_time = CurrentTime();
        //make to the current time toString, and find the substring of the current hour and current mins.
        int current_hour = Integer.parseInt(current_time.substring(0, 2));
        int current_min = Integer.parseInt(current_time.substring(3, 5));
        //construct an empty arraylist that will store the remaining tasks today.
        List<String> remain_tasks = new ArrayList<>();
        //compare the daily tasks time with the current time, if it is later than now, then add to remain_tasks.
        for (String time : timesList) {
            String task_hour = time.substring(0, 2);
            String task_min = time.substring(3, 5);
            //the condition while the hour is the same but min is different.
            if (Integer.parseInt(task_hour) == current_hour) {
                if (Integer.parseInt(task_min) > current_min) {
                    remain_tasks.add(time);
                }
            }
            //the condition while the hour and minn are both different.
            if (Integer.parseInt(task_hour) > current_hour) {
                remain_tasks.add(time);
            }
        }
        //set up a StringBuilder to collect all the information for the remaining tasks.
        StringBuilder RemainTaskSb = new StringBuilder();
        RemainTaskSb.append("Remain tasks: \n");
        for (String task_time : remain_tasks) {
            RemainTaskSb.append(task_time);
            RemainTaskSb.append(":");
            RemainTaskSb.append(this.dailyPlannerTask.get(task_time));
            RemainTaskSb.append("\n");
        }
        return RemainTaskSb.toString();
    }

}