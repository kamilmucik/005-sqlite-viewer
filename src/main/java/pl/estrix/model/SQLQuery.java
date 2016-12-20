package pl.estrix.model;

public class SQLQuery implements Comparable<SQLQuery> {

    private Integer id = 0;

    private String name;

    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int compareTo(SQLQuery o) {
        return ((SQLQuery) o).getId().compareTo(id);
    }
}
