public class Task {
    public enum Type {
        TODO("T"),
        DEADLINE("D"),
        EVENT("E");

        private final String shortName;

        Type(String shortName) {
            this.shortName = shortName;
        }
    }

    private final String description;
    private final Type type;
    private final String by;
    private final String from;
    private final String to;
    private boolean isDone;

    public Task(String description) {
        this(Type.TODO, description, null, null, null);
    }

    public Task(String description, String by) {
        this(Type.DEADLINE, description, by, null, null);
    }

    public Task(String description, String from, String to) {
        this(Type.EVENT, description, null, from, to);
    }

    private Task(Type type, String description, String by, String from, String to) {
        this.type = type;
        this.description = description;
        this.by = by;
        this.from = from;
        this.to = to;
        this.isDone = false;
    }

    public void markAsDone() {
        isDone = true;
    }

    public void unmark() {
        isDone = false;
    }

    @Override
    public String toString() {
        String status = isDone ? "[X]" : "[ ]";
        String taskDetails = "[" + type.shortName + "]" + status + " " + description;
        if (type == Type.DEADLINE) {
            taskDetails += " (by: " + by + ")";
        } else if (type == Type.EVENT) {
            taskDetails += " (from: " + from + " to: " + to + ")";
        }
        return taskDetails;
    }
}
