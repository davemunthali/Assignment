public class MyObject {
    int id;
    String name;
    String sname;
    static int count = 0;
    MyObject() {
        this.id = count + 1;
        count += 1;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSname() {
        return sname;
    }
    public void setSname(String sname) {
        this.sname = sname;
    }
    @Override
    public String toString() {
        return "MyObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sname='" + sname + '\'' +
                '}';
    }
}

