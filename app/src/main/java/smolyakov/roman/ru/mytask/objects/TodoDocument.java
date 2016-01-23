package smolyakov.roman.ru.mytask.objects;

import java.io.Serializable;
import java.util.Date;


public class TodoDocument implements Serializable, Comparable<TodoDocument>{
    private static final long serialVersionUID = 6379234629775400593L;




    private PriorityType priorityType = PriorityType.LOW;
    private Integer number;
    private String name;
    private String content;
    private Date createDate;
    private boolean checked;
    private String imagePath;

    public TodoDocument() {
    }

    public TodoDocument(String name, String content, Date createDate, PriorityType priorityType, String imagePath) {
        super();
        this.name = name;
        this.content = content;
        this.createDate = createDate;
        this.priorityType = priorityType;
        this.imagePath = imagePath;
    }
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public PriorityType getPriorityType() {
        return priorityType;
    }

    public void setPriorityType(PriorityType priorityType) {
        this.priorityType = priorityType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TodoDocument that = (TodoDocument) o;

        return number == that.getNumber();

    }

    @Override
    public int hashCode() {
        return number;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getNumber() {

        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(TodoDocument another) {
        return another.getCreateDate().compareTo(createDate);
    }
}
