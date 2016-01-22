package smolyakov.roman.ru.mytask.objects;


import java.util.Comparator;

public class TodoListComparator {

    public static DateComparator dateComparator;
    public static NameComparator nameComparator;
    public static PriorityComparator priorityComparator;

    public static Comparator<TodoDocument> getDateComparator(){
        if (dateComparator==null){
            dateComparator = new DateComparator();
        }
        return dateComparator;
    }

    public static Comparator<TodoDocument> getNameComparator(){
        if (nameComparator==null){
            nameComparator = new NameComparator();
        }
        return nameComparator;
    }

    public static Comparator<TodoDocument> getPriorityComparator(){
        if (priorityComparator==null){
            priorityComparator = new PriorityComparator();
        }
        return priorityComparator;
    }

    private static class NameComparator implements Comparator<TodoDocument>{
        @Override
        public int compare(TodoDocument lhs, TodoDocument rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    private static class DateComparator implements Comparator<TodoDocument>{
        @Override
        public int compare(TodoDocument lhs, TodoDocument rhs) {
            return lhs.getCreateDate().compareTo(rhs.getCreateDate());
        }
    }


    private static class PriorityComparator implements Comparator<TodoDocument>{
        @Override
        public int compare(TodoDocument lhs, TodoDocument rhs) {
            int result = lhs.getPriorityType().compareTo(rhs.getPriorityType());
            if(result == 0){
                result = lhs.getCreateDate().compareTo(rhs.getCreateDate());
            }
            return result;
        }
    }
}


































